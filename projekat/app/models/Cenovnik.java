package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Cenovnik extends Model {

	@Required
	@Column(columnDefinition = "varchar(40)")
	public String naziv;

	@Required
	@Column(columnDefinition = "date")
	public Date datumVazenja;

	@OneToMany(mappedBy = "cenovnik")
	public List<StavkaCenovnika> stavkeCenovnika;

	public Cenovnik(String naziv, Date datumVazenja) {
		super();
		this.naziv = naziv;
		this.datumVazenja = datumVazenja;
	}

}
