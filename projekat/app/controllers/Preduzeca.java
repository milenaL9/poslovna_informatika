package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import models.Drzava;
import models.Faktura;
import models.Grupa;
import models.NaseljenoMesto;
import models.PoslovnaGodina;
import models.PoslovniPartner;
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

	/** TODO: Kako uraditi filter sa int atributima? */
	public static void filter(Preduzece preduzece) {
		// String pibStr = convertIntToString(preduzece.pib);
		// String telefonStr = convertIntToString(preduzece.telefon);
		// String maticniBrojStr = Long.toString(preduzece.maticniBroj);
		// List<Preduzece> preduzeca = Preduzece
		// .find("byNazivLikeAndPibLikeAndMestoLikeAndAdresaLikeAndTelefonLikeAndMaticniBrojLikeAndTekuciRacunLike",
		// "%" + preduzece.naziv + "%", "%" + pibStr + "%", "%" +
		// preduzece.mesto + "%",
		// "%" + preduzece.adresa + "%", "%" + telefonStr + "%", "%" +
		// maticniBrojStr + "%",
		// "%" + preduzece.tekuciRacun + "%")
		// .fetch();
		// session.put("mode", "edit");
		// renderTemplate("preduzeca/show.html", preduzeca);
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

	/**
	 * TODO: Kako proslediti i parametar forma? Kada prosledim oba ne prepoznaje
	 * nijedan.
	 */
	public static void nextForm(Long id, String forma) {
		List<Preduzece> preduzeca = checkCache();

		System.out.println("id:" + id);
		System.out.println("forma:" + forma);

		// session.put("idPreduzeca", id);
		clearSession();

		renderTemplate("preduzeca/show.html", preduzeca);
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

	public static String convertIntToString(int number) {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		sb.append(number);
		String strI = sb.toString();

		return strI;
	}

	public static List<PoslovnaGodina> findPoslovneGodine(Long idPreduzeca) {
		List<PoslovnaGodina> poslovneGodineAll = PoslovnaGodina.findAll();
		List<PoslovnaGodina> poslovneGodine = new ArrayList<>();

		for (PoslovnaGodina pg : poslovneGodineAll) {
			if (pg.preduzece.id == idPreduzeca) {
				poslovneGodine.add(pg);
			}
		}

		return poslovneGodine;
	}

	public static List<Grupa> findGrupe(Long idPreduzeca) {
		List<Grupa> grupeAll = Grupa.findAll();
		List<Grupa> grupe = new ArrayList<>();

		for (Grupa g : grupeAll) {
			if (g.preduzece.id == idPreduzeca) {
				grupe.add(g);
			}
		}

		return grupe;
	}

	public static List<PoslovniPartner> findPoslovniPartneri(Long idPreduzeca) {
		List<PoslovniPartner> poslovniPartneriAll = PoslovniPartner.findAll();
		List<PoslovniPartner> poslovniPartneri = new ArrayList<>();

		for (PoslovniPartner pp : poslovniPartneriAll) {
			if (pp.preduzece.id == idPreduzeca) {
				poslovniPartneri.add(pp);
			}
		}

		return poslovniPartneri;
	}

	public static List<Faktura> findFakture(Long idPreduzeca) {
		List<Faktura> faktureAll = Faktura.findAll();
		List<Faktura> fakture = new ArrayList<>();

		for (Faktura f : faktureAll) {
			if (f.preduzece.id == idPreduzeca) {
				fakture.add(f);
			}
		}

		return fakture;
	}
}
