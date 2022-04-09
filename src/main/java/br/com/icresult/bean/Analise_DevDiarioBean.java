package br.com.icresult.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.dao.complementos.Analise_DiarioDAO;
import br.com.icresult.dao.complementos.ControleGitDevDAO;
import br.com.icresult.dao.complementos.ControleRtcDevDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Diario;
import br.com.icresult.domain.complementos.ControleGitDev;
import br.com.icresult.domain.complementos.ControleRtcDev;
import br.com.icresult.email.EntregaDiariaDevList;
import br.com.icresult.email.EntregaDiariaDevListBloqueioCritical;
import br.com.icresult.email.EnviarEmail;

/**
 * -Classe BEAN AnaliseCodigoHGBean.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 21-11-2018
 *
 */

@SuppressWarnings("serial")
@ManagedBean(name = "analise_DevDiarioBean")
@SessionScoped
public class Analise_DevDiarioBean implements Serializable {

	private Analise_Dev_Diario analise;
	private Analise_DiarioDAO dao;
	private List<Analise_Dev_Diario> listaAnalise;
	private List<Analise_Dev_Diario> listaResultado;
	private int total;
	private String siglaAtual;
	private Analise_DiarioDAO analiseDao;
	private Date filtrarDataEmail;
	private String chaveEMail;
	private String origemEMail;
	private String destinoEMail;
	private static Logger log = LoggerFactory.getLogger(Analise_DevDiarioBean.class);
	private String liberado = "Liberado";
	private String bloqueado = "Bloqueado";
	private String alerta = "Alerta";

	/**
	 * Metodo executado após a contrução da classe Analise_DevDiarioBean
	 */
	@PostConstruct
	public void init() {
		filtrarDataEmail = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
	}

	/**
	 * Selecionar um objeto Analise_DevDiario para edição
	 * 
	 * @param evento - Evento gerado pelo PrimeFaces, que por meio deste evento
	 *               localizamos a Analise_Dev_Diario a ser editada
	 */

	public void selecionarAnalise(ActionEvent evento) {
		dao = new Analise_DiarioDAO();
		Analise_Dev_Diario analiseParaEditar = (Analise_Dev_Diario) evento.getComponent().getAttributes()
				.get("meuSelect");
		analiseParaEditar.setSelecionado(
				analiseParaEditar.getSelecionado().equals("ui-icon-blank") ? "ui-icon-check" : "ui-icon-blank");
		dao.editar(analiseParaEditar);
	}

	/**
	 * Metodo que exclui uma ou mais Analises Selecionadas
	 * 
	 * @param evento - Evento gerado pelo PrimeFaces, que por meio deste evento
	 *               localizamos uma lista de Analise_Dev_Diario a ser excluida
	 */

