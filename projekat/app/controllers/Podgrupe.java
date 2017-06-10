package controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;

import models.Grupa;
import models.KatalogRobeIUsluga;
import models.Podgrupa;
import models.PoslovnaGodina;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class Podgrupe extends Controller {
	
	public static void show(){
		validation.clear();
		clearSession();
		
		//session.put("idGrupe","null");
		session.put("mode","edit");
		
		String mode = session.get("mode");
		
		List<Podgrupa> podgrupe = checkCache();
		List<Grupa> grupe = Grupe.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		render("Podgrupe/show.html",podgrupe,grupe,nadredjeneForme);
	}
	
	
	public static void changeMode(String mode){
		
		if(mode == null || mode.equals("")) {
			mode="edit";
		}
		session.put("mode", mode);
		
		List<Grupa> grupe = Grupe.checkCache();
		List<Podgrupa> podgrupe = fillList();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		renderTemplate("Podgrupe/show.html",podgrupe,grupe,mode,nadredjeneForme);
		
	}
	
	public static void edit(Podgrupa podgrupa, Long grupa){
		validation.clear();
		clearSession();
		
		validation.valid(podgrupa);
		
		session.put("mode", "edit");
		String mode = session.get("mode");
		
		List<Podgrupa> podgrupe = null;
		List<Grupa> grupe = Grupe.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if(!validation.hasErrors()){
			podgrupe = Podgrupa.findAll();
			
			Grupa findGrupa = null;
			
			if(grupa == null){
				Long id = Long.parseLong(session.get("idGrupe"));
				findGrupa = Grupa.findById(id);
			} else {
				findGrupa = Grupa.findById(grupa);
			}
			
			for(Podgrupa p:podgrupe){
				if(p.id == podgrupa.id){
					p.nazivPodgrupe = podgrupa.nazivPodgrupe;
					p.grupa = findGrupa;
					p.save();
					break;
				}
			}
			
			Cache.set("podgrupe", podgrupe);
			podgrupe.clear();
			podgrupe = fillList();
			validation.clear();
		}
		else{
			validation.keep();
			session.put("idPodgrupe", podgrupa.id);
			session.put("nazivPodgrupe",podgrupa.nazivPodgrupe);
		}
		
		renderTemplate("Podgrupe/show.html",podgrupe,grupe,nadredjeneForme);
		
	}
	
	
	public static void create(Podgrupa podgrupa, Long grupa){
		validation.clear();
		clearSession();
		
		validation.valid(podgrupa);
		
		session.put("mode", "add");
		String mode = session.get("mode");
		
		List<Podgrupa> podgrupe = null;
		List<Grupa> grupe = Grupe.checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if(!validation.hasErrors()){
			podgrupe = Podgrupa.findAll();
			
			Grupa findGrupa = null;
			
			if(grupa==null){
				Long id = Long.parseLong(session.get("idGrupe"));
				findGrupa = Grupa.findById(id);
			}
			else 
				findGrupa = Grupa.findById(grupa);
			
			podgrupa.grupa = findGrupa;
			podgrupa.save();
			podgrupe.add(podgrupa);
			
			Cache.set("podgrupe", podgrupe);
			
			Long idd = podgrupa.id;
			
			podgrupe.clear();
			podgrupe = fillList();
			validation.clear();
			
			renderTemplate("Podgrupe/show.html", podgrupe,grupe, idd, mode,nadredjeneForme);
			
		}
		else {
			validation.keep();
			podgrupe = fillList();
			
			session.put("idPodgrupe",podgrupa.id);
			session.put("nazivPodgrupe",podgrupa.nazivPodgrupe);
			renderTemplate("Podgrupe/show.html", podgrupe,grupe, mode,nadredjeneForme);
		}
		
		
	}
	
	public static boolean clearSession(){
		session.put("nazivPodgrupe", null);
		session.put("idPodgrupe", null);
		
		return true;
	}
	
	public static void filter(Podgrupa podgrupa){
		List<Podgrupa> podgrupe = Podgrupa.find("byNazivPodgrupe", podgrupa.nazivPodgrupe).fetch();
		List<Grupa> grupe = Grupe.checkCache();
		session.put("mode", "edit");
		String mode = session.get("mode");
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		renderTemplate("Podgrupe/show.html",podgrupe,grupe,mode,nadredjeneForme);
	}
	
	public static List<Podgrupa> fillList() {
		List<Podgrupa> podgrupe = null;
		
		if(!session.get("idGrupe").equals("null")){
			Long id = Long.parseLong(session.get("idGrupe"));
			podgrupe = Grupe.findPodgrupe(id); // for now
		}
		
		else podgrupe = checkCache();
		
		return podgrupe;
	}
	
	public static void delete(Long id){
		String mode = session.get("mode");
		
		List<Grupa> grupe = Grupe.checkCache();
		List<Podgrupa> podgrupe = checkCache();
		
		Podgrupa podgrupa = Podgrupa.findById(id);
		Long idd = null;
		
		for (int i = 1; i < podgrupe.size(); i++) {
			if (podgrupe.get(i).id == id) {
				Podgrupa prethodni = podgrupe.get(i - 1);
				idd = prethodni.id;
			}
		}
		podgrupa.delete();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

	
		Cache.set("podgrupe", podgrupe);
		podgrupe.clear();
		podgrupe = fillList();
		renderTemplate("Podgrupe/show.html",podgrupe,grupe,idd,mode,nadredjeneForme);
	}
	
	public static void refresh(){
		String mode = session.get("mode");
		
		List<Grupa> grupe = Grupe.checkCache();
		List<Podgrupa> podgrupe = checkCache();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		renderTemplate("Podgrupe/show.html",podgrupe,grupe,mode,nadredjeneForme);
	}
	
	
	public static List<Podgrupa> checkCache(){
		
		List<Podgrupa> podgrupe = (List<Podgrupa>) Cache.get("podgrupe");
		
		if((podgrupe==null) || (podgrupe.size()==0) ){
			podgrupe = Podgrupa.findAll();
			Cache.set("podgrupe", podgrupe);
			
		}
		
		return podgrupe;
	}
	
	public static void pickup(String forma) {
		if (forma.equals("grupa")) {
			Grupe.show("edit");
		}
	}
	
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class klasa = Podgrupa.class;
		Field[] fields = klasa.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(ManyToOne.class);
			if (annotation instanceof ManyToOne) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}
	
	/**
	 * Pomocna metoda za nextForm mehanizam. Nalazi sve kataloge robe i usluga u
	 * okviru jedne izabrane podgrupe.
	 * 
	 * @param idPodgrupe
	 *            ID izabrane podgrupe
	 *            
	 *            
	 */
	public static List<KatalogRobeIUsluga> findKataloziRobeIUsluga(Long idPodgrupe) {
		List<KatalogRobeIUsluga> kataloziAll = PoslovnaGodina.findAll();
		List<KatalogRobeIUsluga> katalozi = new ArrayList<>();

		for (KatalogRobeIUsluga kru : kataloziAll) {
			if (kru.podgrupa.id == idPodgrupe) {
				katalozi.add(kru);
			}
		}

		return katalozi;
	}
}
