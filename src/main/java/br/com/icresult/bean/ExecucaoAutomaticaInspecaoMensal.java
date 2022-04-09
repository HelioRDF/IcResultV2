package br.com.icresult.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import br.com.icresult.domain.complementos.ControleRtcDev;
import jxl.common.Logger;

@SuppressWarnings("serial")
@ManagedBean(name = "atualizacaoAutomatica")
@ApplicationScoped
public class ExecucaoAutomaticaInspecaoMensal implements Serializable {

	private static Calendar horaExecucao;
	private static Boolean permissao;
	private static Boolean executando;
	private static String mensagem;
	private static Logger LOG = Logger.getLogger(ExecucaoAutomaticaInspecaoMensal.class);

	public void pararExecucao() {
		ExecucaoAutomaticaInspecaoMensal.permissao = new Boolean(false);
		executando = new Boolean(false);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Parando..."));
	}

	public void dispararExecucao() {
		try {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Atualização Agendada para : "
					+ horaExecucao.get(Calendar.HOUR_OF_DAY) + ":" + horaExecucao.get(Calendar.MINUTE)));
			permissao = true;
			Thread thread = new Thread(atualizacaoRTC);
			thread.start();
		} catch (Exception e) {
			LOG.error("\n\nErro ao disparar a atualização do RTC\n\n", e);
		}
	}

	@PostConstruct
	public void init() {

		Calendar horarioManha = Calendar.getInstance();
		horarioManha.set(Calendar.HOUR_OF_DAY, 9);
		horarioManha.set(Calendar.MINUTE, 0);
		horaExecucao = horarioManha;
		permissao = new Boolean(false);
		mensagem = new String();
		executando = new Boolean(false);
		setMensagem("Não Iniciado");
	}

	Runnable atualizacaoRTC = new Runnable() {

		@Override
		public void run() {
			while (permissao) {
				setMensagem("Analisando Horários");
				Calendar agora = Calendar.getInstance();
				if (horaExecucao.get(Calendar.HOUR_OF_DAY) == agora.get(Calendar.HOUR_OF_DAY)
						&& horaExecucao.get(Calendar.MINUTE) == agora.get(Calendar.MINUTE) && !executando) {
					executando = new Boolean(true);
					LOG.info("\n\nIniciando Execução de Atualizações\n\n");
					if (permissao) {
						new ControleRtcDevBean().gerarLogRTC();
						new SiglasGitBean().verificarAtualizacoes();
						executaInspecaoMensal();
					}
				}
			}

		}
	};

	/**
	 * Aguarda atualizações do RTC , executa siglas do RTC atualizadas. Executa
	 * siglas do Git atualizada.
	 * 
	 */
	private static void executaInspecaoMensal() {
		try {
			executaSiglasRTC();
			executaSiglaGIT();
		} catch (Exception e) {
			LOG.error("Erro ao executar a inspeção mensal", e);
		}
	}

	/**
	 * Aguarda as atualizações do RTC terminarem e executa as que foram atualizadas.
	 */
	private static void executaSiglasRTC() {
		ControleRtcDevBean controleRTCDev = new ControleRtcDevBean();
		new ModulosRTCDEVBean().validarModulos();
		LocalDateTime umMinutoAtras = LocalDateTime.now().minusMinutes(1);
		/* Aguarda leitura dos LOGS do RTC Dev */
		LocalDateTime dataAtualizacaoUltimoModulo = buscaDataVerificacaoUltimoModuloRTCDEV();
		if (dataAtualizacaoUltimoModulo == null) {
			while (dataAtualizacaoUltimoModulo == null && permissao) {
				dataAtualizacaoUltimoModulo = buscaDataVerificacaoUltimoModuloRTCDEV();
			}
		} else {
			while (dataAtualizacaoUltimoModulo.isBefore(umMinutoAtras) && permissao) {
				dataAtualizacaoUltimoModulo = buscaDataVerificacaoUltimoModuloRTCDEV();
			}
		}
		// -------------------------------------------------------------------------------------//
		controleRTCDev.executarTodasSiglasAtualizadasAutomaticoMensal();
	}

	/**
	 * Executa as siglas do GIT que foram atualizadas durante os mês.
	 */
	private static void executaSiglaGIT() {
		SiglasGitBean siglaGit = new SiglasGitBean();
		siglaGit.executarTodasSiglasAtualizadasAutomaticoMensal();
	}

	private static LocalDateTime buscaDataVerificacaoUltimoModuloRTCDEV() {
		ControleRtcDevBean bean = new ControleRtcDevBean();
		bean.listarInfos();
		List<ControleRtcDev> listaRtc = bean.getListaControle();
		return listaRtc.get(listaRtc.size() - 1).getDataVerificacao().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
				.toLocalDateTime();
	}

	// ----------------------------------------
	// Getters e Setters
	// ----------------------------------------

	public void setMensagem(String mensagem) {
		ExecucaoAutomaticaInspecaoMensal.mensagem = mensagem;
	}

	public Date getHoraExecucao() {
		return horaExecucao.getTime();
	}

	public void setHoraExecucao(Date horaExecucao) {
		ExecucaoAutomaticaInspecaoMensal.horaExecucao.setTime(horaExecucao);
	}

	public boolean getPermissao() {
		return permissao;
	}

	public String getMensagem() {
		return mensagem;
	}

}
