package br.com.icresult.bean;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;

import br.com.icresult.dao.complementos.AnaliseDevDAO;
import br.com.icresult.dao.complementos.Modulos_EspanhaDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.domain.complementos.Modulos_Espanha;
import br.com.icresult.model.Captura;
import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.ProjectName;
import br.com.icresult.nomeprojeto.Repositorio;
import br.com.icresult.util.YourThreadFactory;
import jxl.common.Logger;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class Modulos_Espanha_Bean implements Serializable {

	/**
	 * 
	 */
	private List<Modulos_Espanha> listaSiglas;
	private Modulos_Espanha obj;
	private Modulos_EspanhaDAO dao;
	private int total;
	private static Logger LOG = Logger.getLogger(LiferayBean.class);

	/**
	 * Seleciona um objeto Modulos_Espanha, deixando o objeto marcado para execução
	 * da captura em grupo de modulos
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarModulo(ActionEvent evento) {

		try {
			obj = (Modulos_Espanha) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getChave());
			obj.setSelecionado(obj.getSelecionado().equals("ui-icon-check") ? "ui-icon-blank" : "ui-icon-check");

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	/**
	 * 
	 * Seleciona o objeto que executa o scan ou captura
	 * 
	 */

	public void selecionarObj(ActionEvent event) {
		try {
			obj = (Modulos_Espanha) event.getComponent().getAttributes().get("meuSelect");
			System.out.println("PACOTE :" + obj);
		} catch (Exception e) {
			System.out.println("Erro ao selecionar objeto");
		}
	}

	/**
	 * @return - retorna os modulos selecioandos
	 */
	public List<Modulos_Espanha> paineisSelecionados() {
		return new Modulos_EspanhaDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
				.collect(Collectors.toList());
	}

	/**
	 * @return - retorna os modulos não selecionados
	 */
	public List<Modulos_Espanha> paineisNaoSelecionados() {
		return new Modulos_EspanhaDAO().listar().stream().filter(r -> r.getSelecionado().equals("ui-icon-blank"))
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		Modulos_EspanhaDAO controleDAO = new Modulos_EspanhaDAO();
		for (Modulos_Espanha controle : paineisNaoSelecionados()) {
			controle.setSelecionado("ui-icon-check");
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/**
	 * Limpa a seleção de todos os modulos
	 */
	public void limparSelecaoTodosModulos() {
		Modulos_EspanhaDAO controleDAO = new Modulos_EspanhaDAO();
		for (Modulos_Espanha controle : paineisSelecionados()) {
			controle.setSelecionado("ui-icon-blank");
			controleDAO.editar(controle);
		}
		listarInfos();
	}

	/***
	 * Executa captura individualmente
	 */
	public void executarCaptura() {
		Executors.newFixedThreadPool(1, new YourThreadFactory(obj.getId())).submit(captura);
	}

	/**
	 * Lista os objetos do tipo SiglasGit
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			dao = new Modulos_EspanhaDAO();
			dao.preencherCampoSelecionado();
			listaSiglas = dao.listar();
			total = listaSiglas.size();
			Messages.addGlobalInfo("Lista Módulos Espanha Atualizada!");
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao  Atualizar Lista.");
		}
	}

	/***
	 * Executa a captura para todas as siglas selecionadas
	 * 
	 * @param evento - parametro que contém a lista das siglas do Git
	 * 
	 */
	public void executarCapturaTodasSiglasSelecionadas(ActionEvent evento) {
		try {
			@SuppressWarnings("unchecked")
			List<Modulos_Espanha> listaModulosSelecionados = ((List<Modulos_Espanha>) evento.getComponent()
					.getAttributes().get("tabela")).stream().filter(r -> r.getSelecionado().equals("ui-icon-check"))
							.collect(Collectors.toList());
			for (Modulos_Espanha controle : listaModulosSelecionados) {
				System.out.println(controle.getChave());
				new Modulos_EspanhaDAO().editar(controle);
			}
			new Thread(executaCapturaPaineisSelecionados).start();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	Runnable executaCapturaPaineisSelecionados = new Runnable() {

		@Override
		public void run() {
			try {
				List<Modulos_Espanha> listaModulosSelecionados = new Modulos_EspanhaDAO().listar().stream()
						.filter(r -> r.getSelecionado().equals("ui-icon-check")).collect(Collectors.toList());
				for (Modulos_Espanha controle : listaModulosSelecionados) {
					Executors.newFixedThreadPool(10, new YourThreadFactory(controle.getId())).submit(captura);
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	};

	/**
	 * 
	 * Runnable responsavel por capturar os paineis
	 * 
	 */
	private static Runnable captura = new Runnable() {

		public void run() {
			try {
				System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				Modulos_EspanhaDAO daoTemp = new Modulos_EspanhaDAO();
				Modulos_Espanha tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarEspanhaBean sonar = new ColetaInformacoesSonarEspanhaBean();
				AnaliseDevDAO analiseDAO = new AnaliseDevDAO();
				String chave = tempObj.getChave();
				String nomeProjeto = tempObj.getNome_Projeto();
				Captura captura = sonar.getSonarApi(chave);
				Repositorio repositorio = Repositorio.INDETERMINADA;
				Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
				String sigla = tempObj.getSigla();
				if (captura != null) {
					Analise_Dev_Mensal analise = new Analise_Dev_Mensal(captura);
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(nomeProjeto);
					analise.setAmbiente(ambiente.getAmbiente());
					analise.setRepositorio(repositorio.getRepositorio());
					analise.setPadraoNomeSonar(
							new ProjectName(sigla, nomeProjeto, repositorio, ambiente).getPadraoNomeProjeto());

					tempObj.setDataSonar(analise.getDataSonar());
					tempObj.setVersao(analise.getVersao());
					tempObj.setCapturado(true);
					daoTemp.editar(tempObj);
					System.out.println(analise);
					analiseDAO.salvar(analise);
				} else {
					LOG.info("Captura nula, pesquisando a ultima inspeção para esta chave");
					analiseDAO.buscaUltimaInspecaoComNomeProjeto(chave,
							new ProjectName(sigla, nomeProjeto, repositorio, ambiente).getPadraoNomeProjeto(),
							repositorio.getRepositorio(), ambiente.getAmbiente());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// -------------Getters e Setters
	public List<Modulos_Espanha> getListaSiglas() {
		return listaSiglas;
	}

	public Modulos_Espanha getObj() {
		return obj;
	}

	public int getTotal() {
		return total;
	}

}
