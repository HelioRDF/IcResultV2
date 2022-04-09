package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import br.com.icresult.domain.complementos.Anotacoes;
import br.com.icresult.util.HibernateUtil;

public class AnotacoesDAO extends GenericDAO<Anotacoes> {

	private static final long serialVersionUID = 8310774627865134113L;
	
	/**
	 * lista todos as Anotacoes da mais recente Anotação para a mais velha
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Anotacoes> listar() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Anotacoes.class);
			consulta.addOrder(Order.desc("dataInclusao"));
			List<Anotacoes> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}
