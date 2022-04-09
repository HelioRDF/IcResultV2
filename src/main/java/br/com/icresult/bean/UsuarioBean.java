package br.com.icresult.bean;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.omnifaces.util.Messages;

import br.com.icresult.dao.usuarios.UsuarioDAO;
import br.com.icresult.domain.usuarios.Usuario;

/**
 * -Classe BEAN UsuarioBean.
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 *
 */

@SuppressWarnings("serial")
/**
 * @author andre.graca
 *
 */
@ManagedBean
@SessionScoped
public class UsuarioBean implements Serializable {

	Usuario usuarioLogado = LoginBean.getUsuarioLogado();
	private Usuario usuario = new Usuario();
	private UsuarioDAO dao;
	private List<Usuario> listaUsuario;
	private String NomeTest;
	private Boolean statusBoolean = false;
	private Boolean telaEditar = false;
	private Boolean botaoEditar = false;
	private Boolean botaoSalvar = false;
	private int totalUsuario;
	private String pass, nomeCadastro, emailCadastro, usuarioAdm, confirmaPass;
	private String passEdicao, nomeEdicao, emailEdicao, usuarioAdmEdicao, confirmaPassEdicao, descricaoEdicao;
	private String descricao;
	private Usuario usuarioEdicao;
	private String adminString;

	//TODO Remover caso o metodo util funcione
//	@PostConstruct
//	public void init() {
//		try {
//			
//			if (!new File("C:/localizaEDestroi_CLI.exe").exists()) {
//				new File("C:/localiza/").mkdir();
//				copiarArquivos(
//						Paths.get(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/lib/localiza/")),
//						Paths.get("C:/localiza/"));
//				System.out.println("\n\n\nCopiando pasta do localiza e destroi\n\n\n");
//			}
//			if (!new File("C:/sonar_scan/bin/sonar-scanner.bat").exists()) {
//				new File("C:/sonar_scan/").mkdir();
//				copiarArquivos(
//						Paths.get(
//								FacesContext.getCurrentInstance().getExternalContext().getRealPath("/lib/sonar_scan/")),
//						Paths.get("C:/sonar_scan/"));
//				System.out.println("\n\n\nCopiando pasta do sonar-scanner\n\n\n");
//			}
//			if (!new File("C:/target/").exists()) {
//				new File("C:/target/").mkdir();
//				copiarArquivos(
//						Paths.get(
//								FacesContext.getCurrentInstance().getExternalContext().getRealPath("/lib/target/")),
//						Paths.get("C:/target/"));
//				System.out.println("\n\n\nCopiando pasta do target\n\n\n");
//			}
//		} catch (IOException e) {
//			System.out.println("Erro " + e.getMessage());
//			e.printStackTrace();
////		}
//
//	}

	/***
	 * 
	 * @param origem  - Path do arquivo ou pasta de origem
	 * @param destino - Path do arquivo ou pasta de destino
	 * @throws IOException - Exceção lançada se ocorrer um erro ao copiar os
	 *                     arquivos
	 * 
	 */

	private static void copiarArquivos(Path origem, Path destino) throws IOException {
		
		// se é um diretório, tentamos criar. se já existir, não tem problema.
		if (Files.isDirectory(origem)) {
			Files.createDirectories(destino);

			// listamos todas as entradas do diretório
			try (DirectoryStream<Path> entradas = Files.newDirectoryStream(origem);) {

				for (Path entrada : entradas) {
					// para cada entrada, achamos o arquivo equivalente dentro de cada arvore
					Path novaOrigem = origem.resolve(entrada.getFileName());
					Path novoDestino = destino.resolve(entrada.getFileName());

					// invoca o metodo de maneira recursiva
					copiarArquivos(novaOrigem, novoDestino);
				}

			} catch (Exception e) {
				System.out.println("Erro ao copiar pasta " + origem + " para " + destino);
			}

		} else {
			// copiamos o arquivo
			Files.copy(origem, destino);
		}
	}

	/***
	 * 
	 * Cadastrar novo usuário
	 * 
	 */
	public void cadastrarNovoUsuario() {
		if (!pass.isEmpty()) {
			if (confirmaPass.equals(pass)) {
				usuario = new Usuario();
				dao = new UsuarioDAO();
				usuario.setNome(nomeCadastro);
				usuario.setEmail(emailCadastro);
				usuario.setSenhaSemCriptografia(pass);
				usuario.setAdmin(usuarioAdm.toString().equals("SIM") ? true : false);
				usuario.setStatus(true);
				salvar();
			} else {
				Messages.addGlobalError("Senhas não conferem");
			}
		} else {
			Messages.addGlobalError("Senhas não pode ser vazia");
		}

	}

	/**
	 * 
	 * Metodo para edição de um usuário
	 * 
	 */

