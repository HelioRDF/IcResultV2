package br.com.icresult.bean;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.dao.complementos.ConfigDAO;
import br.com.icresult.dao.complementos.ConfigGitDAO;
import br.com.icresult.dao.complementos.EntregaDAO;
import br.com.icresult.domain.complementos.Config;
import br.com.icresult.domain.complementos.ConfigGit;
import br.com.icresult.domain.complementos.Entrega;
import br.com.icresult.util.MetodosUteis;

/**
 * -Classe BEAN ConfigBean.
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 11-06-2019
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class ConfigBean implements Serializable {

	private static Logger LOG = LoggerFactory.getLogger(ConfigBean.class);
	private final String SUCESSO = "Salvo com Sucesso!";
	private final String EXCLUSAO = "Exclusão realizada com sucesso!";
	private Config config;
	private ConfigDAO dao;
	private String userSonar;
	private String acessoSonar;
	private String acessoSonarTemp;
	private String urlSonar;
	private String urlGit, userGit, acessoGitTemp, acessoGit;
	@javax.validation.constraints.NotBlank(message = "Novo tipo de entrega não pode ser vazio")
	private String tipoEntrega;
	private List<Entrega> tiposDeEntrega;
	private Entrega valorTipoEntrega;
	private List<Entrega> entregas;
	private List<Entrega> entregasSelecionadas;
	private List<ConfigGit> configuracoesGitSelecionadas, configuracoesGit;
	private List<Config> configuracoesSonar;
	private String nomePMessagesGit = "git";
	private String nomePMessagesEntrega = "entrega";
	private Config configuracaoSelecionada;

	/**
	 * Salva a configuração padrão para o SONAR
	 */
	public void salvarConfiguracaoPadrao() {
		try {
			Config configuracaoPadraoASerSalva = configuracaoSelecionada;
			salvarConfiguracaoPadrao(configuracaoPadraoASerSalva);
			new MetodosUteis().inserirInformacoesPropertiesSONARCli(configuracaoPadraoASerSalva);
			listarInfos();
			FacesContext.getCurrentInstance().addMessage("sonar", new FacesMessage("Seleção realizada com sucesso"));

		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage());
			FacesContext.getCurrentInstance().addMessage("sonar", new FacesMessage("Seleção falhou"));
		}

	}

	private void salvarConfiguracaoPadrao(Config configuracaoPadraoASerSalva) {
		dao = new ConfigDAO();
		List<Config> configuracoesSonarSalva = dao.listar();
		configuracoesSonarSalva.forEach(configuracao -> {
			configuracao.setPadrao(configuracao.equals(configuracaoPadraoASerSalva));
			dao.editar(configuracao);
		});
	}

	/**
	 * Criar uma lista com os objetos do tipo AnaliseCodigoBean
	 * 
	 */
	@PostConstruct
	public void listarInfos() {
		try {
			tiposDeEntrega = new EntregaDAO().listar();
			config = new Config();
			dao = new ConfigDAO();

			Config obj = new ConfigDAO().buscarPorConfiguracaoSelecionada();

			if (obj != null) {
				this.userSonar = obj.getLogin();
				this.acessoSonar = obj.getAcessoSonar();
				this.urlSonar = obj.getUrl();
			}

			entregas = new EntregaDAO().listar();
			configuracoesGit = new ConfigGitDAO().listar();
			configuracoesSonar = new ConfigDAO().listar();

			LOG.info("Lista Atualizada");

		} catch (Exception e) {
			LOG.error("Erro ao lista informações da Analise Em Testes", e);
		}
	}

	public void salvarNovaConfiguracaoSonar() {
		this.acessoSonar = this.acessoSonarTemp;
		config = new Config();
		config.setLogin(this.userSonar);
		config.setAcessoSonar(this.acessoSonar);
		config.setUrl(this.urlSonar);
		verificaSeUsuarioJaExiste();
		dao.salvar(config);
		salvarConfiguracaoPadrao(config);
		new MetodosUteis().inserirInformacoesPropertiesSONARCli(config);
		listarInfos();
		FacesContext.getCurrentInstance().addMessage("sonar", new FacesMessage(SUCESSO));

	}

	/**
	 * Verifica se o usuário de userSonar e urlSonar já existe, se existir, é excluido
	 */
	private void verificaSeUsuarioJaExiste() {
		List<Config> configuracaoIgualExiste = dao.listar().stream()
				.filter(configuracaoSalva -> configuracaoSalva.getLogin().toLowerCase().equals(this.userSonar)
						&& configuracaoSalva.getUrl().toLowerCase().equals(this.urlSonar))
				.collect(Collectors.toList());
		if (!configuracaoIgualExiste.isEmpty()) {
			configuracaoIgualExiste.forEach(configuracao -> {
				dao.excluir(configuracao);
			});
		}
	}

	public void salvarGit() {
		ConfigGit configGit = new ConfigGit();
		this.acessoGit = this.acessoGitTemp;

		configGit.setLogin(this.userGit);
		configGit.setAcessoSonar(this.acessoGit);
		configGit.setUrl(this.urlGit);

		ConfigGitDAO configGitDAO = new ConfigGitDAO();
		List<ConfigGit> contasComMesmoNome = configGitDAO.buscaPorUsuario(configGit);
		if (!contasComMesmoNome.isEmpty()) {
			for (ConfigGit configuracao : contasComMesmoNome) {
				configGitDAO.excluir(configuracao);
			}
		}
		configGitDAO.salvar(configGit);
		listarInfos();

		FacesContext.getCurrentInstance().addMessage(nomePMessagesGit, new FacesMessage(SUCESSO));

	}

	public void salvarTipoEntrega() {
		try {
			Entrega novoTipoEntrega = new Entrega();
			novoTipoEntrega.setTipoEntrega(this.tipoEntrega);
			new EntregaDAO().salvaEntregaComValidacao(novoTipoEntrega);
			FacesContext.getCurrentInstance().addMessage(nomePMessagesEntrega, new FacesMessage(SUCESSO));
			listarInfos();
		} catch (Exception e) {
			LOG.error(e.getCause().toString());
			FacesContext.getCurrentInstance().addMessage(nomePMessagesEntrega,
					new FacesMessage("Erro ao inserir o tipo de entrega contante o administrador!"));
		}
	}

	/**
	 * Exclui as entregas cadastradas selecionadas
	 */
	public void excluirEntregasSelecionadas() {
		if (entregasSelecionadas.isEmpty()) {
			LOG.info("Lista de entrega vazia, assim não é possível salvar uma nova analise");
			FacesContext.getCurrentInstance().addMessage(nomePMessagesEntrega,
					new FacesMessage("Nenhuma entrega selecionada!"));
		} else {
			try {
				EntregaDAO entregaDAO = new EntregaDAO();
				for (Entrega entrega : entregasSelecionadas) {
					entregaDAO.excluir(entrega);
				}
				listarInfos();
				FacesContext.getCurrentInstance().addMessage(nomePMessagesEntrega, new FacesMessage(EXCLUSAO));
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(nomePMessagesEntrega,
						new FacesMessage("Ocorreu um erro ao excluir as entregas selecionadas!"));
			}
		}
	}

	/**
	 * Exclui as configurações do git cadastradas selecionadas
	 */
	public void excluirConfiguracoesGitSelecionadas() {
		if (configuracoesGitSelecionadas.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(nomePMessagesGit,
					new FacesMessage("Nenhuma configuração selecionada!"));
		} else {
			try {
				ConfigGitDAO configuracaoDAO = new ConfigGitDAO();
				for (ConfigGit configuracaoGit : configuracoesGitSelecionadas) {
					configuracaoDAO.excluir(configuracaoGit);
				}
				listarInfos();
				FacesContext.getCurrentInstance().addMessage(nomePMessagesGit, new FacesMessage(EXCLUSAO));
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(nomePMessagesGit,
						new FacesMessage("Ocorreu um erro ao excluir as configurações selecionadas!"));
			}
		}
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public String getTipoEntrega() {
		return tipoEntrega;
	}

	public void setTipoEntrega(String tipoEntrega) {
		this.tipoEntrega = tipoEntrega;
	}

	public String getUserSonar() {
		return userSonar;
	}

	public void setUserSonar(String userSonar) {
		this.userSonar = userSonar;
	}

	public String getUrlSonar() {
		return urlSonar;
	}

	public void setUrlSonar(String urlSonar) {
		this.urlSonar = urlSonar;
	}

	public String getAcessoSonarTemp() {
		return acessoSonarTemp;
	}

	public void setAcessoSonarTemp(String acessoSonarTemp) {
		this.acessoSonarTemp = acessoSonarTemp;
	}

	public String getUserGit() {
		return userGit;
	}

	public String getUrlGit() {
		return urlGit;
	}

	public String getAcessoGitTemp() {
		return acessoGitTemp;
	}

	public void setAcessoGitTemp(String acessoGitTemp) {
		this.acessoGitTemp = acessoGitTemp;
	}

	public void setUserGit(String userGit) {
		this.userGit = userGit;
	}

	public void setUrlGit(String urlGit) {
		this.urlGit = urlGit;
	}

	public List<Entrega> getTiposDeEntrega() {
		return tiposDeEntrega;
	}

	public Entrega getValorTipoEntrega() {
		return valorTipoEntrega;
	}

	public void setValorTipoEntrega(Entrega valorTipoEntrega) {
		this.valorTipoEntrega = valorTipoEntrega;
	}

	public List<Entrega> getEntregas() {
		return entregas;
	}

	public void setEntregas(List<Entrega> entregas) {
		this.entregas = entregas;
	}

	public List<Entrega> getEntregasSelecionadas() {
		if (entregasSelecionadas != null)
			entregasSelecionadas.sort(Entrega.getComparatorPorId());
		return entregasSelecionadas;
	}

	public void setEntregasSelecionadas(List<Entrega> entregasSelecionadas) {
		this.entregasSelecionadas = entregasSelecionadas;
	}

	public List<ConfigGit> getConfiguracoesGit() {
		return configuracoesGit;
	}

	public List<ConfigGit> getConfiguracoesGitSelecionadas() {
		return configuracoesGitSelecionadas;
	}

	public void setConfiguracoesGit(List<ConfigGit> configuracoesGit) {
		this.configuracoesGit = configuracoesGit;
	}

	public void setConfiguracoesGitSelecionadas(List<ConfigGit> configuracoesGitSelecionadas) {
		this.configuracoesGitSelecionadas = configuracoesGitSelecionadas;
	}

	public List<Config> getConfiguracoesSonar() {
		return configuracoesSonar;
	}

	public void setConfiguracoesSonar(List<Config> configuracoesSonar) {
		this.configuracoesSonar = configuracoesSonar;
	}

	public Config getConfiguracaoSelecionada() {
		return configuracaoSelecionada;
	}

	public void setConfiguracaoSelecionada(Config configuracaoSelecionada) {
		this.configuracaoSelecionada = configuracaoSelecionada;
	}
	
	public Config getConfiguracaoSONARSalva() {
		return new ConfigDAO().buscarPorConfiguracaoSelecionada();
	}
}