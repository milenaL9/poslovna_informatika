package controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import models.Drzava;
import models.NaseljenoMesto;
import play.cache.Cache;
import play.data.validation.Valid;

import play.mvc.Controller;
import play.mvc.With;

//@With(Secure.class)
//@Check("administrator")
public class Drzave extends Controller {

	public static void show(String mode) {
		validation.clear();
		session.put("idDrzave", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Drzava> drzave = checkCache();

		render(drzave, mode);
	}

	public static void edit(Drzava drzava) {
		validation.clear();
		validation.valid(drzava);
		clearSession();
		session.put("mode", "edit");

		List<Drzava> drzave = null;

		if (!validation.hasErrors()) {
			drzave = Drzava.findAll();

			for (Drzava tmp : drzave) {
				if (tmp.id == drzava.id) {
					tmp.oznaka = drzava.oznaka;
					tmp.naziv = drzava.naziv;
					tmp.save();
					break;
				}
			}

			Cache.set("drzave", drzave);

			validation.clear();

		} else {
			drzave = checkCache();

			validation.keep();

			session.put("idDrz", drzava.id);
			session.put("editOznaka", drzava.oznaka);
			session.put("editNaziv", drzava.naziv);
		}

		renderTemplate("drzave/show.html", drzave);
	}

	public static void create(Drzava drzava) {
		validation.clear();
		validation.valid(drzava);

		session.put("mode", "add");

		List<Drzava> drzave = checkCache();

		if (!validation.hasErrors()) {
			drzava.save();
			drzave.add(drzava);
			Cache.set("drzave", drzave);

			Long idd = drzava.id;

			validation.clear();
			clearSession();

			renderTemplate("drzave/show.html", drzave, idd);
		} else {
			validation.keep();

			session.put("editOznaka", drzava.oznaka);
			session.put("editNaziv", drzava.naziv);

			renderTemplate("drzave/show.html", drzave);
		}
	}

	public static void filter(Drzava drzava) {
		List<Drzava> drzave = Drzava
				.find("byNazivLikeAndOznakaLike", "%" + drzava.naziv + "%", "%" + drzava.oznaka + "%").fetch();
		session.put("mode", "edit");
		renderTemplate("drzave/show.html", drzave);
	}

	public static void delete(Long id) {
		List<Drzava> drzave = checkCache();

		Drzava drzava = Drzava.findById(id);
		Long idd = null;

		for (int i = 1; i < drzave.size(); i++) {
			if (drzave.get(i).id == id) {
				Drzava prethodni = drzave.get(i - 1);
				idd = prethodni.id;
			}
		}
		drzava.delete();

		drzave = Drzava.findAll();
		Cache.set("drzave", drzave);

		renderTemplate("drzave/show.html", drzave, idd);

	}

	public static void nextForm(Long id) {
		List<Drzava> drzave = checkCache();

		List<NaseljenoMesto> naseljenaMesta = findNaseljenaMesta(id);
		session.put("idDrzave", id);
		session.put("id", null);
		session.put("oznaka", null);
		session.put("naziv", null);
		session.put("postanskiBroj", null);

		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
	}

	public static void refresh() {
		List<Drzava> drzave = checkCache();

		renderTemplate("drzave/show.html", drzave);
	}

	/**
	 * Pomocne metode.
	 */
	public static List<Drzava> checkCache() {
		List<Drzava> drzave = (List<Drzava>) Cache.get("drzave");

		if (drzave == null) {
			drzave = Drzava.findAll();
			Cache.set("drzave", drzave);
		}

		return drzave;
	}

	private static boolean clearSession() {
		session.put("idDrz", null);
		session.put("editOznaka", null);
		session.put("editNaziv", null);

		return true;
	}

	public static List<NaseljenoMesto> findNaseljenaMesta(Long idDrzave) {
		List<NaseljenoMesto> naseeljenaMestaAll = NaseljenoMesto.findAll();
		List<NaseljenoMesto> naseljenaMesta = new ArrayList<>();

		for (NaseljenoMesto nm : naseeljenaMestaAll) {
			if (nm.drzava.id == idDrzave) {
				naseljenaMesta.add(nm);
			}
		}

		return naseljenaMesta;
	}

}
