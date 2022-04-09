package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_HK;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */

public class AnaliseHKDAO extends GenericDAO<Analise_HK> {

	private static final long serialVersionUID = 5640293968135258957L;

	// ---------------------------------------------------------------------------------
	/**
	 * @return - Retorna uma lista de Analise_Dev_Mensal, aonde a nota é nula
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_HK> listaNotaVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			consulta.add(Restrictions.isNull("notaProjeto"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_HK> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// ---------------------------------------------------------------------------------
	/**
	 * @return - Retorna uma lista de Analise_HK
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_HK> listaResultadoVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			consulta.add(Restrictions.isNull("resultado"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_HK> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// ---------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_HK
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_HK> listaTipoVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			consulta.add(Restrictions.isNull("codigoAlterado"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_HK> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// ---------------------------------------------------------------------------------
	/**
	 * 
	 * @param codigo
	 *            - int
	 * @param sigla
	 *            - String
	 * @param projeto
	 *            - String
	 * @return - Retorna uma objeto Analise_HK
	 */
	public Analise_HK buscarAnterior(int codigo, String sigla, String projeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			// consulta.add(Restrictions.idEq(codigo)); // Realiza uma consulta baseada no
			// ID.
			consulta.add(Restrictions.lt("id", codigo));
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.add(Restrictions.eq("nomeProjeto", projeto));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("id"));
			Analise_HK resultado = (Analise_HK) consulta.uniqueResult(); // Utilizado para retornar um unico
																					// resultado
			return resultado;
		} catch (RuntimeException erro) {

			throw erro;
		} finally {
			sessao.close();
		}
	}

	// ---------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista Analise_HK com dataCommit = Null
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_HK> listarParaDataCommit() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			consulta.add(Restrictions.isNull("dataCommit"));
			List<Analise_HK> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// ---------------------------------------------------------------------------------
	/**
	 * 
	 * @param codigo
	 *            - int
	 * @param sigla
	 *            - string
	 * @param projeto
	 *            - string
	 * @return - Retorna a quantidade na lista
	 */
	public int qtdList(int codigo, String sigla, String projeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			consulta.add(Restrictions.lt("id", codigo));
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.add(Restrictions.eq("nomeProjeto", projeto));
			@SuppressWarnings("unchecked")
			List<Analise_HK> resultado = consulta.list();
			return resultado.size();
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// ---------------------------------------------------------------------------------
	/**
	 * 
	 * @param codigo
	 *            - Int
	 * @return - Retorna um objeto filtrado por código
	 */

	public Analise_HK buscarPorID(int codigo) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			consulta.add(Restrictions.idEq(codigo)); // Realiza uma consulta baseada no ID.
			Analise_HK resultado = (Analise_HK) consulta.uniqueResult(); // Utilizado para retornar um unico
																					// resultado
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	//-----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_HK
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_HK> listaDebitoTecnico() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_HK.class);
			consulta.add(Restrictions.isNull("debitoTecnicoMinutos"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_HK> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}