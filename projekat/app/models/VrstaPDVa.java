package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class VrstaPDVa extends Model {

	@Required
	@Column(columnDefinition="varchar(40)")
	public String nazivVrstePDva;


	@OneToMany(mappedBy = "vrstaPDVa")
	public List<StopaPDVa> stopePDVa;
	
	
	@OneToMany(mappedBy = "vrstaPDVa")
	public List<Grupa> grupe;
	
	
	public VrstaPDVa(String nazivVrstePDva) {
		super();
		this.nazivVrstePDva = nazivVrstePDva;
	}
	
	
	
	
}
