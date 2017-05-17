package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class StavkaFakture extends Model {

	@Required
	public int kolicina;
	
	@Required
	public float cena;
	
	@Required
	public int rabat;
	
	@Required
	public int osnovicaZaPDV;
	
	@Required
	public StopaPDVa stopaPDVa;
	
	@Required
	public int iznosPDVa;
	
	@Required
	public int ukupno;
	
	
	@ManyToOne
	public Faktura faktura;
	
	@ManyToOne
	public KatalogRobeIUsluga katalogRobeIUsluga;

	public StavkaFakture(int kolicina, float cena, int rabat, int osnovicaZaPDV, StopaPDVa stopaPDVa, int iznosPDVa,
			int ukupno) {
		super();
		this.kolicina = kolicina;
		this.cena = cena;
		this.rabat = rabat;
		this.osnovicaZaPDV = osnovicaZaPDV;
		this.stopaPDVa = stopaPDVa;
		this.iznosPDVa = iznosPDVa;
		this.ukupno = ukupno;
	}
	
	
	
	
}
