package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Entrega;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 11-06-2019
 * 
 */

public class EntregaDAO extends GenericDAO<Entrega> {

	private static final long serialVersionUID = -1237735069072150482L;

	public void salvaEntregaComValidacao(Entrega novoTipoEntrega) {
		if (validaSeEntregaJaExiste(novoTipoEntrega).isEmpty()) {
			String tipoEntrega = novoTipoEntrega.getTipoEntrega();
			Character primeiraLetra = tipoEntrega.charAt(0);
			StringBuilder nomeDoTipoEntreComPrimeiraLetraMaiscula = new StringBuilder();
			nomeDoTipoEntreComPrimeiraLetraMaiscula.append(Character.toUpperCase(primeiraLetra));
			nomeDoTipoEntreComPrimeiraLetraMaiscula
					.append(tipoEntrega.length() > 1 ? tipoEntrega.substring(1).toLowerCase() : "");
			System.out.println(nomeDoTipoEntreComPrimeiraLetraMaiscula.toString());
			novoTipoEntrega.setTipoEntrega(nomeDoTipoEntreComPrimeiraLetraMaiscula.toString());
			salvar(novoTipoEntrega);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Entrega> validaSeEntregaJaExiste(Entrega novoTipoEntrega) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Entrega.class);
			consulta.add(Restrictions.eq("tipoEntrega", novoTipoEntrega.getTipoEntrega()));
			List<Entrega> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			System.out.println(erro.getCause());
			throw erro;
		} finally {
			sessao.close();
		}
	}

}