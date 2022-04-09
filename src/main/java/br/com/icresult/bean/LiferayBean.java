package br.com.icresult.bean;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.json.JSONException;
import org.omnifaces.util.Messages;

import br.com.icresult.dao.complementos.AnaliseDevDAO;
import br.com.icresult.dao.complementos.Analise_HomologacaoDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.dao.complementos.LiferayDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.domain.complementos.Analise_Homologacao;
import br.com.icresult.domain.complementos.Liferay;
import br.com.icresult.model.Captura;
import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.Repositorio;
import br.com.icresult.util.YourThreadFactory;
import jxl.common.Logger;

/**
 * 
 * @author andre.graca
 * @since 09-01-19
 *
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class LiferayBean implements Serializable {

	private Liferay obj;
	private LiferayDAO dao;
	private List<Liferay> listaObj;
	private List<Liferay> listaSnapshot;
	private int total;
	private int id;
	private int totalSnapshot;
	private String painelGestor;
	private String sigla;
	private String nome_Projeto;
	private String devOps;
	private String chave;
	private static Logger LOG = Logger.getLogger(LiferayBean.class);
	private boolean tipoCaptura;// tipoCaptura == true -> Salva captura na tabela de Homologação
	// tipoCaptura == false -> Salva captura na tabela de Desenvolvimento

	/**
	 * Executa a captura de um Liferay selecionado
	 */
	public void capturar() {
		Long id = obj.getId();
		System.out.println("\n\nID:" + id + "\n");
		if (tipoCaptura) {
			Executors.newFixedThreadPool(1, new YourThreadFactory(id)).submit(capturaHomologacao);
		} else {
			Executors.newFixedThreadPool(1, new YourThreadFactory(id)).submit(capturaDesenvolvimento);
		}
	}

	/**
	 * 
	 * Runnable para capturar Paineis DevOps para a tabela de Homologação
	 * 
	 */
	private static Runnable capturaHomologacao = new Runnable() {

		public void run() {
			try {
				System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				LiferayDAO daoTemp = new LiferayDAO();
				Liferay tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				Captura captura = sonar.getSonarApi(tempObj.getChave());
				if (captura != null) {
					Analise_HomologacaoDAO analiseDAO = new Analise_HomologacaoDAO();
					Analise_Homologacao analise = new Analise_Homologacao(captura);
					analise.setPainelGestor(new ControleSiglasDAO().buscaGestorPorSigla(tempObj.getSigla()));
					analise.setSigla(tempObj.getSigla());
					analise.setNomeProjeto(tempObj.getNome_Projeto());
					System.out.println(analise);
					analiseDAO.salvar(analise);
					new Analise_Homologacao_Bean().calcNotaInfosAnt();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 
	 * Runnable responsavel por capturar os paineis DevOps para a teabela de
	 * Desenvolvimento
	 * 
	 */
	private static Runnable capturaDesenvolvimento = new Runnable() {
		public void run() {
			try {
				System.out.println("\n-----------------------\nID identificado: " + Thread.currentThread().getName());
				LiferayDAO daoTemp = new LiferayDAO();
				Liferay tempObj = daoTemp.buscar(Long.valueOf(Thread.currentThread().getName()));
				ColetaInformacoesSonarBean sonar = new ColetaInformacoesSonarBean();
				AnaliseDevDAO analiseDAO = new AnaliseDevDAO();
				String chave = tempObj.getChave();
				String nomeProjeto = tempObj.getNome_Projeto();
				Captura captura = sonar.getSonarApi(chave);
				Repositorio repositorio = new Analise_DevMensalBean().buscaRepositorio(nomeProjeto);
				Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
				String sigla = tempObj.getSigla();
				if (captura != null) {
					Analise_Dev_Mensal analise = new Analise_Dev_Mensal(captura);
					analise.setSigla(sigla);
					analise.setNomeProjeto(nomeProjeto);
					analise.setAmbiente(ambiente.getAmbiente());
					analise.setRepositorio(repositorio.getRepositorio());
					analise.setPadraoNomeSonar(nomeProjeto);
					System.out.println(analise);
					analiseDAO.salvar(analise);
				} else {
					LOG.info("Captura nula, pesquisando a ultima inspeção para esta chave");
					analiseDAO.buscaUltimaInspecaoComNomeProjeto(tempObj.getNome_Projeto(),
							tempObj.getNome_Projeto(),
							repositorio.getRepositorio(), ambiente.getAmbiente());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * @return - lista de Liferay selecionados
	 */
	public List<Liferay> paineisSelecionados() {
		return new LiferayDAO().listar().stream()
				.filter(r -> r.getSelecionado() != null && r.getSelecionado().equals("ui-icon-check"))
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona se um modulo será capturado na tabela da Inspeção Diária ou da
	 * Inspeção Mensal
	 * 
	 */
	public void editaTipoCaptura() {
		tipoCaptura = !tipoCaptura;
		String tipo = tipoCaptura ? "homologação" : "desenvolvimento";
		System.out.println(tipo);
	}

	/**
	 * @return - lista de Liferay não selecionados
	 */
	public List<Liferay> paineisNaoSelecionados() {
		return new LiferayDAO().listar().stream()
				.filter(r -> r.getSelecionado() != null && r.getSelecionado().equals("ui-icon-blank"))
				.collect(Collectors.toList());
	}

	/**
	 * Seleciona todos os modulos
	 */
	public void selecionarTodosModulos() {
		for (Liferay relacao : paineisNaoSelecionados()) {
			relacao.setSelecionado("ui-icon-check");
			new LiferayDAO().editar(relacao);
		}
		listarInfosSnapshot();
	}

	/**
	 * Seleciona um objeto Liferay, deixando o objeto marcado para captura em grupo
	 * de modulos
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarModulo(ActionEvent evento) {
		try {
			obj = (Liferay) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getChave());
			obj.setSelecionado(obj.getSelecionado().equals("ui-icon-check") ? "ui-icon-blank" : "ui-icon-check");
			new LiferayDAO().editar(obj);

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	/**
	 * Limpa a seleção dos modulos
	 */
	public void limparSelecaoTodosModulos() {
		for (Liferay relacao : paineisSelecionados()) {
			relacao.setSelecionado("ui-icon-blank");
			new LiferayDAO().editar(relacao);
		}
		listarInfosSnapshot();
	}

	/**
	 * Executa a captura de todos os modulos Liferay selecionados
	 * 
	 * @param evento - contem a lista de Liferay para serem capturadas
	 */
	public void executarCapturaModulosSelecionados(ActionEvent evento) {
		try {
			new Thread(runnableExecutaCapturaModulosSelecionados).start();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	Runnable runnableExecutaCapturaModulosSelecionados = new Runnable() {

		@Override
		public void run() {
			try {
				List<Liferay> listaModulosSelecionados = new LiferayDAO().listar().stream()
						.filter(r -> r.getSelecionado().equals("ui-icon-check")).collect(Collectors.toList());
				for (Liferay relacao : listaModulosSelecionados) {
					if (tipoCaptura) {
						Executors.newFixedThreadPool(5, new YourThreadFactory(relacao.getId()))
								.submit(capturaHomologacao);
					} else {
						Executors.newFixedThreadPool(100, new YourThreadFactory(relacao.getId()))
								.submit(capturaDesenvolvimento);
					}
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	};

	/**
	 * Limpa os atributos e objetos
	 */
	// -------------------------------------------------------------------------------------
	@PostConstruct
	public void iniciarObj() {
		obj = new Liferay();
		dao = new LiferayDAO();

		painelGestor = "";
		sigla = "";
		nome_Projeto = "";
		devOps = "";
		chave = "";
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Criar uma lista com os objetos Liferay que são DEVOPS
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfosSnapshot() {
		try {
			listaSnapshot = dao.listar();
			List<Liferay> listaSelecionadosNulos = listaSnapshot.stream().filter(r -> r.getSelecionado() == null)
					.collect(Collectors.toList());
			for (Liferay relacao : listaSelecionadosNulos) {
				relacao.setSelecionado("ui-icon-blank");
				dao.editar(relacao);
			}
			listaSnapshot = dao.listar();
			totalSnapshot = listaSnapshot.size();
			Messages.addGlobalInfo("Lista Atualizada!");
			System.out.println("Total: " + total);

		} catch (Exception e) {
			Messages.addGlobalError("Erro listarInfos  ");
			System.out.println("\nErro: " + e.getCause());
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Criar uma lista com os objetos Liferay
	 */
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	public void listarInfos() {
		try {
			listaObj = dao.listar();
			total = listaObj.size();
			Messages.addGlobalInfo("Lista Atualizada!");
			System.out.println("Total: " + total);

		} catch (Exception e) {
			Messages.addGlobalError("Erro listarInfos  ");
			System.out.println("\nErro: " + e.getCause());
		}
	}

	/**
	 * Exclui um objeto Liferay
	 */
	// -------------------------------------------------------------------------------------
	public void excluir() {

		try {
			System.out.println("-------------Chamado");
			dao.excluir(obj);
			Messages.addGlobalInfo("Removido com sucesso!!!");
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Remover: ");

		}
	}

	/**
	 * Seleciona um objeto Liferay da tabela
	 */
	// -------------------------------------------------------------------------------------
	public void selecionarObj(ActionEvent evento) {

		try {
			obj = (Liferay) evento.getComponent().getAttributes().get("meuSelect");
			System.out.println("\n\n\n\n------------ID:--------\n" + obj.getId());
			new LiferayDAO().editar(obj);

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Selecionar: ");
		}
	}

	// Editar
	// -------------------------------------------------------------------------------------------
	public void editar() {

		try {
			dao.merge(obj);
			Messages.addGlobalInfo("Editado com sucesso!!!");

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Editar");
		}
	}

	// --------------------------------------------------
	// Get e Set
	// --------------------------------------------------

	public Liferay getObj() {
		return obj;
	}

	public void setObj(Liferay obj) {
		this.obj = obj;
	}

	public List<Liferay> getListaObj() {
		return listaObj;
	}

	public void setListaObj(List<Liferay> listaObj) {
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

	public List<Liferay> getListaSnapshot() {
		return listaSnapshot;
	}

	public int getTotalSnapshot() {
		return totalSnapshot;
	}

	public boolean isTipoCaptura() {
		return tipoCaptura;
	}

	public void setTipoCaptura(boolean tipoCaptura) {
		this.tipoCaptura = tipoCaptura;
	}
}
