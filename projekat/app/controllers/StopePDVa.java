package controllers;

import java.util.List;

import models.Drzava;
import models.StopaPDVa;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;

/**
 * Obrati paznju na VrstuPDVa unutar StopePDVa
 * 
 * 
 * */





public class StopePDVa extends Controller {

	public static void show(String mode) {
		validation.clear();
		session.put("idStopePDVa", "null");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<StopaPDVa> stopePDVa = checkCache();

		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa);
	}

	public static void edit(StopaPDVa stopaPDVa) {
		validation.clear();
		validation.valid(stopaPDVa);
		clearSession();
		session.put("mode", "edit");

		List<StopaPDVa> stopePDVa = null;

		if (!validation.hasErrors()) {
			stopePDVa = StopaPDVa.findAll();

			for (StopaPDVa tmp : stopePDVa) {
				if (tmp.id == stopaPDVa.id) {
					tmp.vrstaPDVa = stopaPDVa.vrstaPDVa;
					tmp.procenatPDVa = stopaPDVa.procenatPDVa;
					tmp.datumKreiranja = stopaPDVa.datumKreiranja;
					tmp.save();
					break;
				}
			}

			Cache.set("stopePDVa", stopePDVa);

			validation.clear();

		} else {
			stopePDVa = checkCache();

			validation.keep();

			session.put("idStopePDVa", stopaPDVa.id);
			session.put("datumKreiranja", stopaPDVa.datumKreiranja);
			session.put("procenatPDVa", stopaPDVa.procenatPDVa);
			// session.put("editOznaka", drzava.oznaka);
			// session.put("editNazivStopePDVa", stopaPDVa.nazivVrstePDva);
		}

		renderTemplate("stopePDVa/show.html", stopePDVa);
	}

	public static void create(StopaPDVa stopaPDVa, VrstaPDVa vrstaPDVa) {
		validation.clear();
		validation.valid(stopaPDVa);

		session.put("mode", "add");

		List<StopaPDVa> stopePDVa = null;
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		
		if (!validation.hasErrors()) {
			
			stopePDVa = StopaPDVa.findAll();
			
			if(vrstaPDVa.id == null) {
				Long id = Long.parseLong(session.get("id"));
				VrstaPDVa newvrstaPDVa = VrstaPDVa.findById(id);
				vrstaPDVa = newvrstaPDVa;
			}
			
			stopaPDVa.vrstaPDVa = vrstaPDVa;
			
			
			stopaPDVa.save();
			stopePDVa.add(stopaPDVa);
			Cache.set("stopePDVa", stopePDVa);

			Long idd = stopaPDVa.id;

			
			
			validation.clear();
			clearSession();

			renderTemplate("stopePDVa/show.html", stopePDVa, idd);
		} else {
			validation.keep();

			// session.put("editNazivVrstePDVa", stopaPDVa.nazivVrstePDva);
			// session.put("editNaziv", vrstaPDVa.naziv);
			session.put("datumKreiranja", stopaPDVa.datumKreiranja);
			session.put("procenatPDVa", stopaPDVa.procenatPDVa);
			session.put("vrstaPDVa", stopaPDVa.vrstaPDVa);
			renderTemplate("stopePDVa/show.html", stopePDVa);
		}
	}

//	public static void filter(StopaPDVa stopaPDVa) {
////			List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
//////					StopaPDVa.find("byProcenatPDVaLike",
//////					"%" + stopaPDVa.procenatPDVa + "%"
//////					, "%" + stopaPDVa.vrstaPDVa.nazivVrstePDva + "%"
//////					).fetch();
////		session.put("mode", "edit");
////		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
////		renderTemplate("stopePDVa/show.html", stopePDVa,vrstePDVa);
//	}

	public static void delete(Long id) {
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<StopaPDVa> stopePDVa = checkCache();

		StopaPDVa stopaPDVa = StopaPDVa.findById(id);
		Long idd = null;

		for (int i = 1; i < stopePDVa.size(); i++) {
			if (stopePDVa.get(i).id == id) {
				StopaPDVa prethodni = stopePDVa.get(i - 1);
				idd = prethodni.id;
			}
		}
		stopaPDVa.delete();

		stopePDVa = StopaPDVa.findAll();
		Cache.set("stopePDVa", stopePDVa);

		renderTemplate("stopePDVa/show.html", stopePDVa, vrstePDVa, idd);

	}

	// public static void nextForm(Long id) {
	// List<VrstaPDVa> vrstePDVa = checkCache();
	//
	// List<NaseljenoMesto> naseljenaMesta = findNaseljenaMesta(id);
	// session.put("idDrzave", id);
	// session.put("id", null);
	// session.put("oznaka", null);
	// session.put("naziv", null);
	// session.put("postanskiBroj", null);
	//
	// renderTemplate("NaseljenaMesta/show.html", naseljenaMesta, drzave);
	// }

	public static void refresh() {
		List<StopaPDVa> stopePDVa = checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		renderTemplate("stopePDVa/show.html", stopePDVa, vrstePDVa);
	
		
	}

	/**
	 * Pomocne metode.
	 */
	public static List<StopaPDVa> checkCache() {
		List<StopaPDVa> stopePDVa = (List<StopaPDVa>) Cache.get("stopePDVa");

		if (stopePDVa == null) {
			stopePDVa = StopaPDVa.findAll();
			Cache.set("stopePDVa", stopePDVa);
		}

		return stopePDVa;
	}

	private static boolean clearSession() {
		session.put("idStopePDVa", null);
		// session.put("editOznaka", null);
		// session.put("editNaziv", null);

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