	@SuppressWarnings("unchecked")
	public void excluirAnalises(ActionEvent evento) {
		FacesMessage msg = null;
		dao = new Analise_DiarioDAO();

		List<Analise_Dev_Diario> listaAnalisesSelecionadas = ((List<Analise_Dev_Diario>) evento.getComponent()
				.getAttributes().get("tabela")).stream().filter(c -> c.getSelecionado().equals("ui-icon-check"))
						.collect(Collectors.toList());

		try {
			log.info("Analises sendo excluidas :");
			listaAnalisesSelecionadas.forEach(c -> log.info(c.toString()));
			listaAnalisesSelecionadas.forEach(c -> dao.excluir(c));
			msg = new FacesMessage("Info:", "Analises Excluídas");
		} catch (Exception e) {
			log.error("Erro excluir analises diárias", e);
			msg = new FacesMessage("Erro:", "Erro ao excluir analises");
		} finally {
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	/**
	 * Criar uma lista com os objetos Analise_Dev_Diario
	 */

	public void listarInfos() {
		try {
			dao = new Analise_DiarioDAO();
			dao.limparSelecaoNovasAnalises();
			List<Analise_Dev_Diario> listaAnaliseTemp = dao.listarAnalises();
			listaAnalise = listaAnaliseTemp;
			listaAnalise.sort(Analise_Dev_Diario.getComparadorPorDataCapturaDescrecente());
			total = listaAnalise.size();
			Messages.addGlobalInfo("Lista de Análises Diárias Atualizadas!");
			log.info("Lista Atualizada");
		} catch (Exception e) {
			log.error("Erro ao atualizar lista", e);
		}
	}

	/**
	 * Calcula a nota, blocker e resultado da análise diárias de acordo com os
	 * blockers e criticals
	 */

	public void calcNotaResultadoBlockersCriticals() {
		try {
			calculaNota();
			calculaCriticalReferencia();
			calculaBlockerReferencia();
			calcularResultadoCriticals();
			atualizaGestorN2();
			listarInfos();
			log.info("Cálculo realizado");
		} catch (Exception e) {
			log.error("Erro ao calcular Nota+Resultado", e);
		}
	}

	/**
	 * Atualiza o gestor nivel 2 para gestores que mudaram com o mês
	 */
	private void atualizaGestorN2() {
		dao = new Analise_DiarioDAO();
		for (Analise_Dev_Diario analise : dao.listar()) {
			String gestor = new ControleSiglasDAO().buscaGestorEntregaENivel2PorSigla(analise.getSigla());
			if (gestor != null && !(gestor.isEmpty())) {
				if (analise.getPainelGestor() == null || !analise.getPainelGestor().equals(gestor)) {
					analise.setPainelGestor(gestor);
					dao.editar(analise);
				}

			}
		}
	}

	/**
	 * Busca a ultima execução do modulo e define o status dele como bloqueado
	 */
	private void defineBloqueio() {
		dao = new Analise_DiarioDAO();
		ControleGitDevDAO daoGit = new ControleGitDevDAO();
		for (ControleGitDev controle : daoGit.listar()) {
			Analise_Dev_Diario analiseGit = dao.ultimaAnaliseInseridaPorNome(controle.getChave());
			if (analiseGit != null) {
				log.info(analiseGit.toString());
				controle.setResultadoUltimaInspecao(analiseGit.getResultado());
				log.info(controle.toString());
				new ControleGitDevDAO().editar(controle);
			}

		}
		ControleRtcDevDAO daoRtc = new ControleRtcDevDAO();
		for (ControleRtcDev controle : daoRtc.listar()) {
			Analise_Dev_Diario analiseRTC = dao.ultimaAnaliseInseridaPorNome(controle.getNomeSistema());
			if (analiseRTC != null) {
				log.info(analiseRTC.toString());
				controle.setResultadoUltimaInspecao(analiseRTC.getResultado());
				log.info(controle.toString());
				new ControleRtcDevDAO().editar(controle);
			}
		}
	}

	/**
	 * 
	 * Calcula a nota das analises
	 * 
	 */

	public void calculaNota() {
		listarInfos();
		analiseDao = new Analise_DiarioDAO();
		List<Analise_Dev_Diario> analisesComNotaNula = listaAnalise.stream()
				.filter(analise -> analise.getNotaProjeto() == null).collect(Collectors.toList());
		List<Analise_Dev_Diario> analisesComNotaNulaEZeroLinhasDeCodigo = listaAnalise.stream()
				.filter(analise -> analise.getNotaProjeto() == null && analise.getLinhaCodigo() == 0)
				.collect(Collectors.toList());
		analisesComNotaNulaEZeroLinhasDeCodigo.forEach(analise -> {
			analise.setNotaProjeto("0");
			new Analise_DiarioDAO().editar(analise);
		});
		for (Analise_Dev_Diario obj : analisesComNotaNula) {
			try {
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
				major = (major / linhaCodigo);
				double soma = blocker + critical + major;
				double nota = ((1 - soma) * 100);
				Integer resultado;
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
				log.info("Nota incluída:" + obj.getSigla() + " Nota:" + obj.getNotaProjeto() + "%");

				analiseDao.editar(obj);
			} catch (Exception e) {
				log.error("Erro ao calcular nota do " + obj.getNomeProjeto());
			}
		}
	}

	/**
	 * 
	 * Calcula blockers de referência
	 * 
	 */

	public void calculaBlockerReferencia() {
		listarInfos();
		analiseDao = new Analise_DiarioDAO();

		List<Analise_Dev_Diario> listaExecucoesComBlockerReferenciaNulo = listaAnalise.stream()
				.filter(execucao -> execucao.getBlockerReferencia() == null).collect(Collectors.toList());

		for (Analise_Dev_Diario inspecaoDiaria : listaExecucoesComBlockerReferenciaNulo) {
			List<Analise_Dev_Diario> listaExecucoesIguais = new Analise_DiarioDAO().listar().stream()
					.filter(inspecao -> inspecao.getSigla().equals(inspecaoDiaria.getSigla())
							&& inspecao.getNomeProjeto().equals(inspecaoDiaria.getNomeProjeto()))
					.sorted(Analise_Dev_Diario.getComparadorPorDataCapturaCrescente()).collect(Collectors.toList());

			List<Analise_Dev_Diario> listaExecucoesIguaisComBlockerReferenciaNulo = listaExecucoesIguais.stream()
					.filter(execucao -> execucao.getBlockerReferencia() == null)
					.sorted(Analise_Dev_Diario.getComparadorPorDataCapturaCrescente()).collect(Collectors.toList());

			for (Analise_Dev_Diario atual : listaExecucoesIguaisComBlockerReferenciaNulo) {

				Analise_Dev_Diario anterior;
				int blockerAtual = atual.getIssuesMuitoAlta();
				int indexAtual = listaExecucoesIguais.indexOf(atual);

				if (indexAtual == 0) {
					atual.setBlockerReferencia(blockerAtual);
				} else {
					anterior = listaExecucoesIguais.get(indexAtual - 1);
					atual.setBlockerReferencia(blockerAtual <= anterior.getBlockerReferencia() ? blockerAtual
							: anterior.getBlockerReferencia());
				}
				analiseDao.editar(atual);
			}
		}

	}

	/**
	 * 
	 * Calcula criticals de referencia
	 * 
	 */

	public void calculaCriticalReferencia() {
		listarInfos();
		analiseDao = new Analise_DiarioDAO();
		List<Analise_Dev_Diario> analisesComCriticalReferenciaNula = listaAnalise.parallelStream()
				.filter(analise -> analise.getCriticalReferencia() == null).collect(Collectors.toList());
		for (Analise_Dev_Diario inspecaoDiaria : analisesComCriticalReferenciaNula) {
			List<Analise_Dev_Diario> listaExecucoesIguais = new Analise_DiarioDAO().listar().stream()
					.filter(p -> p.getSigla().equals(inspecaoDiaria.getSigla())
							&& p.getNomeProjeto().equals(inspecaoDiaria.getNomeProjeto()))
					.sorted(Analise_Dev_Diario.getComparadorPorDataCapturaCrescente()).collect(Collectors.toList());

			List<Analise_Dev_Diario> listaExecucoesIguaisComCriticalReferenciaNulo = listaExecucoesIguais.stream()
					.filter(analise -> analise.getCriticalReferencia() == null)
					.sorted(Analise_Dev_Diario.getComparadorPorDataCapturaCrescente()).collect(Collectors.toList());

			for (Analise_Dev_Diario atual : listaExecucoesIguaisComCriticalReferenciaNulo) {

				Analise_Dev_Diario anterior;
				int criticalAtual = atual.getIssuesAlta();
				int indexAtual = listaExecucoesIguais.indexOf(atual);

				if (indexAtual == 0) {
					atual.setCriticalReferencia(criticalAtual);
				} else {
					anterior = listaExecucoesIguais.get(indexAtual - 1);
					atual.setCriticalReferencia(criticalAtual <= anterior.getCriticalReferencia() ? criticalAtual
							: anterior.getCriticalReferencia());
				}
				analiseDao.editar(atual);

			}
		}
	}

	/**
	 * Calcula o resultado da inspeção e insere os critical da inspecao anterior na
	 * inspeção atual
	 * 
	 */
	public void calcularResultadoCriticals() {
		listarInfos();
		analiseDao = new Analise_DiarioDAO();
		for (Analise_Dev_Diario inspecaoDiaria : listaAnalise) {
			if (inspecaoDiaria.getResultado() == null) {
				List<Analise_Dev_Diario> listaExecucoesIguais = new Analise_DiarioDAO().listar().stream()
						.filter(p -> p.getSigla().equals(inspecaoDiaria.getSigla())
								&& p.getNomeProjeto().equals(inspecaoDiaria.getNomeProjeto()))
						.collect(Collectors.toList());

				listaExecucoesIguais.sort(Analise_Dev_Diario.getComparadorPorDataCapturaCrescente());

				for (int c = 0; c < listaExecucoesIguais.size(); c++) {
					Analise_Dev_Diario atual = listaExecucoesIguais.get(c);
					if (atual.getResultado() == null) {
						analiseDao = new Analise_DiarioDAO();
						analiseDao.editar(geraResultado(listaExecucoesIguais, c, atual));
					}

				}
			}
		}
	}

	public Analise_Dev_Diario geraResultado(List<Analise_Dev_Diario> listaExecucoesIguais, int posicaoElementoAtual,
			Analise_Dev_Diario atual) {

		Analise_Dev_Diario anterior;
		int criticalAtual = atual.getIssuesAlta();
		int blockerAtual = atual.getIssuesMuitoAlta();

		if (atual.getLinhaCodigo() == 0) {
			atual.setResultado("?");
			if (posicaoElementoAtual == 0) {
				atual.setIssuesMuitoAltaAnterior(blockerAtual);
			} else {
				anterior = listaExecucoesIguais.get(posicaoElementoAtual - 1);
				atual.setIssuesMuitoAltaAnterior(anterior.getIssuesMuitoAlta());
			}
		} else {
			if (posicaoElementoAtual == 0) {
				atual.setResultado(liberado);
				atual.setIssuesMuitoAltaAnterior(atual.getIssuesMuitoAlta());
			} else {
				anterior = listaExecucoesIguais.get(posicaoElementoAtual - 1);
				atual.setIssuesMuitoAltaAnterior(anterior.getIssuesMuitoAlta());
				if (blockerAtual <= anterior.getBlockerReferencia()
						&& criticalAtual <= anterior.getCriticalReferencia()) {
					atual.setResultado(liberado);
				} else {
					atual.setResultado(bloqueado);
				}
			}
		}
		log.info(atual.toString());
		return atual;
	}

	/**
	 * Dispara o envio de E-mail para siglas bloqueadas utilizando os critérios de
	 * blockers e criticals
	 * 
	 * @return
	 */
	public List<Analise_Dev_Diario> enviarEmailBlockerCriticals() {
		defineBloqueio();
		EnviarEmail email = new EnviarEmail();
		dao = new Analise_DiarioDAO();
		StringBuilder resultado = new StringBuilder();

		List<ControleGitDev> listaModulosBloqueadosGit = new ControleGitDevDAO().listarModulosNaoLiberados();
		List<ControleRtcDev> listaModulosBloqueadosRTC = new ControleRtcDevDAO().listarModulosNaoLiberados();
		List<Analise_Dev_Diario> listaObj = buscaModulosBloqueados(listaModulosBloqueadosGit,
				listaModulosBloqueadosRTC);
		if (listaModulosBloqueadosRTC.isEmpty() && listaModulosBloqueadosGit.isEmpty()) {
			resultado.append("Sem siglas bloqueadas.");
		}

		listaObj.sort(Analise_Dev_Diario.getComparatorPorSigla());
		listaObj.sort(Analise_Dev_Diario.getComparadorPorDataCapturaCrescente());

		for (Analise_Dev_Diario obj : listaObj) {
			EntregaDiariaDevList list = new EntregaDiariaDevListBloqueioCritical();
			log.debug(obj.getSigla() + " selecionado para enviar por e-mail.");
			resultado.append(list.alertaInspecao(obj));
		}
		email.emailHtmlInspecaoDiariaBloqueioPorCritical(resultado.toString(), "Monitor de Inspeção Diária", chaveEMail,
				origemEMail, destinoEMail);
		return listaObj;
	}

	public List<Analise_Dev_Diario> buscaModulosBloqueados(List<ControleGitDev> listaModulosBloqueadosGit,
			List<ControleRtcDev> listaModulosBloqueadosRTC) {
		dao = new Analise_DiarioDAO();
		LocalDate apartir = LocalDate.of(2019, 06, 10);
		List<Analise_Dev_Diario> listaObj = new ArrayList<>();
		for (ControleGitDev git : listaModulosBloqueadosGit) {
			Analise_Dev_Diario ultimaAnalise = dao.ultimaAnaliseInseridaPorNome(git.getChave());
			String resultadoUltimaInspecaoGIT = ultimaAnalise.getResultado();
			if (ultimaAnalise.getDataCaptura()
					.after(Date.from(Instant.from(apartir.atStartOfDay().toInstant(ZoneOffset.UTC))))
					&& (resultadoUltimaInspecaoGIT.equals(alerta) || resultadoUltimaInspecaoGIT.equals(bloqueado)))
				listaObj.add(ultimaAnalise);
		}
		for (ControleRtcDev rtc : listaModulosBloqueadosRTC) {
			Analise_Dev_Diario ultimaAnalise = dao.ultimaAnaliseInseridaPorNome(rtc.getNomeSistema());
			String resultadoUltimaInspecaoRTC = ultimaAnalise.getResultado();

			if (ultimaAnalise.getDataCaptura()
					.after(Date.from(Instant.from(apartir.atStartOfDay().toInstant(ZoneOffset.UTC))))
					&& (resultadoUltimaInspecaoRTC.trim().equalsIgnoreCase(alerta)
							|| resultadoUltimaInspecaoRTC.equals(bloqueado)))
				listaObj.add(ultimaAnalise);
		}
		return listaObj;
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public Analise_Dev_Diario getAnalise() {
		return analise;
	}

	public Date getFiltrarDataEmail() {
		return filtrarDataEmail;
	}

	public String getChaveEMail() {
		return chaveEMail;
	}

	public String getOrigemEMail() {
		return origemEMail;
	}

	public String getDestinoEMail() {
		return destinoEMail;
	}

	public void setFiltrarDataEmail(Date filtrarDataEmail) {
		this.filtrarDataEmail = filtrarDataEmail;
	}

	public void setChaveEMail(String chaveEMail) {
		this.chaveEMail = chaveEMail;
	}

	public void setOrigemEMail(String origemEMail) {
		this.origemEMail = origemEMail;
	}

	public void setDestinoEMail(String destinoEMail) {
		this.destinoEMail = destinoEMail;
	}

	public void setAnalise(Analise_Dev_Diario analise) {
		this.analise = analise;
	}

	public Analise_DiarioDAO getDao() {
		return dao;
	}

	public void setDao(Analise_DiarioDAO dao) {
		this.dao = dao;
	}

	public List<Analise_Dev_Diario> getListaAnalise() {
		return listaAnalise;
	}

	public void setListaAnalise(List<Analise_Dev_Diario> listaAnalise) {
		this.listaAnalise = listaAnalise;
	}

	public List<Analise_Dev_Diario> getListaResultado() {
		return listaResultado;
	}

	public void setListaResultado(List<Analise_Dev_Diario> listaResultado) {
		this.listaResultado = listaResultado;
	}

	public int getTotal() {
		return total;
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

}