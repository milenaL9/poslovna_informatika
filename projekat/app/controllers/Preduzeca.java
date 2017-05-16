package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Drzava;
import models.NaseljenoMesto;
import models.Preduzece;
import play.cache.Cache;
import play.mvc.Controller;

public class Preduzeca extends Controller {

	public static void show(String mode) {
		validation.clear();
		session.put("idPreduzeca", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Preduzece> preduzeca = checkCache();

		render(preduzeca, mode);
	}

	public static void edit(Preduzece preduzece) {
		validation.clear();
		validation.valid(preduzece);
		clearSession();
		session.put("mode", "edit");

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

		renderTemplate("preduzeca/show.html", preduzeca);
	}

	public static void create(Preduzece preduzece) {
		validation.clear();
		validation.valid(preduzece);

		session.put("mode", "add");

		List<Preduzece> preduzeca = checkCache();

		if (!validation.hasErrors()) {
			preduzece.save();
			preduzeca.add(preduzece);
			Cache.set("preduzeca", preduzeca);

			Long idd = preduzece.id;

			validation.clear();
			clearSession();

			renderTemplate("preduzeca/show.html", preduzeca, idd);
		} else {
			validation.keep();

			session.put("nazivPred", preduzece.naziv);
			session.put("pibPred", preduzece.pib);
			session.put("mestoPred", preduzece.mesto);
			session.put("adresaPred", preduzece.adresa);
			session.put("telefonPred", preduzece.telefon);
			session.put("maticniBrojPred", preduzece.maticniBroj);
			session.put("tekuciRacunPred", preduzece.tekuciRacun);

			renderTemplate("preduzeca/show.html", preduzeca);
		}
	}

	public static void filter(Preduzece preduzece) {
//		List<Preduzece> preduzeca = Preduzece
//				.find("byNazivLikeAndPibLikeAndMestoLikeAndAdresaLikeAndTelefonLikeAndMaticniBrojLikeAndTekuciRacunLike",
//						"%" + preduzece.naziv + "%", "%" + preduzece.pib + "%", "%" + preduzece.mesto + "%",
//						"%" + preduzece.adresa + "%", "%" + preduzece.telefon + "%", "%" + preduzece.maticniBroj + "%",
//						"%" + preduzece.tekuciRacun + "%")
//				.fetch();
//		session.put("mode", "edit");
//		renderTemplate("preduzeca/show.html", preduzeca);
	}

	public static void delete(Long id) {
		List<Preduzece> preduzeca = checkCache();

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

		renderTemplate("preduzeca/show.html", preduzeca, idd);

	}

	public static void nextForm(Long id) {

	}

	public static void refresh() {
		List<Preduzece> preduzeca = checkCache();

		renderTemplate("preduzeca/show.html", preduzeca);
	}

	/**
	 * Pomocne metode.
	 */
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

	public static List<Preduzece> checkCache() {
		List<Preduzece> preduzeca = (List<Preduzece>) Cache.get("preduzeca");

		if (preduzeca == null) {
			preduzeca = Preduzece.findAll();
			Cache.set("preduzeca", preduzeca);
		}

		return preduzeca;
	}
}
