package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class StopaPDVa extends Model {



	@Temporal(TemporalType.DATE)
	// @DateTimeFormat(pattern="yyyy-MM-dd")
//	@Required
	public Date datumKreiranja;

	
	public int procenatPDVa;
	
	@ManyToOne
	public VrstaPDVa vrstaPDVa;

	public StopaPDVa(int procenatPDVa, Date datumKreiranja) {
		super();
		this.procenatPDVa = procenatPDVa;
		this.datumKreiranja = datumKreiranja;
	}

}
