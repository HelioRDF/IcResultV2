package br.com.icresult.dao.complementos;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ControleRtcDev;
import br.com.icresult.domain.complementos.ModulosRTCHK;
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

public class ControleRtcDevDAO extends GenericDAO<ControleRtcDev> {
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
			Criteria consulta = sessao.createCriteria(ControleRtcDev.class);
			consulta.add(Restrictions.eq("nomeSistema", nomeSistema));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleRtcDev resultado = (ControleRtcDev) consulta.uniqueResult(); // Utilizado para retornar um unico
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
			Criteria consulta = sessao.createCriteria(ControleRtcDev.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleRtcDev resultado = (ControleRtcDev) consulta.uniqueResult(); // Utilizado para retornar um unico
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
	public List<ControleRtcDev> listarOrdenandoAlteracao() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcDev.class);
			consulta.addOrder(Order.desc("alteracao"));
			List<ControleRtcDev> resultado = consulta.list();
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
			Criteria update = sessao.createCriteria(ControleRtcDev.class);
			update.add(Restrictions.isNull("selecionado"));
			List<ControleRtcDev> listaControle = update.list();
			for (ControleRtcDev controle : listaControle) {
				controle.setSelecionado("ui-icon-blank");
				editar(controle);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca as data de atualização do RTC para cada Sigla
	 */
	public void buscaDataAtualizacaoPorSigla() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {

			for (ControleRtcDev controle : listar()) {
				System.out.println(controle.getSigla());
				Criteria select = sessao.createCriteria(ModulosRTCHK.class);
				select.add(Restrictions.eq("sigla", controle.getSigla()));
				Order order = Order.desc("dataCommit");
				select.addOrder(order);
				select.setMaxResults(1);
				ModulosRTCHK resultado = (ModulosRTCHK) select.uniqueResult();
				Date dataCommit = resultado.getDataCommit();
				Date dataBanco = controle.getDataCommit();
				if (dataBanco == null) {
					controle.setDataCommit(dataCommit);
				} else {
					dataBanco = new Date(dataBanco.getTime());
					dataCommit = new Date(dataCommit.getTime());
					if (!(dataBanco.toString().equals(dataCommit.toString()))) {
						controle.setDataCommitAnt(dataBanco);
						controle.setDataCommit(dataCommit);
						controle.setAlteracao(true);
					}
				}
				editar(controle);
			}
		} catch (RuntimeException e) {
			throw e;
		} finally {
			sessao.close();
		}
	}

	public ControleRtcDev buscaModuloPorNome(String nomeProjeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcDev.class);
			consulta.add(Restrictions.eq("nomeSistema", nomeProjeto));
			return (ControleRtcDev) consulta.uniqueResult();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<ControleRtcDev> listarModulosNaoLiberados() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleRtcDev.class);
			List<String> parametros = Arrays.asList("Bloqueado","Alerta");
			consulta.add(Restrictions.in("resultadoUltimaInspecao",parametros));
			return (List<ControleRtcDev>) consulta.list();

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
	
	

}