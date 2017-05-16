package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PoslovnaGodina extends Model {

	@Required
	@Column(columnDefinition = "numeric")
	public int brojGodine;

	@Required
	@MaxSize(1)
	@Column(columnDefinition = "character(1)")
	public boolean aktivna;

	@ManyToOne
	public Preduzece preduzece;

	public PoslovnaGodina(int brojGodine, boolean aktivna) {
		super();
		this.brojGodine = brojGodine;
		this.aktivna = aktivna;
	}

}
