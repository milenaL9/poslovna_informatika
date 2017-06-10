package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;

import models.KatalogRobeIUsluga;
import models.Narudzba;
import models.StavkaCenovnika;
import models.StavkaNarudzbe;
import models.StopaPDVa;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class StavkeNarudzbe extends Controller {
	
	
	public static void show(){
		validation.clear();
		clearSession();

		session.put("idKataloga", "null");
		session.put("idNarudzbe", "null");

		session.put("mode", "edit");
		String mode = session.get("mode");
		
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<StavkaNarudzbe> stavkeNarudzbe = checkCache();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		render(kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode);
	}
	
	public static void changeMode(String mode) {
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);

		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		//List<StavkaNarudzbe> stavkeNarudzbe = fillList();
		List<StavkaNarudzbe> stavkeNarudzbe = checkCache();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode);

	}
	
	public static void create(StavkaNarudzbe stavkaNarudzbe, Long narudzba, Long katalogRobeIUsluga){
		validation.clear();
		clearSession();

		validation.valid(stavkaNarudzbe);

		session.put("mode", "add");
		String mode = session.get("mode");
		
		List<StavkaNarudzbe> stavkeNarudzbe = null;
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if(!validation.hasErrors()){
			stavkeNarudzbe = StavkaNarudzbe.findAll();
			
			Narudzba findNarudzba = null;
			if(narudzba==null){
				Long id = Long.parseLong(session.get("idNarudzbe"));
				findNarudzba = Narudzba.findById(id);
			} else{
				findNarudzba = Narudzba.findById(narudzba);
			}
			
			KatalogRobeIUsluga findKatalog = null;
			if (katalogRobeIUsluga == null) {
				Long id = Long.parseLong(session.get("idKataloga"));
				findKatalog = KatalogRobeIUsluga.findById(id);
			} else {
				findKatalog = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			}
			
			stavkaNarudzbe.katalogRobeIUsluga = findKatalog;
			stavkaNarudzbe.narudzba = findNarudzba;
			
			List<StavkaCenovnika> stavkeCenovnika = stavkaNarudzbe.katalogRobeIUsluga.stavkeCenovnika;
			for(StavkaCenovnika sc : stavkeCenovnika) {
				if(sc.katalogRobeIUsluga == stavkaNarudzbe.katalogRobeIUsluga) {
					stavkaNarudzbe.cena =  (float) sc.cena;
				}
			}
			
			stavkaNarudzbe.save();
			
			List<StopaPDVa> stopePDVa = stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa.stopePDVa;
			
			for(StopaPDVa sp : stopePDVa) {
				if(sp.vrstaPDVa == stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa) {
					stavkaNarudzbe.stopaPDVa = sp.procenatPDVa;
				}
			}
			
			stavkaNarudzbe.osnovicaZaPDV = (int) stavkaNarudzbe.cena;
			stavkaNarudzbe.save();
			stavkaNarudzbe.iznosPDVa = (int) ((stavkaNarudzbe.osnovicaZaPDV) * stavkaNarudzbe.stopaPDVa / 100) ;	
			stavkaNarudzbe.cena = (float) (stavkaNarudzbe.cena + stavkaNarudzbe.iznosPDVa) ;
			
			stavkaNarudzbe.save();
			stavkaNarudzbe.ukupno = (int) (stavkaNarudzbe.cena*stavkaNarudzbe.kolicina);
			stavkaNarudzbe.save();
			
			stavkeNarudzbe.add(stavkaNarudzbe);
			Cache.set("stavkeNarudzbe", stavkeNarudzbe);

			Long idd = stavkaNarudzbe.id;

			stavkeNarudzbe.clear();
			stavkeNarudzbe = fillList();

			validation.clear();
			
			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,idd);

			
		}else{
			validation.keep();
			stavkeNarudzbe = fillList();
			session.put("idSN", null);
			renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode);
		}
		
		
	}
	
	public static void edit(StavkaNarudzbe stavkaNarudzbe, Long narudzba, Long katalogRobeIUsluga){
		validation.clear();
		clearSession();

		validation.valid(stavkaNarudzbe);

		session.put("mode", "edit");
		String mode = session.get("mode");
		
		List<StavkaNarudzbe> stavkeNarudzbe = null;
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if(!validation.hasErrors()){
			stavkeNarudzbe = StavkaNarudzbe.findAll();
			
			Narudzba findNarudzba = null;
			if(narudzba==null){
				Long id = Long.parseLong(session.get("idNarudzbe"));
				findNarudzba = Narudzba.findById(id);
			} else{
				findNarudzba = Narudzba.findById(narudzba);
			}
			
			KatalogRobeIUsluga findKatalog = null;
			if (katalogRobeIUsluga == null) {
				Long id = Long.parseLong(session.get("idKataloga"));
				findKatalog = KatalogRobeIUsluga.findById(id);
			} else {
				findKatalog = KatalogRobeIUsluga.findById(katalogRobeIUsluga);
			}
			
			stavkaNarudzbe.katalogRobeIUsluga = findKatalog;
			stavkaNarudzbe.narudzba = findNarudzba;
			
			List<StavkaCenovnika> stavkeCenovnika = stavkaNarudzbe.katalogRobeIUsluga.stavkeCenovnika;
			for(StavkaCenovnika sc : stavkeCenovnika) {
				if(sc.katalogRobeIUsluga == stavkaNarudzbe.katalogRobeIUsluga) {
					stavkaNarudzbe.cena =  (float) sc.cena;
				}
			}
			
			stavkaNarudzbe.save();
			
			List<StopaPDVa> stopePDVa = stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa.stopePDVa;
			
			for(StopaPDVa sp : stopePDVa) {
				if(sp.vrstaPDVa == stavkaNarudzbe.katalogRobeIUsluga.podgrupa.grupa.vrstaPDVa) {
					stavkaNarudzbe.stopaPDVa = sp.procenatPDVa;
				}
			}
			
			stavkaNarudzbe.osnovicaZaPDV = (int) stavkaNarudzbe.cena;
			stavkaNarudzbe.save();
			stavkaNarudzbe.iznosPDVa = (int) ((stavkaNarudzbe.osnovicaZaPDV) * stavkaNarudzbe.stopaPDVa / 100) ;	
			stavkaNarudzbe.cena = (float) (stavkaNarudzbe.cena + stavkaNarudzbe.iznosPDVa);
			
			
			for (StavkaNarudzbe tmp : stavkeNarudzbe) {
				if (tmp.id == stavkaNarudzbe.id) {
					tmp.kolicina = stavkaNarudzbe.kolicina;
					tmp.save();
					
					tmp.ukupno =(int) ((tmp.cena) * (tmp.kolicina));
					tmp.save();
					
					break;
				}
			}
			
			
			Cache.set("stavkeNarudzbe", stavkeNarudzbe);
			stavkeNarudzbe.clear();
			stavkeNarudzbe = fillList();
			
			validation.clear();
		}else{
			validation.keep();
			
			stavkeNarudzbe = fillList();
			
			session.put("idSN", null);
		}
		
		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode);
	}
	
	public static void filter(){
		
	}
	
	public static void delete(Long id){
		String mode = session.get("mode");
		
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<StavkaNarudzbe> stavkeNarudzbe = checkCache();
		
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		StavkaNarudzbe stavkaNarudzbe = StavkaNarudzbe.findById(id);
		Long idd = null;
		
		for (int i = 1; i < stavkeNarudzbe.size(); i++) {
			if (stavkeNarudzbe.get(i).id == id) {
				StavkaNarudzbe prethodni = stavkeNarudzbe.get(i - 1);
				idd = prethodni.id;
			}
		}
		stavkaNarudzbe.delete();

		Cache.set("stavkeNarudzbe", stavkeNarudzbe);
		
		stavkeNarudzbe.clear();
		stavkeNarudzbe = fillList();
		
		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode,idd);
		
	}
	
	public static void refresh(){
		List<Narudzba> narudzbe = Narudzbe.checkCache();
		List<KatalogRobeIUsluga> kataloziRobeIUsluga = KataloziRobeIUsluga.checkCache();
		List<StavkaNarudzbe> stavkeNarudzbe = fillList();
		

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		String mode = session.get("mode");
		
		renderTemplate("StavkeNarudzbe/show.html",kataloziRobeIUsluga, narudzbe, stavkeNarudzbe ,nadredjeneForme,mode);
	}
	
	public static void pickup(String forma){
		if (forma.equals("narudzba")) {
			Narudzbe.show();
		} else if (forma.equals("katalogRobeIUsluga")) {
			KataloziRobeIUsluga.show();
		}
		
	}
	
	public static List<StavkaNarudzbe> fillList(){
		List<StavkaNarudzbe> nesto = StavkaNarudzbe.findAll();
				
		return nesto;
	}
	
	public static List<StavkaNarudzbe> checkCache(){
		List<StavkaNarudzbe> stavkeNarudzbe = (List<StavkaNarudzbe>) Cache.get("stavkeNarudzbe");

		if ((stavkeNarudzbe == null) || (stavkeNarudzbe.size() == 0)) {
			stavkeNarudzbe = StavkaNarudzbe.findAll();
			Cache.set("stavkeNarudzbe", stavkeNarudzbe);
		}

		return stavkeNarudzbe;
	}
	
	public static boolean clearSession(){
		session.put("idSF", null);
		return true;
	}
	
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class stavkaFaktureClass = StavkaNarudzbe.class;
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
