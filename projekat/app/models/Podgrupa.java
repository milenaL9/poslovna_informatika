package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Podgrupa extends Model {

	@Required
	@Column(columnDefinition = "varchar(40)")
	public String nazivPodgrupe;

	@ManyToOne
	public Grupa grupa;

	public Podgrupa(String nazivPodgrupe) {
		super();
		this.nazivPodgrupe = nazivPodgrupe;
	}

}
