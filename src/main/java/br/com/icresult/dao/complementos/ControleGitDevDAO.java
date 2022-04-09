package br.com.icresult.dao.complementos;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ControleGitDev;
import br.com.icresult.util.HibernateUtil;

/**
 * [ Detalhes... ]
 * 
 * -Classe DAO ControleGIT Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.8
 * @since 13-07-2018
 * 
 */

public class ControleGitDevDAO extends GenericDAO<ControleGitDev> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3955388182778766107L;

	/**
	 * Busca ordenada por alteração
	 * 
	 * @return - Retorna uma lista de ControleGitDev
	 */
	@SuppressWarnings("unchecked")
	public List<ControleGitDev> listarOrdenandoAlteracao() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitDev.class);
			consulta.addOrder(Order.desc("alteracao"));
			List<ControleGitDev> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca o commit mais recente por sigla, nome do sistema...
	 * 
	 * @param sigla - String
	 * @return - Retorna uma String com a Data
	 */
	public String buscarDataCommit(String nomeSistema) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitDev.class);
			consulta.add(Restrictions.eq("nomeSistema", nomeSistema));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleGitDev resultado = (ControleGitDev) consulta.uniqueResult(); // Utilizado para retornar um unico
																					// resultado
			System.out.println("-- Achou:" + resultado.getSigla());
			return resultado.getDataCommit().toString();
		} catch (RuntimeException erro) {
			System.out.println("\nObjeto não encontrado como do GitLab: " + nomeSistema + "\n");
			return "N/A";

		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca o commit mais recente por sigla, nome do sistema...
	 * 
	 * @param sigla - String
	 * @return - Retorna uma String com tipo Legado/Novo
	 */
	public String buscarAlteracaoCommit(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitDev.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleGitDev resultado = (ControleGitDev) consulta.uniqueResult(); // Utilizado para retornar um unico
			String alteracao = "N/A";

			if (resultado.isAlteracao()) {
				alteracao = "Novo";
			} else {
				alteracao = "Legado";
			}

			System.out.println("-- Achou:" + resultado.getSigla());
			return alteracao;
		} catch (RuntimeException erro) {
			System.out.println("\nObjeto não encontrado: " + sigla);
			System.out.println(erro + "\n");
			return "N/A";

		} finally {
			sessao.close();
		}
	}

	/***
	 * 
	 * Preenche o campo selecionado com o icone correto
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void preencherCampoSelecionado() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria update = sessao.createCriteria(ControleGitDev.class);
			update.add(Restrictions.isNull("selecionado"));
			List<ControleGitDev> listaControle = update.list();
			for (ControleGitDev controle : listaControle) {
				controle.setSelecionado("ui-icon-blank");
				editar(controle);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	public ControleGitDev buscaModuloPorNome(String nomeProjeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitDev.class);
			consulta.add(Restrictions.eq("pacoteChaveNomePainelSonar", nomeProjeto));
			return (ControleGitDev) consulta.uniqueResult();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<ControleGitDev> listarModulosNaoLiberados() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitDev.class);
			List<String> parametros = Arrays.asList("Bloqueado","Alerta");
			consulta.add(Restrictions.in("resultadoUltimaInspecao",parametros));
			return (List<ControleGitDev>) consulta.list();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}