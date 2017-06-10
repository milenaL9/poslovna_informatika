package controllers;

import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;


import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import models.Narudzba;
import models.StavkaNarudzbe;
import play.cache.Cache;
import play.mvc.Controller;

public class Narudzbe extends Controller {
	
	

	public static void show() {
		validation.clear();
		
		session.put("idNarudzbe", "null"); // 

		session.put("idPoslovnogPartnera", "null"); 
		session.put("idPoslovneGodine", "null");
		session.put("idPreduzeca", "null"); 
		
		session.put("mode", "edit");
		String mode = session.get("mode");

		if (mode == null || mode.equals("")) {
			mode = "edit";
		}

	
		
		session.put("mode", mode);
		
		

		List<String> nadredjeneForme = getForeignKeysFieldsManyToOne();

		
		
		
	}
	
	public static List<Narudzba> checkCache(){
		List<Narudzba> narudzbe = (List<Narudzba>) Cache.get("narudzbe");

		if ((narudzbe == null) || (narudzbe.size() == 0)) {
			narudzbe = Narudzba.findAll();
			Cache.set("narudzbe", narudzbe);
		}

		return narudzbe;
	}
	
	public static List<String> getForeignKeysFieldsManyToOne() {
		Class faktureClass = Narudzba.class;
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
	
}
