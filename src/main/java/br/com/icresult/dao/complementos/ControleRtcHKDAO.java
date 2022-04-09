package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ControleRtcHK;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ]
 * 
 * -Classe DAO ControleRtcHKDAO Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.8
 * @since 12-07-2018
 * 
 */

public class ControleRtcHKDAO extends GenericDAO<ControleRtcHK> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2275059803497130333L;

	/**
	 * Busca o commit mais recente por sigla, nome do sistema...
	 * 
	 * @param sigla
	 *            - String
	 * @return - Retorna uma String com a Data
	 */
	public String buscarDataCommit(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcHK.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleRtcHK resultado = (ControleRtcHK) consulta.uniqueResult(); // Utilizado para retornar um unico
			// resultado

			System.out.println("-- Achou:" + resultado.getSigla());
			return resultado.getDataCommit().toString();
		} catch (RuntimeException erro) {
			System.out.println("\n --- XXXX --- Objeto não encontrado: " + sigla);
			System.out.println(erro + "\n ---- XXXX ---");
			return "N/A";

		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca o commit mais recente por sigla, nome do sistema...
	 * 
	 * @param sigla
	 *            - String
	 * @return - Retorna uma String com tipo Legado/Novo
	 */

	public String buscarAlteracaoCommit(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcHK.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleRtcHK resultado = (ControleRtcHK) consulta.uniqueResult(); // Utilizado para retornar um unico
			String alteracao = "N/A";

			if (resultado.isAlteracao()) {
				alteracao = "Novo";
			} else {
				alteracao = "Legado";
			}

			System.out.println("-- Achou:" + resultado.getSigla());
			return alteracao;
		} catch (RuntimeException erro) {
			System.out.println("\n --- XXXX --- Objeto não encontrado: " + sigla);
			System.out.println(erro + "\n ---- XXXX ---");
			return "N/A";

		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca ordenada por alteração
	 * 
	 * @return - Retorna uma lista de ControleRtcHK
	 */

	@SuppressWarnings("unchecked")
	public List<ControleRtcHK> listarOrdenandoAlteracao() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcHK.class);
			consulta.addOrder(Order.desc("alteracao"));
			List<ControleRtcHK> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
}