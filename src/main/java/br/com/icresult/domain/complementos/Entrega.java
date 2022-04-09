package br.com.icresult.domain.complementos;

import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "Entrega")
public class Entrega extends GenericDomainID {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Entrega() {
	}

	@Column(name = "tipo_entrega")
	private String tipoEntrega;

	// toString / Hash / Equal
	// --------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipoEntrega == null) ? 0 : tipoEntrega.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return tipoEntrega;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entrega other = (Entrega) obj;
		if (tipoEntrega == null) {
			if (other.tipoEntrega != null)
				return false;
		} else if (!tipoEntrega.equals(other.tipoEntrega))
			return false;
		return true;
	}

	// Get e Set
	// --------------------------------------------------

	public String getTipoEntrega() {
		return tipoEntrega;
	}

	public void setTipoEntrega(String tipoEntrega) {
		this.tipoEntrega = tipoEntrega;
	}

	public static Comparator<Entrega> getComparatorPorId() {
		return new Comparator<Entrega>() {
			@Override
			public int compare(Entrega o1, Entrega o2) {
				return o1.getId().compareTo(o2.getId());
			}
		};
	}

	// --------------------------------------------------

}
