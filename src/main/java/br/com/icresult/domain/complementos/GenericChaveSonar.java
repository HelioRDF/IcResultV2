package br.com.icresult.domain.complementos;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * -Classe POJO RelacaoProjetoSiglaGestor entity do DB via Hibernate.
 * 
 * @author helio.franca
 * @version v2.4.4
 * @since 18-10-2018
 * 
 */

@MappedSuperclass
public abstract class GenericChaveSonar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5156440927678553582L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "Sigla")
	private String sigla;

	@Column(name = "Nome_Projeto")
	private String nome_Projeto;

	@Column(name = "Chave")
	private String chave;
	
	@Column(name = "Versao")
	private String versao;
	
	@Column(name = "DataSonar")
	private Date dataSonar;

	// --------------------------------------------------


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public Date getDataSonar() {
		return dataSonar;
	}

	public void setDataSonar(Date dataSonar) {
		this.dataSonar = dataSonar;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome_Projeto() {
		return nome_Projeto;
	}

	public void setNome_Projeto(String nome_Projeto) {
		this.nome_Projeto = nome_Projeto;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}
}
