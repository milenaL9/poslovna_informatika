package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
public class Cenovnik extends Model {

	@Required
	@Column(columnDefinition = "varchar(40)")
	public String naziv;

	@Required
	@Unique
	@Column(columnDefinition = "varchar(10)")
	public String datumVazenja;

	@OneToMany(mappedBy = "cenovnik")
	public List<StavkaCenovnika> stavkeCenovnika;

	public Cenovnik(String naziv, String datumVazenja) {
		super();
		this.naziv = naziv;
		this.datumVazenja = datumVazenja;
	}

}
