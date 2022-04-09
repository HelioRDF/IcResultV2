package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.AnaliseDevDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.dao.complementos.ListaCloudBeesDAO;
import br.com.icresult.dao.complementos.PainelDoSonarListaGeralDAO;
import br.com.icresult.dao.complementos.RelacaoProjetoSiglaGestorDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.domain.complementos.ListaCloudBees;
import br.com.icresult.domain.complementos.PainelDoSonarListaGeral;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
import br.com.icresult.util.MetodosUteis;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * -Classe BEAN ListaCloudBeesBean.
 * 
 * @author andre.graca
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ListaCloudBeesBean implements Serializable {
	static final Runtime run = Runtime.getRuntime();
	static Process pro;
	static BufferedReader read;
	private static ListaCloudBees controle;
	private ListaCloudBeesDAO dao;
	private List<ListaCloudBees> listaControle;
	private String pathSigla;
	private String path;
	private int total;
	static String CAMINHO = "";
	private boolean selecao;
	private static Logger logDaClasse = Logger.getLogger(ListaCloudBeesBean.class);

	/**
	 * // Salvar um objeto do tipo ListaCloudBees
	 */
	private void salvar() {
		try {
			String sigla = controle.getSigla();
			String tecnologia = controle.getTecnologia();
			if (sigla.contains("_")) {
				controle.setTecnologia(
						sigla.substring(sigla.indexOf("_")) + (tecnologia.isEmpty() ? "" : "-") + tecnologia);
				controle.setSigla(sigla.substring(0, sigla.indexOf("_")));
			}
			dao.salvar(controle);
		} catch (Exception e) {
			logDaClasse.error("Não foi possível salvar o objeto", e);
		}
	}

	/**
	 * 
	 * Lista os objetos do tipo ListaCloudBees
	 * 
	 */
	public void listarInfos() {
		try {
			dao = new ListaCloudBeesDAO();
			listaControle = dao.listar();
			total = listaControle.size();
			Messages.addGlobalInfo("Lista de módulos do CloudBees!");
			logDaClasse.info("Lista Atualizada!");
		} catch (Exception e) {
			logDaClasse.error("Erro ao  Atualizar Lista.", e);
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 */
	public void salvarPlanilha() {
		setControle(new ListaCloudBees());
		dao = new ListaCloudBeesDAO();
		String master, sigla, tecnologia, job, url;
		Date dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());

		// Carrega a planilha
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(new File(CAMINHO));

			// Seleciona a aba do excel
			Sheet sheet = workbook.getSheet(0);

			// Numero de linhas com dados do xls
			int linhas = sheet.getRows();
			limparDB();

			for (int i = 1; i < linhas; i++) {
				Cell celula1 = sheet.getCell(0, i); // coluna 1 - Master.
				Cell celula2 = sheet.getCell(1, i); // coluna 2 - Sigla.
				Cell celula3 = sheet.getCell(2, i); // coluna 3 - Tecnologia.
				Cell celula4 = sheet.getCell(3, i); // coluna 4 - Job.
				Cell celula5 = sheet.getCell(4, i); // coluna 5 - URL.

				master = celula1.getContents().toString().trim().toUpperCase();
				sigla = celula2.getContents().toString().trim().toUpperCase();
				tecnologia = celula3.getContents().toString().trim();
				job = celula4.getContents().toString().trim();
				url = celula5.getContents().toString().trim();

				// Encerra a leitura quando encontra linha vazia
				if (master.isEmpty()) {
					break;
				}

				if (!master.isEmpty()) {
					dateC = new Date();
					controle.setMaster(master);
					controle.setSigla(sigla);
					controle.setTecnologia(tecnologia);
					controle.setUrlCloudBees(url);
					controle.setCaminhoDaLista(CAMINHO);
					controle.setDataUltimaCarga(dateC);
					controle.setJob(job);
					salvar();
				}
			}

			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			logDaClasse.error("Erro ao salvar planilha", e);
			Messages.addGlobalError("Não foi possível salvar (salvarPlanilha())");
		}
	}

	public void realizaAnaliseDevOps() {
		localizaPaineisNoSonarENaListaJaExistente();

		limpaModulos();

		verificaSiglaDoPainelCloudBeesEstaNoControleSiglas();
	}

	private void limpaModulos() {

		dao = new ListaCloudBeesDAO();
		/* Adicionando paineis com sigla em branco em uma lista para serem excluidos */

		List<ListaCloudBees> listaCloudBees = dao.listar();

		List<ListaCloudBees> modulosParaExcluir = new ArrayList<ListaCloudBees>();

		modulosParaExcluir
				.addAll(listaCloudBees.stream().filter(el -> el.getSigla().isEmpty()).collect(Collectors.toList()));

		/* Adicionando paineis duplicados em uma lista para serem excluidos */

		Set<String> nomesAplicacoesJaAvaliadas = new HashSet<String>();
		for (ListaCloudBees painelCloudBees : listaCloudBees) {
			if (!nomesAplicacoesJaAvaliadas.contains(painelCloudBees.getNomePainel())) {
				modulosParaExcluir.addAll(listaCloudBees.stream()
						.filter(el -> el.getNomePainel() != null
								&& el.getNomePainel().equals(painelCloudBees.getNomePainel())
								&& !el.getId().equals(painelCloudBees.getId()))
						.collect(Collectors.toList()));
				nomesAplicacoesJaAvaliadas.add(painelCloudBees.getNomePainel());
			}
		}

		/*
		 * Excluindo paineis com sigla em branco e também os paineis que estão repetidos
		 */

		modulosParaExcluir.stream().collect(Collectors.toSet()).forEach(el -> {
			System.out.println("Tentando excluir painel CloudBees ID: " + el.getId());
			dao.excluir(el);
		});
	}

	private void localizaPaineisNoSonarENaListaJaExistente() {

		dao = new ListaCloudBeesDAO();

		String sigla;
		String tecnologia;
		String job;
		List<PainelDoSonarListaGeral> paineisSonar = new PainelDoSonarListaGeralDAO().listarDesc("ultimaAnalise"); // Lista
		// de
		// Painéis
		// no
		// SONAR.

		List<RelacaoProjetoSiglaGestor> listaDevOpsAtuais = new RelacaoProjetoSiglaGestorDAO().listaSnapshot(); // Lista
		// de
		// DevOps
		// do
		// mês
		// atual
		// para
		// pesquisar
		// se
		// o
		// painel
		// já
		// existe
		// na
		// lista
		// de
		// DevOps

		for (ListaCloudBees painelCloudBees : dao.listar()) {
			StringBuilder nomeConcatenado = new StringBuilder();

			// Validar nome concatenado
			// Nome concatenado, se tecnologia vazia:
			// sigla + "-" + job
			// senão:
			// sigla + "-" + tecnologia + "-" + job.

			tecnologia = painelCloudBees.getTecnologia();
			sigla = painelCloudBees.getSigla();
			job = painelCloudBees.getJob();

			if (tecnologia.isEmpty()) {
				nomeConcatenado.append(sigla).append("-").append(job);
			} else {
				if (tecnologia.charAt(0) == '_') {
					nomeConcatenado.append(sigla).append(tecnologia).append("-").append(job);
				} else {
					nomeConcatenado.append(sigla).append("-").append(tecnologia).append("-").append(job);
				}
			}

			// Validar se o nome concatenado consta na lista do SONAR.
			// Se constar, traz as informação do painel do Sonar relacionado ao painel do
			// Cloudbees
			// Senão, verifica se o painel do CloudBees foi adicionado na última lista de
			// DevOps

			PainelDoSonarListaGeral painelNalistaDoSonar = null;

			for (PainelDoSonarListaGeral painel : paineisSonar) {
				if (painel.getNomePainel().equals(nomeConcatenado.toString())) {
					painelNalistaDoSonar = painel;
					break;
				}
			}

			if (painelNalistaDoSonar != null) {
				painelCloudBees.setChavePainel(painelNalistaDoSonar.getChavePainel());
				painelCloudBees.setNomePainel(nomeConcatenado.toString());
				painelCloudBees.setDataDoPainelSonar(painelNalistaDoSonar.getUltimaAnalise());
				painelCloudBees.setTipoSonar(painelNalistaDoSonar.getTipoSonar());
			} else {
				Analise_Dev_Mensal analiseAnterioMensal = new AnaliseDevDAO()
						.buscaUltimaInspecao(nomeConcatenado.toString());

				if (analiseAnterioMensal != null) {
					painelCloudBees.setChavePainel("encontrado_mes_anterior");
					painelCloudBees.setNomePainel(analiseAnterioMensal.getNomeProjeto());
					painelCloudBees.setTipoSonar("encontrado_mes_anterior");
				} else {
					Analise_Dev_Mensal analiseAnterioMensalComNomeSemTecnologia = new AnaliseDevDAO()
							.buscaUltimaInspecao(nomeConcatenado.toString().replace(tecnologia, ""));
					if (analiseAnterioMensalComNomeSemTecnologia != null) {
						painelCloudBees.setChavePainel("encontrado_mes_anterior");
						painelCloudBees.setNomePainel(analiseAnterioMensalComNomeSemTecnologia.getNomeProjeto());
						painelCloudBees.setTipoSonar("encontrado_mes_anterior");
					}

				}
			}

			dao.editar(painelCloudBees);
		}
	}

	/**
	 * 
	 * Verifica quais módulos do cloudbees já tem sua sigla no controle de sigla e
	 * se caso tenha mudar o valor da variavel podeEntrarComoDevOps para indicar que
	 * o módulo é elegível para virar DevOps.
	 * 
	 */
	private void verificaSiglaDoPainelCloudBeesEstaNoControleSiglas() {

		Set<String> listaSiglasNoControle = new ControleSiglasDAO().listar().stream()
				.map(controle -> controle.getSigla()).collect(Collectors.toSet());

		dao = new ListaCloudBeesDAO();

		List<ListaCloudBees> listaCloudBees = dao.listar();

		listaCloudBees.stream().forEach(el -> {
			if (listaSiglasNoControle.contains(el.getSigla())) {
				el.setPodeEntrarComoDevOps(true);
			}
		});

		listaCloudBees.stream().filter(
				el -> el.getPodeEntrarComoDevOps() && el.getChavePainel() != null && !(el.getChavePainel().isEmpty()))
				.forEach(el -> dao.editar(el));

	}

	/**
	 * Limpa a tabela de módulos do CloudBees.
	 */
	public void limparDB() {
		try {
			listarInfos();
			for (ListaCloudBees cloudBees : listaControle) {
				ListaCloudBees entidade = dao.buscar(cloudBees.getId());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			logDaClasse.error("Não foi possivel limpar o Banco de Dados", e);
		}
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public List<ListaCloudBees> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ListaCloudBees> listaControle) {
		this.listaControle = listaControle;
	}

	public String getPathSigla() {
		return pathSigla;
	}

	public void setPathSigla(String pathSigla) {
		this.pathSigla = pathSigla;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public static String getCAMINHO() {
		return CAMINHO;
	}

	public static void setCAMINHO(String cAMINHO) {
		CAMINHO = cAMINHO;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		setCAMINHO(path);
	}

	public boolean isSelecao() {
		return selecao;
	}

	public void setSelecao(boolean selecao) {
		this.selecao = selecao;
	}

	public static void setControle(ListaCloudBees controle) {
		ListaCloudBeesBean.controle = controle;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public StreamedContent getExemploListaCloudBees() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_lista_cloudbees.xls",
				"exemplo_lista_cloudbees.xls");
	}
}
