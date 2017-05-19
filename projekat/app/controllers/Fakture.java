package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Drzava;
import models.Faktura;
import models.NaseljenoMesto;
import play.cache.Cache;
import play.mvc.Controller;

public class Fakture extends Controller {
	
	public static void show(String mode) {
		validation.clear();
		session.put("idFakture", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Faktura> fakture = checkCache();

		render(fakture, mode);
	}
	
	public static void edit(Faktura faktura) {
		validation.clear();
		validation.valid(faktura);
		clearSession();
		session.put("mode", "edit");

		List<Faktura> fakture = null;

		if (!validation.hasErrors()) {
			fakture = Faktura.findAll();

			for (Faktura tmp : fakture) {
				if (tmp.id == faktura.id) {
					tmp.brojFakture= faktura.brojFakture;
					tmp.datumFakture = faktura.datumFakture;
					tmp.datumValute = faktura.datumValute;
					tmp.ukupnoOsnovica = faktura.ukupnoOsnovica;
					tmp.ukupnoPDV = faktura.ukupnoPDV;
					tmp.ukupnoZaPlacanje = faktura.ukupnoZaPlacanje;
					
					tmp.save();
					break;
				}
			}

			Cache.set("fakture", fakture);

			validation.clear();

		} else {
			fakture = checkCache();

			validation.keep();

			session.put("idFak", faktura.id);
			//session.put("editOznaka", drzava.oznaka);
			//session.put("editNaziv", drzava.naziv);
		}

		renderTemplate("fakture/show.html", fakture);
	}

	public static void create(Faktura faktura) {
		validation.clear();
		validation.valid(faktura);

		session.put("mode", "add");

		List<Faktura> fakture = checkCache();

		if (!validation.hasErrors()) {
			faktura.save();
			fakture.add(faktura);
			Cache.set("fakture", fakture);

			Long idd = faktura.id;

			validation.clear();
			clearSession();

			renderTemplate("fakture/show.html", fakture, idd);
		} else {
			validation.keep();

			//session.put("editOznaka", drzava.oznaka);
			//session.put("editNaziv", drzava.naziv);

			renderTemplate("fakture/show.html", fakture);
		}
	}
	
	public static void filter(Faktura faktura) {
//		List<Faktura> fakture = Faktura
//				.find("byNazivLikeAndOznakaLike", "%" + drzava.naziv + "%", "%" + drzava.oznaka + "%").fetch();
//		session.put("mode", "edit");
//		renderTemplate("drzave/show.html", drzave);
	}

	
	
	public static void delete(Long id) {
		List<Faktura> fakture = checkCache();

		Faktura faktura = Faktura.findById(id);
		Long idd = null;

		for (int i = 1; i < fakture.size(); i++) {
			if (fakture.get(i).id == id) {
				Faktura prethodni = fakture.get(i - 1);
				idd = prethodni.id;
			}
		}
		faktura.delete();

		fakture = Faktura.findAll();
		Cache.set("fakture", fakture);

		renderTemplate("fakture/show.html", fakture, idd);

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
		List<Faktura> fakture = checkCache();

		renderTemplate("fakture/show.html", fakture);
	}

	/**
	 * Pomocne metode.
	 */
	public static List<Faktura> checkCache() {
		List<Faktura> fakture = (List<Faktura>) Cache.get("fakture");

		if (fakture == null) {
			fakture = Faktura.findAll();
			Cache.set("fakture", fakture);
		}

		return fakture;
	}

	private static boolean clearSession() {
		session.put("idFak", null);
		//session.put("editOznaka", null);
		//session.put("editNaziv", null);

		return true;
	}

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

	

