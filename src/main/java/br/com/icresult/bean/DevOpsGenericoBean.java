package br.com.icresult.bean;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

import br.com.icresult.dao.complementos.Analise_SonarDAO;
import br.com.icresult.dao.complementos.DevOpsGenericoDAO;
import br.com.icresult.domain.complementos.Analise_Sonar;
import br.com.icresult.domain.complementos.DevOpsGenerico;
import br.com.icresult.model.SonarAtributos;
import br.com.icresult.util.Branch;
import br.com.icresult.util.MetodosUteis;
import br.com.icresult.util.YourThreadFactory;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.common.Logger;

/**
 * 
 * @author andre.graca
 * @since 19-06-2019
 *
 */
@ManagedBean
@SessionScoped
public class DevOpsGenericoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7679539502546441664L;
	private DevOpsGenerico obj;
	private DevOpsGenericoDAO dao;
	private List<DevOpsGenerico> listaObj, listaObjFiltrada;
	private int total;
	private int id;
	private String painelGestor;
	private String sigla;
	private String nome_Projeto;
	private String devOps;
	private String chave;
	private String path;
	private String valorEntregaTexto;
	private static Logger LOG = Logger.getLogger(DevOpsGenericoBean.class);
	private String branch;

	/**
	 * 
	 * Runnable responsavel por capturar os paineis
	 * 
	 */
	private static Runnable captura = new Runnable() {

		public void run() {
			try {
				String IDComTipoEntrega = Thread.currentThread().getName();
				String[] split = IDComTipoEntrega.split("-");
				Long id = Long.parseLong(split[0]);
			//	String tipoEntrega = split[1];
				String tipoEntrega = "AnaliseQA";
				LOG.debug("ID identificado: " + id);
				LOG.debug("Entrega identificada: " + tipoEntrega);
				DevOpsGenericoDAO daoTemp = new DevOpsGenericoDAO();
				DevOpsGenerico tempObj = daoTemp.buscar(id);
				SonarAPIBean sonar = new SonarAPIBean();
				Analise_SonarDAO analiseDAO = new Analise_SonarDAO();
				SonarAtributos captura = sonar.getSonarApi(tempObj.getChave());
				if (captura != null) {
					Analise_Sonar analise = new Analise_Sonar(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setTipoEntrega(tipoEntrega);
					analiseDAO.salvar(analise);
					LOG.info("Projeto: " + analise.getModulo() + " capturado com sucesso");
				}

			} catch (Exception e) {
				LOG.error("Erro ao capturar painel", e);
			}
		}
	};

	/**
	 * Executa a captura de um DevOpsGenerico selecionado
	 */
	public void capturar() {
		Long id = obj.getId();
		System.out.println("\n\nID:" + id + "\n");
		Executors.newFixedThreadPool(1, new YourThreadFactory(id + "-" + valorEntregaTexto + "-" + branch))
				.submit(captura);
	}

	/**
	 * @return - lista de DevOpsGenerico selecionados
	 */
	public List<DevOpsGenerico> paineisSelecionados() {
		return new DevOpsGenericoDAO().listar().stream().filter(r -> r.getSelecionado()).collect(Collectors.toList());
	}

	/**
	 * @return - lista de DevOpsGenerico não selecionados
	 */
	public List<DevOpsGenerico> paineisNaoSelecionados() {
		return new DevOpsGenericoDAO().listar().stream().filter(r -> !r.getSelecionado()).collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		for (DevOpsGenerico relacao : paineisNaoSelecionados()) {
			relacao.setSelecionado(true);
			new DevOpsGenericoDAO().editar(relacao);
		}
		listarInfos();
	}

	/**
	 * Seleciona um objeto DevOpsGenerico, deixando o objeto marcado para captura em
	 * grupo de modulos
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarModulo(ActionEvent evento) {
		try {
			obj = (DevOpsGenerico) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getChave());
			obj.setSelecionado(obj.getSelecionado() ? false : true);
			new DevOpsGenericoDAO().editar(obj);
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: " + obj);
		}
	}

	/**
	 * Limpa a seleção dos modulos
	 */
	public void limparSelecaoTodosModulos() {
		for (DevOpsGenerico relacao : paineisSelecionados()) {
			relacao.setSelecionado(false);
			new DevOpsGenericoDAO().editar(relacao);
		}
		listarInfos();
	}

	/**
	 * Executa a captura de todos os modulos DevOpsGenerico selecionados
	 * 
	 * @param evento - contem a lista de DevOpsGenerico para serem capturadas
	 */
	public void executarCapturaModulosSelecionados(ActionEvent evento) {
		try {
			Executors.newFixedThreadPool(1, new YourThreadFactory(valorEntregaTexto))
					.submit(runnableExecutaCapturaModulosSelecionados);
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	Runnable runnableExecutaCapturaModulosSelecionados = new Runnable() {

		@Override
		public void run() {
			try {
				String tipoEntrega = Thread.currentThread().getName();
				List<DevOpsGenerico> listaModulosSelecionados = new DevOpsGenericoDAO().listar().stream()
						.filter(r -> r.getSelecionado()).collect(Collectors.toList());
				for (DevOpsGenerico relacao : listaModulosSelecionados) {
					Executors.newFixedThreadPool(10, new YourThreadFactory(relacao.getId() + "-" + tipoEntrega))
							.submit(captura);
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	};

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Criar uma lista com os objetos DevOpsGenerico
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			dao = new DevOpsGenericoDAO();
			listaObj = dao.listar();
			total = listaObj.size();
			Messages.addGlobalInfo("Lista Atualizada!");
			System.out.println("Total: " + total);
			PrimeFaces.current().ajax().update("fr:form_devops:dataTableDevOps");

		} catch (Exception e) {
			Messages.addGlobalError("Erro listarInfos  ");
			System.out.println("\nErro: " + e.getCause());
		}
	}

	/**
	 * Seleciona um objeto DevOpsGenerico da tabela
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarObj(ActionEvent evento) {

		try {
			obj = (DevOpsGenerico) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getId());

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	// --------------------------------------------------
	// Get e Set
	// --------------------------------------------------

	public DevOpsGenerico getObj() {
		return obj;
	}

	public void setObj(DevOpsGenerico obj) {
		this.obj = obj;
	}

	public List<DevOpsGenerico> getListaObj() {
		return listaObj;
	}

	public void setListaObj(List<DevOpsGenerico> listaObj) {
		this.listaObj = listaObj;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPainelGestor() {
		return painelGestor;
	}

	public void setPainelGestor(String painelGestor) {
		this.painelGestor = painelGestor;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome_Projeto() {
		return nome_Projeto;
	}

	public void setNome_Projeto(String nome_Projeto) {
		this.nome_Projeto = nome_Projeto;
	}

	public String getDevOps() {
		return devOps;
	}

	public void setDevOps(String devOps) {
		this.devOps = devOps;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public void salvarPlanilha(String caminho) {
		DevOpsGenerico modulo = new DevOpsGenerico();
		String sigla, nomeProjeto, chave;
		dao = new DevOpsGenericoDAO();

		// Carrega a panilha
		Workbook planilha = null;

		try {
			planilha = Workbook.getWorkbook(new File(caminho));

			Sheet primeiraAba = planilha.getSheet(0);

			int qtdLinhasComDadosParaInclusao = primeiraAba.getRows();

			for (int i = 1; i < qtdLinhasComDadosParaInclusao; i++) {
				Cell celulaSigla = primeiraAba.getCell(0, i);
				Cell celulaNomeProjeto = primeiraAba.getCell(1, i);
				Cell celulaChave = primeiraAba.getCell(2, i);

				sigla = celulaSigla.getContents().toString().trim();
				nomeProjeto = celulaNomeProjeto.getContents().toString().trim();
				chave = celulaChave.getContents().toString().toString().trim();

				if (sigla.isEmpty()) {
					break;
				} else {
					modulo.setNomeProjeto(nomeProjeto);
					modulo.setChave(chave);
					modulo.setSigla(sigla);
					modulo.setSelecionado(false);

					dao.salvar(modulo);
				}
			}

			Messages.addGlobalInfo("Execução realizada com sucesso");
		} catch (Exception e) {
			Messages.addGlobalError("Não foi possivel salvar a planilha");
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setValorEntregaTexto(String valorEntregaTexto) {
		this.valorEntregaTexto = valorEntregaTexto;
	}

	public String getValorEntregaTexto() {
		return valorEntregaTexto;
	}

	public StreamedContent getExemploCarga() {
		return new MetodosUteis().getExcelComoStreamedContent("/resources/exemplos_excel/exemplo_carga_devops.xls",
				"exemplo_carga_devops.xls");
	}

	public List<DevOpsGenerico> getListaObjFiltrada() {
		return listaObjFiltrada;
	}

	public void setListaObjFiltrada(List<DevOpsGenerico> listaObjFiltrada) {
		this.listaObjFiltrada = listaObjFiltrada;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public List<String> getBranchs() {
		return new Branch().listaBranchs();
	}
}
