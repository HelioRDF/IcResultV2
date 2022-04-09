package br.com.icresult.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import br.com.icresult.dao.complementos.SonarPainelDAO;
import br.com.icresult.domain.complementos.SonarPainel;

/**
 * -Classe BEAN Analise_SonarBean.
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 11-06-2019
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class SonarPainelBean implements Serializable {

	private static org.apache.log4j.Logger LOG = Logger.getLogger(SonarPainel.class);
	// private Analise_Sonar analise;
	private SonarPainelDAO dao;
	private List<SonarPainel> listaPainel;
	private int total;

	/**
	 * Criar uma lista com os objetos do tipo AnaliseCodigoHGBean
	 */
	public void listarInfos() {
		try {

			dao = new SonarPainelDAO();
			listaPainel = dao.listar();
			total = listaPainel.size();
			LOG.info("Lista Atualizada");
		} catch (Exception e) {
			LOG.error("Erro ao lista informações da Analise Em Testes", e);
		}
	}
	
	/**
	 * Limpa a tabela do banco de dados
	 */

	// -------------------------------------------------------------------------------------
	public  void limparDB() {
		try {
			listarInfos();
			for (SonarPainel obj : listaPainel) {
				SonarPainel entidade = dao.buscar(obj.getId());
				dao.excluir(entidade);
			}
		} catch (Exception e) {
			LOG.error("Não foi possível limparDB ", e);
		}
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<SonarPainel> getListaPainel() {
		return listaPainel;
	}

	public void setListaPainel(List<SonarPainel> listaPainel) {
		this.listaPainel = listaPainel;
	}
	
	

}