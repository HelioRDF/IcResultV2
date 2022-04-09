package br.com.icresult.domain.complementos;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * -Classe genérica para Domain.
 * 
 * @author helio.franca
 * @version v1.7
 * @since  N/A
 * 
 */

@MappedSuperclass
public class GenericDomain implements Serializable {

	private static final long serialVersionUID = 5608542373930315736L;
	
	
	/**
	 * Detalhes:
	 * 
	 * Id: Define a chave primaria 
	 * GeneratedValue: Gera uma chave primária de modo automático no DB
	 * 
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long codigo;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	@Override
	public String toString() {
		return "GenericDomain [codigo=" + codigo + ", getCodigo()=" + getCodigo() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	// Hash e equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericDomain other = (GenericDomain) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}
