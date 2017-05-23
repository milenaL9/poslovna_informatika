package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.sun.org.apache.xml.internal.utils.NSInfo;

import jj.play.ns.com.jhlabs.image.CellularFilter;
import models.Drzava;
import models.NaseljenoMesto;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class NaseljenaMesta extends Controller {

	public static void changeMode(String mode) {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Drzava> drzave = Drzave.checkCache();
		List<NaseljenoMesto> naseljenaMesta = null;
		if (!session.get("idDrzave").equals("null")) {
			Long id = Long.parseLong(session.get("idDrzave"));
			naseljenaMesta = Drzave.findNaseljenaMesta(id);
		} else {
			naseljenaMesta = checkCache();
		}

		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
	}

	/**
	 * Stranica se vraca u pocetno stanje. Brisu se sesije, i iscitavaju se svi
	 * podaci.
	 */
	public static void show() {
		validation.clear();
		session.put("idDrzave", "null");
		clearSession();

		session.put("mode", "edit");

		List<Drzava> drzave = Drzave.checkCache();
		List<NaseljenoMesto> naseljenaMesta = checkCache();

		render(naseljenaMesta, drzave);
	}

	public static void edit(NaseljenoMesto naseljenoMesto, Drzava drzava) {
		validation.clear();
		validation.valid(naseljenoMesto);
		clearSession();
		session.put("mode", "edit");

		List<NaseljenoMesto> naseljenaMesta = null;
		List<Drzava> drzave = Drzave.checkCache();

		if (!validation.hasErrors()) {
			naseljenaMesta = NaseljenoMesto.findAll();

			if (drzava.id == null) {
				Long id = Long.parseLong(session.get("idDrzave"));
				Drzava drzavaEdit = Drzava.findById(id);
				drzava = drzavaEdit;
			}

			for (NaseljenoMesto tmp : naseljenaMesta) {
				if (tmp.id == naseljenoMesto.id) {
					tmp.oznaka = naseljenoMesto.oznaka;
					tmp.naziv = naseljenoMesto.naziv;
					tmp.postanskiBroj = naseljenoMesto.postanskiBroj;
					tmp.drzava = drzava;
					tmp.save();
					break;
				}
			}

			Cache.set("naseljenaMesta", naseljenaMesta);

			if (!session.get("idDrzave").equals("null")) {
				naseljenaMesta.clear();
				Long id = Long.parseLong(session.get("idDrzave"));
				naseljenaMesta = Drzave.findNaseljenaMesta(id);
			}

			validation.clear();

		} else {
			if (session.get("idDrzave").equals("null")) {
				naseljenaMesta = checkCache();
			} else {
				naseljenaMesta = Drzave.findNaseljenaMesta(naseljenoMesto.drzava.id);
			}

			validation.keep();

			session.put("idNM", naseljenoMesto.id);
			session.put("oznaka", naseljenoMesto.oznaka);
			session.put("naziv", naseljenoMesto.naziv);
			session.put("postanskiBroj", naseljenoMesto.postanskiBroj);
		}

		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
	}

	public static void create(NaseljenoMesto naseljenoMesto, Drzava drzava) {
		validation.clear();
		validation.valid(naseljenoMesto);
		clearSession();
		session.put("mode", "add");
		System.out.println("drzava:"+drzava.naziv);

		List<NaseljenoMesto> naseljenaMesta = null;
		List<Drzava> drzave = Drzave.checkCache();

		if (!validation.hasErrors()) {
			naseljenaMesta = NaseljenoMesto.findAll();

			if (drzava.id == null) {
				Long id = Long.parseLong(session.get("idDrzave"));
				Drzava newDrzava = Drzava.findById(id);
				drzava = newDrzava;
			}
			naseljenoMesto.drzava = drzava;
			naseljenoMesto.save();
			naseljenaMesta.add(naseljenoMesto);
			Cache.set("naseljenaMesta", naseljenaMesta);

			Long idd = naseljenoMesto.id;

			if (!session.get("idDrzave").equals("null")) {
				naseljenaMesta.clear();
				Long id = Long.parseLong(session.get("idDrzave"));
				naseljenaMesta = Drzave.findNaseljenaMesta(id);
			}

			validation.clear();

			renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave, idd);
		} else {
			validation.keep();

			if (session.get("idDrzave").equals("null")) {
				naseljenaMesta = checkCache();
			} else {
				Long id = Long.parseLong(session.get("idDrzave"));
				naseljenaMesta = Drzave.findNaseljenaMesta(id);
			}

			session.put("oznaka", naseljenoMesto.oznaka);
			session.put("naziv", naseljenoMesto.naziv);
			session.put("postanskiBroj", naseljenoMesto.postanskiBroj);

			renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
		}

	}

	public static void filter(NaseljenoMesto naseljenoMesto) {
		List<NaseljenoMesto> naseljenaMesta = NaseljenoMesto
				.find("byOznakaLikeAndNazivLikeAndPostanskiBrojLike", "%" + naseljenoMesto.oznaka + "%",
						"%" + naseljenoMesto.naziv + "%", "%" + naseljenoMesto.postanskiBroj + "%")
				.fetch();

		List<Drzava> drzave = Drzave.checkCache();

		session.put("mode", "edit");
		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
	}

	public static void delete(Long id) {
		List<Drzava> drzave = Drzave.checkCache();
		List<NaseljenoMesto> naseljenaMesta = checkCache();

		NaseljenoMesto naseljenoMesto = NaseljenoMesto.findById(id);
		Long idd = null;

		for (int i = 1; i < naseljenaMesta.size(); i++) {
			if (naseljenaMesta.get(i).id == id) {
				NaseljenoMesto prethodni = naseljenaMesta.get(i - 1);
				idd = prethodni.id;
			}
		}
		naseljenoMesto.delete();

		naseljenaMesta = NaseljenoMesto.findAll();
		Cache.set("naseljenaMesta", naseljenaMesta);

		if (!session.get("idDrzave").equals("null")) {
			Long idDrzave = Long.parseLong(session.get("idDrzave"));
			naseljenaMesta = Drzave.findNaseljenaMesta(idDrzave);
		}

		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave, idd);
	}

	public static void refresh() {
		List<NaseljenoMesto> naseljenaMesta = null;
		List<Drzava> drzave = Drzave.checkCache();

		if (session.get("idDrzave").equals("null")) {
			naseljenaMesta = checkCache();
		} else {
			Long idDrzave = Long.parseLong(session.get("idDrzave"));
			naseljenaMesta = Drzave.findNaseljenaMesta(idDrzave);
		}

		renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
	}

	/**
	 * Pomocne metode
	 */
	public static List<NaseljenoMesto> checkCache() {
		List<NaseljenoMesto> naseljenaMesta = (List<NaseljenoMesto>) Cache.get("naseljenaMesta");

		if (naseljenaMesta == null || naseljenaMesta.size() == 0) {
			naseljenaMesta = NaseljenoMesto.findAll();
			Cache.set("naseljenaMesta", naseljenaMesta);
		}

		return naseljenaMesta;
	}

	public static boolean clearSession() {
		session.put("idNM", null);
		session.put("oznaka", null);
		session.put("naziv", null);
		session.put("postanskiBroj", null);

		return true;
	}

}
