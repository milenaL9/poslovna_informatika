package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import models.Cenovnik;
import models.StavkaCenovnika;
import play.cache.Cache;
import play.mvc.Controller;

public class Cenovnici extends Controller {

	/**
	 * Stranica se vraca u pocetno stanje. Iz sesije se ukljanja ID, koji je bio
	 * potreban za nextForm mehanizam. Vrsi se ponovno ucitavanje podataka.
	 */
	public static void show(String mode) {
		validation.clear();

		// potrebno za nextForm mehanizam
		session.put("idCenovnika", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Cenovnik> cenovnici = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		render(cenovnici, povezaneForme, mode);
	}

	public static void edit(Cenovnik cenovnik) {
		validation.clear();
		validation.valid(cenovnik);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Cenovnik> cenovnici = null;

		if (!validation.hasErrors()) {
			cenovnici = Cenovnik.findAll();

			for (Cenovnik tmp : cenovnici) {
				if (tmp.id == cenovnik.id) {
					tmp.naziv = cenovnik.naziv;
					tmp.datumVazenja = cenovnik.datumVazenja;
					tmp.save();
					break;
				}
			}

			Cache.set("cenovnici", cenovnici);

			validation.clear();

		} else {
			cenovnici = checkCache();

			validation.keep();

			session.put("idCen", cenovnik.id);
			session.put("nazivCenovnik", cenovnik.naziv);
			session.put("datumCenovnik", cenovnik.datumVazenja);
		}

		renderTemplate("cenovnici/show.html", cenovnici, povezaneForme, mode);
	}

	public static void create(Cenovnik cenovnik) {
		validation.clear();
		validation.valid(cenovnik);

		session.put("mode", "add");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Cenovnik> cenovnici = checkCache();

		if (!validation.hasErrors()) {
			cenovnik.save();
			cenovnici.add(cenovnik);
			Cache.set("cenovnici", cenovnici);

			// potrebno da bi se selektovao dodati red na view delu
			Long idd = cenovnik.id;

			validation.clear();
			clearSession();

			renderTemplate("cenovnici/show.html", cenovnici, povezaneForme, idd, mode);
		} else {
			validation.keep();

			// potrebno da bi se ispisla greska
			session.put("nazivCenovnik", cenovnik.naziv);
			session.put("datumCenovnik", cenovnik.datumVazenja);

			renderTemplate("cenovnici/show.html", cenovnici, povezaneForme, mode);
		}
	}

	public static void filter(Cenovnik cenovnik) {
		List<Cenovnik> cenovnici = Cenovnik
				.find("byNazivLikeAndDatumVazenjaLike", "%" + cenovnik.naziv + "%", "%" + cenovnik.datumVazenja + "%")
				.fetch();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("cenovnici/show.html", cenovnici, povezaneForme, mode);
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<Cenovnik> cenovnici = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		Cenovnik cenovnik = Cenovnik.findById(id);
		Long idd = null;

		for (int i = 1; i < cenovnici.size(); i++) {
			if (cenovnici.get(i).id == id) {
				Cenovnik prethodni = cenovnici.get(i - 1);
				idd = prethodni.id;
			}
		}
		cenovnik.delete();

		cenovnici = Cenovnik.findAll();
		Cache.set("cenovnici", cenovnici);

		renderTemplate("cenovnici/show.html", cenovnici, idd, povezaneForme, mode);
	}

	/**
	 * Metoda koja na osnovu oznacenog cenovnika, prelazi na odabarnu formu, i
	 * prikazuje samo podatke izabrane forme u okviru tog cenovnika.
	 * 
	 * @param id
	 *            ID oznacenog Cenovnika
	 * @param forma
	 *            Izabrana forma na koju se prelazi
	 */
	public static void nextForm(Long id, String forma) {
		session.put("idCenovnika", id);
		clearSession();

		if (forma.equals("stavkeCenovnika")) {
			List<Cenovnik> cenovnici = checkCache();
			List<StavkaCenovnika> stavkeCenovnika = findStavkeCenovnika(id);
			renderTemplate("StavkeCenovnika/show.html", stavkeCenovnika, cenovnici);
		}
	}

	public static void refresh() {
		String mode = session.get("mode");

		List<Cenovnik> cenovnici = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("cenovnici/show.html", cenovnici, povezaneForme, mode);
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<Cenovnik> checkCache() {
		List<Cenovnik> cenovnici = (List<Cenovnik>) Cache.get("cenovnici");

		if (cenovnici == null) {
			cenovnici = Cenovnik.findAll();
			Cache.set("cenovnici", cenovnici);
		}

		return cenovnici;
	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	private static boolean clearSession() {
		session.put("idCen", null);
		session.put("nazivCenovnik", null);
		session.put("datumCenovnik", null);

		return true;
	}

	/**
	 * Pomocna metoda koja vraca listu povezanih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFields() {
		Class cenovnikClass = Cenovnik.class;
		Field[] fields = cenovnikClass.getFields();

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
	 * Pomocna metoda za nextForm mehanizam. Nalazi sve stavke ccenovnika u
	 * okviru jednog izabranog cenovnika.
	 * 
	 * @param idCenovnika
	 *            ID izabranog cenovnika
	 */
	public static List<StavkaCenovnika> findStavkeCenovnika(Long idCenovnika) {
		List<StavkaCenovnika> stavkeCenovnikaAll = StavkaCenovnika.findAll();
		List<StavkaCenovnika> stavkeCenovnika = new ArrayList<>();

		for (StavkaCenovnika sc : stavkeCenovnikaAll) {
			if (sc.cenovnik.id == idCenovnika) {
				stavkeCenovnika.add(sc);
			}
		}

		return stavkeCenovnika;
	}
}
