package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.PainelDoSonarListaGeralDAO;
import br.com.icresult.domain.complementos.PainelDoSonarListaGeral;
import br.com.icresult.util.MetodosUteis;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * -Classe BEAN PainelDoSonarListaGeralBean.
 * 
 * @author andre.graca
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class PainelDoSonarListaGeralBean implements Serializable {
	static final Runtime run = Runtime.getRuntime();
	static Process pro;
	static BufferedReader read;
	private static PainelDoSonarListaGeral controle;
	private PainelDoSonarListaGeralDAO dao;
	private List<PainelDoSonarListaGeral> listaControle;
	private String pathSigla;
	private String path;
	private int total;
	static String CAMINHO = "";
	private boolean selecao;
	private static Logger logDaClasse = Logger.getLogger(PainelDoSonarListaGeralBean.class);

	/**
	 * // Salvar um objeto do tipo PainelDoSonarListaGeral
	 */
	private void salvar() {
		try {
			dao.salvar(controle);
		} catch (Exception e) {
			logDaClasse.error("Não foi possível salvar o objeto", e);
		}
	}

	/**
	 * Lista os objetos do tipo PainelDoSonarListaGeral
	 */
	public void listarInfos() {
		try {
			dao = new PainelDoSonarListaGeralDAO();
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
		setControle(new PainelDoSonarListaGeral());
		dao = new PainelDoSonarListaGeralDAO();
		String nomePainel, chave, tipoSonar;
		Date ultimaAnalise = null;

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
				Cell celula1 = sheet.getCell(0, i); // coluna 1 - Nome Painel.
				Cell celula2 = sheet.getCell(1, i); // coluna 2 - Chave.
				Cell celula3 = sheet.getCell(2, i); // coluna 3 - Última Análise.
				Cell celula4 = sheet.getCell(3, i); // coluna 4 - Sonar.

				nomePainel = celula1.getContents().toString().trim();
				chave = celula2.getContents().toString().replaceAll(":\\w*:\\S*", "");
				String dataUltimaAnaliseEmTexto = celula3.getContents().toString();
				if (!dataUltimaAnaliseEmTexto.isEmpty())
					ultimaAnalise = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).parse(dataUltimaAnaliseEmTexto); // Precisa
																													// virar
																													// uma
																													// Data
																													// 01/10/2019
																													// 09:49:51
				tipoSonar = celula4.getContents().toString().trim();

				// Encerra a leitura quando encontra linha vazia
				if (nomePainel.isEmpty()) {
					break;
				}

				if (!nomePainel.isEmpty()) {
					controle.setChavePainel(chave);
					controle.setNomePainel(nomePainel);
					controle.setTipoSonar(tipoSonar);
					controle.setUltimaAnalise(ultimaAnalise);

					salvar();
				}
			}

			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			logDaClasse.error("Erro ao salvar planilha", e);
			Messages.addGlobalError("Não foi possível salvar (salvarPlanilha())");
		}
	}

	/**
	 * Limpa a tabela de paineis do SONAR.
	 */
	public void limparDB() {
		try {
			listarInfos();
			for (PainelDoSonarListaGeral painelSonar : listaControle) {
				PainelDoSonarListaGeral entidade = dao.buscar(painelSonar.getId());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			logDaClasse.error("Não foi possivel limpar o Banco de Dados", e);
		}
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public List<PainelDoSonarListaGeral> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<PainelDoSonarListaGeral> listaControle) {
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

	public static void setControle(PainelDoSonarListaGeral controle) {
		PainelDoSonarListaGeralBean.controle = controle;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public StreamedContent getExemploListaSonar() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_lista_sonar.xls",
				"exemplo_lista_sonar.xls");
	}
}
