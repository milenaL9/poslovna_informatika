package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import org.apache.ivy.plugins.resolver.util.FileURLLister;


import models.Faktura;
import models.KatalogRobeIUsluga;
import models.PoslovnaGodina;
import models.PoslovniPartner;
import models.Preduzece;

import models.StopaPDVa;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;

/*
 * 
 * Kad bude odradjen model Narudzbe, odradi sve isto za narudzbu kao za fakturu.
 * 
 * 
 * 
 * */

public class PoslovniPartneri extends Controller {

	public static void show() {
		validation.clear();

		// za next mehanizam
		session.put("idPoslovnogPartnera", "null");

		session.put("idPreduzeca", "null"); // ManyToOne
		session.put("mode", "edit");
		String mode = session.get("mode");
		
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

		session.put("mode", mode);

		List<PoslovniPartner> poslovniPartneri = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		
		render(poslovniPartneri, povezaneForme, mode, preduzeca);
	}

	public static void changeMode(String mode) {

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = fillList();
		List<String> povezaneForme = getForeignKeysFields();
		
		renderTemplate("PoslovniPartneri/show.html", preduzeca, povezaneForme, poslovniPartneri, mode);
	}

	public static List<String> getForeignKeysFields() {
		Class poslovniPartnerClass = PoslovniPartner.class;
		Field[] fields = poslovniPartnerClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	public static List<PoslovniPartner> checkCache() {
		List<PoslovniPartner> poslovniPartneri = (List<PoslovniPartner>) Cache.get("poslovniPartneri");

		if ((poslovniPartneri == null) || (poslovniPartneri.size() == 0) ) {
			poslovniPartneri = PoslovniPartner.findAll();
			Cache.set("poslovniPartneri", poslovniPartneri);
		}

		return poslovniPartneri;
	}

	public static void edit(PoslovniPartner poslovniPartner, Long preduzece) {

		validation.clear();
		validation.valid(poslovniPartner);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Preduzece> preduzeca = Preduzeca.checkCache();

		List<PoslovniPartner> poslovniPartneri = null;
		if (!validation.hasErrors()) {
			poslovniPartneri = PoslovniPartner.findAll();

			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			for (PoslovniPartner tmp : poslovniPartneri) {
				if (tmp.id == poslovniPartner.id) {
					tmp.naziv = poslovniPartner.naziv;
					tmp.adresa = poslovniPartner.adresa;
					tmp.mesto = poslovniPartner.mesto;
					tmp.pib = poslovniPartner.pib;
					tmp.preduzece = findPreduzece;
					tmp.tekuciRacun = poslovniPartner.tekuciRacun;
					tmp.telefon = poslovniPartner.telefon;
					tmp.vrsta = poslovniPartner.vrsta;

					tmp.save();
					break;
				}
			}

			Cache.set("poslovniPartneri", poslovniPartneri);

			poslovniPartneri.clear();
			poslovniPartneri = fillList();
			validation.clear();

		} else {
			poslovniPartneri = checkCache();

			validation.keep();

			session.put("idPP", poslovniPartner.id);
			session.put("naziv", poslovniPartner.naziv);
			session.put("adresa", poslovniPartner.adresa);
			session.put("mesto", poslovniPartner.mesto);
			session.put("pib", poslovniPartner.pib);
			session.put("tekuciRacun", poslovniPartner.tekuciRacun);
			session.put("telefon", poslovniPartner.telefon);
			session.put("vrsta", poslovniPartner.vrsta);

		}

		renderTemplate("poslovniPartneri/show.html", poslovniPartneri, povezaneForme, preduzeca, mode);
	}

	public static void create(PoslovniPartner poslovniPartner, Long preduzece) {
		validation.clear();
		validation.valid(poslovniPartner);
		clearSession();

		session.put("mode", "add");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<PoslovniPartner> poslovniPartneri = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		if (!validation.hasErrors()) {

			poslovniPartneri = PoslovniPartner.findAll();

			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			poslovniPartner.preduzece = findPreduzece;

			poslovniPartner.save();
			poslovniPartneri.add(poslovniPartner);
			Cache.set("poslovniPartneri", poslovniPartneri);

			// potrebno da bi se selektovao dodati red na view delu
			Long idd = poslovniPartner.id;

			poslovniPartneri.clear();
			poslovniPartneri = fillList();
			validation.clear();
			clearSession();

			renderTemplate("poslovniPartneri/show.html", poslovniPartneri, povezaneForme, preduzeca, idd, mode);
		} else {
			validation.keep();

			poslovniPartneri = fillList();
			// potrebno da bi se ispisla greska
			session.put("idPP", poslovniPartner.id);
			session.put("naziv", poslovniPartner.naziv);
			session.put("adresa", poslovniPartner.adresa);
			session.put("mesto", poslovniPartner.mesto);
			session.put("pib", poslovniPartner.pib);
			session.put("tekuciRacun", poslovniPartner.tekuciRacun);
			session.put("telefon", poslovniPartner.telefon);
			session.put("vrsta", poslovniPartner.vrsta);

			renderTemplate("poslovniPartneri/show.html", poslovniPartneri, povezaneForme, preduzeca, mode);
		}
	}

	public static void filter(PoslovniPartner poslovniPartner) {
		List<PoslovniPartner> poslovniPartneri = PoslovniPartner
				.find("byNazivLikeAndVrstaLike", "%" + poslovniPartner.naziv + "%", "%" + poslovniPartner.vrsta + "%")
				.fetch();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("poslovniPartneri/show.html", poslovniPartneri, preduzeca, povezaneForme, mode);
	}

	public static boolean clearSession() {
		session.put("idPP", null);
		session.put("naziv", null);
		session.put("mesto", null);
		session.put("adresa", null);
		session.put("vrsta", null);
		session.put("telefon", null);
		session.put("pib", null);
		session.put("tekuciRacun", null);

		return true;

	}

	public static List<PoslovniPartner> fillList() {
		List<PoslovniPartner> poslovniPartneri = null;
		if (!session.get("idPreduzeca").equals("null")) {
			Long id = Long.parseLong(session.get("idPreduzeca"));
			poslovniPartneri = Preduzeca.findPoslovniPartneri(id);
		} else {
			poslovniPartneri = checkCache();
		}

		return poslovniPartneri;
	}

	public static void nextForm(Long id, String forma) {
		session.put("idPoslovnogPartnera", id);
		session.put("idPreduzeca", "null");
		session.put("idPoslovneGodine", "null");

		clearSession();

		if (forma.equals("fakture")) {
			List<PoslovniPartner> poslovniPartneri = checkCache();
			List<Faktura> fakture = findFakture(id);
			List<Preduzece> preduzeca = Preduzeca.checkCache();
			List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();

			renderTemplate("Fakture/show.html", poslovniPartneri, fakture, preduzeca, poslovneGodine);
		}

		// DODATI ZA NARUDZBU
	}

	public static List<Faktura> findFakture(Long idPoslovnogPartnera) {
		List<Faktura> faktureAll = Faktura.findAll();
		List<Faktura> fakture = new ArrayList<>();

		for (Faktura sc : faktureAll) {
			if (sc.poslovniPartner.id == idPoslovnogPartnera) {
				fakture.add(sc);
			}
		}

		return fakture;
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<PoslovniPartner> poslovniPartneri = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		PoslovniPartner poslovniPartner = PoslovniPartner.findById(id);
		Long idd = null;

		for (int i = 1; i < poslovniPartneri.size(); i++) {
			if (poslovniPartneri.get(i).id == id) {
				PoslovniPartner prethodni = poslovniPartneri.get(i - 1);
				idd = prethodni.id;
			}
		}
		poslovniPartner.delete();

		poslovniPartneri = PoslovniPartner.findAll();
		Cache.set("poslovniPartneri", poslovniPartneri);

		renderTemplate("poslovniPartneri/show.html", poslovniPartneri, preduzeca, idd, povezaneForme, mode);
	}

	public static void refresh() {
		String mode = session.get("mode");

		List<PoslovniPartner> poslovniPartneri = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		
		renderTemplate("poslovniPartneri/show.html", preduzeca, poslovniPartneri, povezaneForme, mode);
	}
}
