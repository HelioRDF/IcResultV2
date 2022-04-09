package br.com.icresult.bean;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.dao.complementos.PaineisDentroSonarDAO;
import br.com.icresult.dao.complementos.SonarPainelDAO;
import br.com.icresult.domain.complementos.PaineisDentroSonar;
import br.com.icresult.domain.complementos.SonarPainel;
import br.com.icresult.model.SonarAtributos;
import br.com.icresult.util.YourThreadFactory;

/**
 * -Classe BEAN Analise_SonarBean.
 * 
 * @author andre.graca
 * @version v3.0.0
 * @since 25-06-2019
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class PaineisDentroSonarBean implements Serializable {

	private Logger log = LoggerFactory.getLogger(PaineisDentroSonarBean.class);
	private PaineisDentroSonarDAO dao;
	private List<PaineisDentroSonar> listaAnalise;
	private int total;

	/**
	 * Criar uma lista com os objetos do tipo AnaliseCodigoHGBean
	 */
	public void listarInfos() {
		try {
			dao = new PaineisDentroSonarDAO();
			listaAnalise = dao.listar();
			listaAnalise.sort(PaineisDentroSonar.getComparadorPorDataCaptura());
			total = listaAnalise.size();
			log.info("Lista Atualizada");
			Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Lista atualizada!", null));
		} catch (Exception e) {
			log.error("Erro ao lista informações da Amalise Em Testes", e);
		}
	}

	/**
	 * Executa a captura de todos os painéis.
	 */
	public void capturaTodosPaineisSonar() {
		new Thread(capturaTodosPaineisSonar).start();
	}

	private Runnable capturaTodosPaineisSonar = new Runnable() {

		@Override
		public void run() {
			try {
				long inicio = System.currentTimeMillis();
				SonarAPIBean capturaNomeDosPaineis = new SonarAPIBean();
				capturaNomeDosPaineis.iniciarApi();
				List<SonarPainel> listaPaineisNoSonar = new SonarPainelDAO().listar();
				limparBancoDados();
				for (SonarPainel painel : listaPaineisNoSonar) {
					Executors.newFixedThreadPool(1, new YourThreadFactory(painel.getChave())).submit(captura);
					Thread.sleep(1000);
				}
				listarInfos();
				long fim = System.currentTimeMillis();
				log.info("A captura de todos os painéis ocorreu em : " + ((fim - inicio) / 60000) + " minutos e "
						+ ((fim - inicio) % 60000) + " segundos.");
			} catch (Exception e) {
				log.error("Erro ao capturar todos os paineis do SONAR: " + e.getCause());
			}
		}
	};

	private Runnable captura = new Runnable() {

		@Override
		public void run() {
			String chave = Thread.currentThread().getName();
			SonarAPIBean capturaNomeDosPaineis = new SonarAPIBean();
			SonarAtributos atributosDoPainel = capturaNomeDosPaineis.getSonarApi(chave);
			PaineisDentroSonar painelSonar = new PaineisDentroSonar(atributosDoPainel);
			dao = new PaineisDentroSonarDAO();
			dao.salvar(painelSonar);
		}
	};

	public void limparBancoDados() {
		try {
			dao = new PaineisDentroSonarDAO();
			for (PaineisDentroSonar painel : dao.listar()) {
				dao.excluir(painel);
			}
		} catch (Exception e) {
			log.error("Erro ao excluir paineis salvos " + e.getCause());
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

	public List<PaineisDentroSonar> getListaAnalise() {
		return listaAnalise;
	}

	public void setListaAnalise(List<PaineisDentroSonar> listaAnalise) {
		this.listaAnalise = listaAnalise;
	}

}