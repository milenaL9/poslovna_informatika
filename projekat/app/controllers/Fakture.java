package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Faktura;
import models.PoslovnaGodina;
import models.PoslovniPartner;
import models.Preduzece;
import models.StavkaFakture;
import play.cache.Cache;
import play.mvc.Controller;

public class Fakture extends Controller {

	public static void show() {
		validation.clear();

		session.put("idFakture", "null"); // za next mehanizam

		session.put("idPoslovnogPartnera", "null"); // ManyToOne
		session.put("idPoslovneGodine", "null");// ManyToOne
		session.put("idPreduzeca", "null"); // ManyToOne

		session.put("mode", "edit");
		String mode = session.get("mode");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

		session.put("mode", mode);

		List<String> povezaneForme = getForeignKeysFields();
		List<Faktura> fakture = Fakture.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		render(povezaneForme, fakture, mode, poslovniPartneri, poslovneGodine, preduzeca);

	}

	public static void edit(Faktura faktura, Long poslovniPartner, Long poslovnaGodina, Long preduzece) {

		validation.clear();
		validation.valid(faktura);
		clearSession();

		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<Faktura> fakture = null;

		if (!validation.hasErrors()) {
			fakture = Faktura.findAll();

			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			PoslovnaGodina findPoslovnaGodina = null;
			if (poslovnaGodina == null) {
				Long id = Long.parseLong(session.get("idPoslovneGodine"));
				findPoslovnaGodina = PoslovnaGodina.findById(id);
			} else {
				findPoslovnaGodina = PoslovnaGodina.findById(poslovnaGodina);
			}

			PoslovniPartner findPoslovniPartner = null;
			if (poslovniPartner == null) {
				Long id = Long.parseLong(session.get("idPoslovnogPartnera"));
				findPoslovniPartner = PoslovniPartner.findById(id);
			} else {
				findPoslovniPartner = PoslovniPartner.findById(poslovniPartner);
			}

			for (Faktura tmp : fakture) {
				if (tmp.id == faktura.id) {
					tmp.brojFakture = faktura.brojFakture;
					tmp.datumFakture = faktura.datumFakture;
					tmp.datumValute = faktura.datumValute;
					tmp.ukupnoOsnovica = faktura.ukupnoOsnovica;
					tmp.ukupnoPDV = faktura.ukupnoPDV;
					tmp.ukupnoZaPlacanje = faktura.ukupnoZaPlacanje;

					tmp.preduzece = findPreduzece;
					tmp.poslovnaGodina = findPoslovnaGodina;
					tmp.poslovniPartner = findPoslovniPartner;

					tmp.save();
					break;
				}
			}

			Cache.set("fakture", fakture);

			fakture.clear();
			fakture = fillList();
			validation.clear();

		} else {
			fakture = checkCache();

			validation.keep();

			session.put("idF", faktura.id);
			session.put("datumFakture", faktura.datumFakture);
			session.put("datumValute", faktura.datumValute);
			session.put("brojFakture", faktura.brojFakture);
			session.put("ukupnoOsnovica", faktura.ukupnoOsnovica);
			session.put("ukupnoPDV", faktura.ukupnoPDV);
			session.put("ukupnoZaPlacanje", faktura.ukupnoZaPlacanje);

		}

		renderTemplate("fakture/show.html", poslovniPartneri, povezaneForme, preduzeca, poslovneGodine, fakture, mode);
	}

	public static void create(Faktura faktura, Long poslovniPartner, Long poslovnaGodina, Long preduzece) {
		validation.clear();
		validation.valid(faktura);
		clearSession();

		session.put("mode", "add");
		String mode = session.get("mode");
		List<String> povezaneForme = getForeignKeysFields();

		List<Faktura> fakture = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();

		if (!validation.hasErrors()) {

			fakture = Faktura.findAll();

			Preduzece findPreduzece = null;
			if (preduzece == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}

			PoslovnaGodina findPoslovnaGodina = null;
			if (poslovnaGodina == null) {
				Long id = Long.parseLong(session.get("idPoslovneGodine"));
				findPoslovnaGodina = PoslovnaGodina.findById(id);
			} else {
				findPoslovnaGodina = PoslovnaGodina.findById(poslovnaGodina);
			}

			PoslovniPartner findPoslovniPartner = null;
			if (poslovniPartner == null) {
				Long id = Long.parseLong(session.get("idPoslovnogPartnera"));
				findPoslovniPartner = PoslovniPartner.findById(id);
			} else {
				findPoslovniPartner = PoslovniPartner.findById(poslovniPartner);
			}

			faktura.preduzece = findPreduzece;
			faktura.poslovnaGodina = findPoslovnaGodina;
			faktura.poslovniPartner = findPoslovniPartner;

			faktura.save();
			fakture.add(faktura);
			Cache.set("fakture", fakture);

			// potrebno da bi se selektovao dodati red na view delu
			Long idd = faktura.id;

			fakture.clear();
			fakture = fillList();
			validation.clear();
			clearSession();

			renderTemplate("fakture/show.html", poslovniPartneri, povezaneForme, preduzeca, fakture, poslovneGodine,
					idd, mode);
		} else {
			validation.keep();

			fakture = fillList();

			// potrebno da bi se ispisala greska
			session.put("idF", faktura.id);
			session.put("datumFakture", faktura.datumFakture);
			session.put("datumValute", faktura.datumValute);
			session.put("brojFakture", faktura.brojFakture);
			session.put("ukupnoOsnovica", faktura.ukupnoOsnovica);
			session.put("ukupnoPDV", faktura.ukupnoPDV);
			session.put("ukupnoZaPlacanje", faktura.ukupnoZaPlacanje);

			renderTemplate("fakture/show.html", poslovniPartneri, fakture, poslovneGodine, povezaneForme, preduzeca,
					mode);
		}
	}

