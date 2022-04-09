package br.com.icresult.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.icresult.dao.complementos.AnotacoesDAO;
import br.com.icresult.domain.complementos.Anotacoes;

/**
 * 
 * @author andre.graca
 * @since 08-01-2019
 *
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class AnotacoesBean implements Serializable {

	private List<Anotacoes> listaNotas;

	private Anotacoes novaAnotacao;
	private int total;

	/**
	 * Incia após a criação de um objeto do tipo AnotacoesBean
	 */
	@PostConstruct
	public void init() {
		listarInfos();
		novaAnotacao = new Anotacoes();

	}

	/**
	 * Adiciona uma nova anotação ao banco de dados
	 */
	public void adicionarAnotacao() {
		String mensagem = novaAnotacao.getMensagem();

		if (mensagem.length() > 500) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Anotação não adicionada, mensagem maior que 500 caracteres!"));
		} else {
			Anotacoes note = new Anotacoes();
			note.setMensagem(novaAnotacao.getMensagem());
			note.setUsuario(LoginBean.getUsuarioLogado().getNome());
			new AnotacoesDAO().salvar(note);
			listarInfos();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Anotação adicionada com sucesso!!"));
			novaAnotacao = new Anotacoes();
		}
	}

	/**
	 * Cria uma lista de Anotacoes
	 */
	public void listarInfos() {
		listaNotas = new AnotacoesDAO().listar();
		total = listaNotas.size();
	}

	// ---------------------
	// Getters e Setters
	// ---------------------

	public void setNovaAnotacao(Anotacoes novaAnaotacao) {
		this.novaAnotacao = novaAnaotacao;
	}

	public Anotacoes getNovaAnotacao() {
		return novaAnotacao;
	}

	public int getTotal() {
		return total;
	}

	public List<Anotacoes> getListaNotas() {
		return listaNotas;
	}

}