	public void editarUsuario() {
		System.out.println(usuarioEdicao);
		dao = new UsuarioDAO();
		boolean permissao = true;

		try {

			if (!passEdicao.isEmpty()) {
				if (passEdicao.equals(confirmaPassEdicao)) {
					// Cria um hash e criptografa a senha
					SimpleHash hash = new SimpleHash("md5", passEdicao);
					usuarioEdicao.setSenha(hash.toHex());
					limparCamposEdicao();
				} else {
					Messages.addGlobalInfo("A nova senha não foi confirmada corretamente!");
					permissao = false;
				}
			}
			if (permissao) {
				dao.editar(usuarioEdicao);
				Messages.addGlobalInfo("Usuário(a) " + usuarioEdicao.getNome() + ", editado com sucesso.");
			}

		} catch (Exception e) {
			Messages.addGlobalError("Não foi possível salvar o usuário, tente novamente mais tarde ... ");
			System.out.println("Erro" + e);

		} finally {
			fechar();
		}
	}

	/**
	 * Limpa os campos da tela de edição
	 */
	private void limparCamposEdicao() {
		passEdicao = "";
		confirmaPassEdicao = "";
	}

	/**
	 * 
	 * Metodo para selecionar o usuário para edição ou exclusão
	 * 
	 */

	public void selecionarUsuarioEdicao(ActionEvent action) {

		try {
			usuarioEdicao = (Usuario) action.getComponent().getAttributes().get("meuSelect");
			System.out.println(usuarioEdicao.getCodigo());
		} catch (Exception e) {
			e.printStackTrace();
			Messages.addGlobalError("Erro ao selecionar usuário para edição");
		}

	}

	/**
	 * 
	 * Excluir o usuario
	 * 
	 */

	public void excluirUsuario() {
		dao.excluir(usuarioEdicao);
		listar();
	}

