package br.com.icresult.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.dao.complementos.AnaliseDevDAO;
import br.com.icresult.dao.complementos.ControleGitDevDAO;
import br.com.icresult.dao.complementos.ControleRtcDevDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.dao.complementos.NaoInspecionadasDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.dao.complementos.SiglasGitDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.ProjectName;
import br.com.icresult.nomeprojeto.Repositorio;

/**
 * -Classe BEAN AnaliseCodigoHGBean.
 * 
 * @author helio.franca
 * @version v2.3.0
 * @since 11-09-2018
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class Analise_DevMensalBean implements Serializable {

	private static Logger log = LoggerFactory.getLogger(Analise_DevMensalBean.class);
	private Analise_Dev_Mensal analise;
	private AnaliseDevDAO dao;
	private List<Analise_Dev_Mensal> listaAnalise;
	private List<Analise_Dev_Mensal> listaResultado;
	private int total;
	private String siglaAtual;
	private Map<String, Repositorio> relacaoModuloRepositorio;

	/**
	 * Executa os métodos que inserem Coeficiente, Tipo e DataCommit para as
	 * inspeções mensais
	 */

	public void calculaResultados() {
		try {
			dataCommit();
			calcularCoeficiente();
			calcNotaInfosAnt();
			buscarGestorNvl1();
			buscarGestorEntrega();
			insereRepositorioAmbienteNomePadraoSonarParaInspecoesNulas();
			log.info("Resultados calculados com sucesso");
		} catch (Exception e) {
			log.error("Erro ao calcular resultados", e);
		}
	}

	public void calculaCampoDevOps() {
		try {
			insereRelacaoDevOpsParaInspecoesComCampoDevOpsNulo();
			log.info("Campo DevOps inserido com sucesso!");
		} catch (Exception e) {
			log.error("Erro ao calcular campo DevOps", e);
		}
	}

	private void insereRelacaoDevOpsParaInspecoesComCampoDevOpsNulo() {
		dao = new AnaliseDevDAO();
		dao.setDevOpsMesAtual();
	}

	private void insereRepositorioAmbienteNomePadraoSonarParaInspecoesNulas() {
		try {

			dao = new AnaliseDevDAO();
			List<Analise_Dev_Mensal> analisesComRepositorioAmbienteNulos = dao.analisesComRepositorioAmbienteNulos();
			for (Analise_Dev_Mensal analise : analisesComRepositorioAmbienteNulos) {
				dao.editar(insereRepositorioAmbienteNomePadraoSonar(analise));
			}
		} catch (Exception erro) {
			log.error("Erro ao inserir Repositorio, Ambiente e PadaoNomeSonar. Erro " + erro.getMessage());
		}
	}

	public Repositorio buscaRepositorio(String nomeProjeto) {
		return getRelacaoModuloRepositorio().containsKey(nomeProjeto) ? relacaoModuloRepositorio.get(nomeProjeto)
				: nomeProjeto.contains("BKS") ? Repositorio.SONAR_ESPANHA
						: nomeProjeto.contains("Liferay") ? Repositorio.SONAR_BRASIL : Repositorio.GIT;
	}

	private Map<String, Repositorio> buscaNomeModulosComRepositorios() {

		Map<String, Repositorio> listaDeModulosComRepositorio = new HashMap<>();

		SiglasGitDAO siglasGitDAO = new SiglasGitDAO();
		ControleRtcDevDAO rtcDAO = new ControleRtcDevDAO();
		RelacaoProjetoSiglaGestorDAO relacaoDAO = new RelacaoProjetoSiglaGestorDAO();

		siglasGitDAO.listar()
				.forEach(sigla -> listaDeModulosComRepositorio.put(sigla.getNomePainel(), Repositorio.GIT));
		rtcDAO.listar().forEach(sigla -> listaDeModulosComRepositorio.put(sigla.getNomePainel(), Repositorio.RTC));
		relacaoDAO.listaSnapshot()
				.forEach(sigla -> listaDeModulosComRepositorio.put(sigla.getNome_Projeto(), Repositorio.DEVOPS));

		return listaDeModulosComRepositorio;

	}

	private Analise_Dev_Mensal insereRepositorioAmbienteNomePadraoSonar(Analise_Dev_Mensal analise) {
		Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
		Repositorio repositorio = buscaRepositorio(analise.getNomeProjeto());
		analise.setRepositorio(repositorio.getRepositorio());
		analise.setAmbiente(ambiente.getAmbiente());
		String repositorioString = repositorio.getRepositorio();
		if (repositorioString.equals("RTC") || repositorioString.equals("GIT")) {
			analise.setPadraoNomeSonar(
					new ProjectName(analise.getSigla(), analise.getNomeProjeto(), repositorio, ambiente)
							.getPadraoNomeProjeto());
		} else {
			analise.setPadraoNomeSonar(analise.getNomeProjeto());
		}

		return analise;
	}

	private void buscarGestorEntrega() {
		dao = new AnaliseDevDAO();
		List<Analise_Dev_Mensal> analiseSemGestorN2 = dao.gestorEntregaNull();
		for (Analise_Dev_Mensal obj : analiseSemGestorN2) {
			setGestorEntrega(obj);
		}
	}

	private void setGestorEntrega(Analise_Dev_Mensal analise) {
		try {
			String gestorEntregaEGestorNivel2 = new ControleSiglasDAO()
					.buscaGestorEntregaENivel2PorSigla(analise.getSigla());
			if (gestorEntregaEGestorNivel2 != null) {
				analise.setPainelGestor(gestorEntregaEGestorNivel2);
				dao = new AnaliseDevDAO();
				dao.editar(analise);
				log.info("Gestor de nivel 2 inserido com sucesso para a inspeção :" + analise.getId());
			}
		} catch (Exception e) {
			log.error("Erro ao inserir gestor de nivel 2 na inspeção : " + e.getCause());
		}
	}

	private void buscarGestorNvl1() {
		dao = new AnaliseDevDAO();
		List<Analise_Dev_Mensal> analiseSemGestorN1 = dao.gestorNvl1Null();
		for (Analise_Dev_Mensal obj : analiseSemGestorN1) {
			setGestorNivel1(obj);
		}
	}

	/**
	 * Procura o gestor de nível 2 e insere seu valor na inspeção
	 */
	public void setGestorNivel1(Analise_Dev_Mensal analise) {
		try {
			String gestorN1 = new ControleSiglasDAO().buscaGestorNivel1PorSigla(analise.getSigla());

			if (gestorN1 != null) {
				analise.setGestorNivel1(gestorN1);
				dao = new AnaliseDevDAO();
				dao.editar(analise);
				log.info("Gestor de nivel 1 inserido com sucesso para a inspeção :" + analise.getId());
			}
		} catch (Exception e) {
			log.error("Erro ao inserir gestor de nivel 1 na inspeção " + e.getMessage());
		}
	}

	/**
	 * Exclui Analises selecionadas
	 */

	@SuppressWarnings("unchecked")
	public void excluirAnalises(ActionEvent evento) {
		FacesMessage msg = null;
		try {
			dao = new AnaliseDevDAO();
			List<Analise_Dev_Mensal> listaAnalisesSelecionadas = ((List<Analise_Dev_Mensal>) evento.getComponent()
					.getAttributes().get("tabela")).stream().filter(c -> c.getSelecionado().equals("ui-icon-check"))
							.collect(Collectors.toList());
			log.info("Analises sendo excluidas :");
			listaAnalisesSelecionadas.forEach(c -> log.info(c.toString()));
			listaAnalisesSelecionadas.forEach(c -> dao.excluir(c));
			msg = new FacesMessage("Info:", "Analises excluidas com sucesso");
		} catch (Exception e) {
			log.error("Erro ao excluir analises!", e);
			msg = new FacesMessage("Erro:", "Erro ao excluir analises!");
		} finally {
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	/**
	 * Selecionar um objeto Analise_DevMensal para exclusão
	 */

	public void selecionarAnalise(ActionEvent evento) {
		try {
			dao = new AnaliseDevDAO();
			Analise_Dev_Mensal analiseParaEditar = (Analise_Dev_Mensal) evento.getComponent().getAttributes()
					.get("meuSelect");
			analiseParaEditar.setSelecionado(
					analiseParaEditar.getSelecionado().equals("ui-icon-blank") ? "ui-icon-check" : "ui-icon-blank");
			dao.editar(analiseParaEditar);
		} catch (Exception e) {
			log.error("Erro ao selecionar objeto", e);
		}
	}

	/**
	 * Criar uma lista com os objetos do tipo AnaliseCodigoHGBean
	 */
	public void listarInfos() {
		try {

			calculaResultados();
			dao = new AnaliseDevDAO();
			dao.limparSelecaoNovasAnalises();
			List<Analise_Dev_Mensal> listaAnaliseTemp = dao.listar();
			verificarAnalises(listaAnaliseTemp);
			verificarAnalisesDescomission(listaAnaliseTemp);
			listaAnalise = listaAnaliseTemp;
			listaAnaliseTemp.sort(Analise_Dev_Mensal.getComparadorPorDataCaptura());
			total = listaAnalise.size();
			Messages.addGlobalInfo("Lista de Análises Mensais Atualizadas!");
			PrimeFaces.current().ajax().update(":formTb:fr:dataTableAnaliseDEV");
			log.info("Lista Atualizada");
		} catch (Exception e) {
			log.error("Erro ao lista informações da Amalise Mensal", e);
		}
	}

	/**
	 * @param listaAnalise - Lista de Analise_Dev_Mensal para analisar duplicações
	 *                     de chave e nome de projeto dos paineis por mês
	 */
	private void verificarAnalises(List<Analise_Dev_Mensal> listaAnalise) {
		long inicio = System.currentTimeMillis();
		dao = new AnaliseDevDAO();
		List<Analise_Dev_Mensal> listaDeAnalisesComRevisaoNull = dao.getListaComRevisaoNull();

		for (Analise_Dev_Mensal analise : listaDeAnalisesComRevisaoNull) {

			if (analise.getRevisarAnalise() == null) {

				Integer mesAnalise = analise.getDataCaptura().toInstant().atZone(ZoneId.of("Etc/GMT+3")).toLocalDate()
						.getMonthValue();
				Integer anoAnalise = analise.getDataCaptura().toInstant().atZone(ZoneId.of("Etc/GMT+3")).toLocalDate()
						.getYear();

				List<Analise_Dev_Mensal> listaSoComElementosIguaisDoMesmoMes = listaAnalise.parallelStream()
						.filter(analiseIterator -> analiseIterator.getDataCaptura().toInstant()
								.atZone(ZoneId.of("Etc/GMT+3")).toLocalDate().getMonthValue() == mesAnalise
								&& analiseIterator.getDataCaptura().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
										.toLocalDate().getYear() == anoAnalise
								&& (analise.getUrl().equals(analiseIterator.getUrl())
										|| analise.getNomeProjeto().equals(analiseIterator.getNomeProjeto())))
						.collect(Collectors.toList());
				if (listaSoComElementosIguaisDoMesmoMes.size() > 1) {
					listaSoComElementosIguaisDoMesmoMes.forEach(elemento -> {

						elemento.setRevisarAnalise(true);
						new AnaliseDevDAO().editar(elemento);
					});

				}
			}
		}

		long fim = System.currentTimeMillis();

		List<Analise_Dev_Mensal> listaDeAnalisesComRevisaoNull2 = dao.getListaComRevisaoNull();

		for (Analise_Dev_Mensal analise : listaDeAnalisesComRevisaoNull2) {
			analise.setRevisarAnalise(false);
			dao.editar(analise);
		}

		System.out.println("\n\nTEMPO: " + (fim - inicio));

	}

	/**
	 * @param listaAnalise - Lista de Analise_Dev_Mensal para analisar se alguma
	 *                     sigla inspecionada foi descomissioada
	 */
	private void verificarAnalisesDescomission(List<Analise_Dev_Mensal> listaAnalise) {
		dao = new AnaliseDevDAO();
		List<String> siglasDescomissionadas = new NaoInspecionadasDAO().listar().stream()
				.map(elemento -> elemento.getSigla()).collect(Collectors.toList());

		List<Analise_Dev_Mensal> listaDeAnalisesComSiglasDescomissionadasEAnalisarDescomissionNula = listaAnalise
				.stream()
				.filter(inspecao -> inspecao.getAnalisarDescomission() == null
						&& siglasDescomissionadas.contains(inspecao.getSigla()) && inspecao.getDataCaptura().toInstant()
								.atZone(ZoneId.of("Etc/GMT+3")).toLocalDate().isAfter(LocalDate.of(2018, 01, 01)))
				.collect(Collectors.toList());

		for (Analise_Dev_Mensal analise : listaDeAnalisesComSiglasDescomissionadasEAnalisarDescomissionNula) {
			analise.setAnalisarDescomission(true);
			dao.editar(analise);
		}

		List<Analise_Dev_Mensal> listaDeAnalisesComAnalisarDescomissionNula = listaAnalise.stream()
				.filter(inspecao -> inspecao.getAnalisarDescomission() == null && inspecao.getDataCaptura().toInstant()
						.atZone(ZoneId.of("Etc/GMT+3")).toLocalDate().isAfter(LocalDate.of(2018, 01, 01)))
				.collect(Collectors.toList());

		for (Analise_Dev_Mensal analise : listaDeAnalisesComAnalisarDescomissionNula) {
			analise.setAnalisarDescomission(false);
			dao.editar(analise);
		}

	}

	/**
	 * Captura a última data de Commit e tipo em controle git/rtc e carimba na
	 * analíse.
	 */
	public void dataCommit() {
		try {

			dao = new AnaliseDevDAO();
			List<Analise_Dev_Mensal> listaAnaliseTemp = dao.listarParaDataCommit();

			for (Analise_Dev_Mensal obj : listaAnaliseTemp) {
				ControleGitDevDAO daoGit = new ControleGitDevDAO();
				ControleRtcDevDAO daoRtc = new ControleRtcDevDAO();
				String dataCommit = daoGit.buscarDataCommit(obj.getNomeProjeto().trim());

				if (!dataCommit.equals("N/A")) {
					dataCommit = dataCommit.substring(0, 11);

				} else {

					dataCommit = daoRtc.buscarDataCommit(obj.getNomeProjeto().trim());
					if (dataCommit.length() > 8) {
						dataCommit = dataCommit.substring(0, 11);
					}

				}
				obj.setDataCommit(dataCommit);

				dao.editar(obj);
				log.info("Data de Commit atualizada! " + obj.getSigla());
			}

		} catch (Exception e) {
			log.error("Erro ao atualizar data de commit", e);
		}
	}

	/**
	 * Trata a coluna debito técnico, deixando apenas o numeral dia.
	 * 
	 * @author helio.franca
	 * @since 13-08-2018
	 * 
	 */
	public void tratarDebitoTecnico() {

		dao = new AnaliseDevDAO();
		List<Analise_Dev_Mensal> listaObj = dao.listaDebitoTecnico();

		for (Analise_Dev_Mensal obj : listaObj) {

			if (obj.getDebitoTecnico().contains("d")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("d");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					debitoTecnicoMinutos = debitoTecnicoMinutos * 24 * 60;
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));
					dao.editar(obj);

				} catch (Exception e) {
					// Erro de conversão String para Interger.
					log.error("Erro tratarDebitoTecnico ", e);
				}

			} else if (obj.getDebitoTecnico().contains("h")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("h");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					debitoTecnicoMinutos = debitoTecnicoMinutos * 60;
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));

				} catch (Exception e) {
					// Erro de conversão String para Interger.
					log.error("Erro de conversão String para Interger. ", e);
				}
			} else if (obj.getDebitoTecnico().contains("m")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("m");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));

				} catch (Exception e) {
					// Erro de conversão String para Interger.
					log.error("Erro de conversão String para Interger.  ", e);
				}

				finally {
					dao.editar(obj);
					calcularCoeficiente();
					alteracaoSigla();
				}

			} else {
				obj.setDebitoTecnicoMinutos("0");
				dao.editar(obj);

			}

		}
	}

	/**
	 * Calcula o coeficiente, utilizando a formula (=[@[Débito Técnico em Dias
	 * ]]/[@[Total Issus]] ).
	 * 
	 * @author helio.franca
	 * @since 13-08-2018
	 * 
	 */
	public void calcularCoeficiente() {

		dao = new AnaliseDevDAO();
		List<Analise_Dev_Mensal> listaObj = dao.listaCoeficiente();

		for (Analise_Dev_Mensal obj : listaObj) {
			double totalIssues = (obj.getIssuesMuitoAlta() + obj.getIssuesAlta() + obj.getIssuesMedia()
					+ obj.getIssuesBaixa());
			double debitoDias = 0;
			double coeficiente = 0;
			try {
				int numtemp = (Integer.parseInt(obj.getDebitoTecnicoMinutos()) / 60 / 24);
				debitoDias = numtemp;
				coeficiente = 0;
			} catch (Exception e) {
				log.debug("Erro ao calcular coeficiente", e);
			}

			if (totalIssues > 0) {
				coeficiente = debitoDias / totalIssues;
			}
			obj.setCoeficiente(Double.toString(coeficiente));
			dao.editar(obj);
		}
		alteracaoSigla();
	}

	/**
	 * Retorna Uma lista do tipo Analise_Dev_Mensal
	 * 
	 * @author helio.franca
	 * @since 13-08-2018
	 * @return Lista de Analise_Dev_Mensal
	 */
	public List<Analise_Dev_Mensal> listarCodigoDev() {
		try {
			dao = new AnaliseDevDAO();
			List<Analise_Dev_Mensal> listaAnaliseTemp = dao.listar();
			listaAnalise = listaAnaliseTemp;
			log.info("Lista Atualizada!");
			return listaAnaliseTemp;

		} catch (Exception e) {
			log.error("Erro ao listarCodigoDev", e);
			return new ArrayList<Analise_Dev_Mensal>();
		}
	}

	/**
	 * Calcula a nota da análise atual, e seta nota anterior e a quantidade de
	 * linhas anterior.
	 */
	public void calcNotaInfosAnt() {

		dao = new AnaliseDevDAO();
		List<Analise_Dev_Mensal> listaAnaliseTemp = dao.listaNotaVazia();

		for (Analise_Dev_Mensal obj : listaAnaliseTemp) {

			if (obj.getLinhaCodigo() == 1) {
				obj.setNotaProjeto("0");
			} else {
				log.info("Calculando info anteriores para " + obj.getNomeProjeto());
				int resultado;
				double blocker;
				double critical;
				double major;
				int linhaCodigo;
				blocker = obj.getIssuesMuitoAlta();
				critical = obj.getIssuesAlta();
				major = obj.getIssuesMedia();
				linhaCodigo = obj.getLinhaCodigo();
				blocker = ((blocker / linhaCodigo) * 10);
				critical = ((critical / linhaCodigo) * 5);
				major = major / linhaCodigo;

				double soma = blocker + critical + major;
				log.debug("Soma: " + soma);
				double nota = ((1 - soma) * 100);

				if (nota < 0) {

					obj.setNotaProjeto("0");

				} else {

					DecimalFormat df = new DecimalFormat("###,###");
					if (soma >= 0) {
						resultado = Integer.parseInt(df.format(nota));

					} else {
						resultado = 0;
					}

					obj.setNotaProjeto(String.valueOf(resultado));
				}
			}

			try {
				Analise_Dev_Mensal objAnterior = dao.buscarAnterior(obj.getId(), obj.getSigla(), obj.getNomeProjeto());
				obj.setNotaAnterior(objAnterior.getNotaProjeto());
				obj.setLinhaCodigoAnt(objAnterior.getLinhaCodigo());

			} catch (Exception e) {
				// Objeto anterior não existe.
				log.error("Erro Objeto anterior não existe.", e);
			} finally {
				dao = new AnaliseDevDAO();
				dao.editar(obj);
			}
		}
	}

	/**
	 * Identifica se ocorreu alteração na sigla.
	 * 
	 */
	public void alteracaoSigla() {

		dao = new AnaliseDevDAO();
		List<Analise_Dev_Mensal> listaAnaliseTemp = dao.listaTipoVazio();

		for (Analise_Dev_Mensal obj : listaAnaliseTemp) {
			int linhasAtual;
			int linhasAnt;
			linhasAtual = obj.getLinhaCodigo();
			linhasAnt = obj.getLinhaCodigoAnt();
			String codigoAlterado = "LEGADO";

			if (linhasAtual != linhasAnt) {
				codigoAlterado = "NOVO";
			}

			obj.setCodigoAlterado(codigoAlterado);
			dao.editar(obj);

		}
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public int getTotal() {
		return total;
	}

	public Analise_Dev_Mensal getAnalise() {
		return analise;
	}

	public void setAnalise(Analise_Dev_Mensal analise) {
		this.analise = analise;
	}

	public List<Analise_Dev_Mensal> getListaAnalise() {
		return listaAnalise;
	}

	public void setListaAnalise(List<Analise_Dev_Mensal> listaAnalise) {
		this.listaAnalise = listaAnalise;
	}

	public List<Analise_Dev_Mensal> getListaResultado() {
		return listaResultado;
	}

	public void setListaResultado(List<Analise_Dev_Mensal> listaResultado) {
		this.listaResultado = listaResultado;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getSiglaAtual() {
		return siglaAtual;
	}

	public void setSiglaAtual(String siglaAtual) {
		this.siglaAtual = siglaAtual;
	}

	public Map<String, Repositorio> getRelacaoModuloRepositorio() {
		if (relacaoModuloRepositorio == null) {
			relacaoModuloRepositorio = buscaNomeModulosComRepositorios();
		}
		return relacaoModuloRepositorio;
	}

}