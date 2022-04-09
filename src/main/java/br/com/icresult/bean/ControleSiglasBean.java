package br.com.icresult.bean;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.omnifaces.util.Messages;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.domain.complementos.ControleSiglas;
import br.com.icresult.util.MetodosUteis;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * -Classe BEAN ControleSiglasBean.
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ControleSiglasBean implements Serializable {

	private ControleSiglas controle;
	private ControleSiglasDAO dao;
	private List<ControleSiglas> listaControle;
	static String CAMINHO = "";
	String path;
	private int total;
	private static Logger LOG = Logger.getLogger(ControleSiglasBean.class);

	/**
	 * Salva um objeto do tipo ControleSiglas
	 */
	// -------------------------------------------------------------------------------------
	public void salvar() {
		try {
			dao.salvar(controle);
		} catch (Exception e) {
			LOG.error("Não foi possível salvar.", e);
		}
	}

	/**
	 * Limpa a tabela do banco de dados
	 */
	// -------------------------------------------------------------------------------------
	public void limparDB() {
		try {
			listarInfos();
			for (ControleSiglas controleSiglas : listaControle) {
				ControleSiglas entidade = dao.buscar(controleSiglas.getCodigo());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			LOG.error("Não foi possível limparDB ", e);
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 */
	// -------------------------------------------------------------------------------------------
	public void salvarPlanilha() {
		controle = new ControleSiglas();
		dao = new ControleSiglasDAO();
		String sigla, sistema, linguagem, mapa, espanha, rtc, git, devOps, validaAdocaoDevOps, projetoArsenal, teamArea,
				workspace, inclusaoExclusao, nivel1, nivel2, instrucoes, liderTeste;
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
				Cell celula1 = sheet.getCell(0, i); // coluna 1 -Sigla
				Cell celula2 = sheet.getCell(1, i); // coluna 2 -Sistema
				Cell celula3 = sheet.getCell(2, i); // coluna 3 - Linguagem
				Cell celula4 = sheet.getCell(3, i); // coluna 4 - Mapa
				Cell celula5 = sheet.getCell(4, i); // coluna 5 - Espanha
				Cell celula6 = sheet.getCell(5, i); // coluna 6 - RTC
				Cell celula7 = sheet.getCell(6, i); // coluna 7 - Git
				Cell celula8 = sheet.getCell(7, i); // coluna 8 - DevOps
				Cell celula9 = sheet.getCell(8, i); // coluna 9 - Valida Adoção DevOps
				Cell celula10 = sheet.getCell(9, i); // coluna 10 - Projeto Arsenal
				Cell celula11 = sheet.getCell(10, i); // coluna 11 - Team Area
				Cell celula12 = sheet.getCell(11, i); // coluna 12 - Workspace
				Cell celula13 = sheet.getCell(12, i); // coluna 13 - Inclusao/Exclusao
				Cell celula14 = sheet.getCell(13, i); // coluna 14 - Nl1
				Cell celula15 = sheet.getCell(14, i); // coluna 15 - Nl2
				Cell celula16 = sheet.getCell(15, i); // coluna 16 - Instruções
				Cell celula17 = sheet.getCell(16, i); // coluna 17 - Lider Teste

				sigla = celula1.getContents().toString().trim().toUpperCase();
				sistema = celula2.getContents().toString().trim().toUpperCase();
				linguagem = celula3.getContents().toString().trim().toUpperCase();
				mapa = celula4.getContents().toString().trim().toUpperCase();
				espanha = celula5.getContents().toString().trim();
				rtc = celula6.getContents().toString().trim().toUpperCase();
				git = celula7.getContents().toString().trim().toUpperCase();
				devOps = celula8.getContents().toString().trim().toUpperCase();
				validaAdocaoDevOps = celula9.getContents().toString().trim().toUpperCase();
				projetoArsenal = celula10.getContents().toString().trim().toUpperCase();
				teamArea = celula11.getContents().toString().trim().toUpperCase();
				workspace = celula12.getContents().toString().trim();
				inclusaoExclusao = celula13.getContents().toString().trim();
				nivel1 = celula14.getContents().toString().trim();
				nivel2 = celula15.getContents().toString().trim();
				instrucoes = celula16.getContents().toString().trim();
				liderTeste = celula17.getContents().toString().trim();

				// Encerra a leitura quando encontra linha vazia
				if (sigla.isEmpty()) {
					break;
				}

				if (!sigla.isEmpty()) {
					dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					controle.setSigla(sigla);
					controle.setNomeSistema(sistema);
					controle.setLinguagem(linguagem);
					controle.setMapa(mapa);
					controle.setEspanha(espanha);
					controle.setRtc(rtc);
					controle.setGit(git);
					controle.setDevOps(devOps);
					controle.setValidaAdocaoDevOps(validaAdocaoDevOps);
					controle.setProjetoArsenal(projetoArsenal);
					controle.setTeamArea(teamArea);
					controle.setWorkspace(workspace);
					controle.setInclusaoExclusao(inclusaoExclusao);
					controle.setNivel1(nivel1);
					controle.setNivel2(nivel2);
					controle.setInstrucoes(instrucoes);
					controle.setLiderTeste(liderTeste);
					controle.setDataCadastro(dateC);
					controle.setNomeArquivo(CAMINHO);
					salvar();
				}
			}
			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			e.getStackTrace();
			Messages.addGlobalError("Não foi possível salvar Planilha ");
		}
	}

	/**
	 * Lista os objetos do tipo ControleSiglas
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			dao = new ControleSiglasDAO();
			listaControle = dao.listar();
			total = listaControle.size();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lista Atualizada!"));

		} catch (Exception e) {
			LOG.error("Erro ao  Atualizar Lista.", e);
		}
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public ControleSiglas getControle() {
		return controle;
	}

	public void setControle(ControleSiglas controle) {
		this.controle = controle;
	}

	public static String getCAMINHO() {
		return CAMINHO;
	}

	public static void setCAMINHO(String cAMINHO) {
		CAMINHO = cAMINHO;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		CAMINHO = path;
	}

	public List<ControleSiglas> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleSiglas> listaControle) {
		this.listaControle = listaControle;
	}

	public StreamedContent getExemploControleSiglas() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_controleSiglas.xls",
				"exemplo_controleSiglas.xls");
	}

}
