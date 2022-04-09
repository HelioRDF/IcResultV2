package br.com.icresult.dao.complementos;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_Sonar;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.8
 * @since 24-08-2018
 * 
 */

public class Analise_SonarDAO extends GenericDAO<Analise_Sonar> {

	private static final long serialVersionUID = -126164611692514074L;

	/**
	 * 
	 * @return - Retorna uma lista de Analise_Sonar pelo tipo de Entrega e a Data de
	 *         Captura
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Sonar> listarPorTipoEntregaDataCaptura(String tipoEntrega, Date dataCaptura) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Sonar.class);
			consulta.add(Restrictions.eq("tipoEntrega", tipoEntrega));
			consulta.add(Restrictions.gt("dataCaptura", dataCaptura));
			List<Analise_Sonar> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * 
	 * @return - Retorna uma lista de Analise_Sonar
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Sonar> listaNotaVazia() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Sonar.class);
			consulta.addOrder(Order.desc("id"));
			List<Analise_Sonar> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}