package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.ManyToOne;

import models.Cenovnik;
import models.Faktura;
import models.KatalogRobeIUsluga;
import models.StavkaCenovnika;
import models.StavkaFakture;
import models.StopaPDVa;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class StavkeFakture extends Controller {

	public static void show() throws ParseException {
		validation.clear();
		clearSession();

		session.put("idKataloga", "null"); // ManytoOne
		session.put("idFakture", "null");
		// session.put("idStopePDVa", "null");

		session.put("mode", "edit");
		String mode = session.get("mode");

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Faktura> fakture = Fakture.checkCache();
		List<StavkaFakture> stavkeFakture = checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		// List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(kataloziRobeIUsluga, fakture, stavkeFakture, mode, nadredjeneForme, stavkeCenovnika);

	}

	public static void changeMode(String mode) throws ParseException {
		clearSession();

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Faktura> fakture = Fakture.checkCache();
		List<StavkaFakture> stavkeFakture = fillList();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();
		// List<StopaPDVa> stopePDVa = StopePDVa.checkCache();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("StavkeFakture/show.html", kataloziRobeIUsluga, fakture, nadredjeneForme, stavkeFakture,
				stavkeCenovnika, mode);

	}

	public static void create(StavkaFakture stavkaFakture, Long faktura, Long katalogRobeIUsluga)
			throws ParseException {
		validation.clear();
		clearSession();

		validation.valid(stavkaFakture);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StavkaFakture> stavkeFakture = null;
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();

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

			KatalogRobeIUsluga katalogRobeIUslugaFind = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			stavkaFakture.katalogRobeIUsluga = katalogRobeIUslugaFind;

			for (StavkaCenovnika sc : stavkeCenovnika) {
				if (sc.katalogRobeIUsluga.id == stavkaFakture.katalogRobeIUsluga.id) {
					stavkaFakture.cena = (float) sc.cena;
				}
			}
			stavkaFakture.vrednost = stavkaFakture.cena * stavkaFakture.kolicina;
			stavkaFakture.iznosRabata = stavkaFakture.vrednost * (stavkaFakture.rabat / 100);
			stavkaFakture.osnovicaZaPDV = stavkaFakture.vrednost - stavkaFakture.iznosRabata;

			stavkaFakture.stopaPDVa = findStopaPDVa(findFaktura.id,
					stavkaFakture.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa).procenatPDVa;

			stavkaFakture.iznosPDVa = (stavkaFakture.osnovicaZaPDV * stavkaFakture.stopaPDVa) / 100;
			stavkaFakture.ukupno = stavkaFakture.vrednost - stavkaFakture.iznosRabata + stavkaFakture.iznosPDVa;

			stavkaFakture.faktura = findFaktura;

			stavkaFakture.faktura.ukupnoPDV += stavkaFakture.iznosPDVa;
			stavkaFakture.faktura.ukupnoZaPlacanje += stavkaFakture.ukupno;
			stavkaFakture.faktura.ukupnoOsnovica += stavkaFakture.osnovicaZaPDV;

			stavkaFakture.faktura.save();
			stavkaFakture.save();

			List<Faktura> faktureAll = Faktura.findAll();
			Cache.set("fakture", faktureAll);

			stavkeFakture.add(stavkaFakture);
			Cache.set("stavkeFakture", stavkeFakture);

			Long idd = stavkaFakture.id;

			stavkeFakture.clear();
			stavkeFakture = fillList();

			validation.clear();

			renderTemplate("StavkeFakture/show.html", stavkeFakture, nadredjeneForme, fakture, kataloziRobeIUsluga, idd,
					mode, stavkeCenovnika);
		} else {
			validation.keep();

			stavkeFakture = fillList();

			session.put("kolicinaSF", stavkaFakture.kolicina);
			session.put("rabatSF", stavkaFakture.rabat);

			renderTemplate("StavkeFakture/show.html", stavkeFakture, fakture, nadredjeneForme, kataloziRobeIUsluga,
					stavkaFakture, mode);
		}

	}

	public static StopaPDVa findStopaPDVa(Long idFakture, VrstaPDVa vrstaPDVa) throws ParseException {
		Faktura faktura = Faktura.findById(idFakture);
		String datumFakture = faktura.datumFakture;
		Date datumFaktureDate = Fakture.convertToDate(datumFakture);

		List<StopaPDVa> stopePDVaSaDatumima = new ArrayList<>();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
		for (StopaPDVa tmp : stopePDVa) {
			String datumStopePDVa = tmp.datumKreiranja;
			Date datumStopePDVaDate = Fakture.convertToDate(datumStopePDVa);

			if (!datumStopePDVaDate.after(datumFaktureDate) && (tmp.vrstaPDVa.id == vrstaPDVa.id)) {
				stopePDVaSaDatumima.add(tmp);
			}
		}

		List<Date> datumi = new ArrayList<>();
		for (StopaPDVa tmp : stopePDVaSaDatumima) {
			Date d = Fakture.convertToDate(tmp.datumKreiranja);
			datumi.add(d);
		}

		Collections.sort(datumi, new Comparator<Date>() {
			@Override
			public int compare(Date arg0, Date arg1) {
				return arg0.compareTo(arg1);
			}
		});

		// trazim stopuPDVa
		StopaPDVa stopaPDVa = null;
		for (StopaPDVa tmp : stopePDVaSaDatumima) {
			String string = new SimpleDateFormat("MM/dd/yyyy").format(datumi.get(datumi.size() - 1));
			if (tmp.datumKreiranja.equals(string)) {
				stopaPDVa = tmp;
			}
		}

		return stopaPDVa;
	}

	public static void edit(StavkaFakture stavkaFakture, Long faktura, Long katalogRobeIUsluga, Long stopaPDVa)
			throws ParseException {
		validation.clear();
		clearSession();

		validation.valid(stavkaFakture);

		session.put("mode", "add");
		String mode = session.get("mode");

		List<StavkaFakture> stavkeFakture = null;
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		// List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();

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

			// StopaPDVa findStopaPDVa = null;
			// if (stopaPDVa == null) {
			// Long id = Long.parseLong(session.get("idStopePDVa"));
			// findStopaPDVa = StopaPDVa.findById(id);
			// } else {
			// findStopaPDVa = StopaPDVa.findById(stopaPDVa);
			// }

			stavkaFakture.faktura = findFaktura;
			stavkaFakture.katalogRobeIUsluga = findKatalog;
			stavkaFakture.save();

			//////////////////////////////////////

			// List<StavkaCenovnika> stavkeCenovnika1 =
			// stavkaFakture.katalogRobeIUsluga.stavkeCenovnika;
			// for (StavkaCenovnika sc : stavkeCenovnika1) {
			// if (sc.katalogRobeIUsluga == stavkaFakture.katalogRobeIUsluga) {
			// stavkaFakture.cena = (float) sc.cena;
			// }
			// }

			StavkaCenovnika stavkaCenovnika = StavkaCenovnika.findById(katalogRobeIUsluga);
			stavkaCenovnika.katalogRobeIUsluga = stavkaCenovnika.katalogRobeIUsluga;

			stavkaFakture.save();
			List<StopaPDVa> stopePDVa = stavkaFakture.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa.stopePDVa;

			for (StopaPDVa sp : stopePDVa) {
				if (sp.vrstaPDVa == stavkaFakture.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa) {
					stavkaFakture.stopaPDVa = sp.procenatPDVa;
				}
			}

			stavkaFakture.osnovicaZaPDV = stavkaFakture.cena;
			stavkaFakture.save();
			stavkaFakture.iznosPDVa = (stavkaFakture.osnovicaZaPDV) * stavkaFakture.stopaPDVa / 100;
			stavkaFakture.cena = stavkaFakture.cena + stavkaFakture.iznosPDVa;
			stavkaFakture.save();

			//////////////////////////////////////////
			for (StavkaFakture tmp : stavkeFakture) {
				if (tmp.id == stavkaFakture.id) {
					tmp.kolicina = stavkaFakture.kolicina;
					tmp.rabat = stavkaFakture.rabat;
					tmp.save();

					tmp.ukupno = ((tmp.cena) * (tmp.kolicina)) - tmp.rabat;
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

			session.put("idSF", stavkaFakture.id);
			session.put("kolicinaSF", stavkaFakture.kolicina);
			session.put("rabatSF", stavkaFakture.rabat);

		}

		renderTemplate("StavkeFakture/show.html", stavkeFakture, fakture, nadredjeneForme, kataloziRobeIUsluga,
				stavkeCenovnika, mode);
	}

	public static void filter(StavkaFakture stavkaFakture) throws ParseException {
		List<StavkaFakture> stavkeFakture = StavkaFakture.find("byCena", stavkaFakture.cena).fetch();

		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		session.put("mode", "edit");
		String mode = session.get("mode");

		renderTemplate("StavkeFakture/show.html", stopePDVa, stavkeFakture, fakture, nadredjeneForme,
				kataloziRobeIUsluga, stavkeCenovnika, mode);
	}

	public static void delete(Long id) throws ParseException {
		String mode = session.get("mode");

		List<StavkaFakture> stavkeFakture = checkCache();
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();

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
				kataloziRobeIUsluga, idd, stavkeCenovnika, mode);
	}

	public static void nextForm() {

	}

	public static void refresh() throws ParseException {
		List<Faktura> fakture = Fakture.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StavkaFakture> stavkeFakture = fillList();
		List<StopaPDVa> stopePDVa = StopePDVa.checkCache();
		List<StavkaCenovnika> stavkeCenovnika = fillListStavkeCenovnika();

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		String mode = session.get("mode");

		renderTemplate("StavkeFakture/show.html", stavkeFakture, stopePDVa, fakture, nadredjeneForme,
				kataloziRobeIUsluga, stavkeCenovnika, mode);
	}

	/** Prelazak na nadredjenu formu */
	public static void pickup(String forma) {
		if (forma.equals("faktura")) {
			Fakture.show();
		} else if (forma.equals("katalogRobeIUsluga")) {
			KataloziRobeIUsluga.show();
		} else if (forma.equals("stopaPDVa")) {
			StopePDVa.show();
		}
	}

	public static boolean clearSession() {
		session.put("idSF", null);
		session.put("kolicinaSF", null);
		session.put("rabatSF", null);

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

	public static List<StavkaCenovnika> fillListStavkeCenovnika() throws ParseException {
		List<StavkaCenovnika> stavkeCenovnika = null;

		if (!session.get("idFakture").equals("null")) {
			Long id = Long.parseLong(session.get("idFakture"));
			stavkeCenovnika = Fakture.findStavkeCenovnika(id);
		}

		return stavkeCenovnika;
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
