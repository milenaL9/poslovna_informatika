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
	public double osnovicaZaPDV;
	
	@Required
	public StopaPDVa stopaPDVa;
	
	@Required
	public double iznosPDVa;
	
	@Required
	public double ukupno;
	
	
	@ManyToOne
	public Faktura faktura;
	
	@ManyToOne
	public KatalogRobeIUsluga katalogRobeIUsluga;

	public StavkaFakture(int kolicina, float cena, int rabat, double osnovicaZaPDV, StopaPDVa stopaPDVa, double iznosPDVa,
			double ukupno) {
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
