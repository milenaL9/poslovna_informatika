package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class StavkaNarudzbe extends Model {
	
	@Required
	public int kolicina;
	
	@Required
	public float cena;
	

	@Required
	public int osnovicaZaPDV;
	
	@Required
	public float stopaPDVa;
	
	@Required
	public int iznosPDVa;
	
	@Required
	public int ukupno;
	
	@ManyToOne
	public KatalogRobeIUsluga katalogRobeIUsluga;
	
	@ManyToOne
	public Narudzba narudzba;
	
	
	
	
	
	public StavkaNarudzbe(int kolicina, float cena, int rabat, int osnovicaZaPDV, float stopaPDVa, int iznosPDVa,
			int ukupno){
		
		super();
		this.kolicina = kolicina;
		this.cena = cena;
		
		this.osnovicaZaPDV = osnovicaZaPDV;
		this.stopaPDVa = stopaPDVa;
		this.iznosPDVa = iznosPDVa;
		this.ukupno = ukupno;
		
	}
	
	
	
	

}
