package controllers;

import java.util.List;

import models.Cenovnik;
import models.Preduzece;
import play.cache.Cache;
import play.mvc.Controller;

public class Cenovnici extends Controller {

	public static void show(String mode) {
		validation.clear();
		session.put("idCenovnika", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Cenovnik> cenovnici = checkCache();

		render(cenovnici, mode);
	}

	public static void edit(Preduzece preduzece) {

	}

	/** TODO: Ne pokupi vrednost za datum */
	public static void create(Cenovnik cenovnik) {
		System.out.println("naziv: " + cenovnik.naziv);
		System.out.println("datum: " + cenovnik.datumVazenja);
		validation.clear();
		validation.valid(cenovnik);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<Cenovnik> cenovnici = checkCache();

		if (!validation.hasErrors()) {
			cenovnik.save();
			cenovnici.add(cenovnik);
			Cache.set("cenovnici", cenovnici);

			Long idd = cenovnik.id;

			validation.clear();
			clearSession();

			renderTemplate("cenovnici/show.html", cenovnici, idd, mode);
		} else {
			validation.keep();

			session.put("nazivCenovnik", cenovnik.naziv);
			session.put("datumCenovnik", cenovnik.datumVazenja);

			renderTemplate("cenovnici/show.html", cenovnici, mode);
		}
	}

	public static void filter(Preduzece preduzece) {

	}

	public static void delete(Long id) {

	}

	public static void nextForm(Long id, String forma) {

	}

	public static void refresh() {

	}

	/**
	 * Za cuvanje kompleksnijih podataka koji treba da budu dostupni kroz vise
	 * zahteva koristi se Cache. Vrsi se provera da li su zeljeni podaci i dalje
	 * na Cache-u.
	 */
	public static List<Cenovnik> checkCache() {
		List<Cenovnik> cenovnici = (List<Cenovnik>) Cache.get("cenovnici");

		if (cenovnici == null) {
			cenovnici = Cenovnik.findAll();
			Cache.set("cenovnici", cenovnici);
		}

		return cenovnici;
	}

	/** Brisanje podataka iz sesije. */
	private static boolean clearSession() {
		session.put("idCenovnik", null);
		session.put("datumCenovnik", null);

		return true;
	}
}
