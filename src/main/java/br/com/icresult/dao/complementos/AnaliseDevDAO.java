package br.com.icresult.dao.complementos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.nomeprojeto.Repositorio;
import br.com.icresult.util.HibernateUtil;
import jxl.common.Logger;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author andre.graca
 * @version v1.0
 * @since 20-03-2020
 * 
 */

public class AnaliseDevDAO extends GenericDAO<Analise_Dev_Mensal> {

	private static final long serialVersionUID = -126164611692514074L;
	private static Logger LOG = Logger.getLogger(GenericDAO.class);
	private static final String DEV_OPS = "DevOps";
	private static final String PARCIAL = "Parcial";
	private static final String NAO = "Não";

	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal, aonde a nota é nula
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> listaNotaVazia() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("notaProjeto"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal, aonde o gestor nvl1 é nulo
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> gestorNvl1Null() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("gestorNivel1"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> listaResultadoVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("resultado"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> listaDebitoTecnico() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("debitoTecnicoMinutos"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> listaCoeficiente() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("coeficiente"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> listaTipoVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("codigoAlterado"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @param codigo  - int
	 * @param sigla   - String
	 * @param projeto - String
	 * @return - Retorna uma objeto AnaliseCodigoHG
	 */
	public Analise_Dev_Mensal buscarAnterior(int codigo, String sigla, String projeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			// consulta.add(Restrictions.idEq(codigo)); // Realiza uma consulta baseada no
			// ID.
			consulta.add(Restrictions.lt("id", codigo));
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.add(Restrictions.eq("nomeProjeto", projeto));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("id"));
			Analise_Dev_Mensal resultado = (Analise_Dev_Mensal) consulta.uniqueResult(); // Utilizado para
																							// retornar um
																							// unico
			// resultado
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista Analise_Dev_Mensal com dataCommit = Null
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> listarParaDataCommit() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("dataCommit"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @param codigo  - int
	 * @param sigla   - string
	 * @param projeto - string
	 * @return - Retorna a quantidade na lista
	 */
	public int qtdList(int codigo, String sigla, String projeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.lt("id", codigo));
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.add(Restrictions.eq("nomeProjeto", projeto));
			@SuppressWarnings("unchecked")
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado.size();
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * @return - Retorna uma lista de Analise_HK_Poc_URA, aonde a nota é nula
	 */
	@SuppressWarnings("unchecked")
	public void limparSelecaoNovasAnalises() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("selecionado"));
			for (Analise_Dev_Mensal analise : (List<Analise_Dev_Mensal>) consulta.list()) {
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
	public List<Analise_Dev_Mensal> gestorEntregaNull() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("painelGestor"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> getListaComRevisaoNull() {
		// Só será listada as analises a partir de agosto de 2019
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions
					.sqlRestriction("MONTH(DataCaptura) > 7 AND YEAR(DataCaptura) = 2019 AND Revisar IS NULL"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> getListaComAnaliseDescomissionNull() {
		// Só será listada as analises a partir de agosto de 2019
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.sqlRestriction(
					"MONTH(DataCaptura) > 7 AND YEAR(DataCaptura) = 2019 AND Analisar_Descomission IS NULL"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void buscaUltimaInspecaoComNomeProjeto(String nomeProjeto, String nomeProjetoPadronizado, String repositorio,
			String ambiente) {
		// Lista todas as inspeçãos com mesma chave passada e retorna a com a data de
		// captura mais recente
		try {
			Analise_Dev_Mensal resultado = buscaUltimaInspecao(nomeProjeto);
			if (resultado == null) {
				LOG.error("Não foi encontrada nenhuma inspeção com a chave " + nomeProjeto);
			} else {
				Analise_Dev_Mensal analiseEncontrada = resultado;
				analiseEncontrada
						.setDataCaptura(Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
				analiseEncontrada.setDescricao("Inspeção copiada da inspeção de ID = " + analiseEncontrada.getId());
				analiseEncontrada.setGestorNivel1(null);
				analiseEncontrada.setPainelGestor(null);
				analiseEncontrada.setAmbiente(ambiente);
				analiseEncontrada.setRepositorio(repositorio);
				analiseEncontrada.setPadraoNomeSonar(nomeProjetoPadronizado);
				analiseEncontrada.setRevisarAnalise(null);
				salvar(analiseEncontrada);
				LOG.info(analiseEncontrada);
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "finally" })
	public Analise_Dev_Mensal buscaUltimaInspecao(String nomeProjeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		List<Analise_Dev_Mensal> resultado = new ArrayList<Analise_Dev_Mensal>();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.eq("nomeProjeto", nomeProjeto));
			consulta.addOrder(Order.desc("id"));
			consulta.setMaxResults(1);
			resultado = consulta.list();
			return (resultado == null || resultado.isEmpty())
					? nomeProjeto.contains("^\\w*\\_") ? buscaUltimaInspecao(nomeProjeto.replaceAll("\\_\\w*", ""))
							: null
					: resultado.get(0);
		} catch (RuntimeException erro) {
			throw erro;

		} finally {
			sessao.close();
			return (resultado == null || resultado.isEmpty()) ? null : resultado.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Mensal> analisesComRepositorioAmbienteNulos() {
		// Lista todas as analises com repositorio e ambiente nulo apartir do mês 10 de
		// 2019
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.isNull("ambiente"));
			consulta.add(Restrictions.isNull("repositorio"));
			consulta.add(Restrictions.gt("dataCaptura",
					Date.from(LocalDate.of(2019, 10, 01).atStartOfDay(ZoneId.systemDefault()).toInstant())));
			List<Analise_Dev_Mensal> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	public void setDevOpsMesAtual() {
		// TODO ajustar para um script de update mais rápido
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {

			Analise_Dev_Mensal analiseMensalMaisRecente = inspecaoMensalMaisRecente(sessao);

			LOG.info(analiseMensalMaisRecente.getDataCaptura());

			LocalDate dataCapturaInspecaoMensalMaisRecente = analiseMensalMaisRecente.getDataCaptura().toInstant()
					.atZone(ZoneId.systemDefault()).toLocalDate();

			List<Analise_Dev_Mensal> analisesDaUltimaMensal = analisesDaUltimaMensal(
					dataCapturaInspecaoMensalMaisRecente, sessao);

			List<String> siglas = analisesDaUltimaMensal.stream().map(analise -> analise.getSigla()).distinct()
					.collect(Collectors.toList());

			List<StatusSigla> statusSigla = new ArrayList<AnaliseDevDAO.StatusSigla>();

			for (String sigla : siglas) {
				String statusDevOps = getResultadoDevOpsSigla(analisesDaUltimaMensal, sigla);
				statusSigla.add(new StatusSigla(statusDevOps, sigla));
			}

			analisesDaUltimaMensal.stream().filter(analise -> analise.getDevops() == null).forEach(analise -> {
				analise.setDevops(statusSigla.stream().filter(status -> status.getSigla().equals(analise.getSigla()))
						.findFirst().get().getStatus());
				editar(analise);
			});

		} catch (Exception e) {
			LOG.error("Erro ao preencher campo DevOps : " + e.getMessage());
		} finally {
			sessao.close();
		}

	}

	private void updateCampoDevOpsPorSiglaParaTodasInspecoesEntre(List<StatusSigla> relacaoDevOpsSiglas,
			LocalDate dataCapturaInspecaoMensalMaisRecente, Session sessao) {

		try {
			List<List<StatusSigla>> listaPorStatusSigla = new ArrayList<List<StatusSigla>>();
			listaPorStatusSigla.add(relacaoDevOpsSiglas.stream().filter(status -> status.getStatus().equals(DEV_OPS))
					.collect(Collectors.toList()));
			listaPorStatusSigla.add(relacaoDevOpsSiglas.stream().filter(status -> status.getStatus().equals(NAO))
					.collect(Collectors.toList()));
			listaPorStatusSigla.add(relacaoDevOpsSiglas.stream().filter(status -> status.getStatus().equals(PARCIAL))
					.collect(Collectors.toList()));

			for (List<StatusSigla> listaPorStatus : listaPorStatusSigla) {
				updateCampoDevOpsPorResultadoDevOps(listaPorStatus, dataCapturaInspecaoMensalMaisRecente, sessao);
			}

		} catch (Exception e) {
			LOG.error("Ao atualizar campo DevOps. Message: " + e.getMessage());
		}

	}

	private int updateCampoDevOpsPorResultadoDevOps(List<StatusSigla> listaSiglaComMesmoResultado,
			LocalDate dataCapturaInspecaoMensalMaisRecente, Session sessao) {
		int updateDevOps = 0;
		String status = listaSiglaComMesmoResultado.get(0).getStatus();
		try {
			String queryString = "UPDATE Analise_Dev_Mensal analise SET devOps = '" + status
					+ "' WHERE analise.sigla IN (" + listaSiglaComMesmoResultado.toString().replaceAll("[\\[\\]]", "")
					+ ") AND MONTH(analise.dataCaptura) = " + dataCapturaInspecaoMensalMaisRecente.getMonthValue()
					+ " AND YEAR(analise.dataCaptura) = " + dataCapturaInspecaoMensalMaisRecente.getYear();
			System.out.println(queryString);
			updateDevOps = sessao.createQuery(queryString).executeUpdate();
			System.out.println("Registros atualizados:" + updateDevOps);
		} catch (Exception e) {
			LOG.error("Ao inserir DevOps = " + status);
		}
		return updateDevOps;
	}

	private List<Analise_Dev_Mensal> analisesDaUltimaMensal(LocalDate dataCapturaInspecaoMensalMaisRecente,
			Session sessao) {
		List<Analise_Dev_Mensal> analisesDaUltimaMensal = new ArrayList<Analise_Dev_Mensal>();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);

			Date primeiroDiaMesAtual = Date.from(dataCapturaInspecaoMensalMaisRecente.withDayOfMonth(1)
					.atStartOfDay(ZoneId.systemDefault()).toInstant());

			Date ultimoDiaMesAtual = Date.from(dataCapturaInspecaoMensalMaisRecente
					.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());

			Criterion criteriaBetweenFirstAndLastDayOfMonth = Restrictions.between("dataCaptura", primeiroDiaMesAtual,
					ultimoDiaMesAtual);
			consulta.add(criteriaBetweenFirstAndLastDayOfMonth);
			analisesDaUltimaMensal.addAll(consulta.list());
		} catch (Exception e) {
			LOG.error("Erro listar a ultima inspeção mensal : " + e.getMessage());
		}

		return analisesDaUltimaMensal;
	}

	private Analise_Dev_Mensal inspecaoMensalMaisRecente(Session sessao) {
		Analise_Dev_Mensal analiseMensalMaisRecente = null;
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.addOrder(Order.desc("dataCaptura"));
			consulta.setMaxResults(1);
			analiseMensalMaisRecente = (Analise_Dev_Mensal) consulta.uniqueResult();

		} catch (Exception e) {
			LOG.error("Erro ao selecionar inspeção mensal mais recente : " + e.getMessage());
		}

		if (analiseMensalMaisRecente == null) {
			throw new RuntimeException("Não foi possivel recuperar a inspeção mensal mais recente");
		}

		return analiseMensalMaisRecente;

	}

	private String getResultadoDevOpsSigla(List<Analise_Dev_Mensal> analisesDaUltimaMensal, String sigla) {
		List<String> listaRelacaoRepositorioSigla = analisesDaUltimaMensal.stream()
				.filter(analise -> analise.getSigla().equals(sigla)).map(analise -> analise.getRepositorio()).distinct()
				.collect(Collectors.toList());

		if (!listaRelacaoRepositorioSigla.contains(Repositorio.DEVOPS.getRepositorio())) {
			return "Não";
		} else if (listaRelacaoRepositorioSigla.size() == 1
				&& listaRelacaoRepositorioSigla.get(0).equals(Repositorio.DEVOPS.getRepositorio())) {
			return "DevOps";
		} else {
			return "Parcial";
		}
	}

	private class StatusSigla {
		private String status;
		private String sigla;

		public StatusSigla(String status, String sigla) {

			this.status = status;
			this.sigla = sigla;
		}

		public Object getSigla() {
			return sigla;
		}

		public String getStatus() {
			return status;
		}

		@Override
		public String toString() {
			return "'" + sigla + "'";
		}

	}

}