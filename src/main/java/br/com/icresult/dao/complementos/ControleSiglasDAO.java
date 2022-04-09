package br.com.icresult.dao.complementos;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ControleSiglas;
import br.com.icresult.util.HibernateUtil;

/**
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */

public class ControleSiglasDAO extends GenericDAO<ControleSiglas> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 786315802961294772L;

	public String buscaGestorPorSigla(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleSiglas.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			ControleSiglas resultado = (ControleSiglas) consulta.uniqueResult(); // Utilizado para retornar um unico
																					// resultado
			System.out.println(resultado.getNivel2());
			return resultado.getNivel2();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}

	}

	public String buscaGestorNivel1PorSigla(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleSiglas.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			ControleSiglas resultado = (ControleSiglas) consulta.uniqueResult(); // Utilizado para retornar um unico
																					// resultado
			System.out.println(resultado);
			return resultado.getNivel1();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	public String buscaGestorEntregaENivel2PorSigla(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleSiglas.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			ControleSiglas resultado = (ControleSiglas) consulta.uniqueResult(); // Utilizado para retornar um unico
																					// resultado
			
			return resultado == null ? null : resultado.getNivel2();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}