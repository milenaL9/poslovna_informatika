package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import models.Grupa;
import models.Podgrupa;
import models.Preduzece;
import models.VrstaPDVa;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
@Check("administrator")
public class Grupe extends Controller {
	
	public static void show(String m) {
		validation.clear();
		clearSession();
		
		
		session.put("idVrstePDVa", "null");
		session.put("idPreduzeca","null");
		
		session.put("mode","edit");
		String mode = session.get("mode");
		
		List<Grupa> grupe = checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		
		List<String> povezaneForme = getForeignKeysFields();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		
		render(grupe,preduzeca,vrstePDVa,mode,povezaneForme,nadredjeneForme);

		
	}
	
	public static void changeMode(String mode){
		if (mode == null || mode.equals("")) {
			mode = "edit";
		}
		session.put("mode", mode);
		
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		List<Grupa> grupe = fillList();
		
		List<String> povezaneForme = getForeignKeysFields();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		renderTemplate("Grupe/show.html",grupe,vrstePDVa,preduzeca,mode,povezaneForme,nadredjeneForme);
	}
	
	public static void create(Grupa grupa,Long vrstaPDVa,Long preduzece){
		validation.clear();
		clearSession();

		validation.valid(grupa);
		
		session.put("mode", "add");
		String mode = session.get("mode");
		
		System.out.println("STIGLO OVO: ++: " +preduzece + "i ovo ++:" +  vrstaPDVa + "i ovov +++" + grupa.nazivGrupe );
		List<Grupa> grupe = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		
		List<String> povezaneForme = getForeignKeysFields();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if (!validation.hasErrors()) {
			grupe = Grupa.findAll();
			
			VrstaPDVa findVrstaPDVa = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idVrstaPDVa"));
				findVrstaPDVa = VrstaPDVa.findById(id);
			} else {
				findVrstaPDVa = VrstaPDVa.findById(vrstaPDVa);
			}
			
			
			Preduzece findPreduzece = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}
			
			
			grupa.vrstaPDVa = findVrstaPDVa;
			grupa.preduzece = findPreduzece;
			
			grupa.save();
			grupe.add(grupa);
			Cache.set("grupe", grupe);
			
			Long idd = grupa.id;
			
			String idPreduzeca = (String)findPreduzece.naziv;
			Long idVrsta = findVrstaPDVa.id;
			
			grupe.clear();
			grupe = fillList();

			validation.clear();
			
