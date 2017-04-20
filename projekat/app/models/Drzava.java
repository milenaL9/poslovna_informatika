package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Drzava extends Model {
	
	@Required
	@MinSize(1)
	@MaxSize(3)
	@Column(columnDefinition="character(3)")
	public String oznaka;
	
	@Required
	@MinSize(3)
	@MaxSize(40)
	@Column(columnDefinition="varchar(40)")
	public String naziv;

	// mappedBy - da ne bi kreirao medjutabelu Drzava_NaseljenoMesto
	@OneToMany(mappedBy = "drzava")
	public List<NaseljenoMesto> naseljenaMesta;

	public Drzava(String oznaka, String naziv) {
		super();
		this.oznaka = oznaka;
		this.naziv = naziv;
	}
}
