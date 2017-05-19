package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;


/*
 * 
 * MALA NAPOMENA:
 * Dodati vezu 1:1 sa klasom Naruzba
 * 
 * 
 * */

@Entity
public class Faktura extends Model {


	@Temporal(TemporalType.DATE)
	public Date datumFakture;
	
	@Required
	public int brojFakture;
	

	@Temporal(TemporalType.DATE)
	public Date datumValute;
	
	@Required
	public int ukupnoOsnovica;
	
	@Required
	public float ukupnoPDV;
	
	@Required
	public float ukupnoZaPlacanje;
	
	
	@OneToMany(mappedBy = "faktura")
	public List<StavkaFakture> stavkeFakture;
	
	@ManyToOne
	public PoslovniPartner poslovniPartner;
	
	@ManyToOne
	public PoslovnaGodina poslovnaGodina;
	
	@ManyToOne 
	public Preduzece preduzece;
	


	public Faktura(Date datumFakture, int brojFakture, Date datumValute, int ukupnoOsnovica, float ukupnoPDV,
			float ukupnoZaPlacanje) {
		super();
		this.datumFakture = datumFakture;
		this.brojFakture = brojFakture;
		this.datumValute = datumValute;
		this.ukupnoOsnovica = ukupnoOsnovica;
		this.ukupnoPDV = ukupnoPDV;
		this.ukupnoZaPlacanje = ukupnoZaPlacanje;
	}
	
	
	
	
	
	
}