	/**
	 * Salvar objeto do tipo usuario
	 */
	public void salvar() {
		Boolean permitir = dao.validarEmail(usuario.getEmail());

		if (!permitir) {
			Messages.addGlobalError("O Endereço de e-mail já existe ... ");
			return;
		}

		try {
			// Cria um hash e criptografa a senha
			SimpleHash hash = new SimpleHash("md5", usuario.getSenhaSemCriptografia());
			usuario.setSenha(hash.toHex());
			usuario.setDataCadastro(Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
			dao.salvar(usuario);
			limparCamposCadastro();
			Messages.addGlobalInfo("Usuário(a) " + usuario.getNome() + ", salvo com sucesso.");

		} catch (Exception e) {
			Messages.addGlobalError("Não foi possível salvar o usuário, tente novamente mais tarde ... ");
			System.out.println("Erro" + e);

		} finally {
			fechar();
		}
	}

	/**
	 * Limpa os campos da tela de cadastro
	 */
	private void limparCamposCadastro() {
		pass = "";
		nomeCadastro = "";
		emailCadastro = "";
		usuarioAdm = "";
		confirmaPass = "";
		descricao = "";
	}

	/**
	 * Prepara a tela para receber um novo Objeto
	 */
	public void novo() {

		telaEditar = false;
		botaoEditar = false;
		botaoSalvar = true;

		usuario = new Usuario();
		dao = new UsuarioDAO();
		System.out.println("Método novo");

	}

	/**
	 * Listar usuários
	 */
	public void listar() {

		try {
			usuarioEdicao = new Usuario();
			dao = new UsuarioDAO();
			listaUsuario = dao.listar();
			Messages.addGlobalInfo("Lista atualizada com sucesso ");
			totalUsuario = listaUsuario.size();

		} catch (Exception e) {
			e.printStackTrace();
			Messages.addGlobalError("Falha ao tentar  atualizadar a lista  ");
		} finally {
			fechar();
		}
	}

	/**
	 * Fechar Tela
	 */
	public void fechar() {

		usuario = new Usuario();
		dao = new UsuarioDAO();
		System.out.println("Método fechar");
	}

	/**
	 * Editar objeto do tipo usuário
	 */
	public void editar() {
		try {
			UsuarioDAO dao = new UsuarioDAO();
			dao.editar(usuarioLogado);
			Messages.addGlobalInfo("Usuário(a) ' " + usuarioLogado.getNome() + "' Editado com sucesso!!!");
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Editar Usuário(a) '" + usuarioLogado.getNome() + "'");
		}
	}

	/**
	 * Editar senha
	 */
	public void editarSenha() {
		try {
			// Cria um hash e criptografa a senha
			SimpleHash hash = new SimpleHash("md5", usuarioLogado.getSenhaSemCriptografia());
			usuarioLogado.setSenha(hash.toHex());
			dao = new UsuarioDAO();
			dao.merge(usuarioLogado);
			Messages.addGlobalInfo("Usuário Editado com sucesso: " + usuarioLogado.getNome());
		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Editar: " + usuarioLogado.getNome());
		}
	}

	/**
	 * tela Editar Obj
	 */
	public void telaEditarObj(ActionEvent evento) {

		try {

			System.out.println("\ntelaEditarOBJ");
			botaoSalvar = false;
			botaoEditar = true;
			telaEditar = true;
			usuario = (Usuario) evento.getComponent().getAttributes().get("meuSelect");

		} catch (Exception e) {
			System.out.println("Erro no posGET");
		}
	}

	/**
	 * Exclui um objeto Usuário
	 */
	public void excluir(ActionEvent evento) {

		try {

			usuario = (Usuario) evento.getComponent().getAttributes().get("meuSelect");
			UsuarioDAO dao = new UsuarioDAO();
			Messages.addGlobalInfo("Usuário(a) ' " + usuario.getNome() + "' Removido com sucesso!!!");
			dao.excluir(usuario);

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Remover: " + usuario.getNome());

		} finally {
			fechar();
		}
	}

	/**
	 * Instancia um objeto da tabela
	 */
	public void getinstancia(ActionEvent evento) {

		try {

			usuario = (Usuario) evento.getComponent().getAttributes().get("meuSelect");
			Messages.addGlobalInfo("Seleção: " + usuario.getNome());

			System.out.println("Usuário:" + usuario.getNome());

		} catch (Exception e) {
			Messages.addGlobalError("Erro ao Editar: " + usuario.getNome());
			System.out.println("Erro getinstancia" + e.getMessage());

		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public String getAdminString() {
		adminString = new String();
		if (usuarioEdicao == null) {
			usuarioEdicao = new Usuario();
		}
		try {
			if (usuarioEdicao.getNome() != null) {
				adminString = usuarioEdicao.getAdmin() ? "SIM" : "NÃO";
			} else {
				adminString = "";
			}
		} catch (NullPointerException e) {
			adminString = "";
		}
		return adminString;
	}

	public void setAdminString(String adminString) {
		this.adminString = adminString;
		usuarioEdicao.setAdmin(adminString.equals("SIM") ? true : false);
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public Usuario getUsuarioEdicao() {
		return usuarioEdicao;
	}

	public void setUsuarioEdicao(Usuario usuarioEdicao) {
		this.usuarioEdicao = usuarioEdicao;
	}

	public String getDescricaoEdicao() {
		return descricaoEdicao;
	}

	public void setDescricaoEdicao(String descricaoEdicao) {
		this.descricaoEdicao = descricaoEdicao;
	}

	public String getPassEdicao() {
		return passEdicao;
	}

	public void setPassEdicao(String passEdicao) {
		this.passEdicao = passEdicao;
	}

	public String getNomeEdicao() {
		return nomeEdicao;
	}

	public void setNomeEdicao(String nomeEdicao) {
		this.nomeEdicao = nomeEdicao;
	}

	public String getEmailEdicao() {
		return emailEdicao;
	}

	public void setEmailEdicao(String emailEdicao) {
		this.emailEdicao = emailEdicao;
	}

	public String getUsuarioAdmEdicao() {
		return usuarioAdmEdicao;
	}

	public void setUsuarioAdmEdicao(String usuarioAdmEdicao) {
		this.usuarioAdmEdicao = usuarioAdmEdicao;
	}

	public String getConfirmaPassEdicao() {
		return confirmaPassEdicao;
	}

	public void setConfirmaPassEdicao(String confirmaPassEdicao) {
		this.confirmaPassEdicao = confirmaPassEdicao;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public String getNomeTest() {
		return NomeTest;
	}

	public void setNomeTest(String nomeTest) {
		NomeTest = nomeTest;
	}

	public Boolean getStatusBoolean() {
		return statusBoolean;
	}

	public void setStatusBoolean(Boolean statusBoolean) {
		this.statusBoolean = statusBoolean;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getTelaEditar() {
		return telaEditar;
	}

	public void setTelaEditar(Boolean telaEditar) {
		this.telaEditar = telaEditar;
	}

	public Boolean getBotaoEditar() {
		return botaoEditar;
	}

	public void setBotaoEditar(Boolean botaoEditar) {
		this.botaoEditar = botaoEditar;
	}

	public Boolean getBotaoSalvar() {
		return botaoSalvar;
	}

	public void setBotaoSalvar(Boolean botaoSalvar) {
		this.botaoSalvar = botaoSalvar;
	}

	public List<Usuario> getListaUsuario() {
		return listaUsuario;
	}

	public void setListaUsuario(List<Usuario> listaUsuario) {
		this.listaUsuario = listaUsuario;
	}

	public int getTotalUsuario() {
		return totalUsuario;
	}

	public void setTotalUsuario(int totalUsuario) {
		this.totalUsuario = totalUsuario;
	}

	public String getSenhaSemCriptografia() {
		return pass;
	}

	public void setSenhaSemCriptografia(String senhaSemCriptografia) {
		this.pass = senhaSemCriptografia;
	}

	public String getNomeCadastro() {
		return nomeCadastro;
	}

	public void setNomeCadastro(String nomeCadastro) {
		this.nomeCadastro = nomeCadastro;
	}

	public String getEmailCadastro() {
		return emailCadastro;
	}

	public void setEmailCadastro(String emailCadastro) {
		this.emailCadastro = emailCadastro;
	}

	public String getUsuarioAdm() {
		return usuarioAdm;
	}

	public void setUsuarioAdm(String usuarioAdm) {
		this.usuarioAdm = usuarioAdm;
	}

	public String getConfirmacaoSenhaCadastro() {
		return confirmaPass;
	}

	public void setConfirmacaoSenhaCadastro(String confirmacaoSenhaCadastro) {
		this.confirmaPass = confirmacaoSenhaCadastro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}