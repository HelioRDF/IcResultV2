package br.com.icresult.bean;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import br.com.icresult.dao.usuarios.UsuarioDAO;
import br.com.icresult.domain.usuarios.Usuario;
import br.com.icresult.email.EnviarEmail;
import br.com.icresult.util.MetodosUteis;

/**
 * -Classe BEAN ControleSiglasBean.
 * 
 * @author helio.franca
 * @version v2.4.1
 * @since 04-10-2018
 *
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

	private int tipoLogin = 1;
	private Usuario usuario;
	private static Usuario usuarioLogado;
	private Boolean userlogadoB = false;
	private String textoExemplo;

	@PostConstruct
	public void init() {
		usuario = new Usuario();
		textoExemplo = new String();
	}

	/**
	 * 
	 * Redireciona para uma nova página
	 * 
	 * 
	 */

	public void redPaginaRecuperacaoSenha() {
		try {
			Faces.redirect("./pages/publicas/recuperar_senha.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Gera uma nova senha
	 * 
	 */

	private String geradorSenha() {

		// Referência : http://www.guj.com.br/t/gerador-de-senha-alfanumerico/40241
		String[] carct = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h",
				"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C",
				"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
				"Y", "Z" };

		String retorno = "";

		for (int x = 0; x < 10; x++) {
			int j = (int) (Math.random() * carct.length);
			retorno += carct[j];
		}

		return retorno;
	}

	/***
	 * 
	 * Envia e-mail para o usuário que perdeu a senha
	 * 
	 */

	public void emailParaRecuperacaoSenha() {

		String emailDestino = usuario.getEmail();
		UsuarioDAO dao = new UsuarioDAO();
		Usuario usuarioEsqueceuSenha = dao.localizarUsuario(emailDestino);
		if (usuarioEsqueceuSenha != null) {
			String novaSenha = geradorSenha();
			boolean emailEnviado = new EnviarEmail().emailHtmlRecuperarSenha("Sua senha nova é " + novaSenha,
					"Nova senha da aplicação", "", "", emailDestino);
			if (emailEnviado) {
				SimpleHash hash = new SimpleHash("md5", novaSenha);
				usuarioEsqueceuSenha.setSenha(hash.toHex());
				dao.editar(usuarioEsqueceuSenha);
			}
		} else {
			Messages.addGlobalError("E-mail não encontrado");
		}

	}

	/**
	 * Login
	 * 
	 */
	@PostConstruct
	public void iniciar() {
		usuario = new Usuario();

	}

	/**
	 * Autenticar usuário na aplicação.
	 */
	public void autenticar() {
		if (tipoLogin == 1) {
			try {
				UsuarioDAO usuarioDAO = new UsuarioDAO();
				usuarioLogado = usuarioDAO.autenticar(usuario.getEmail().trim(), usuario.getSenha());

				if (usuarioLogado == null) {
					Messages.addGlobalWarn("Usuário e/ou senha, incorretos");
					return;
				} else {
					// Verifica se usuário está Ativo
					if (!usuarioLogado.getStatus()) {
						Messages.addGlobalError("Usuário Desativado.");
						usuarioLogado = null;
						return;
					}
				}
				// Usuário Ok...
				userlogadoB = true;
				Faces.redirect("./pages/administrativas/diaria.xhtml");
				usuarioLogado.setUltimoLogin(Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
				usuarioDAO.editar(usuarioLogado);
				MetodosUteis metodosUteis = new MetodosUteis();
				metodosUteis.copiaArquivosNecessarios();

			} catch (IOException e) {
				Messages.addGlobalError("Erro  ");
			}
		} // fim do If
	}

	// Logoff
	// -------------------------------------------------------------------------------------------
	/**
	 * Sair da aplicação
	 */
	public void sair() {
		try {
			userlogadoB = false;
			usuarioLogado = null;

			// Destroi as sessões após loggof do usuário.
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
			session.invalidate();

			// Redireciona para a página de login
			Faces.redirect("./pages/publicas/login.xhtml");
			Messages.addGlobalInfo("Logout");

			return;
		} catch (IOException e) {
			Messages.addGlobalError("Erro  ");
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------------------------
	// Getters e Setters
	// ------------
	public static Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public static void setUsuarioLogado(Usuario usuarioLogado) {
		LoginBean.usuarioLogado = usuarioLogado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getTipoLogin() {
		return tipoLogin;
	}

	public void setTipoLogin(int tipoLogin) {
		this.tipoLogin = tipoLogin;
	}

	public Boolean getUserlogadoB() {
		return userlogadoB;
	}

	public void setUserlogadoB(Boolean userlogadoB) {
		this.userlogadoB = userlogadoB;
	}

	public String getTextoExemplo() {
		return textoExemplo;
	}

	public void setTextoExemplo(String textoExemplo) {
		this.textoExemplo = textoExemplo;
	}

	// ------------------------------------------------------------

}
