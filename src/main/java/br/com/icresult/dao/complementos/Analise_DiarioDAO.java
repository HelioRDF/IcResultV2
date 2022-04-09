package br.com.icresult.dao.complementos;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_Dev_Diario;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * @author helio.franca
 *
 */
public class Analise_DiarioDAO extends GenericDAO<Analise_Dev_Diario> {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @return - Retorna uma lista de objetos filtrado por data
	 * @param data - Objeto do tipo data
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Diario> listarPorData(Date data) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Diario.class);
			consulta.add(Restrictions.ge("dataCaptura", data));
			consulta.addOrder(Order.desc("id"));
			consulta.addOrder(Order.desc("sigla"));
			consulta.addOrder(Order.desc("dataCaptura"));
			List<Analise_Dev_Diario> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * 
	 * @param analise - Objeto do tipo Analise_Dev_Diario que será verificado, pois
	 *                se estiver nulo, não poder ser slavo no banco de dados
	 *
	 */
	public void salvarAnalise(Analise_Dev_Diario analise) {

		try {
			if (analise.getDataSonar() != null) {
				System.out.println("Salvando analise do modulo: " + analise.getNomeProjeto());
				salvar(analise);
			} else {
				System.out.println("Analise não foi salva porque a captura é nula");
			}
		} catch (RuntimeException erro) {
			throw erro;
		}
	}

	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Diario, aonde o resultado é nulo
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Diario> listaResultadoVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Diario.class);
			consulta.add(Restrictions.isNull("resultado"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Diario> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * Limpa a seleção de analises inseridas
	 */
	@SuppressWarnings("unchecked")
	public void limparSelecaoNovasAnalises() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Diario.class);
			consulta.add(Restrictions.isNull("selecionado"));
			for (Analise_Dev_Diario analise : (List<Analise_Dev_Diario>) consulta.list()) {
				analise.setSelecionado("ui-icon-blank");
				editar(analise);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Diario> listarAnalises() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Diario.class);
			List<Analise_Dev_Diario> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public Analise_Dev_Diario ultimaAnaliseInseridaPorNome(String projeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Diario.class);
			consulta.add(Restrictions.eq("nomeProjeto", projeto));
			consulta.addOrder(Order.desc("dataCaptura"));
			List<Analise_Dev_Diario> listaExecucoes = (List<Analise_Dev_Diario>) consulta.list();
			if (!listaExecucoes.isEmpty())
				return listaExecucoes.get(0);
			return null;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}
