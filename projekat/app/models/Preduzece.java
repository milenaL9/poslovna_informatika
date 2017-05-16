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
public class Preduzece extends Model {

	@Required
	@MinSize(3)
	@MaxSize(1024)
	@Column(columnDefinition = "varchar(1024)")
	public String naziv;

	@Required
	@MinSize(8)
	@MaxSize(8)
	@Column(columnDefinition = "numeric(8)")
	public int pib;

	@MinSize(2)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String mesto;

	@Required
	@MinSize(3)
	@MaxSize(40)
	@Column(columnDefinition = "varchar(40)")
	public String adresa;

	@Column(columnDefinition = "numeric")
	public int telefon;

	@Required
	@MinSize(13)
	@MaxSize(13)
	@Column(columnDefinition = "numeric(13)")
	public int matciniBroj;

	@Required
	@MinSize(3)
	@MaxSize(18)
	@Column(columnDefinition = "character(18)")
	public String tekuciRacun;

	@OneToMany(mappedBy = "preduzece")
	public List<PoslovnaGodina> poslovneGodine;
	
	@OneToMany(mappedBy = "preduzece")
	public List<Grupa> grupe;

	public Preduzece(String naziv, int pib, String mesto, String adresa, int telefon, int matciniBroj,
			String tekuciRacun) {
		super();
		this.naziv = naziv;
		this.pib = pib;
		this.mesto = mesto;
		this.adresa = adresa;
		this.telefon = telefon;
		this.matciniBroj = matciniBroj;
		this.tekuciRacun = tekuciRacun;
	}

}
