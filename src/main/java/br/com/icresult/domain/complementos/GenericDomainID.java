package br.com.icresult.domain.complementos;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * -Classe genérica para Domain.
 * 
 * @author andre.graca
 * @version 2.5.1
 * @since N/A
 * 
 */

@MappedSuperclass
public class GenericDomainID implements Serializable {

	private static final long serialVersionUID = 5608542373930315736L;

	/**
	 * Detalhes:
	 * 
	 * Id: Define a chave primaria GeneratedValue: Gera uma chave primária de modo
	 * automático no DB
	 * 
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
