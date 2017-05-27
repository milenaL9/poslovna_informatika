package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import models.KatalogRobeIUsluga;
import models.Podgrupa;
import play.cache.Cache;
import play.mvc.Controller;

public class KataloziRobeIUsluga extends Controller {

	/**
	 * Metoda se pokrece pri prvom ucitavanju stranice. Stranica se vraca u
	 * pocetno stanje. Brisu se sesije, i iscitavaju se svi podaci.
	 */
	public static void show() {
		validation.clear();
		clearSession();

		session.put("idPodgrupe", "null");

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

		renderTemplate("KataloziRobeIUsluga/show.html", podgrupe, kataloziRobeIUsluga, mode);
	}

	public static void create(KatalogRobeIUsluga katalogRobeIUsluga, Long podgrupa) {
		validation.clear();
		clearSession();

		validation.valid(katalogRobeIUsluga);

		session.put("mode", "add");
		String mode = session.get("mode");

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

			renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, idd, mode);
		} else {
			validation.keep();

			kataloziRobeIUsluga = fillList();

			session.put("nazivStavkeKataloga", katalogRobeIUsluga.nazivStavkeKataloga);
			session.put("opisStavkeKataloga", katalogRobeIUsluga.opisStavkeKataloga);

			renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, mode);
		}
	}

	public static void edit(KatalogRobeIUsluga katalogRobeIUsluga, Long podgrupa) {
//		validation.clear();
//		validation.valid(katalogRobeIUsluga);
//
//		clearSession();
//
//		session.put("mode", "add");
//		String mode = session.get("mode");
//
//		List<KatalogRobeIUsluga> kataloziRobeIUsluga = null;
//		List<Podgrupa> podgrupe = Podgrupe.checkCache();
//
//		if (!validation.hasErrors()) {
//			kataloziRobeIUsluga = KatalogRobeIUsluga.findAll();
//
//			// kada je disable- ovan combobox ne pokupi vrednost
//			Podgrupa findPodgrupa = null;
//			if (podgrupa == null) {
//				Long id = Long.parseLong(session.get("idPodgrupe"));
//				findPodgrupa = Podgrupa.findById(id);
//			} else {
//				findPodgrupa = Podgrupa.findById(podgrupa);
//			}
//
//			for (KatalogRobeIUsluga tmp : kataloziRobeIUsluga) {
//				if (tmp.id == katalogRobeIUsluga.id) {
//					tmp.nazivStavkeKataloga = katalogRobeIUsluga.nazivStavkeKataloga;
//					tmp.opisStavkeKataloga = katalogRobeIUsluga.opisStavkeKataloga;
//					tmp.podgrupa = findPodgrupa;
//					tmp.save();
//					break;
//				}
//			}
//
//			Cache.set("kataloziRobeIUsluga", kataloziRobeIUsluga);
//
//			kataloziRobeIUsluga.clear();
//			kataloziRobeIUsluga = fillList();
//
//			validation.clear();
//		} else {
//			validation.keep();
//
//			kataloziRobeIUsluga = fillList();
//
//			session.put("idKRU", katalogRobeIUsluga.id);
//			session.put("nazivStavkeKataloga", katalogRobeIUsluga.nazivStavkeKataloga);
//			session.put("opisStavkeKataloga", katalogRobeIUsluga.opisStavkeKataloga);
//
//		}
//		renderTemplate("KataloziRobeIUsluga/show.html", kataloziRobeIUsluga, podgrupe, mode);
	}

	public static void filter() {

	}

	public static void refresh() {

	}

	public static void nextForm() {

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

		if (kataloziRobeIUsluga == null) {
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
}
