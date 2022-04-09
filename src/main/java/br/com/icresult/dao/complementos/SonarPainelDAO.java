package br.com.icresult.dao.complementos;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.SonarPainel;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 13-06-2019
 * 
 */

public class SonarPainelDAO extends GenericDAO<SonarPainel> {

	private static final long serialVersionUID = -126164611692514074L;

	/**
	 * 
	 * @return - Retorna se um SonarPainel já existe no banco de dados
	 */
	public boolean existe(SonarPainel painel) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(SonarPainel.class);
			consulta.add(Restrictions.eq("chave", painel.getChave()));
			consulta.add(Restrictions.eq("nomeProjeto", painel.getNomeProjeto()));
			SonarPainel resultado = (SonarPainel) consulta.uniqueResult();
			return resultado != null;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}

	}

}