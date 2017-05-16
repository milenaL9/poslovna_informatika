package models;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Cenovnik extends Model {

	@Required
	@Column(columnDefinition = "date")
	public Date datumVazenja;

	@OneToMany(mappedBy = "cenovnik")
	public List<StavkaCenovnika> stavkeCenovnika;

	public Cenovnik(Date datumVazenja) {
		super();
		this.datumVazenja = datumVazenja;
	}

}
