package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import models.Cenovnik;
import models.KatalogRobeIUsluga;
import models.Podgrupa;
import models.StavkaCenovnika;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class KataloziRobeIUsluga extends Controller {

	/**
	 * Metoda se pokrece pri prvom ucitavanju stranice. Stranica se vraca u
	 * pocetno stanje. Iz sesije se ukljanja ID, koji je bio potreban za
	 * nextForm mehanizam. Vrsi se ponovno ucitavanje podataka.
	 */
	public static void show() {
		validation.clear();
		clearSession();

		// potrebno za nextForm mehanizam
		session.put("idPodgrupe", "null");
		session.put("idKataloga", "null");

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<Podgrupa> podgrupe = Podgrupe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = checkCache();

		List<String> povezaneForme = getForeignKeysFields();

		render(kataloziRobeIUsluga, podgrupe, povezaneForme, mode);
	}

	/**
	 * Metoda pomocu koje se vrsi promena stanja.
	 * 
	 * @param mode
	 *            U renderTemplate ga je obavezno proslediti, jer se na osnovu
	 *            njega na view delu menja action.
	 */
	public static void changeMode(String mode) {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Podgrupa> podgrupe = Podgrupe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = fillList();
		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("KataloziRobeIUsluga/show.html", podgrupe, povezaneForme, kataloziRobeIUsluga, mode);
	}

	public static void create(KatalogRobeIUsluga katalogRobeIUsluga, Long podgrupa) {
		validation.clear();
		clearSession();

		validation.valid(katalogRobeIUsluga);

		session.put("mode", "add");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = null;
		List<Podgrupa> podgrupe = Podgrupe.checkCache();

		if (!validation.hasErrors()) {
			kataloziRobeIUsluga = KatalogRobeIUsluga.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Podgrupa findPodgrupa = null;
			if (podgrupa == null) {
				Long id = Long.parseLong(session.get("idPodgrupe"));
				findPodgrupa = Podgrupa.findById(id);
			} else {
				findPodgrupa = Podgrupa.findById(podgrupa);
			}

			katalogRobeIUsluga.podgrupa = findPodgrupa;

			katalogRobeIUsluga.save();
			kataloziRobeIUsluga.add(katalogRobeIUsluga);
			Cache.set("kataloziRobeIUsluga", kataloziRobeIUsluga);

			Long idd = katalogRobeIUsluga.id;

			kataloziRobeIUsluga.clear();
			kataloziRobeIUsluga = fillList();

			validation.clear();

			renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, idd, povezaneForme, mode);
		} else {
			validation.keep();

			kataloziRobeIUsluga = fillList();

			session.put("nazivStavkeKataloga", katalogRobeIUsluga.nazivStavkeKataloga);
			session.put("opisStavkeKataloga", katalogRobeIUsluga.opisStavkeKataloga);

			renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, povezaneForme, mode);
		}
	}

	public static void edit(KatalogRobeIUsluga katalogRobeIUsluga, Long podgrupa) {
		validation.clear();
		validation.valid(katalogRobeIUsluga);

		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = null;
		List<Podgrupa> podgrupe = Podgrupe.checkCache();

		if (!validation.hasErrors()) {
			kataloziRobeIUsluga = KatalogRobeIUsluga.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Podgrupa findPodgrupa = null;
			if (podgrupa == null) {
				Long id = Long.parseLong(session.get("idPodgrupe"));
				findPodgrupa = Podgrupa.findById(id);
			} else {
				findPodgrupa = Podgrupa.findById(podgrupa);
			}

			for (KatalogRobeIUsluga tmp : kataloziRobeIUsluga) {
				if (tmp.id == katalogRobeIUsluga.id) {
					tmp.nazivStavkeKataloga = katalogRobeIUsluga.nazivStavkeKataloga;
					tmp.opisStavkeKataloga = katalogRobeIUsluga.opisStavkeKataloga;
					tmp.podgrupa = findPodgrupa;
					tmp.save();
					break;
				}
			}

			Cache.set("kataloziRobeIUsluga", kataloziRobeIUsluga);

			kataloziRobeIUsluga.clear();
			kataloziRobeIUsluga = fillList();

			validation.clear();
		} else {
			validation.keep();

			kataloziRobeIUsluga = fillList();

			session.put("idKRU", katalogRobeIUsluga.id);
			session.put("nazivStavkeKataloga", katalogRobeIUsluga.nazivStavkeKataloga);
			session.put("opisStavkeKataloga", katalogRobeIUsluga.opisStavkeKataloga);

		}

		renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, povezaneForme, mode);
	}

	public static void filter(KatalogRobeIUsluga katalogRobeIUsluga) {
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KatalogRobeIUsluga
				.find("byNazivStavkeKatalogaLikeAndOpisStavkeKatalogaLike",
						"%" + katalogRobeIUsluga.nazivStavkeKataloga + "%",
						"%" + katalogRobeIUsluga.opisStavkeKataloga + "%")
				.fetch();

		List<Podgrupa> podgrupe = Podgrupe.checkCache();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, povezaneForme, mode);
	}

	public static void refresh() {
		List<Podgrupa> podgrupe = Podgrupe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = fillList();
		List<String> povezaneForme = getForeignKeysFields();

		String mode = session.get("mode");

		renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, povezaneForme, mode);

	}

	/**
	 * Metoda koja na osnovu oznacenog kataloga robe i usluga, prelazi na
	 * odabarnu formu, i prikazuje samo podatke izabrane forme u okviru tog
	 * kataloga.
	 * 
	 * @param id
	 *            ID oznacenog Kataloga robe i usluga
	 * @param forma
	 *            Izabrana forma na koju se prelazi
	 */
	public static void nextForm(Long id, String forma) {
		session.put("idKataloga", id);
		session.put("idCenovnika", "null");
		clearSession();

		if (forma.equals("stavkeCenovnika")) {
			List<KatalogRobeIUsluga> kataloziRobeIUsluga = checkCache();
			List<Cenovnik> cenovnici = Cenovnici.checkCache();
			List<StavkaCenovnika> stavkeCenovnika = findStavkeCenovnika(id);

			renderTemplate("StavkeCenovnika/show.html", kataloziRobeIUsluga, cenovnici, stavkeCenovnika);

		} else if (forma.equals("stavkeFakture")) {

		}
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = checkCache();
		List<Podgrupa> podgrupe = Podgrupe.checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		KatalogRobeIUsluga katalogRobeIUsluga = KatalogRobeIUsluga.findById(id);
		Long idd = null;

		for (int i = 0; i < kataloziRobeIUsluga.size(); i++) {
			if (kataloziRobeIUsluga.get(i).id == id) {
				KatalogRobeIUsluga prethodni = kataloziRobeIUsluga.get(i - 1);
				idd = prethodni.id;
			}
		}
		katalogRobeIUsluga.delete();

		Cache.set("kataloziRobeIUsluga", kataloziRobeIUsluga);

		kataloziRobeIUsluga.clear();
		kataloziRobeIUsluga = fillList();

		renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, idd, povezaneForme, mode);

	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	public static boolean clearSession() {
		session.put("idKRU", null);
		session.put("nazivStavkeKataloga", null);
		session.put("opisStavkeKataloga", null);
		return true;
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<KatalogRobeIUsluga> checkCache() {
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = (List<KatalogRobeIUsluga>) Cache.get("kataloziRobeIUsluga");

		if ((kataloziRobeIUsluga == null) || (kataloziRobeIUsluga.size() == 0)) {
			kataloziRobeIUsluga = KatalogRobeIUsluga.findAll();
			Cache.set("kataloziRobeIUsluga", kataloziRobeIUsluga);
		}

		return kataloziRobeIUsluga;
	}

	/**
	 * Pomocna metoda koja vraca listu povezanih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFields() {
		Class katalogRobeiUslugaClass = KatalogRobeIUsluga.class;
		Field[] fields = katalogRobeiUslugaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	/**
	 * Pomocna metoda koja popunjava listu kataloga roba i usluga. Vrsi se
	 * provera da li se radi nextForm mehanizam ili normalno ucitavanje
	 * stranice. Ukoliko se radi nextForm, potrebno je vratiti samo one kataloge
	 * roba i usluga u okviru izabrane podgrupe.
	 */
	public static List<KatalogRobeIUsluga> fillList() {
		List<KatalogRobeIUsluga> katalozi = null;
		if (!session.get("idPodgrupe").equals("null")) {
			Long id = Long.parseLong(session.get("idPodgrupe"));
			katalozi = Podgrupe.findKataloziRobeIUsluga(id);
		} else {
			katalozi = checkCache();
		}

		return katalozi;
	}

	/**
	 * Pomocna metoda za nextForm mehanizam. Nalazi sve stavke cenovnika u
	 * okviru jednog izabranog kataloga.
	 * 
	 * @param idKataloga
	 *            ID izabranog kataloga
	 */
	public static List<StavkaCenovnika> findStavkeCenovnika(Long idKataloga) {
		List<StavkaCenovnika> stavkeCenovnikaAll = StavkaCenovnika.findAll();
		List<StavkaCenovnika> stavkeCenovnika = new ArrayList<>();

		for (StavkaCenovnika sc : stavkeCenovnikaAll) {
			if (sc.katalogRobeIUsluga.id == idKataloga) {
				stavkeCenovnika.add(sc);
			}
		}

		return stavkeCenovnika;
	}
}
