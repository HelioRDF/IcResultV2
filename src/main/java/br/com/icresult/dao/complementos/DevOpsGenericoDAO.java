package br.com.icresult.dao.complementos;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.DevOpsGenerico;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author andre.graca
 * @version v1.0
 * @since 19-06-2019
 * 
 */

public class DevOpsGenericoDAO extends GenericDAO<DevOpsGenerico> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1235518855863417697L;

	public DevOpsGenerico buscar(String nomePainel) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(DevOpsGenerico.class);
			consulta.add(Restrictions.eq("nomeProjeto",nomePainel));
			return (DevOpsGenerico)consulta.uniqueResult();
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
}
