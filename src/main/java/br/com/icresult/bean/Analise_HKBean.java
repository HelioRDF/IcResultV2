package br.com.icresult.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import br.com.icresult.dao.complementos.AnaliseHKDAO;
import br.com.icresult.dao.complementos.ControleGitHKDAO;
import br.com.icresult.dao.complementos.ControleRtcHKDAO;
import br.com.icresult.domain.complementos.Analise_HK;

/**
 * -Classe BEAN Analise_HKBean.
 * 
 * @author helio.franca
 * @version v2.1.8
 * @since 21-08-2018
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class Analise_HKBean implements Serializable {

	private Analise_HK analise;
	private AnaliseHKDAO dao;
	private List<Analise_HK> listaAnalise;
	private int total;
	private String siglaAtual;
	private final String ERRO_DEBITO = "Erro ao tratar debito técnico";
	private static org.apache.log4j.Logger log = Logger.getLogger(Analise_HKBean.class);

	/**
	 * Criar uma lista com os objetos Analise_HKBean
	 */
	public void listarInfos() {
		try {
			dao = new AnaliseHKDAO();
			List<Analise_HK> listaAnaliseTemp = dao.listar();
			listaAnalise = listaAnaliseTemp;
			total = listaAnalise.size();
			log.info("Lista Atualizada!");
		} catch (Exception e) {
			log.error("Erro ao atualizar lista", e);
		}
	}

	/**
	 * Captura a última data de Commit e tipo em controle git/rtc e carimba na
	 * analíse.
	 */
	public void DataCommit() {
		try {
			dao = new AnaliseHKDAO();
			List<Analise_HK> listaAnaliseTemp = dao.listarParaDataCommit();
			for (Analise_HK obj : listaAnaliseTemp) {
				ControleGitHKDAO daoGit = new ControleGitHKDAO();
				ControleRtcHKDAO daoRtc = new ControleRtcHKDAO();
				String dataCommitGit = daoGit.buscarDataCommit(obj.getSigla().trim());
				String dataCommitRtc = daoRtc.buscarDataCommit(obj.getSigla().trim());
				String dataCommit = "";

				if (!dataCommitGit.equals("N/A")) {
					dataCommitGit = dataCommitGit.substring(0, 11);
					dataCommit = dataCommitGit;
				}
				if (dataCommitRtc.length() > 8) {
					dataCommitRtc = dataCommitRtc.substring(0, 11);
					dataCommit = dataCommitRtc;
				}
				obj.setDataCommit(dataCommit);
				dao.editar(obj);
				log.info("Data de Commit atualizada! " + obj.getSigla());
			}
		} catch (Exception e) {
			log.error("Erro ao capturar data de commit", e);
		} finally {
			// Chama o método AlteracaoSigla ...
			alteracaoSigla();

		}
	}

	/**
	 * Captura Resultados nulos e seta as regras de Alerta/LIBERADO + nota anterior
	 */
	public void resultado() {
		dao = new AnaliseHKDAO();
		List<Analise_HK> listaResultado = dao.listaResultadoVazio();

		for (Analise_HK obj : listaResultado) {

			if (obj.getIssuesMuitoAlta() > 0) {
				obj.setResultado("ALERTA");
			} else {
				obj.setResultado("LIBERADO");
			}

			dao.editar(obj);

		} // Fim do For

	}

	/**
	 * Calcula a nota da análise
	 */
	public void calcNota() {
		dao = new AnaliseHKDAO();
		List<Analise_HK> listaAnaliseTemp = dao.listaNotaVazio();
		for (Analise_HK obj : listaAnaliseTemp) {

			try {
				Analise_HK objAnt = dao.buscarAnterior(obj.getId(), obj.getSigla(), obj.getNomeProjeto());
				obj.setNotaAnterior(objAnt.getNotaProjeto());
			} catch (Exception e) {
				obj.setNotaAnterior(null);
			}

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
			int resultado;
			DecimalFormat df = new DecimalFormat("###,###");
			if (soma >= 0) {
				resultado = Integer.parseInt(df.format(nota));
			} else {
				resultado = 0;
			}
			obj.setNotaProjeto(String.valueOf(resultado));
			dao = new AnaliseHKDAO();
			dao.editar(obj);
			log.info("Nota incluída:" + obj.getSigla() + " Nota:" + resultado + "%");
		}
		// Chama o método Resultado ...
		resultado();
	}

	/**
	 * Identifica se ocorreu alteração na sigla com base na sigla.
	 * 
	 */
	public void alteracaoSigla() {

		dao = new AnaliseHKDAO();
		List<Analise_HK> listaAnaliseTemp = dao.listaTipoVazio();

		for (Analise_HK obj : listaAnaliseTemp) {
			String codigoAlterado = "NÃO";
			Analise_HK objAnterior = dao.buscarAnterior(obj.getId(), obj.getSigla(), obj.getNomeProjeto());
			try {
				if (obj.getLinhaCodigo() != objAnterior.getLinhaCodigo()) {
					codigoAlterado = "SIM";
				} else {
					codigoAlterado = "NÃO";
				}
			} catch (Exception e) {
				log.error("Erro ao verificar atualização na sigla", e);
			} finally {
				obj.setCodigoAlterado(codigoAlterado);
				dao.editar(obj);
			}
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

		dao = new AnaliseHKDAO();
		List<Analise_HK> listaObj = dao.listaDebitoTecnico();

		for (Analise_HK obj : listaObj) {

			if (obj.getDebitoTecnico().contains("d")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = new String[2];
				array = debitoTecnico.split("d");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					debitoTecnicoMinutos = debitoTecnicoMinutos * 24 * 60;
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));
					dao.editar(obj);

				} catch (Exception e) {
					// Erro de conversão String para Interger.
					log.error(ERRO_DEBITO, e);
				}

			} else if (obj.getDebitoTecnico().contains("h")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("h");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					debitoTecnicoMinutos = debitoTecnicoMinutos * 60;
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));

				} catch (Exception e) {
					log.error(ERRO_DEBITO, e);
				}
			} else if (obj.getDebitoTecnico().contains("m")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("m");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));

				} catch (Exception e) {
					log.error(ERRO_DEBITO, e);
				}

			} else {
				obj.setDebitoTecnicoMinutos("0");

			}
			dao.editar(obj);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public Analise_HK getAnalise() {
		return analise;
	}

	public void setAnalise(Analise_HK analise) {
		this.analise = analise;
	}

	public List<Analise_HK> getListaAnalise() {
		return listaAnalise;
	}

	public void setListaAnalise(List<Analise_HK> listaAnalise) {
		this.listaAnalise = listaAnalise;
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