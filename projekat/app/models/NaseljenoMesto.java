package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class NaseljenoMesto extends Model {

	@Required
	@MaxSize(3)
	@Column(columnDefinition = "character(3)")
	public String oznaka;

	@Required
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String naziv;

	@Required
	@MaxSize(5)
	@Column(columnDefinition = "character(5)")
	public String postanskiBroj;

	// ne mora u konstruktor
	@ManyToOne
	public Drzava drzava;

	public NaseljenoMesto(String oznaka, String naziv, String postanskiBroj, Drzava drzava) {
		super();
		this.oznaka = oznaka;
		this.naziv = naziv;
		this.postanskiBroj = postanskiBroj;
		this.drzava = drzava;
	}

}
