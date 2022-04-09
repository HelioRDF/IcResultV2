package br.com.icresult.bean;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.json.JSONException;

import br.com.icresult.dao.complementos.Analise_HomologacaoDAO;
import br.com.icresult.dao.complementos.ConfigDAO;
import br.com.icresult.dao.complementos.ConfigGitDAO;
import br.com.icresult.dao.complementos.ControleGitDevDAO;
import br.com.icresult.dao.complementos.ControleGitHKDAO;
import br.com.icresult.dao.complementos.ControleRtcHKDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.domain.complementos.Analise_Homologacao;
import br.com.icresult.domain.complementos.ConfigGit;
import br.com.icresult.domain.complementos.ControleGitDev;
import br.com.icresult.domain.complementos.ControleGitHK;
import br.com.icresult.domain.complementos.ControleRtcDev;
import br.com.icresult.domain.complementos.ControleRtcHK;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
import br.com.icresult.model.Captura;
import br.com.icresult.util.YourThreadFactory;
import jxl.common.Logger;

@SuppressWarnings("serial")
@ManagedBean(name = "execucaoAutomatica")
@ApplicationScoped
public class ExecucaoAutomaticaInspecaoDiaria implements Serializable {

	private static Calendar horaExecucao;
	private static Calendar tempoEspera;
	private static Boolean permissao;
	private static Boolean executandoInspecao;
	private static String inspecaoEmExecucao;
	private static String mensagem;
	private String usuarioDoUltimoPacoteAnalisadoPeloGitPullDev, usuarioDoUltimoPacoteAnalisadoPeloGitPullHK;
	private static Logger LOG = Logger.getLogger(ExecucaoAutomaticaInspecaoDiaria.class);

	private static void buscaAtualizacoes() {
		try {
			executaInspecao();
		} catch (Exception e) {
			LOG.error("Erro ao executar a automação que conecta a VPN");
		}
	}

	/**
	 * Executa a inspeção de atualização git, analisa os devops atualizados e
	 * executa a inspeção do RTC de HK.
	 */
	private static void executaInspecao() {
		if (permissao) {

//			try {

			executaAtualizacoesDoGit();

//				executaCapturaModulosDevOpsAtualizados();

			// Executa o RTCMats de HK
//				String caminhoSiglasRTCHK = "cd C:/InspecaoSonar/HK/RTC/";
//				String exeAutomacaoRTCHK = "RTCMats-HK.exe";
//				String[] cmdsHK = { caminhoSiglasRTCHK, exeAutomacaoRTCHK };
//				ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmdsHK));
//				builder.redirectErrorStream(true);
//				builder.start();
//
//				LOG.info("Foi disparado a execução do RTCMats HK");
//				Integer tempoEspera = 300000;
//				LOG.info("Aguardando a execução do RTCMats de HK por " + (tempoEspera / 60000) + " minutos");
//
//				// Aguarda a conclusão do RTCMats de HK
//				Thread.sleep(tempoEspera);
//
//			} catch (Exception e) {
//				LOG.error("Erro ao iniciar o RTCMats", e);
//			}
		}
	}

	/**
	 * Verifica quais modulos DevOps foram atualizados e se foram atualizados os
	 * capturam.
	 */
	private static void executaCapturaModulosDevOpsAtualizados() {

		LOG.info("Modulos DevOps Atualizados:");

		Set<String> listaSiglasProcuraDevOps = new HashSet<>();

		for (ControleGitHK controleGit : new ControleGitHKDAO().listar()) {
			listaSiglasProcuraDevOps.add(controleGit.getSigla());
		}

		for (ControleRtcHK controleRTC : new ControleRtcHKDAO().listar()) {
			listaSiglasProcuraDevOps.add(controleRTC.getSigla());
		}

		listaSiglasProcuraDevOps.add("IBE");
		listaSiglasProcuraDevOps.add("IBF");

		List<RelacaoProjetoSiglaGestor> listaModulos = new ArrayList<RelacaoProjetoSiglaGestor>();
		for (String sigla : listaSiglasProcuraDevOps) {
			List<RelacaoProjetoSiglaGestor> listaModulosEncontrados = new RelacaoProjetoSiglaGestorDAO().listar()
					.stream().filter(c -> c.getSigla().equals(sigla) && c.getDevOps().equals("SIM"))
					.collect(Collectors.toList());
			if (!listaModulosEncontrados.isEmpty()) {
				listaModulos.addAll(listaModulosEncontrados);
			}
		}

		ColetaInformacoesSonarBean captura = new ColetaInformacoesSonarBean(new ConfigDAO().listar().stream().filter(
				configuracaoSonar -> configuracaoSonar.getUrl().equals("https://sonarqube-ce.paas.santanderbr.corp/"))
				.findAny().get());

		for (RelacaoProjetoSiglaGestor relacao : listaModulos) {

			Analise_Homologacao ultimaAnalise = new RelacaoProjetoSiglaGestorDAO()
					.buscarUltimaInspecaoPorNomePainel(relacao);

			LocalDateTime dataSonarAtual = captura.getDataSonar(relacao.getChave()).toInstant()
					.atZone(ZoneId.of("Etc/GMT+3")).toLocalDateTime();

			boolean painelCapturado = false;

			if (ultimaAnalise != null && dataSonarAtual != null) {
				Integer segundos = dataSonarAtual.getSecond();
				if (segundos > 30) {
					dataSonarAtual = dataSonarAtual.plusMinutes(1);
				}
				dataSonarAtual = dataSonarAtual.minusSeconds(segundos);
				LocalDateTime dataUltimaAnalise = ultimaAnalise.getDataSonar().toInstant()
						.atZone(ZoneId.of("Etc/GMT+3")).toLocalDateTime();
				if (dataSonarAtual.isAfter(dataUltimaAnalise)) {
					painelCapturado = true;
					Executors.newFixedThreadPool(10, new YourThreadFactory(relacao.getId())).submit(capturaDevOps);
				}
			} else {
				painelCapturado = true;
				Executors.newFixedThreadPool(10, new YourThreadFactory(relacao.getId())).submit(capturaDevOps);
			}

			if (painelCapturado) {
				LOG.info("PAINEL " + relacao.getNome_Projeto() + " ATUALIZADO!!");
			} else {
				LOG.info("PAINEL " + relacao.getNome_Projeto() + " NÃO ATUALIZADO!!");
			}

		}
	}

