package controllers;

import java.util.ArrayList;
import java.util.List;

import models.NaseljenoMesto;
import models.PoslovnaGodina;

import play.cache.Cache;
import play.mvc.Controller;

public class PoslovneGodine extends Controller {

	public static void show(String mode) {
		validation.clear();
		session.put("idPoslovneGodine", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<PoslovnaGodina> poslovneGodine = checkCache();

		render(poslovneGodine, mode);
	}
	
	
	public static void edit(PoslovnaGodina poslovnaGodina) {
		validation.clear();
		validation.valid(poslovnaGodina);
		clearSession();
		session.put("mode", "edit");

		List<PoslovnaGodina> poslovneGodine = null;

		if (!validation.hasErrors()) {
			poslovneGodine = PoslovnaGodina.findAll();

			for (PoslovnaGodina tmp : poslovneGodine) {
				if (tmp.id == poslovnaGodina.id) {
					tmp.brojGodine = poslovnaGodina.brojGodine;
					tmp.aktivna = poslovnaGodina.aktivna;
					tmp.save();
					break;
				}
			}

			Cache.set("poslovneGodine", poslovneGodine);

			validation.clear();

		} else {
			poslovneGodine = checkCache();

			validation.keep();

			session.put("idPoslovneGodine", poslovnaGodina.id);
			session.put("brojGodine", poslovnaGodina.brojGodine);
			session.put("aktivna", poslovnaGodina.aktivna);
			
		}

		renderTemplate("poslovneGodine/show.html", poslovneGodine);
	}

	
	public static void create(PoslovnaGodina poslovnaGodina) {
		validation.clear();
		validation.valid(poslovnaGodina);

		session.put("mode", "add");

		List<PoslovnaGodina> poslovneGodine = checkCache();

		if (!validation.hasErrors()) {
			poslovnaGodina.save();
			poslovneGodine.add(poslovnaGodina);
			Cache.set("poslovneGodine", poslovneGodine);

			Long idd = poslovnaGodina.id;

			validation.clear();
			clearSession();

			renderTemplate("poslovneGodine/show.html", poslovneGodine, idd);
		} else {
			validation.keep();

			session.put("aktivna", poslovnaGodina.aktivna);
			session.put("brojGodine", poslovnaGodina.brojGodine);


			renderTemplate("poslovneGodine/show.html", poslovneGodine);
		}
	}
	
	public static void filter(PoslovnaGodina poslovnaGodina) {
		
	}
	public static void delete(Long id) {
		List<PoslovnaGodina> poslovneGodine = checkCache();

		PoslovnaGodina poslovnaGodina = PoslovnaGodina.findById(id);
		Long idd = null;

		for (int i = 1; i < poslovneGodine.size(); i++) {
			if (poslovneGodine.get(i).id == id) {
				PoslovnaGodina prethodni = poslovneGodine.get(i - 1);
				idd = prethodni.id;
			}
		}
		poslovnaGodina.delete();

		poslovneGodine = PoslovnaGodina.findAll();
		Cache.set("poslovneGodine", poslovneGodine);

		renderTemplate("poslovneGodine/show.html", poslovneGodine, idd);

	}

	public static void nextForm(Long id) {

	}

	public static void refresh() {
		List<PoslovnaGodina> poslovneGodine = checkCache();

		renderTemplate("poslovneGodine/show.html", poslovneGodine);
	}

	/**
	 * Pomocne metode.
	 */
	private static boolean clearSession() {
		session.put("idPoslovneGodine", null);
		session.put("brojGodine", null);
		session.put("aktivna", null);
	

		return true;
	}



	public static List<PoslovnaGodina> checkCache() {
		List<PoslovnaGodina> poslovneGodine = (List<PoslovnaGodina>) Cache.get("poslovneGodine");

		if (poslovneGodine == null) {
			poslovneGodine = PoslovnaGodina.findAll();
			Cache.set("poslovneGodine", poslovneGodine);
		}

		return poslovneGodine;
	}
}
