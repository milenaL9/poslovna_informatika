package controllers;

import java.util.ArrayList;
import java.util.List;

import models.NaseljenoMesto;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;

public class VrstePDVa extends Controller {

	public static void show(String mode) {
		validation.clear();
		session.put("idVrstePDVa", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<VrstaPDVa> vrstePDVa = checkCache();

		render(vrstePDVa, mode);
	}

	public static void edit(VrstaPDVa vrstaPDVa) {
		validation.clear();
		validation.valid(vrstaPDVa);
		clearSession();
		session.put("mode", "edit");

		List<VrstaPDVa> vrstePDVa = null;

		if (!validation.hasErrors()) {
			vrstePDVa = VrstaPDVa.findAll();

			for (VrstaPDVa tmp : vrstePDVa) {
				if (tmp.id == vrstaPDVa.id) {

					tmp.nazivVrstePDva = vrstaPDVa.nazivVrstePDva;
					tmp.save();
					break;
				}
			}

			Cache.set("vrstePDVa", vrstePDVa);

			validation.clear();

		} else {
			vrstePDVa = checkCache();

			validation.keep();

			session.put("idVrstePDVa", vrstaPDVa.id);
			// session.put("editOznaka", drzava.oznaka);
			session.put("editNazivVrstePDVa", vrstaPDVa.nazivVrstePDva);
		}

		renderTemplate("vrstePDVa/show.html", vrstePDVa);
	}

	public static void create(VrstaPDVa vrstaPDVa) {
		validation.clear();
		validation.valid(vrstaPDVa);

		session.put("mode", "add");

		List<VrstaPDVa> vrstePDVa = checkCache();

		if (!validation.hasErrors()) {
			vrstaPDVa.save();
			vrstePDVa.add(vrstaPDVa);
			Cache.set("vrstePDVa", vrstePDVa);

			Long idd = vrstaPDVa.id;

			validation.clear();
			clearSession();

			renderTemplate("vrstePDVa/show.html", vrstePDVa, idd);
		} else {
			validation.keep();

			session.put("editNazivVrstePDVa", vrstaPDVa.nazivVrstePDva);
			// session.put("editNaziv", vrstaPDVa.naziv);

			renderTemplate("vrstePDVa/show.html", vrstePDVa);
		}
	}

	 public static void filter(VrstaPDVa vrstaPDVa) {
	 List<VrstaPDVa> vrstePDVa = VrstaPDVa
	 .find("byNazivVrstePDVaLike", "%" + vrstaPDVa.nazivVrstePDva).fetch();
	 session.put("mode", "edit");
	 renderTemplate("vrstePDVa/show.html", vrstePDVa);
	 }

	public static void delete(Long id) {
		List<VrstaPDVa> vrstePDVa = checkCache();

		VrstaPDVa vrstaPDVa = VrstaPDVa.findById(id);
		Long idd = null;

		for (int i = 1; i < vrstePDVa.size(); i++) {
			if (vrstePDVa.get(i).id == id) {
				VrstaPDVa prethodni = vrstePDVa.get(i - 1);
				idd = prethodni.id;
			}
		}
		vrstaPDVa.delete();

		vrstePDVa = VrstaPDVa.findAll();
		Cache.set("vrstePDVa", vrstePDVa);

		renderTemplate("vrstePDVa/show.html", vrstePDVa, idd);

	}

//	public static void nextForm(Long id) {
//		List<VrstaPDVa> vrstePDVa = checkCache();
//
//		List<NaseljenoMesto> naseljenaMesta = findNaseljenaMesta(id);
//		session.put("idDrzave", id);
//		session.put("id", null);
//		session.put("oznaka", null);
//		session.put("naziv", null);
//		session.put("postanskiBroj", null);
//
//		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
//	}

	public static void refresh() {
		List<VrstaPDVa> vrstePDVa = checkCache();

		renderTemplate("vrstePDVa/show.html", vrstePDVa);
	}

	/**
	 * Pomocne metode.
	 */
	public static List<VrstaPDVa> checkCache() {
		List<VrstaPDVa> vrstePDVa = (List<VrstaPDVa>) Cache.get("vrstePDVa");

		if (vrstePDVa == null) {
			vrstePDVa = VrstaPDVa.findAll();
			Cache.set("vrstePDVa", vrstePDVa);
		}

		return vrstePDVa;
	}

	private static boolean clearSession() {
		session.put("idVrstePDVa", null);
		session.put("editOznaka", null);
		session.put("editNaziv", null);

		return true;
	}

	// public static List<NaseljenoMesto> findNaseljenaMesta(Long idDrzave) {
	// List<NaseljenoMesto> naseeljenaMestaAll = NaseljenoMesto.findAll();
	// List<NaseljenoMesto> naseljenaMesta = new ArrayList<>();
	//
	// for (NaseljenoMesto nm : naseeljenaMestaAll) {
	// if (nm.drzava.id == idDrzave) {
	// naseljenaMesta.add(nm);
	// }
	// }
	//
	// return naseljenaMesta;
	// }

}
