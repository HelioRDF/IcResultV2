package br.com.icresult.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.mindmap.DefaultMindmapNode;
import org.primefaces.model.mindmap.MindmapNode;

/**
 * 
 * @author andre.graca
 * @since 09-01-19
 *
 */
@SuppressWarnings("serial")
@ManagedBean
public class MapaSiteBean implements Serializable {

	

	private MindmapNode raiz;

	private MindmapNode galhoSelecionado;

	/**
	 * Incia um MindmapNode com as funcionalidades do sistema
	 * 
	 */
	public MapaSiteBean() {
		raiz = new DefaultMindmapNode("IC", "IC é o nome do sistema utilizado para realização da Inspeção de Código",
				"a5c854", false);

		MindmapNode dev = new DefaultMindmapNode("Desenvolvimento", "Opção no menu Desenvolvimento", "6e9ebf", true);
		dev.addNode(new DefaultMindmapNode("Diária", "Aba que contem informações da Inspeção diária", "a5c854",
				true));
		dev.addNode(new DefaultMindmapNode("Mensal", "Aba que contem informações da Inspeção mensal", "a5c854",
				true));
		dev.addNode(new DefaultMindmapNode("Siglas Git", "Aba que contem informações da siglas compostas por módulos armazenados no GitLab", "a5c854",
				true));
		dev.addNode(new DefaultMindmapNode("Siglas RTC", "Aba que contem informações da siglas compostas por módulos armazenados em um Servidor RTC", "a5c854",
				true));
		dev.addNode(new DefaultMindmapNode("Módulos RTC", "Módulos armazenados em um Servidor RTC utilizados na inspeção", "a5c854",
				true));
		dev.addNode(new DefaultMindmapNode("Módulos Git", "Módulos armazenados no GitLab utilizados na inspeção", "a5c854",
				true));
		dev.addNode(new DefaultMindmapNode("DevOps", "Módulos que não executamos o Sonar Scanner, apenas capturamos os dados do Sonar", "a5c854",
				true));
		dev.addNode(new DefaultMindmapNode("Módulos Espanha", "Módulos que estão em um Sonar na Espanha", "a5c854",
				true));
		MindmapNode hk = new DefaultMindmapNode("Homologação", "Opção no menu Homologação", "6e9ebf", true);
		hk.addNode(new DefaultMindmapNode("Analise URA", "Aba que contem informações sobre a inspeção da sigla URA no ambiente de homologação", "a5c854",
				true));
		hk.addNode(new DefaultMindmapNode("Analise", "Aba que contem informações sobre no ambiente de homologação", "a5c854",
				true));
		hk.addNode(new DefaultMindmapNode("RFCs", "Aba que contem as RFCs utilizadas no ambiente de homologação", "a5c854",
				true));
		hk.addNode(new DefaultMindmapNode("Git", "Aba que contem os módulos do GitLab utilizados na inspeção do ambiente de homologação", "a5c854",
				true));
		hk.addNode(new DefaultMindmapNode("RTC", "Aba que contem os módulos do RTC utilizados na inspeção do ambiente de homologação", "a5c854",
				true));
		MindmapNode info = new DefaultMindmapNode("Info", "Opção no menu Informações", "6e9ebf", true);
		info.addNode(new DefaultMindmapNode("Controle de Siglas", "Aba que contem informações sobre todas as siglas inspecionadas", "a5c854",
				true));
		info.addNode(new DefaultMindmapNode("Cadastro de Módulos", "Aba que contem informações sobre todos os modulos que se executa o sonar scanner e módulos DevOps", "a5c854",
				true));
		info.addNode(new DefaultMindmapNode("Anotações", "Aba que possibilita guardar informações sobre as inspeções", "a5c854",
				true));
		MindmapNode adm = new DefaultMindmapNode("Adm", "Opção no menu Adm", "6e9ebf", true);
		info.addNode(new DefaultMindmapNode("Usuários", "Aba que contém informações dos usuários do sistema", "a5c854",
				true));

		raiz.addNode(dev);
		raiz.addNode(hk);
		raiz.addNode(adm);
		raiz.addNode(info);
	}

	public void onNodeSelect(SelectEvent event) {
		
	}

	/**
	 * @param event - acionado quando um no é cliacado duas vezes
	 */
	public void onNodeDblselect(SelectEvent event) {
		this.galhoSelecionado = (MindmapNode) event.getObject();
		System.out.println(galhoSelecionado.getData());
	}

	//----------------------------
	//Get e Set
	public MindmapNode getRaiz() {
		return raiz;
	}

	public MindmapNode getGalhoSelecionado() {
		return galhoSelecionado;
	}
}
