package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;

import models.Faktura;
import models.KatalogRobeIUsluga;
import models.StavkaCenovnika;
import models.StavkaFakture;
import models.StopaPDVa;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class StavkeFakture extends Controller {

	public static void show() {
		validation.clear();
		clearSession();

		session.put("idKataloga", "null"); // ManytoOne
		session.put("idFakture", "null");
		session.put("idStopePDVa", "null");

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Faktura> fakture = Fakture.checkCache();
		List<StavkaFakture> stavkeFakture = checkCache();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(kataloziRobeIUsluga, fakture, stopePDVa, stavkeFakture, mode, nadredjeneForme);

	}

	public static void changeMode(String mode) {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Faktura> fakture = Fakture.checkCache();
		List<StavkaFakture> stavkeFakture = fillList();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("StavkeFakture/show.html", kataloziRobeIUsluga, stopePDVa, fakture, nadredjeneForme,
				stavkeFakture, mode);

	}

	public static void create(StavkaFakture stavkaFakture, Long faktura, Long katalogRobeIUsluga, Long stopaPDVa) {
		validation.clear();
		clearSession();

		validation.valid(stavkaFakture);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StavkaFakture> stavkeFakture = null;
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		if (!validation.hasErrors()) {
			stavkeFakture = StavkaFakture.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Faktura findFaktura = null;
			if (faktura == null) {
				Long id = Long.parseLong(session.get("idFakture"));
				findFaktura = Faktura.findById(id);
			} else {
				findFaktura = Faktura.findById(faktura);
			}

			KatalogRobeIUsluga findKatalog = null;
			if (katalogRobeIUsluga == null) {
				Long id = Long.parseLong(session.get("idKataloga"));
				findKatalog = KatalogRobeIUsluga.findById(id);
			} else {
				findKatalog = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			}

			StopaPDVa findStopaPDVa = null;
			if (stopaPDVa == null) {
				Long id = Long.parseLong(session.get("idStopePDVa"));
				findStopaPDVa = StopaPDVa.findById(id);
			} else {
				findStopaPDVa = StopaPDVa.findById(stopaPDVa);
			}

			stavkaFakture.faktura = findFaktura;
			stavkaFakture.katalogRobeIUsluga = findKatalog;
			stavkaFakture.stopaPDVa = findStopaPDVa;

			stavkaFakture.save();
			stavkeFakture.add(stavkaFakture);
			Cache.set("stavkeFakture", stavkeFakture);

			Long idd = stavkaFakture.id;

			stavkeFakture.clear();
			stavkeFakture = fillList();

			validation.clear();

			renderTemplate("StavkeFakture/show.html", stopePDVa, stavkeFakture, nadredjeneForme, fakture,
					kataloziRobeIUsluga, idd, mode);
		} else {
			validation.keep();

			stavkeFakture = fillList();

			session.put("datumFakture", null);
			session.put("brojFakture", null);
			session.put("datumValute", null);
			session.put("ukupnoOsnovica", null);
			session.put("ukupnoPDV", null);
			session.put("ukupnoZaPlacanje", null);

			renderTemplate("StavkeFakture/show.html", stavkeFakture, stopePDVa, fakture, nadredjeneForme,
					kataloziRobeIUsluga, mode);
		}

	}

	public static void edit(StavkaFakture stavkaFakture, Long faktura, Long katalogRobeIUsluga, Long stopaPDVa) {
		validation.clear();
		clearSession();

		validation.valid(stavkaFakture);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StavkaFakture> stavkeFakture = null;
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		if (!validation.hasErrors()) {
			stavkeFakture = StavkaFakture.findAll();

			// kada je disable- ovan combobox ne pokupi vrednost
			Faktura findFaktura = null;
			if (faktura == null) {
				Long id = Long.parseLong(session.get("idFakture"));
				findFaktura = Faktura.findById(id);
			} else {
				findFaktura = Faktura.findById(faktura);
			}

			KatalogRobeIUsluga findKatalog = null;
			if (katalogRobeIUsluga == null) {
				Long id = Long.parseLong(session.get("idKataloga"));
				findKatalog = KatalogRobeIUsluga.findById(id);
			} else {
				findKatalog = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			}

			StopaPDVa findStopaPDVa = null;
			if (stopaPDVa == null) {
				Long id = Long.parseLong(session.get("idStopePDVa"));
				findStopaPDVa = StopaPDVa.findById(id);
			} else {
				findStopaPDVa = StopaPDVa.findById(stopaPDVa);
			}

			stavkaFakture.faktura = findFaktura;
			stavkaFakture.katalogRobeIUsluga = findKatalog;

			for (StavkaFakture tmp : stavkeFakture) {
				if (tmp.id == stavkaFakture.id) {
					tmp.cena = stavkaFakture.cena;
					tmp.iznosPDVa = stavkaFakture.iznosPDVa;
					tmp.kolicina = stavkaFakture.kolicina;
					tmp.osnovicaZaPDV = stavkaFakture.osnovicaZaPDV;
					tmp.rabat = stavkaFakture.rabat;
					tmp.stopaPDVa = stavkaFakture.stopaPDVa;
					tmp.ukupno = stavkaFakture.ukupno;

					tmp.save();
					break;
				}
			}

			Cache.set("stavkeFakture", stavkeFakture);

			stavkeFakture.clear();
			stavkeFakture = fillList();

			validation.clear();
		} else {
			validation.keep();

			stavkeFakture = fillList();

			session.put("idSF", null);
			session.put("datumFakture", null);
			session.put("brojFakture", null);
			session.put("datumValute", null);
			session.put("ukupnoOsnovica", null);
			session.put("ukupnoPDV", null);
			session.put("ukupnoZaPlacanje", null);

		}

		renderTemplate("StavkeFakture/show.html", stavkeFakture, stopePDVa, fakture, nadredjeneForme,
				kataloziRobeIUsluga, mode);
	}

	public static void filter(StavkaFakture stavkaFakture) {
		List<StavkaFakture> stavkeFakture = StavkaFakture.find("byCena", stavkaFakture.cena).fetch();

		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		session.put("mode", "edit");
		String mode = session.get("mode");

		renderTemplate("StavkeFakture/show.html", stopePDVa, stavkeFakture, fakture, nadredjeneForme,
				kataloziRobeIUsluga, mode);
	}

	public static void delete(Long id) {
		String mode = session.get("mode");

		List<StavkaFakture> stavkeFakture = checkCache();
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		StavkaFakture stavkaFakture = StavkaFakture.findById(id);
		Long idd = null;

		for (int i = 1; i < stavkeFakture.size(); i++) {
			if (stavkeFakture.get(i).id == id) {
				StavkaFakture prethodni = stavkeFakture.get(i - 1);
				idd = prethodni.id;
			}
		}
		stavkaFakture.delete();

		Cache.set("stavkeFakture", stavkeFakture);

		stavkeFakture.clear();
		stavkeFakture = fillList();

		renderTemplate("StavkeFakture/show.html", stavkeFakture, stopePDVa, fakture, nadredjeneForme,
				kataloziRobeIUsluga, idd, mode);
	}

	public static void nextForm() {

	}

	public static void refresh() {
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StavkaFakture> stavkeFakture = fillList();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		String mode = session.get("mode");

		renderTemplate("StavkeFakture/show.html", stavkeFakture, stopePDVa, fakture, nadredjeneForme,
				kataloziRobeIUsluga, mode);
	}

	/** Prelazak na nadredjenu formu */
	public static void pickup(String forma) {
		if (forma.equals("faktura")) {
			Fakture.show();
		} else if (forma.equals("katalogRobeIUsluga")) {
			KataloziRobeIUsluga.show();
		}
	}

	public static boolean clearSession() {
		session.put("idSF", null);
		session.put("datumFakture", null);
		session.put("brojFakture", null);
		session.put("datumValute", null);
		session.put("ukupnoOsnovica", null);
		session.put("ukupnoPDV", null);
		session.put("ukupnoZaPlacanje", null);

		return true;
	}

	public static List<StavkaFakture> checkCache() {
		List<StavkaFakture> stavkeFakture = (List<StavkaFakture>) Cache.get("stavkeFakture");

		if ((stavkeFakture == null) || (stavkeFakture.size() == 0)) {
			stavkeFakture = StavkaFakture.findAll();
			Cache.set("stavkeFakture", stavkeFakture);
		}

		return stavkeFakture;
	}

	public static List<StavkaFakture> fillList() {
		List<StavkaFakture> stavkeFakture = null;

		if (!session.get("idKataloga").equals("null")) {
			Long id = Long.parseLong(session.get("idKataloga"));
			stavkeFakture = KataloziRobeIUsluga.findStavkeFakture(id);
		} else if (!session.get("idFakture").equals("null")) {
			Long id = Long.parseLong(session.get("idFakture"));
			stavkeFakture = Fakture.findStavkeFakture(id);
		} else {
			stavkeFakture = checkCache();
		}

		return stavkeFakture;
	}

	/**
	 * Pomocna metoda koja vraca listu nadredjenih formi.
	 * 
	 * @see <a href=
	 *      "http://tutorials.jenkov.com/java-reflection/annotations.html"> Java
	 *      Reflection - Annotations</a>
	 */
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class stavkaFaktureClass = StavkaFakture.class;
		Field[] fields = stavkaFaktureClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}

}