	/**
	 * 
	 * Runnable para capturar Paineis DevOps atualizados
	 * 
	 */
	private static Runnable capturaDevOps = new Runnable() {

		public void run() {
			try {
				System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				RelacaoProjetoSiglaGestorDAO daoTemp = new RelacaoProjetoSiglaGestorDAO();
				RelacaoProjetoSiglaGestor tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Captura captura = sonar.getSonarApi(tempObj.getChave());
				if (captura != null) {
					Analise_HomologacaoDAO analiseDAO = new Analise_HomologacaoDAO();
					Analise_Homologacao analise = new Analise_Homologacao(captura);
					analise.setPainelGestor(new ControleSiglasDAO().buscaGestorPorSigla(tempObj.getSigla()));
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(tempObj.getNome_Projeto());
					System.out.println(analise);
					analiseDAO.salvar(analise);
					new Analise_Homologacao_Bean().calcNotaInfosAnt();
				}
			} catch (JSONException e) {
				LOG.error("Erro ao capturar modulos DevOps", e);
			}
		}
	};

	/**
	 * Executa as atualizações dos pacotes do GIT para DEV e HK e depois executa o
	 * sonar para os modulos que foram atualizados.
	 */
	private static void executaAtualizacoesDoGit() {
		ControleGitDevBean controleGitDev = new ControleGitDevBean();
		controleGitDev.listarInfos();

		controleGitDev.atualizarGit();
		// -------------------------------------------------------------------------/

		/* Aguarda a conclusao do Git Pull DEV */
		// -------------------------------------------------------------------------/
		LocalDateTime dataAtualizacaoUltimoModulo = new ExecucaoAutomaticaInspecaoDiaria()
				.buscaDataVerificacaoUltimoModuloGitDev();
		LocalDateTime umMinutoAtras = LocalDateTime.now().minusMinutes(1);

		if (dataAtualizacaoUltimoModulo == null) {
			while (dataAtualizacaoUltimoModulo == null && permissao) {
				try {
					dataAtualizacaoUltimoModulo = new ExecucaoAutomaticaInspecaoDiaria()
							.buscaDataVerificacaoUltimoModuloGitDev();
					System.out.println(dataAtualizacaoUltimoModulo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			while (dataAtualizacaoUltimoModulo.isBefore(umMinutoAtras) && permissao) {
				try {
					dataAtualizacaoUltimoModulo = new ExecucaoAutomaticaInspecaoDiaria()
							.buscaDataVerificacaoUltimoModuloGitDev();
					System.out.println(dataAtualizacaoUltimoModulo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// -------------------------------------------------------------------------/
		// Executa o sonar-scanner e captura para módulos do GitLab de DEV
		controleGitDev.executarSonarModulosAtualizadosAutomatico();
	}

	public void pararExecucao() {
		ExecucaoAutomaticaInspecaoDiaria.permissao = new Boolean(false);
		executandoInspecao = new Boolean(false);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Parando..."));
	}

	public void dispararExecucao() {
		try {
			permissao = new Boolean(true);
			new Thread(inspecaoDiaria).start();
		} catch (Exception e) {
			LOG.error("Erro ocorrido ao disparar execução automática da diária! : " + e.getMessage());
		}

	}

	public static void main(String[] args) {
		LocalDateTime umMinutoAtras = LocalDateTime.now().minusMinutes(1);
		System.out.println(umMinutoAtras);
		LocalDateTime dataAtualizacaoUltimoModulo = new ExecucaoAutomaticaInspecaoDiaria()
				.buscaDataVerificacaoUltimoModuloGitDev();
		System.out.println(dataAtualizacaoUltimoModulo);
	}

	@PostConstruct
	public void init() {

		Calendar horario = Calendar.getInstance();
		horario.set(Calendar.HOUR_OF_DAY, 13);
		horario.set(Calendar.MINUTE, 0);
		horaExecucao = horario;

		Calendar tempoEspera = Calendar.getInstance();
		tempoEspera.set(Calendar.HOUR_OF_DAY, 0);
		tempoEspera.set(Calendar.MINUTE, 0);
		ExecucaoAutomaticaInspecaoDiaria.tempoEspera = tempoEspera;

		permissao = new Boolean(false);
		mensagem = new String();
		executandoInspecao = new Boolean(false);
		setMensagem("Não Iniciado");

		usuarioDoUltimoPacoteAnalisadoPeloGitPullDev = defineUsuarioUltimoPacoteAnalisadoPeloGitPullDev();
//		usuarioDoUltimoPacoteAnalisadoPeloGitPullHK = defineUsuarioUltimoPacoteAnalisadoPeloGitPullHk();

	}

	private String defineUsuarioUltimoPacoteAnalisadoPeloGitPullDev() {
		List<ConfigGit> loginsGit = new ConfigGitDAO().listar();

		List<ControleGitDev> listaGitDevDiaria = new ControleGitDevDAO().listar();

		StringBuilder usuarioDoUltimoPacoteAnalisadoPeloGitPull = new StringBuilder();

		for (ConfigGit configGit : loginsGit) {
			String usuario = configGit.getLogin();
			listaGitDevDiaria = listaGitDevDiaria.stream()
					.filter(pacote -> usuario.toUpperCase().equals(pacote.getUsuarioGit().toUpperCase()))
					.collect(Collectors.toList());

			if (!(listaGitDevDiaria.isEmpty())) {
				usuarioDoUltimoPacoteAnalisadoPeloGitPull = new StringBuilder();
				usuarioDoUltimoPacoteAnalisadoPeloGitPull.append(usuario);
			}
		}

		return usuarioDoUltimoPacoteAnalisadoPeloGitPull.toString();

	}

	private String defineUsuarioUltimoPacoteAnalisadoPeloGitPullHk() {

		List<ControleGitHK> listaGitHK = new ControleGitHKDAO().listar();

		List<ConfigGit> loginsGit = new ConfigGitDAO().listar();

		StringBuilder usuarioDoUltimoPacoteAnalisadoPeloGitPull = new StringBuilder();

		for (ConfigGit configGit : loginsGit) {
			String usuario = configGit.getLogin();
			listaGitHK = listaGitHK.stream()
					.filter(pacote -> usuario.toUpperCase().equals(pacote.getUsuarioGit().toUpperCase()))
					.collect(Collectors.toList());

			if (!(listaGitHK.isEmpty())) {
				usuarioDoUltimoPacoteAnalisadoPeloGitPull = new StringBuilder();
				usuarioDoUltimoPacoteAnalisadoPeloGitPull.append(usuario);
			}
		}

		return usuarioDoUltimoPacoteAnalisadoPeloGitPull.toString();

	}

	Runnable inspecaoDiaria = new Runnable() {

		@Override
		public void run() {
			while (permissao) {
				setMensagem("Analisando Horários");
				Calendar agora = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+3"));

				if (horaExecucao.get(Calendar.HOUR_OF_DAY) == (agora.get(Calendar.HOUR_OF_DAY))
						&& horaExecucao.get(Calendar.MINUTE) == agora.get(Calendar.MINUTE) && !executandoInspecao) {
					executandoInspecao = new Boolean(true);
					inspecaoEmExecucao = "Execução em andamento";
					LOG.info("Inicio da Execução");
					inspecaoDiaria();
				}
			}
		}
	};

	private void inspecaoDiaria() {
		try {
			setMensagem(inspecaoEmExecucao);
			buscaAtualizacoes();
			LocalDateTime umMinutoAtras = LocalDateTime.now().minusMinutes(1);

			/* Localiza siglas do RTC DEV atualizadas */
			ControleRtcDevBean controleRTCDev = new ControleRtcDevBean();
			controleRTCDev.gerarLogRTC();

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

			/* Localiza siglas do RTC HK atualizadas */
//			ControleRtcHKBean controleRtcHK = new ControleRtcHKBean();
//			controleRtcHK.gerarLogRTC();
//
//			/* Aguarda leitura dos LOGS do RTC HK */
//
//			dataAtualizacaoUltimoModulo = buscaDataVerificacaoUltimoModuloRTCHK();
//
//			if (dataAtualizacaoUltimoModulo == null) {
//				while (dataAtualizacaoUltimoModulo == null && permissao) {
//					try {
//						Thread.sleep(120000);
//						dataAtualizacaoUltimoModulo = buscaDataVerificacaoUltimoModuloRTCHK();
//
//					} catch (Exception e) {
//						LOG.error("Erro", e);
//					}
//				}
//			} else {
//				while (dataAtualizacaoUltimoModulo.isBefore(umMinutoAtras) && permissao) {
//					try {
//						Thread.sleep(120000);
//						dataAtualizacaoUltimoModulo = buscaDataVerificacaoUltimoModuloRTCHK();
//					} catch (Exception e) {
//						LOG.error("Erro", e);
//					}
//				}
//			}

			// -------------------------------------------------------------------------/
			// Verifica se o usuário não cancelou a inspeção, se não, então executa o
			// sonar-scanner e
			// captura para as siglas atualizadas

			if (permissao) {
				LOG.info("Tentando Executar Scans");

				// O sonar-scanner é executado para os modulos dentro das sigla do RTC de HK
//				new ModulosRTCHKBean().executarTodasSiglasAtualizadasAutomatico();

				// O sonar-scanner é executado para as siglas do RTC DEV que receberam
				// atualizações
				controleRTCDev.executarTodasSiglasAtualizadasAutomatico();

			} else {
				setMensagem("Execução Parada");
				permissao = false;
			}

			// Se a inspeção é a da tarde, então é a ultima inspeção dia assim a execução
			// automatica pode parar
			if (inspecaoEmExecucao.equals("Execução da Tarde")) {
				setMensagem("Concluído");
				permissao = false;
			}
		} catch (Exception e) {
			LOG.error("Erro ao executar inspeção", e);
		}

	}

	private LocalDateTime buscaDataVerificacaoUltimoModuloRTCDEV() {
		ControleRtcDevBean bean = new ControleRtcDevBean();
		bean.listarInfos();
		List<ControleRtcDev> listaRtc = bean.getListaControle();
		return listaRtc.get(listaRtc.size() - 1).getDataVerificacao().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
				.toLocalDateTime();
	}

	private LocalDateTime buscaDataVerificacaoUltimoModuloRTCHK() {
		ControleRtcHKBean bean = new ControleRtcHKBean();
		bean.listarInfos();
		List<ControleRtcHK> listaRtc = bean.getListaControle();
		return listaRtc.get(listaRtc.size() - 1).getDataVerificacao().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
				.toLocalDateTime();
	}

	private LocalDateTime buscaDataVerificacaoUltimoModuloGitHK() {
		ControleGitHKDAO dao = new ControleGitHKDAO();
		List<ControleGitHK> listaGit = dao.listar();
		listaGit = listaGit.stream().filter(c -> c.getUsuarioGit().equals(usuarioDoUltimoPacoteAnalisadoPeloGitPullHK))
				.collect(Collectors.toList());
		LocalDateTime dataUltimoModulo = listaGit.get(listaGit.size() - 1).getDataVerificacao().toInstant()
				.atZone(ZoneId.of("Etc/GMT+3")).toLocalDateTime();
		return dataUltimoModulo;
	}

	private LocalDateTime buscaDataVerificacaoUltimoModuloGitDev() {
		ControleGitDevDAO dao = new ControleGitDevDAO();
		List<ControleGitDev> listaGit = dao.listar();

		listaGit = listaGit.stream().filter(modulo -> Files.exists(Paths.get(modulo.getCaminho())))

				.collect(Collectors.toList());
		LocalDateTime dataUltimoModulo = listaGit.get(listaGit.size() - 1).getDataVerificacao().toInstant()
				.atZone(ZoneId.of("Etc/GMT+3")).toLocalDateTime();
		return dataUltimoModulo;
	}

	// ----------------------------------------
	// Getters e Setters
	// ----------------------------------------
	public Date getHoraExecucaoManha() {
		return horaExecucao.getTime();
	}

	public void setHoraExecucaoManha(Date horaExecucaoManha) {
		ExecucaoAutomaticaInspecaoDiaria.horaExecucao.setTime(horaExecucaoManha);
	}

	public void setMensagem(String mensagem) {
		ExecucaoAutomaticaInspecaoDiaria.mensagem = mensagem;
	}

	public boolean getPermissao() {
		return permissao;
	}

	public String getMensagem() {
		return mensagem;
	}

	public Date getTempoEspera() {
		return tempoEspera.getTime();
	}

	public void setTempoEspera(Date tempoEspera) {
		ExecucaoAutomaticaInspecaoDiaria.tempoEspera.setTime(tempoEspera);
	}

}
