package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.OneToMany;

import models.Drzava;
import models.Faktura;
import models.Grupa;
import models.NaseljenoMesto;
import models.PoslovnaGodina;
import models.PoslovniPartner;
import models.Preduzece;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class Preduzeca extends Controller {

	/**
	 * Stranica se vraca u pocetno stanje. Iz sesije se ukljanja ID, koji je bio
	 * potreban za nextForm mehanizam. Vrsi se ponovno ucitavanje podataka.
	 */
	public static void show(String mode) {
		validation.clear();

		// potrebno za nextForm mehanizam
		session.put("idPreduzeca", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Preduzece> preduzeca = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		render(preduzeca, povezaneForme, mode);
	}

	public static void edit(Preduzece preduzece) {
		validation.clear();
		validation.valid(preduzece);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Preduzece> preduzeca = null;

		if (!validation.hasErrors()) {
			preduzeca = Preduzece.findAll();

			for (Preduzece tmp : preduzeca) {
				if (tmp.id == preduzece.id) {
					tmp.naziv = preduzece.naziv;
					tmp.pib = preduzece.pib;
					tmp.mesto = preduzece.mesto;
					tmp.adresa = preduzece.adresa;
					tmp.telefon = preduzece.telefon;
					tmp.maticniBroj = preduzece.maticniBroj;
					tmp.tekuciRacun = preduzece.tekuciRacun;
					tmp.save();
					break;
				}
			}

			Cache.set("preduzeca", preduzeca);

			validation.clear();

		} else {
			preduzeca = checkCache();

			validation.keep();

			session.put("idPred", preduzece.id);
			session.put("nazivPred", preduzece.naziv);
			session.put("pibPred", preduzece.pib);
			session.put("mestoPred", preduzece.mesto);
			session.put("adresaPred", preduzece.adresa);
			session.put("telefonPred", preduzece.telefon);
			session.put("maticniBrojPred", preduzece.maticniBroj);
			session.put("tekuciRacunPred", preduzece.tekuciRacun);
		}

		renderTemplate("preduzeca/show.html", preduzeca, povezaneForme, mode);
	}

	public static void create(Preduzece preduzece) {
		validation.clear();
		validation.valid(preduzece);

		session.put("mode", "add");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Preduzece> preduzeca = checkCache();

		if (!validation.hasErrors()) {
			preduzece.save();
			preduzeca.add(preduzece);
			Cache.set("preduzeca", preduzeca);

			// potrebno da bi se selektovao dodati red na view delu
			Long idd = preduzece.id;

			validation.clear();
			clearSession();

			renderTemplate("preduzeca/show.html", preduzeca, idd, mode);
		} else {
			validation.keep();

			// potrebno da bi se ispisla greska
			session.put("nazivPred", preduzece.naziv);
			session.put("pibPred", preduzece.pib);
			session.put("mestoPred", preduzece.mesto);
			session.put("adresaPred", preduzece.adresa);
			session.put("telefonPred", preduzece.telefon);
			session.put("maticniBrojPred", preduzece.maticniBroj);
			session.put("tekuciRacunPred", preduzece.tekuciRacun);

			renderTemplate("preduzeca/show.html", preduzeca, povezaneForme, mode);
		}
	}

	public static void filter(Preduzece preduzece) {
		List<Preduzece> preduzeca = Preduzece
				.find("byNazivLikeAndPibLikeAndMestoLikeAndAdresaLikeAndTelefonLikeAndMaticniBrojLikeAndTekuciRacunLike",
						"%" + preduzece.naziv + "%", "%" + preduzece.pib + "%", "%" + preduzece.mesto + "%",
						"%" + preduzece.adresa + "%", "%" + preduzece.telefon + "%", "%" + preduzece.maticniBroj + "%",
						"%" + preduzece.tekuciRacun + "%")
				.fetch();
		session.put("mode", "edit");
		String mode = session.get("mode");
		renderTemplate("preduzeca/show.html", preduzeca, mode);
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<Preduzece> preduzeca = checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		Preduzece preduzece = Preduzece.findById(id);
		Long idd = null;

		for (int i = 1; i < preduzeca.size(); i++) {
			if (preduzeca.get(i).id == id) {
				Preduzece prethodni = preduzeca.get(i - 1);
				idd = prethodni.id;
			}
		}
		preduzece.delete();

		preduzeca = Preduzece.findAll();
		Cache.set("preduzeca", preduzeca);

		renderTemplate("preduzeca/show.html", preduzeca, idd, povezaneForme, mode);
	}

	/**
	 * Metoda koja na osnovu oznacenog preduzeca, prelazi na odabarnu formu, i
	 * prikazuje samo podatke izabrane forme u okviru tog preduzeca.
	 * 
	 * @param id
	 *            ID oznacenog Preduzeca
	 * @param forma
	 *            Izabrana forma na koju se prelazi
	 */
	public static void nextForm(Long id, String forma) {

		session.put("idPreduzeca", id);
		clearSession();

		if (forma.equals("poslovneGodine")) {
			List<Preduzece> preduzeca = checkCache();
			List<PoslovnaGodina> poslovneGodine = findPoslovneGodine(id);
			renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca);
		}

	}

	public static void refresh() {
		String mode = session.get("mode");

		List<Preduzece> preduzeca = checkCache();

		renderTemplate("preduzeca/show.html", preduzeca, mode);
	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	private static boolean clearSession() {
		session.put("idPred", null);
		session.put("nazivPred", null);
		session.put("pibPred", null);
		session.put("mestoPred", null);
		session.put("adresaPred", null);
		session.put("telefonPred", null);
		session.put("maticniBrojPred", null);
		session.put("tekuciRacunPred", null);

		return true;
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<Preduzece> checkCache() {
		List<Preduzece> preduzeca = (List<Preduzece>) Cache.get("preduzeca");

		if (preduzeca == null) {
			preduzeca = Preduzece.findAll();
			Cache.set("preduzeca", preduzeca);
		}

		return preduzeca;
	}

	/**
	 * Pomocna metoda za nextForm mehanizam. Nalazi sve poslovne godine u okviru
	 * jednog izabranog preduzeca.
	 * 
	 * @param idPreduzeca
	 *            ID izabranog preduzeca
	 */
	public static List<PoslovnaGodina> findPoslovneGodine(Long idPreduzeca) {
		List<PoslovnaGodina> poslovneGodineAll = PoslovnaGodina.findAll();
		List<PoslovnaGodina> poslovneGodine = new ArrayList<>();

		for (PoslovnaGodina pg : poslovneGodineAll) {
			if (pg.preduzece.id == idPreduzeca) {
				poslovneGodine.add(pg);
			}
		}

		return poslovneGodine;
	}

	/**
	 * Pomocna metoda koja vraca listu povezanih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFields() {
		Class preduzeceClass = Preduzece.class;
		Field[] fields = preduzeceClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

}
