package controllers;

import java.util.ArrayList;
import java.util.List;

import models.KatalogRobeIUsluga;
import models.Podgrupa;
import models.PoslovnaGodina;
import play.cache.Cache;
import play.mvc.Controller;

public class Podgrupe extends Controller {

	/**
	 * Pomocna metoda za proveru da li su zeljeni podaci (koji treba da budu
	 * dostupni kroz vise zahteva) i dalje na Cache-u.
	 */
	public static List<Podgrupa> checkCache() {
		List<Podgrupa> podgrupe = (List<Podgrupa>) Cache.get("podgrupe");

		if (podgrupe == null) {
			podgrupe = Podgrupa.findAll();
			Cache.set("podgrupe", podgrupe);
		}

		return podgrupe;
	}

	/**
	 * Pomocna metoda za nextForm mehanizam. Nalazi sve kataloge robe i usluga u
	 * okviru jedne izabrane podgrupe.
	 * 
	 * @param idPodgrupe
	 *            ID izabrane podgrupe
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
