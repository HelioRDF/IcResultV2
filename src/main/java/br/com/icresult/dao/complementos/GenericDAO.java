package br.com.icresult.dao.complementos;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.domain.complementos.LogApp;
import br.com.icresult.util.HibernateUtil;

/**
 * [ Detalhes... ] -Classe DAO Generic. API Reflection
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */

public class GenericDAO<Entidade> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7039154413917366489L;
	private Class<Entidade> classe;
	private String caminhoAppLog = new LogApp().getClass().getName();
	private static Logger log = LoggerFactory.getLogger(GenericDAO.class);

	// Construtor
	@SuppressWarnings("unchecked")
	public GenericDAO() {
		// API Reflection
		this.classe = (Class<Entidade>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];

	}
	
	private void salvarLog(Entidade entidade, String comecoMensagem) {
		if(!entidade.getClass().getName().equals(caminhoAppLog)) {
			log.info(comecoMensagem+entidade);
		}
	}
	/**
	 * Salva um objeto
	 * 
	 * @param entidade - Entidade
	 */
	public void salvar(Entidade entidade) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		Transaction transacao = null;

		try {
			transacao = sessao.beginTransaction();
			sessao.save(entidade);
			transacao.commit();
		} catch (RuntimeException erro) {
			System.out.println(erro.getCause());
			if (transacao != null) {
				transacao.rollback();
			}
			throw erro;
		} finally {
			sessao.close();
		}
		salvarLog(entidade, "Criação de ");
	}

	/**
	 * 
	 * @return - Retorna uma lista de objetos
	 */
	@SuppressWarnings("unchecked")
	public List<Entidade> listar() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(classe);
			List<Entidade> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
		
	}

	/**
	 * 
	 * @param campoOrdecao - String
	 * @return - Retorna uma lista de objetos ordenados por asc
	 */
	@SuppressWarnings("unchecked")
	public List<Entidade> listar(String campoOrdecao) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(classe);
			consulta.addOrder(Order.asc(campoOrdecao));
			List<Entidade> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
	
	/**
	 * 
	 * @param campoOrdecao - String
	 * @return - Retorna uma lista de objetos ordenados por desc
	 */
	@SuppressWarnings("unchecked")
	public List<Entidade> listarDesc(String campoOrdecao) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(classe);
			consulta.addOrder(Order.desc(campoOrdecao));
			List<Entidade> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * 
	 * @param codigo - long
	 * @return - Retorna um objeto filtrado por código
	 */
	@SuppressWarnings("unchecked")
	public Entidade buscar(Long codigo) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(classe);
			consulta.add(Restrictions.idEq(codigo)); // Realiza uma consulta baseada no ID.
			Entidade resultado = (Entidade) consulta.uniqueResult(); // Utilizado para retornar um unico resultado
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * Exclui o objeto passado no parâmetro
	 * 
	 * @param entidade Entidade
	 */
	public void excluir(Entidade entidade) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		Transaction transacao = null;

		try {
			transacao = sessao.beginTransaction();
			sessao.delete(entidade);
			transacao.commit();

		} catch (RuntimeException erro) {

			if (transacao != null) {
				transacao.rollback();
			}
			throw erro;
		} finally {
			sessao.close();
		}
		
		salvarLog(entidade, "Exclusao de ");

	}

	/**
	 * Edita o objeto do parâmentro
	 * 
	 * @param entidade - Entidade
	 */
	public  void editar(Entidade entidade) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		Transaction transacao = null;

		try {
			transacao = sessao.beginTransaction();
			sessao.update(entidade);
			transacao.commit();

		} catch (RuntimeException erro) {

			if (transacao != null) {
				transacao.rollback();
			}
			throw erro;
		} finally {
			sessao.close();
		}
		salvarLog(entidade, "Edição de ");
	}

	/**
	 * Salva ou edita o objeto
	 * @param entidade - Entidade
	 */
	public void merge(Entidade entidade) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		Transaction transacao = null;

		try {
			transacao = sessao.beginTransaction();
			sessao.merge(entidade);
			transacao.commit();

		} catch (RuntimeException erro) {

			if (transacao != null) {
				transacao.rollback();
			}
			throw erro;
		} finally {
			sessao.close();
		}
		salvarLog(entidade, "Merge de ");
	}

}
