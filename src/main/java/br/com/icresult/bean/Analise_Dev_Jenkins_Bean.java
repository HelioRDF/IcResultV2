package br.com.icresult.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.dao.complementos.AnaliseDevJenkinsDAO;
import br.com.icresult.dao.complementos.ControleSiglasDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Jenkins;
import br.com.icresult.nomeprojeto.Ambiente;
import br.com.icresult.nomeprojeto.Repositorio;

/**
 * -Classe BEAN Analise_Dev_Jenkins_Bean.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 10-03-2020
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class Analise_Dev_Jenkins_Bean implements Serializable {

	private static Logger log = LoggerFactory.getLogger(Analise_Dev_Jenkins_Bean.class);
	private Analise_Dev_Jenkins analise;
	private AnaliseDevJenkinsDAO dao;
	private List<Analise_Dev_Jenkins> listaAnalise;
	private List<Analise_Dev_Jenkins> listaResultado;
	private int total;
	private String siglaAtual;

	/**
	 * Executa os métodos que inserem Coeficiente, Tipo e DataCommit para as
	 * inspeções mensais
	 */

	public void calculaResultados() {
		try {
			calcularCoeficiente();
			calcNotaInfosAnt();
			buscarGestorNvl1();
			buscarGestorEntrega();
			insereRepositorioAmbienteNomePadraoSonarParaInspecoesNulas();
			log.info("Resultados calculados com sucesso");
		} catch (Exception e) {
			log.error("Erro ao calcular resultados", e);
		}
	}

	private void insereRepositorioAmbienteNomePadraoSonarParaInspecoesNulas() {
		try {

			dao = new AnaliseDevJenkinsDAO();
			List<Analise_Dev_Jenkins> analisesComRepositorioAmbienteNulos = dao.analisesComRepositorioAmbienteNulos();
			for (Analise_Dev_Jenkins analise : analisesComRepositorioAmbienteNulos) {
				dao.editar(insereRepositorioAmbienteNomePadraoSonar(analise));
			}
		} catch (Exception erro) {
			log.error("Erro ao inserir Repositorio, Ambiente e PadaoNomeSonar. Erro " + erro.getMessage());
		}
	}

	public Repositorio buscaRepositorio() {
		return Repositorio.DEVOPS;
	}

	private Analise_Dev_Jenkins insereRepositorioAmbienteNomePadraoSonar(Analise_Dev_Jenkins analise) {
		Ambiente ambiente = Ambiente.DESENVOLVIMENTO;
		Repositorio repositorio = buscaRepositorio();
		analise.setRepositorio(repositorio.getRepositorio());
		analise.setAmbiente(ambiente.getAmbiente());
		analise.setPadraoNomeSonar(analise.getNomeProjeto());

		return analise;
	}

	private void buscarGestorEntrega() {
		dao = new AnaliseDevJenkinsDAO();
		List<Analise_Dev_Jenkins> analiseSemGestorN2 = dao.gestorEntregaNull();
		for (Analise_Dev_Jenkins obj : analiseSemGestorN2) {
			setGestorEntrega(obj);
		}
	}

	private void setGestorEntrega(Analise_Dev_Jenkins analise) {
		try {
			String gestorEntregaEGestorNivel2 = new ControleSiglasDAO()
					.buscaGestorEntregaENivel2PorSigla(analise.getSigla());
			if (gestorEntregaEGestorNivel2 != null) {
				analise.setPainelGestor(gestorEntregaEGestorNivel2);
				dao = new AnaliseDevJenkinsDAO();
				dao.editar(analise);
				log.info("Gestor de nivel 2 inserido com sucesso para a inspeção :" + analise.getId());
			}
		} catch (Exception e) {
			log.error("Erro ao inserir gestor de nivel 2 na inspeção : " + e.getCause());
		}
	}

	private void buscarGestorNvl1() {
		dao = new AnaliseDevJenkinsDAO();
		List<Analise_Dev_Jenkins> analiseSemGestorN1 = dao.gestorNvl1Null();
		for (Analise_Dev_Jenkins obj : analiseSemGestorN1) {
			setGestorNivel1(obj);
		}
	}

	/**
	 * Procura o gestor de nível 2 e insere seu valor na inspeção
	 */
	public void setGestorNivel1(Analise_Dev_Jenkins analise) {
		try {
			String gestorN1 = new ControleSiglasDAO().buscaGestorNivel1PorSigla(analise.getSigla());

			if (gestorN1 != null) {
				analise.setGestorNivel1(gestorN1);
				dao = new AnaliseDevJenkinsDAO();
				dao.editar(analise);
				log.info("Gestor de nivel 1 inserido com sucesso para a inspeção :" + analise.getId());
			}
		} catch (Exception e) {
			log.error("Erro ao inserir gestor de nivel 1 na inspeção " + e.getMessage());
		}
	}

	/**
	 * Exclui Analises selecionadas
	 */

	@SuppressWarnings("unchecked")
	public void excluirAnalises(ActionEvent evento) {
		FacesMessage msg = null;
		try {
			dao = new AnaliseDevJenkinsDAO();
			List<Analise_Dev_Jenkins> listaAnalisesSelecionadas = ((List<Analise_Dev_Jenkins>) evento.getComponent()
					.getAttributes().get("tabela")).stream().filter(c -> c.getSelecionado().equals("ui-icon-check"))
							.collect(Collectors.toList());
			log.info("Analises sendo excluidas :");
			listaAnalisesSelecionadas.forEach(c -> log.info(c.toString()));
			listaAnalisesSelecionadas.forEach(c -> dao.excluir(c));
			msg = new FacesMessage("Info:", "Analises excluidas com sucesso");
		} catch (Exception e) {
			log.error("Erro ao excluir analises!", e);
			msg = new FacesMessage("Erro:", "Erro ao excluir analises!");
		} finally {
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	/**
	 * Selecionar um objeto Analise_DevMensal para exclusão
	 */

	public void selecionarAnalise(ActionEvent evento) {
		try {
			dao = new AnaliseDevJenkinsDAO();
			Analise_Dev_Jenkins analiseParaEditar = (Analise_Dev_Jenkins) evento.getComponent().getAttributes()
					.get("meuSelect");
			analiseParaEditar.setSelecionado(
					analiseParaEditar.getSelecionado().equals("ui-icon-blank") ? "ui-icon-check" : "ui-icon-blank");
			dao.editar(analiseParaEditar);
		} catch (Exception e) {
			log.error("Erro ao selecionar objeto", e);
		}
	}

	/**
	 * Criar uma lista com os objetos do tipo AnaliseCodigoHGBean
	 */
	public void listarInfos() {
		try {

			calculaResultados();
			dao = new AnaliseDevJenkinsDAO();
			dao.limparSelecaoNovasAnalises();
			List<Analise_Dev_Jenkins> listaAnaliseTemp = dao.listar();
			listaAnalise = listaAnaliseTemp;
			listaAnaliseTemp.sort(Analise_Dev_Jenkins.getComparadorPorDataCaptura());
			total = listaAnalise.size();
			Messages.addGlobalInfo("Lista de Análises Mensais Atualizadas!");
			PrimeFaces.current().ajax().update(":formTb:fr:dataTableAnaliseDEV");
			log.info("Lista Atualizada");
		} catch (Exception e) {
			log.error("Erro ao lista informações da Amalise Mensal", e);
		}
	}

	/**
	 * Trata a coluna debito técnico, deixando apenas o numeral dia.
	 * 
	 * @author helio.franca
	 * @since 13-08-2018
	 * 
	 */
	public void tratarDebitoTecnico() {

		dao = new AnaliseDevJenkinsDAO();
		List<Analise_Dev_Jenkins> listaObj = dao.listaDebitoTecnico();

		for (Analise_Dev_Jenkins obj : listaObj) {

			if (obj.getDebitoTecnico().contains("d")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("d");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					debitoTecnicoMinutos = debitoTecnicoMinutos * 24 * 60;
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));
					dao.editar(obj);

				} catch (Exception e) {
					// Erro de conversão String para Interger.
					log.error("Erro tratarDebitoTecnico ", e);
				}

			} else if (obj.getDebitoTecnico().contains("h")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("h");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					debitoTecnicoMinutos = debitoTecnicoMinutos * 60;
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));

				} catch (Exception e) {
					// Erro de conversão String para Interger.
					log.error("Erro de conversão String para Interger. ", e);
				}
			} else if (obj.getDebitoTecnico().contains("m")) {
				String debitoTecnico = obj.getDebitoTecnico();
				String array[] = debitoTecnico.split("m");

				try {
					int debitoTecnicoMinutos = Integer.parseInt(array[0]);
					obj.setDebitoTecnicoMinutos(Integer.toString(debitoTecnicoMinutos));

				} catch (Exception e) {
					// Erro de conversão String para Interger.
					log.error("Erro de conversão String para Interger.  ", e);
				}

				finally {
					dao.editar(obj);
					calcularCoeficiente();
					alteracaoSigla();
				}

			} else {
				obj.setDebitoTecnicoMinutos("0");
				dao.editar(obj);

			}

		}
	}

	/**
	 * Calcula o coeficiente, utilizando a formula (=[@[Débito Técnico em Dias
	 * ]]/[@[Total Issus]] ).
	 * 
	 * @author helio.franca
	 * @since 13-08-2018
	 * 
	 */
	public void calcularCoeficiente() {

		dao = new AnaliseDevJenkinsDAO();
		List<Analise_Dev_Jenkins> listaObj = dao.listaCoeficiente();

		for (Analise_Dev_Jenkins obj : listaObj) {
			double totalIssues = (obj.getIssuesMuitoAlta() + obj.getIssuesAlta() + obj.getIssuesMedia()
					+ obj.getIssuesBaixa());
			double debitoDias = 0;
			double coeficiente = 0;
			try {
				int numtemp = (Integer.parseInt(obj.getDebitoTecnicoMinutos()) / 60 / 24);
				debitoDias = numtemp;
				coeficiente = 0;
			} catch (Exception e) {
				log.debug("Erro ao calcular coeficiente", e);
			}

			if (totalIssues > 0) {
				coeficiente = debitoDias / totalIssues;
			}
			obj.setCoeficiente(Double.toString(coeficiente));
			dao.editar(obj);
		}
		alteracaoSigla();
	}

	/**
	 * Retorna Uma lista do tipo Analise_Dev_Jenkins
	 * 
	 * @author helio.franca
	 * @since 13-08-2018
	 * @return Lista de Analise_Dev_Jenkins
	 */
	public List<Analise_Dev_Jenkins> listarCodigoDev() {
		try {
			dao = new AnaliseDevJenkinsDAO();
			List<Analise_Dev_Jenkins> listaAnaliseTemp = dao.listar();
			listaAnalise = listaAnaliseTemp;
			log.info("Lista Atualizada!");
			return listaAnaliseTemp;

		} catch (Exception e) {
			log.error("Erro ao listarCodigoDev", e);
			return new ArrayList<Analise_Dev_Jenkins>();
		}
	}

	/**
	 * Calcula a nota da análise atual, e seta nota anterior e a quantidade de
	 * linhas anterior.
	 */
	public void calcNotaInfosAnt() {

		dao = new AnaliseDevJenkinsDAO();
		List<Analise_Dev_Jenkins> listaAnaliseTemp = dao.listaNotaVazia();

		for (Analise_Dev_Jenkins obj : listaAnaliseTemp) {

			if (obj.getLinhaCodigo() == 1) {
				obj.setNotaProjeto("0");
			} else {
				log.info("Calculando info anteriores para " + obj.getNomeProjeto());
				int resultado;
				double blocker;
				double critical;
				double major;
				int linhaCodigo;
				blocker = obj.getIssuesMuitoAlta();
				critical = obj.getIssuesAlta();
				major = obj.getIssuesMedia();
				linhaCodigo = obj.getLinhaCodigo();
				blocker = ((blocker / linhaCodigo) * 10);
				critical = ((critical / linhaCodigo) * 5);
				major = major / linhaCodigo;

				double soma = blocker + critical + major;
				log.debug("Soma: " + soma);
				double nota = ((1 - soma) * 100);

				if (nota < 0) {

					obj.setNotaProjeto("0");

				} else {

					DecimalFormat df = new DecimalFormat("###,###");
					if (soma >= 0) {
						resultado = Integer.parseInt(df.format(nota));

					} else {
						resultado = 0;
					}

					obj.setNotaProjeto(String.valueOf(resultado));
				}
			}

			try {
				Analise_Dev_Jenkins objAnterior = dao.buscarAnterior(obj.getId(), obj.getSigla(), obj.getNomeProjeto());
				obj.setNotaAnterior(objAnterior.getNotaProjeto());
				obj.setLinhaCodigoAnt(objAnterior.getLinhaCodigo());

			} catch (Exception e) {
				// Objeto anterior não existe.
				log.error("Erro Objeto anterior não existe.", e);
			} finally {
				dao = new AnaliseDevJenkinsDAO();
				dao.editar(obj);
			}
		}
	}

	/**
	 * Identifica se ocorreu alteração na sigla.
	 * 
	 */
	public void alteracaoSigla() {

		dao = new AnaliseDevJenkinsDAO();
		List<Analise_Dev_Jenkins> listaAnaliseTemp = dao.listaTipoVazio();

		for (Analise_Dev_Jenkins obj : listaAnaliseTemp) {
			int linhasAtual;
			int linhasAnt;
			linhasAtual = obj.getLinhaCodigo();
			linhasAnt = obj.getLinhaCodigoAnt();
			String codigoAlterado = "LEGADO";

			if (linhasAtual != linhasAnt) {
				codigoAlterado = "NOVO";
			}

			obj.setCodigoAlterado(codigoAlterado);
			dao.editar(obj);

		}
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public int getTotal() {
		return total;
	}

	public Analise_Dev_Jenkins getAnalise() {
		return analise;
	}

	public void setAnalise(Analise_Dev_Jenkins analise) {
		this.analise = analise;
	}

	public List<Analise_Dev_Jenkins> getListaAnalise() {
		return listaAnalise;
	}

	public void setListaAnalise(List<Analise_Dev_Jenkins> listaAnalise) {
		this.listaAnalise = listaAnalise;
	}

	public List<Analise_Dev_Jenkins> getListaResultado() {
		return listaResultado;
	}

	public void setListaResultado(List<Analise_Dev_Jenkins> listaResultado) {
		this.listaResultado = listaResultado;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getSiglaAtual() {
		return siglaAtual;
	}

	public void setSiglaAtual(String siglaAtual) {
		this.siglaAtual = siglaAtual;
	}
	

}