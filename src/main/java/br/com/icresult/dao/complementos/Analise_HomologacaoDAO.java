package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_Homologacao;
import br.com.icresult.util.HibernateUtil;

public class Analise_HomologacaoDAO extends GenericDAO<Analise_Homologacao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4803439631776804388L;
	
	/**
	 * @return - Retorna uma lista de Analise_Homolocacao, aonde a nota é nula
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Homologacao> listaNotaVazia() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Homologacao.class);
			consulta.add(Restrictions.isNull("notaProjeto"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Homologacao> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}
