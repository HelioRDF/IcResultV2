
package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.omnifaces.util.Messages;

import br.com.icresult.dao.complementos.ControleRtcHKDAO;
import br.com.icresult.domain.complementos.ControleRtcHK;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * -Classe BEAN ControleRtcHKBean.
 * 
 * @author helio.franca
 * @version v2.0.5
 * @since 25-07-2018
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class ControleRtcHKBean implements Serializable {

	private ControleRtcHK obj;
	private ControleRtcHKDAO dao;
	private List<ControleRtcHK> listaControle;
	private static Logger LOG = Logger.getLogger(ControleRtcHKBean.class);
	private int total;
	static String CAMINHO = "";

	/**
	 * Salva objeto do tipo ControleRtcHK
	 */
	// -------------------------------------------------------------------------------------
	private void salvar() {
		try {
			dao.salvar(obj);
		} catch (Exception e) {
			LOG.error("Não foi possível salvar ", e);
		}
	}

	/**
	 * Lista os objetos do tipo ControleRtcHK
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			dao = new ControleRtcHKDAO();
			listaControle = dao.listar();
			total = listaControle.size();

			LOG.info("Lista Atualizada!");

		} catch (Exception e) {
			LOG.error("Erro ao  Atualizar Lista.", e);
		}
	}

	/**
	 * Captura as informações de uma planilha xls e salva no banco de dados
	 * 
	 */
	// -------------------------------------------------------------------------------------------
	public void salvarPlanilha() {
		obj = new ControleRtcHK();
		dao = new ControleRtcHKDAO();
		String sigla, sistema, caminho;
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
				Cell celula3 = sheet.getCell(2, i); // coluna 3 - caminho

				sigla = celula1.getContents().toString().trim().toUpperCase();
				sistema = celula2.getContents().toString().trim().toUpperCase();
				caminho = celula3.getContents().toString().trim().toUpperCase();

				// Encerra a leitura quando encontra linha vazia
				if (sigla.isEmpty()) {
					break;
				}

				if (!sigla.isEmpty()) {
					dateC = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
					obj.setSigla(sigla);
					obj.setNomeSistema(sistema);
					obj.setCaminho(caminho);

					obj.setNomeArquivo(CAMINHO);
					obj.setDataVerificacao(dateC);
					salvar();
				}
			}
			Messages.addGlobalInfo("Planilha salva com sucesso!");
		} catch (Exception e) {
			Messages.addGlobalError("Não foi possível salvar Planilha");
		}
	}

	/**
	 * Limpa as informações da tabela ControleRtcHK no banco de dados
	 */
	// -------------------------------------------------------------------------------------
	public void limparDB() {
		try {
			listarInfos();
			for (ControleRtcHK ControleRtcHK : listaControle) {
				ControleRtcHK entidade = dao.buscar(ControleRtcHK.getCodigo());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			LOG.error("Não foi possível limparDB", e);
		}
	}

	/**
	 * Valida e converte uma objeto do tipo data
	 * 
	 * @param dataInfo - data para validara
	 * @param msg      - mensagem opcional
	 * @return - retorna um objeto do tipo data
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public static Date validadorData(String dataInfo, String msg) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		Date dataFinal = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
		if (dataInfo.isEmpty()) {
			dataFinal = null;
		} else {
			try {
				dataFinal = (Date) formatter.parse(dataInfo);
			} catch (ParseException e) {
				dataFinal = null;
			}
		}
		return dataFinal;
	}

	/**
	 * Chama o Runnable do Log RTC
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void gerarLogRTC() {
		try {
			new Thread(rtcLog).start();
			LOG.info("RTC log em execução!");
		} catch (Exception e) {
			LOG.error("Falha em executar o RTC log",e);
		}
	}

	/**
	 * Faz a leitura de um arquivo txt e trata as informações
	 * 
	 * @param obj - Objeto ControleRtcHK
	 */
	// -------------------------------------------------------------------------------------
	public static void lerLogRtc(ControleRtcHK obj) {
		String sigla = obj.getSigla();
		String path = obj.getCaminho();
		StringBuilder log = new StringBuilder();

		Date dataAnt = obj.getDataCommit();
		obj.setDataVerificacao(Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
		path = path + "//Log_" + sigla + ".txt";
		File file = new File(path);
		String siglaTemp, commitTemp, dataTemp = "01/01/1900";

		try (FileReader fileReader = new FileReader(file);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "UTF-8"));) {

			String info = null;
			int linha = 0;
			while ((info = reader.readLine()) != null) {
				linha++;
				log.append("\n");
				log.append(info);

				if (linha == 1) {
					siglaTemp = info;
					String array[] = new String[2];
					array = siglaTemp.split(":");
					siglaTemp = array[1].trim();

				}
				if (linha == 2) {
					commitTemp = info;
					String array[] = new String[2];
					array = commitTemp.split(":");
					commitTemp = array[1].trim();
					if (commitTemp.equalsIgnoreCase("True")) {
						obj.setAlteracao(true);
					} else {
						obj.setAlteracao(false);
					}

				}
				if (linha == 3) {
					dataTemp = info;
					String array[] = new String[2];
					array = dataTemp.split(":");
					dataTemp = array[1].trim();
					Date dataCommit = validadorData(dataTemp, "");
					if (dataCommit == null) {
						LOG.info("Data Nula");
					} else {
						LOG.info("Achou Data : " + obj.getSigla());

						if (obj.getDataCommit() == null) {
							obj.setAlteracao(false);
							ModulosRTCHKBean modulosRtc = new ModulosRTCHKBean();
							modulosRtc.executaHistory(obj);

						} else {
							LocalDate dataArmazenada = obj.getDataCommit().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
									.toLocalDate();
							LocalDate dataLocalizada = dataCommit.toInstant().atZone(ZoneId.of("Etc/GMT+3"))
									.toLocalDate();
							if (dataArmazenada.equals(dataLocalizada)) {
								obj.setAlteracao(false);
							} else {
								obj.setAlteracao(true);
								ModulosRTCHKBean modulosRtc = new ModulosRTCHKBean();
								modulosRtc.executaHistory(obj);
							}
							LocalDate atual = obj.getDataCommit().toInstant().atZone(ZoneId.of("Etc/GMT+3"))
									.toLocalDate();
							LocalDate anterior = dataAnt.toInstant().atZone(ZoneId.of("Etc/GMT+3")).toLocalDate();
							if (!anterior.equals(atual)) {
								obj.setDataCommitAnt(java.sql.Date.valueOf(anterior));
							}
						}
					}

				}
			}
			info = reader.readLine();
			obj.setDescricaoLog(log.toString());

		} catch (Exception e) {
			LOG.error("Error ao executar o RTC Log",e);
		} finally {
			new ControleRtcHKDAO().editar(obj);
		}
	}

	/**
	 * Runnable para acionar o comando RTClog e capturar as informações.
	 */
	// -------------------------------------------------------------------------------------
	private static Runnable rtcLog = new Runnable() {
		public void run() {

			List<ControleRtcHK> listaControle;
			ControleRtcHKDAO dao = new ControleRtcHKDAO();
			listaControle = dao.listar();

			for (ControleRtcHK obj : listaControle) {
				try {
					lerLogRtc(obj);
				} catch (Exception e) {
					LOG.error("Erro rtcLog",e);
				}
			}
		}
	};

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public ControleRtcHK getControle() {
		return obj;
	}

	public void setControle(ControleRtcHK controle) {
		this.obj = controle;
	}

	public List<ControleRtcHK> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<ControleRtcHK> listaControle) {
		this.listaControle = listaControle;
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

	// ------------------------------------------------------------------------------------------------------------------------------------------------------

}