			renderTemplate("Grupe/show.html",grupe,vrstePDVa,preduzeca,idd,mode,idPreduzeca,idVrsta,povezaneForme,nadredjeneForme);
			
		}else{
			validation.keep();

			grupe = fillList();

			session.put("naziv", null);
			
			renderTemplate("Grupe/show.html",grupe,vrstePDVa,preduzeca,mode,povezaneForme,nadredjeneForme);
		}
		
		
	}
	
	public static void filter(){
		
	}
	
	public static void edit(Grupa grupa,Long vrstaPDVa,Long preduzece){
		validation.clear();
		clearSession();

		validation.valid(grupa);
		
		session.put("mode", "edit");
		String mode = session.get("mode");
		
		List<Grupa> grupe = null;
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		
		List<String> povezaneForme = getForeignKeysFields();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		if (!validation.hasErrors()) {
			grupe = Grupa.findAll();
			
			VrstaPDVa findVrstaPDVa = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idVrstaPDVa"));
				findVrstaPDVa = VrstaPDVa.findById(id);
			} else {
				findVrstaPDVa = VrstaPDVa.findById(vrstaPDVa);
			}
			
			
			Preduzece findPreduzece = null;
			if (vrstaPDVa == null) {
				Long id = Long.parseLong(session.get("idPreduzeca"));
				findPreduzece = Preduzece.findById(id);
			} else {
				findPreduzece = Preduzece.findById(preduzece);
			}
			
			grupa.vrstaPDVa = findVrstaPDVa;
			grupa.preduzece = findPreduzece;
			
			for(Grupa tmp : grupe){
				if(tmp.id==grupa.id){
					tmp.nazivGrupe = grupa.nazivGrupe;
					tmp.vrstaPDVa = grupa.vrstaPDVa;
					tmp.preduzece = grupa.preduzece;
					tmp.save();
				}
			}
			
			Cache.set("grupe", grupe);

			grupe.clear();
			grupe = fillList();

			validation.clear();
			
		} else {
			validation.keep();
			
			grupe = fillList();
			
			session.put("idGrupe", null);
			session.put("naziv", null);
		}
		
		renderTemplate("Grupe/show.html",grupe,vrstePDVa,preduzeca,mode,povezaneForme,nadredjeneForme);
		
	}
	
	public void delete(Long id){
		String mode = session.get("mode");
		
		List<Grupa> grupe = checkCache();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		
		List<String> povezaneForme = getForeignKeysFields();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		Grupa grupa = Grupa.findById(id);
		Long idd = null;
		
		for(int i = 0; i < grupe.size(); i++){
			if(grupe.get(i).id==id){
				Grupa prethodni = grupe.get(i-1);
				idd = prethodni.id;
			}
		}
		
		grupa.delete();
		
		Cache.set("grupa", grupa);
		
		grupe.clear();
		grupe = fillList();
		
		renderTemplate("Grupe/show.html",grupe,vrstePDVa,preduzeca,idd,mode,povezaneForme,nadredjeneForme);
		
	}
	
	public static List<Grupa> checkCache(){
		
		List<Grupa> grupe = (List<Grupa>) Cache.get("grupe");
		
		if((grupe==null) || (grupe.size()==0) ){
			grupe = Grupa.findAll();
			Cache.set("grupe", grupe);
			
		}
		
		return grupe;
	}
	
	public static void refresh(){
		List<Grupa> grupe = fillList();
		List<Preduzece> preduzeca = Preduzeca.checkCache();
		List<VrstaPDVa> vrstePDVa = VrstePDVa.checkCache();
		
		List<String> povezaneForme = getForeignKeysFields();
		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();
		
		String mode = session.get("mode");
		
		renderTemplate("Grupe/show.html",grupe,vrstePDVa,preduzeca,mode,povezaneForme,nadredjeneForme);
		
	}
	
	public static boolean clearSession(){
		session.put("idGrupe", null);
		session.put("naziv", null);
		
		return true;
	}
	
	public static List<Grupa> fillList(){
		List<Grupa> grupe = (List<Grupa>) Cache.get("grupe");
		
		if((grupe==null) || (grupe.size()==0) ){
			grupe = Grupa.findAll();
			Cache.set("grupe", grupe);
			
		}
		
		return grupe;
	}
	
	public static List<Podgrupa> findPodgrupe(Long id){
		List<Podgrupa> PodgrupeAll = Podgrupa.findAll();
		List<Podgrupa> podgrupe = new ArrayList<>();
		
		for(Podgrupa pg: PodgrupeAll){
			if(pg.id==id){
				podgrupe.add(pg);
			}
		}
		
		return podgrupe;
	}
	
	public static List<String> getForeignKeysFields(){
		Class vrstaPDVaClass = Grupa.class;
		Field[] fields = vrstaPDVaClass.getFields();

		List<String> povezaneForme = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			Annotation annotation = fields[i].getAnnotation(OneToMany.class);
			if (annotation instanceof OneToMany) {
				povezaneForme.add(fields[i].getName());
			}
		}

		return povezaneForme;
	}
	
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class klasa = Grupa.class;
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
	
	public static void nextForm(Long id, String forma){
		session.put("idGrupe", id);
		System.out.println("OVO JE ");
		System.out.println("ID JE: "+id+"dd");
		if(forma.equals("podgrupe")){
			List<Grupa> grupe = checkCache();
			List<Podgrupa> podgrupe = findPodgrupe(id);
			
			List<String> nadredjeneForme = Podgrupe.getForeignKeysFieldsManyToOne();
			
			renderTemplate("Podgrupe/show.html",podgrupe,grupe,nadredjeneForme);
			
		}
	}
}
