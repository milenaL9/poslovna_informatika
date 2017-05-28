package controllers;

import java.util.List;

import models.Cenovnik;
import models.KatalogRobeIUsluga;
import models.StavkaCenovnika;
import play.cache.Cache;
import play.mvc.Controller;

public class StavkeCenovnika extends Controller {

	/**
	 * Metoda se pokrece pri prvom ucitavanju stranice. Stranica se vraca u
	 * pocetno stanje. Brisu se sesije, i iscitavaju se svi podaci.
	 */
	public static void show() {
		validation.clear();
		clearSession();

		session.put("idKataloga", "null");
		session.put("idCenovnika", "null");

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = checkCache();

		render(kataloziRobeIUsluga, cenovnici, stavkeCenovnika, mode);

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

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Cenovnik> cenovnici = Cenovnici.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillList();

		renderTemplate("StavkeCenovnika/show.html", kataloziRobeIUsluga, cenovnici, stavkeCenovnika, mode);

	}

	public static void create() {

	}

	public static void edit() {

	}

	public static void filter() {

	}

	public static void delete() {

	}

	public static void nextForm() {

	}

	public static void search() {

	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	public static boolean clearSession() {
		session.put("idSC", null);
		session.put("cena", null);
		return true;
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<StavkaCenovnika> checkCache() {
		List<StavkaCenovnika> stavkeCenovnika = (List<StavkaCenovnika>) Cache.get("stavkeCenovnika");

		if (stavkeCenovnika == null) {
			stavkeCenovnika = StavkaCenovnika.findAll();
			Cache.set("stavkeCenovnika", stavkeCenovnika);
		}

		return stavkeCenovnika;
	}

	/**
	 * Pomocna metoda koja popunjava listu stavki cenovnika. Vrsi se provera da
	 * li se radi nextForm mehanizam ili normalno ucitavanje stranice. Ukoliko
	 * se radi nextForm, potrebno je vratiti samo one stavnke cenovnika u okviru
	 * izabranog kataloga.
	 */
	public static List<StavkaCenovnika> fillList() {
		List<StavkaCenovnika> stavkeCenovnika = null;

		if (!session.get("idKataloga").equals("null")) {
			Long id = Long.parseLong(session.get("idKataloga"));
			stavkeCenovnika = KataloziRobeIUsluga.findStavkeCenovnika(id);
		} else if (!session.get("idCenovnika").equals("null")) {
			Long id = Long.parseLong(session.get("idCenovnika"));
			stavkeCenovnika = Cenovnici.findStavkeCenovnika(id);
		} else {
			stavkeCenovnika = checkCache();
		}

		return stavkeCenovnika;
	}

}