	public static List<Faktura> fillList() {
		List<Faktura> fakture = null;
		if (!session.get("idPreduzeca").equals("null")) {
			Long id = Long.parseLong(session.get("idPreduzeca"));
			fakture = Preduzeca.findFakture(id);
		} else if (!session.get("idPoslovnogPartnera").equals("null")) {
			Long id = Long.parseLong(session.get("idPoslovnogPartnera"));
			fakture = PoslovniPartneri.findFakture(id);
		} else if (!session.get("idPoslovneGodine").equals("null")) {
			Long id = Long.parseLong(session.get("idPoslovneGodine"));
			fakture = PoslovneGodine.findFakture(id);
		} else {
			fakture = checkCache();
		}

		return fakture;
	}

	public static boolean clearSession() {
		session.put("datumFakture", null);
		session.put("brojFakture", null);
		session.put("datumValute", null);

		session.put("ukupnoOsnovica", null);
		session.put("ukupnoPDV", null);
		session.put("ukupnoZaPlacanje", null);

		return true;

	}

	public static void filter(Faktura faktura) {
		List<Faktura> fakture = Faktura.find("byDatumFaktureLikeAndDatumValute", "%" + faktura.datumFakture + "%",
				"%" + faktura.datumValute + "%").fetch();

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();

		List<String> povezaneForme = getForeignKeysFields();

		renderTemplate("poslovniPartneri/show.html", fakture, preduzeca, povezaneForme, poslovniPartneri,
				poslovneGodine, mode);

	}

	public static void changeMode(String mode) {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

		session.put("mode", mode);

		List<String> povezaneForme = getForeignKeysFields();
		List<Faktura> fakture = Fakture.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();

		renderTemplate("Fakture/show.html", povezaneForme, fakture, mode, poslovniPartneri, poslovneGodine, preduzeca);

	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<Faktura> fakture = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();

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

		renderTemplate("fakture/show.html", fakture, poslovneGodine, poslovniPartneri, preduzeca, idd, povezaneForme,
				mode);
	}

	public static void nextForm(Long id, String forma) {
		session.put("idFakture", id);
		session.put("idPreduzeca", "null");
		session.put("idPoslovneGodine", "null");
		session.put("idPoslovnogPartnera", "null");

		clearSession();

		if (forma.equals("stavkeFakture")) {
			List<Faktura> fakture = checkCache();
			List<StavkaFakture> stavkeFakture = findStavkeFakture(id);
			List<Preduzece> preduzeca = Preduzeca.checkCache();
			List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();
			List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();

			renderTemplate("StavkeFakture/show.html", fakture, stavkeFakture, preduzeca, poslovneGodine,
					poslovniPartneri);
		}

		// DODATI ZA NARUDZBU
	}

	public static List<StavkaFakture> findStavkeFakture(Long idFakture) {
		List<StavkaFakture> stavkeFaktureAll = StavkaFakture.findAll();
		List<StavkaFakture> stavkeFakture = new ArrayList<>();

		for (StavkaFakture sc : stavkeFaktureAll) {
			if (sc.faktura.id == idFakture) {
				stavkeFakture.add(sc);
			}
		}

		return stavkeFakture;
	}

	public static void refresh() {
		String mode = session.get("mode");

		List<Faktura> fakture = checkCache();
		List<String> povezaneForme = getForeignKeysFields();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<PoslovniPartner> poslovniPartneri = PoslovniPartneri.checkCache();
		List<PoslovnaGodina> poslovneGodine = PoslovneGodine.checkCache();

		renderTemplate("fakture/show.html", fakture, poslovneGodine, preduzeca, poslovniPartneri, povezaneForme, mode);
	}

	public static List<String> getForeignKeysFields() {
		Class fakturaClass = Faktura.class;
		Field[] fields = fakturaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	/**
	 * Pomocna metoda koja vraca listu nadredjenih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class faktureClass = Faktura.class;
		Field[] fields = faktureClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

	public static List<Faktura> checkCache() {

		List<Faktura> fakture = (List<Faktura>) Cache.get("fakture");

		if ((fakture == null) || (fakture.size() == 0)) {
			fakture = Faktura.findAll();
			Cache.set("fakture", fakture);
		}

		return fakture;
	}
}
