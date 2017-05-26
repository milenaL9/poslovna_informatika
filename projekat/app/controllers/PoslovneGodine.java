package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Drzava;
import models.NaseljenoMesto;
import models.PoslovnaGodina;
import models.Preduzece;
import play.cache.Cache;
import play.mvc.Controller;

public class PoslovneGodine extends Controller {

	/**
	 * Metoda se pokrece pri prvom ucitavanju stranice. Stranica se vraca u
	 * pocetno stanje. Brisu se sesije, i iscitavaju se svi podaci.
	 */
	public static void show() {
		validation.clear();
		clearSession();
		session.put("idPreduzeca", "null");

		String mode = "edit";

		session.put("mode", "edit");
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = checkCache();

		render(preduzeca, poslovneGodine, mode);
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

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = fillList();

		renderTemplate("PoslovneGodine/show.html", preduzeca, poslovneGodine, mode);
	}

	public static void edit(PoslovnaGodina poslovnaGodina, Long preduzece) {
		validation.clear();
		validation.valid(poslovnaGodina);

		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<PoslovnaGodina> poslovneGodine = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		if (!validation.hasErrors()) {
			poslovneGodine = PoslovnaGodina.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			for (PoslovnaGodina tmp : poslovneGodine) {
				if (tmp.id == poslovnaGodina.id) {
					tmp.brojGodine = poslovnaGodina.brojGodine;
					tmp.aktivna = poslovnaGodina.aktivna;
					tmp.preduzece = findPreduzece;
					tmp.save();
					break;
				}
			}

			Cache.set("poslovneGodine", poslovneGodine);

			poslovneGodine.clear();
			poslovneGodine = fillList();

			validation.clear();

		} else {
			validation.keep();

			poslovneGodine = fillList();

			session.put("idPG", poslovnaGodina.id);
			session.put("brojGodine", poslovnaGodina.brojGodine);
			session.put("aktivna", poslovnaGodina.aktivna);

		}
		renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, mode);
	}

	public static void create(PoslovnaGodina poslovnaGodina, Long preduzece) {
		validation.clear();
		clearSession();

		validation.valid(poslovnaGodina);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<PoslovnaGodina> poslovneGodine = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		if (!validation.hasErrors()) {
			poslovneGodine = PoslovnaGodina.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			poslovnaGodina.preduzece = findPreduzece;

			poslovnaGodina.save();
			poslovneGodine.add(poslovnaGodina);
			Cache.set("poslovneGodine", poslovneGodine);

			Long idd = poslovnaGodina.id;

			poslovneGodine.clear();
			poslovneGodine = fillList();

			validation.clear();

			renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, idd, mode);
		} else {
			validation.keep();

			poslovneGodine = fillList();

			session.put("idPG", poslovnaGodina.id);
			session.put("brojGodine", poslovnaGodina.brojGodine);
			session.put("aktivna", poslovnaGodina.aktivna);

			renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, mode);
		}
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<PoslovnaGodina> poslovneGodine = checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		PoslovnaGodina poslovnaGodina = PoslovnaGodina.findById(id);
		Long idd = null;

		for (int i = 1; i < poslovneGodine.size(); i++) {
			if (poslovneGodine.get(i).id == id) {
				PoslovnaGodina prethodni = poslovneGodine.get(i - 1);
				idd = prethodni.id;
			}
		}
		poslovnaGodina.delete();

		Cache.set("poslovneGodine", poslovneGodine);

		poslovneGodine.clear();
		poslovneGodine = fillList();

		renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, idd, mode);
	}

	public static void filter(PoslovnaGodina poslovnaGodina) {
		List<PoslovnaGodina> poslovneGodine = PoslovnaGodina
				.find("byBrojGodineLikeAndAktivna", "%" + poslovnaGodina.brojGodine + "%", poslovnaGodina.aktivna)
				.fetch();

		List<Preduzece> preduzeca = Preduzeca.checkCache();

		session.put("mode", "edit");
		String mode = session.get("mode");

		renderTemplate("PoslovneGodine/show.html", poslovneGodine, preduzeca, mode);
	}

	public static void refresh() {
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = fillList();

		String mode = session.get("mode");

		renderTemplate("PoslovneGodine/show.html", preduzeca, poslovneGodine, mode);
	}

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<PoslovnaGodina> checkCache() {
		List<PoslovnaGodina> poslovneGodine = (List<PoslovnaGodina>) Cache.get("poslovneGodine");

		if (poslovneGodine == null) {
			poslovneGodine = PoslovnaGodina.findAll();
			Cache.set("poslovneGodine", poslovneGodine);
		}

		return poslovneGodine;
	}

	/** Pomocna metoda za brisanje podataka iz sesije. */
	public static boolean clearSession() {
		session.put("idPG", null);
		session.put("brojGodine", null);
		session.put("aktivna", null);
		return true;
	}

	/**
	 * Pomocna metoda koja popunjava listu poslovnih godina. Vrsi se provera da
	 * li se radi nextForm mehanizam ili normalno ucitavanje stranice. Ukoliko
	 * se radi nextForm, potrebno je vratiti samo one poslovne godine u okviru
	 * izabranog preduzeca.
	 */
	public static List<PoslovnaGodina> fillList() {
		List<PoslovnaGodina> poslovneGodine = null;
		if (!session.get("idPreduzeca").equals("null")) {
			Long id = Long.parseLong(session.get("idPreduzeca"));
			poslovneGodine = Preduzeca.findPoslovneGodine(id);
		} else {
			poslovneGodine = checkCache();
		}

		return poslovneGodine;
	}
}
