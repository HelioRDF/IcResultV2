package br.com.icresult.util;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.omnifaces.util.Faces;

import br.com.icresult.bean.LoginBean;
import br.com.icresult.domain.usuarios.Usuario;

/**
 * -Classe p/ validação de autenticação de usuário.
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */
public class AutenticacaoListener implements PhaseListener {

	private static final long serialVersionUID = 5370242150329490062L;
	long tempoInicial = System.currentTimeMillis(); // Captura o tempo inicial da execução da Classe

	/**
	 * Escopo afterPhase de autenticação
	 */
	@SuppressWarnings("static-access")
	@Override
	public void afterPhase(PhaseEvent event) {
		System.out.println("\n--------------------------------------");
		String paginaAtual = Faces.getViewId();
		boolean paginaPublica = false;
		LoginBean loginBean = new LoginBean();
		Usuario usuario = new Usuario();
		if (paginaAtual.contains("login.xhtml") || paginaAtual.contains("index.xhtml")
				|| paginaAtual.contains("telaCadastroUsuario.xhtml") || paginaAtual.contains("recuperar_senha.xhtml")) {
			paginaPublica = true;
		}
		;
		// Verifica se a tela é publica ou privada
		if (!paginaPublica) {
			loginBean = Faces.getSessionAttribute("loginBean");
			// Verifica se o Bean foi criado
			if (loginBean == null) {
				loginBean=null;
				Faces.navigate("/pages/publicas/login.xhtml?faces-redirect=true");
				return;
			}

			// Verifica se o usuário existe
			usuario = loginBean.getUsuarioLogado();
			if (usuario == null) {
				loginBean=null;
				Faces.navigate("/pages/publicas/login.xhtml?faces-redirect=true");
				return;
			}
		}
		System.out.println("\nAfterPhase:" + event.getPhaseId());
		System.out.println("Página Atual:" + paginaAtual);
		long tempoAfter = System.currentTimeMillis(); // Captura o tempo final da execução da Classe
		System.out.printf("\n Tempo de execução: %.3f ms%n", (tempoAfter - tempoInicial) / 1000d); // Imprime o tempo de
																									// execução da
																									// classe em Ms.
	}

	/**
	 * Escopo beforePhase de autenticação
	 */
	@Override
	public void beforePhase(PhaseEvent event) {
		tempoInicial = System.currentTimeMillis(); // Captura o tempo inicial da execução da Classe
	}

	/**
	 * Escopo PhaseId de autenticação
	 */
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}
}
