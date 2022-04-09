package br.com.icresult.domain.complementos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Nao_Inspecionadas")
public class NaoInspecionadas {

	@GeneratedValue(strategy = GenerationType.TABLE)
	@Id
	private Long ID;

	@Column(name = "sigla")
	private String sigla;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Long getID() {
		return ID;
	}

	public void setId(Long ID) {
		this.ID = ID;
	}

	@Override
	public String toString() {
		return "NaoInspecionadas [ID=" + ID + ", sigla=" + sigla + "]";
	}

}
