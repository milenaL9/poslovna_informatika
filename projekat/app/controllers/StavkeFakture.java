package controllers;

import java.util.ArrayList;
import java.util.List;

import models.StavkaFakture;
import play.cache.Cache;
import play.mvc.Controller;

public class StavkeFakture extends Controller {
	
	public static void show(String mode) {
		validation.clear();
		session.put("idStavkeFakture", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<StavkaFakture> stavkeFakture = checkCache();

		render(stavkeFakture, mode);
	}

	public static void edit(StavkaFakture stavkaFakture) {
		validation.clear();
		validation.valid(stavkaFakture);
		clearSession();
		session.put("mode", "edit");

		List<StavkaFakture> stavkeFakture = null;

		if (!validation.hasErrors()) {
			stavkeFakture = StavkaFakture.findAll();

			for (StavkaFakture tmp : stavkeFakture) {
				if (tmp.id == stavkaFakture.id) {
					
					tmp.cena = stavkaFakture.cena;
					tmp.iznosPDVa = stavkaFakture.iznosPDVa;
					tmp.kolicina= stavkaFakture.kolicina;
					tmp.rabat = stavkaFakture.rabat;
					tmp.osnovicaZaPDV = stavkaFakture.osnovicaZaPDV;
					tmp.ukupno = stavkaFakture.ukupno;
					
					tmp.save();
					break;
				}
			}

			Cache.set("stavkeFakture", stavkeFakture);

			validation.clear();

		} else {
			stavkeFakture = checkCache();

			validation.keep();

			session.put("idStF", stavkaFakture.id);
			//session.put("editOznaka", drzava.oznaka);
			//session.put("editNaziv", drzava.naziv);
		}

		renderTemplate("stavkeFakture/show.html", stavkaFakture);
	}

	public static void create(StavkaFakture stavkaFakture) {
		validation.clear();
		validation.valid(stavkaFakture);

		session.put("mode", "add");

		List<StavkaFakture> stavkeFakture = checkCache();

		if (!validation.hasErrors()) {
			stavkaFakture.save();
			stavkeFakture.add(stavkaFakture);
			Cache.set("stavkeFakture", stavkeFakture);

			Long idd = stavkaFakture.id;

			validation.clear();
			clearSession();

			renderTemplate("stavkeFakture/show.html", stavkeFakture, idd);
		} else {
			validation.keep();

			//session.put("editOznaka", drzava.oznaka);
			//session.put("editNaziv", drzava.naziv);

			renderTemplate("stavkeFakture/show.html", stavkeFakture);
		}
	}

	public static void filter(StavkaFakture stavkaFakture) {
//		List<Drzava> drzave = Drzava
//				.find("byNazivLikeAndOznakaLike", "%" + drzava.naziv + "%", "%" + drzava.oznaka + "%").fetch();
//		session.put("mode", "edit");
//		renderTemplate("drzave/show.html", drzave);
	}

	public static void delete(Long id) {
		List<StavkaFakture> stavkeFakture = checkCache();

		StavkaFakture stavkaFakture = StavkaFakture.findById(id);
		Long idd = null;

		for (int i = 1; i < stavkeFakture.size(); i++) {
			if (stavkeFakture.get(i).id == id) {
				StavkaFakture prethodni = stavkeFakture.get(i - 1);
				idd = prethodni.id;
			}
		}
		stavkaFakture.delete();

		stavkeFakture = StavkaFakture.findAll();
		Cache.set("stavkeFakture", stavkeFakture);

		renderTemplate("stavkeFakture/show.html", stavkeFakture, idd);

	}

	public static void nextForm(Long id) {
//		List<Drzava> drzave = checkCache();
//
//		List<NaseljenoMesto> naseljenaMesta = findNaseljenaMesta(id);
//		session.put("idDrzave", id);
//		session.put("id", null);
//		session.put("oznaka", null);
//		session.put("naziv", null);
//		session.put("postanskiBroj", null);
//
//		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
	}

	public static void refresh() {
		List<StavkaFakture> stavkeFakture = checkCache();

		renderTemplate("stavkeFakture/show.html", stavkeFakture);
	}

	/**
	 * Pomocne metode.
	 */
	public static List<StavkaFakture> checkCache() {
		List<StavkaFakture> stavkeFakture = (List<StavkaFakture>) Cache.get("stavkeFakture");

		if (stavkeFakture == null) {
			stavkeFakture = StavkaFakture.findAll();
			Cache.set("stavkeFakture", stavkeFakture);
		}

		return stavkeFakture;
	}

	private static boolean clearSession() {
		session.put("idStF", null);
		//session.put("editOznaka", null);
		//session.put("editNaziv", null);

		return true;
	}
//
//	public static List<NaseljenoMesto> findNaseljenaMesta(Long idDrzave) {
//		List<NaseljenoMesto> naseeljenaMestaAll = NaseljenoMesto.findAll();
//		List<NaseljenoMesto> naseljenaMesta = new ArrayList<>();
//
//		for (NaseljenoMesto nm : naseeljenaMestaAll) {
//			if (nm.drzava.id == idDrzave) {
//				naseljenaMesta.add(nm);
//			}
//		}
//
//		return naseljenaMesta;
//	}

}
