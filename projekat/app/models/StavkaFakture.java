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
	public float rabat;

	@Required
	public float osnovicaZaPDV;

	@Required
	public float iznosPDVa;

	@Required
	public float ukupno;

	@ManyToOne
	public Faktura faktura;

	@ManyToOne
	public KatalogRobeIUsluga katalogRobeIUsluga;

//	@ManyToOne
//	public StopaPDVa stopaPDVa;
	
	public float stopaPDVa;

	public StavkaFakture(int kolicina, float cena, float rabat, float osnovicaZaPDV, float stopaPDVa,
			float iznosPDVa, float ukupno) {
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
