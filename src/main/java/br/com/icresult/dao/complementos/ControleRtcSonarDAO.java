package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ControleRtcSonar;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ]
 * 
 * -Classe DAO ControleRtcDevDAO Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.8
 * @since 12-07-2018
 * 
 */

public class ControleRtcSonarDAO extends GenericDAO<ControleRtcSonar> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6829271323980666024L;

	/**
	 * Busca o commit mais recente por sigla, nome do sistema...
	 * 
	 * @param sigla - String
	 * @return - Retorna uma String com a Data
	 */
	public String buscarDataCommit(String nomeSistema) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcSonar.class);
			consulta.add(Restrictions.eq("nomeSistema", nomeSistema));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleRtcSonar resultado = (ControleRtcSonar) consulta.uniqueResult(); // Utilizado para retornar um unico
			// resultado

			if (resultado.getDataCommit() == null) {
				System.out.println("\n Objeto não encontrado como do RTC: " + nomeSistema);
				return "N/A";
			} else {
				System.out.println("-- Achou:" + resultado.getNomeSistema());
				return resultado.getDataCommit().toString();
			}
		} catch (RuntimeException erro) {
			System.out.println("\n Objeto não encontrado como do RTC: " + nomeSistema);
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
			Criteria consulta = sessao.createCriteria(ControleRtcSonar.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleRtcSonar resultado = (ControleRtcSonar) consulta.uniqueResult(); // Utilizado para retornar um unico
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
	 * @return - Retorna uma lista de ControleRtcDev
	 */

	@SuppressWarnings("unchecked")
	public List<ControleRtcSonar> listarOrdenandoAlteracao() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcSonar.class);
			consulta.addOrder(Order.desc("alteracao"));
			List<ControleRtcSonar> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
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
			Criteria update = sessao.createCriteria(ControleRtcSonar.class);
			update.add(Restrictions.isNull("selecionado"));
			List<ControleRtcSonar> listaControle = update.list();
			for (ControleRtcSonar controle : listaControle) {
				controle.setSelecionado(true);
				editar(controle);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	public ControleRtcSonar buscaModuloPorNome(String nomeProjeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcSonar.class);
			consulta.add(Restrictions.eq("nomeSistema", nomeProjeto));
			return (ControleRtcSonar) consulta.uniqueResult();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
}