package br.com.icresult.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import br.com.icresult.dao.complementos.Analise_HomologacaoDAO;
import br.com.icresult.domain.complementos.Analise_Homologacao;

/**
 * 
 * @author andre.graca
 * @since 08-01-2019
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class Analise_Homologacao_Bean implements Serializable {

	private int total;
	private List<Analise_Homologacao> listaAnalise;
	private Analise_HomologacaoDAO dao;
	private Logger LOG = Logger.getLogger(Analise_Homologacao_Bean.class);

	/**
	 * Calcula a nota da análise atual, e seta nota anterior e linhas anterior.
	 */
	public void calcNotaInfosAnt() {

		dao = new Analise_HomologacaoDAO();
		List<Analise_Homologacao> listaAnaliseTemp = dao.listaNotaVazia();

		for (Analise_Homologacao obj : listaAnaliseTemp) {
			try {
				obj.setNotaProjeto(geraNotaDoProjeto(obj));
				dao.editar(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String geraNotaDoProjeto(Analise_Homologacao obj) {
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
		double nota = ((1 - soma) * 100);

		if (nota < 0) {

			return "0";

		} else {

			DecimalFormat df = new DecimalFormat("###,###");
			if (soma >= 0) {
				resultado = Integer.parseInt(df.format(nota));

			} else {
				resultado = 0;
			}

			return String.valueOf(resultado);
		}
	}

	/**
	 * 
	 * Método responsável por listar os componentes do banco de dados.
	 * 
	 */
	public void listaInfos() {
		try {
			dao = new Analise_HomologacaoDAO();
			listaAnalise = dao.listar();
			total = listaAnalise.size();
			LOG.info("Lista Atualizada!");
		} catch (Exception e) {
			LOG.error("Error ao atualizar lista", e);
		}
	}

	// --------|
	// Getters |
	// --------|

	public List<Analise_Homologacao> getListaAnalise() {
		return listaAnalise;
	}

	public int getTotal() {
		return total;
	}

}
