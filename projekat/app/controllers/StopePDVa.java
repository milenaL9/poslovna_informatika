package controllers;

import java.util.List;

import models.Cenovnik;
import models.Drzava;
import models.PoslovnaGodina;
import models.StopaPDVa;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;

/**
 * Obrati paznju na VrstuPDVa unutar StopePDVa
 * 
 * 
 */

public class StopePDVa extends Controller {

	public static void show() {
		validation.clear();
		clearSession();
		session.put("idVrstePDVa", "null");

		String mode = "edit";

		session.put("mode", mode);

		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<StopaPDVa> stopePDVa = checkCache();

		render(vrstePDVa, stopePDVa, mode);
	}

	public static void  changeMode(String mode) {
		
		if(mode == null || mode.equals("")) {
			mode="edit";
		}
		session.put("mode", mode);
		
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<StopaPDVa> stopePDVa = fillList();

		renderTemplate("StopePDVa/show.html", vrstePDVa, stopePDVa, mode);
	}


	public static void edit(StopaPDVa stopaPDVa, Long vrstaPDVa) {
		validation.clear();
		validation.valid(stopaPDVa);
		clearSession();
		session.put("mode", "edit");
		String mode = session.get("mode");

		List<StopaPDVa> stopePDVa = null;
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();

		if (!validation.hasErrors()) {
			stopePDVa = StopaPDVa.findAll();

			VrstaPDVa findVrstaPDVa = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idVrstePDVa"));
				findVrstaPDVa = VrstaPDVa.findById(id);
			} else {
				findVrstaPDVa = VrstaPDVa.findById(vrstaPDVa);
			}

			for (StopaPDVa tmp : stopePDVa) {
				if (tmp.id == stopaPDVa.id) {
					tmp.vrstaPDVa = findVrstaPDVa;
					tmp.procenatPDVa = stopaPDVa.procenatPDVa;
					tmp.datumKreiranja = stopaPDVa.datumKreiranja;
					tmp.save();
					break;
				}
			}

			Cache.set("stopePDVa", stopePDVa);

			stopePDVa.clear();
			stopePDVa = fillList();
			validation.clear();

		} else {
			validation.keep();
			stopePDVa = fillList();

			session.put("idStopePDVa", stopaPDVa.id);
			session.put("datumKreiranja", stopaPDVa.datumKreiranja);
			session.put("procenatPDVa", stopaPDVa.procenatPDVa);

		}

		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, mode);
	}

	public static void create(StopaPDVa stopaPDVa, Long vrstaPDVa) {
		validation.clear();
		clearSession();

		validation.valid(stopaPDVa);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StopaPDVa> stopePDVa = null;
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();

		if (!validation.hasErrors()) {

			stopePDVa = StopaPDVa.findAll();

			VrstaPDVa findVrstaPDVa = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idVrstePDVa"));
				findVrstaPDVa = VrstaPDVa.findById(id);
			} else {
				findVrstaPDVa = VrstaPDVa.findById(vrstaPDVa);
			}

			stopaPDVa.vrstaPDVa = findVrstaPDVa;

			stopaPDVa.save();
			stopePDVa.add(stopaPDVa);
			Cache.set("stopePDVa", stopePDVa);

			Long idd = stopaPDVa.id;

			validation.clear();
			stopePDVa = fillList();
			validation.clear();

			renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, idd, mode);
		} else

		{
			validation.keep();

			stopePDVa = fillList();
			// session.put("editNazivVrstePDVa", stopaPDVa.nazivVrstePDva);
			// session.put("editNaziv", vrstaPDVa.naziv);
			session.put("datumKreiranja", stopaPDVa.datumKreiranja);
			session.put("procenatPDVa", stopaPDVa.procenatPDVa);
			// session.put("vrstaPDVa", stopaPDVa.vrstaPDVa);
			renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa,  mode);
		}
	}


	public static void filter(StopaPDVa stopaPDVa) {
		
		//Integer broj = Integer.parseInt(stopaPDVa.procenatPDVa);
		List<StopaPDVa> stopePDVa = StopaPDVa
				//.find("byProcenatPDVaLike",  "%" + stopaPDVa.procenatPDVa + "%")
				//.fetch();
				.find("procenatPDVa = ?", stopaPDVa.procenatPDVa).fetch();

	
	
		//Long.parseLong(session.get("idVrstePDVa"))
		session.put("mode", "edit");
		String mode = session.get("mode");

		//List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("StopePDVa/show.html", stopePDVa, mode);
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

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

	
		Cache.set("stopePDVa", stopePDVa);

		stopePDVa.clear();
		stopePDVa = fillList();
		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, idd, mode);

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
		String mode = session.get("mode");
		renderTemplate("StopePDVa/show.html", stopePDVa, vrstePDVa, mode);

	}

	/**
	 * Pomocne metode.
	 */
	public static List<StopaPDVa> checkCache() {
		List<StopaPDVa> stopePDVa = (List<StopaPDVa>) Cache.get("stopePDVa");

		if ((stopePDVa == null) || (stopePDVa.size() == 0) ) {
			stopePDVa = StopaPDVa.findAll();
			Cache.set("stopePDVa", stopePDVa);
		}

		return stopePDVa;
	}

	private static boolean clearSession() {
		session.put("idStopePDVa", null);
		session.put("datumKreiranja", null);
		session.put("procenatPDVa", null);
		
		return true;
	}
	
	
	public static List<StopaPDVa> fillList() {
		List<StopaPDVa> stopePDVa = null;
		if (!session.get("idVrstePDVa").equals("null")) {
			Long id = Long.parseLong(session.get("idVrstePDVa"));
			stopePDVa = VrstePDVa.findStopePDVa(id);
		} else {
			stopePDVa = checkCache();
		}

		return stopePDVa;
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
