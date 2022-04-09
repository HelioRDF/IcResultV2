package br.com.icresult.bean;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.icresult.dao.complementos.Analise_SonarDAO;
import br.com.icresult.domain.complementos.Analise_Sonar;
import br.com.icresult.email.Email;
import br.com.icresult.email.EnviarEmail;
import jxl.common.Logger;

/**
 * -Classe BEAN Analise_SonarBean.
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 11-06-2019
 *
 */

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class Analise_SonarBean implements Serializable {

	private static Logger log = Logger.getLogger(Analise_SonarBean.class);
	private Analise_SonarDAO dao;
	private List<Analise_Sonar> listaAnalise, listaAnaliseSelecionada;
	private int total;
	private String origemEmail, chaveEmail, destinoEmail, valorEntregaTexto;
	private Date filtroDeDataCaptura;

	/**
	 * Filtro Data para componenets h:inputText pt:type="date"
	 * 
	 * @return filtra data maiores que o filtro
	 * 
	 * @param value  - valores da tabela
	 * @param filter - valor vindo do filtro da tabela
	 * @param locale - Locale para pt_BR
	 */
	public boolean filtroData(Object value, Object filter, Locale locale) {

		if (filter == null)
			return true;

		if (filter.toString().isEmpty())
			return true;

		if (value == null)
			return false;

		SimpleDateFormat formatadorValores = new SimpleDateFormat("yyyy-MM-dd");

		try {

			Date dataFiltrada = (Date) formatadorValores.parse(value.toString());
			Date filtroFormatadoParaData = (Date) formatadorValores.parse(filter.toString());

			log.info(dataFiltrada.toString());
			log.info(filtroFormatadoParaData.toString());

			if (dataFiltrada.equals(filtroFormatadoParaData)) {
				return true;
			}
			if (dataFiltrada.getTime() > filtroFormatadoParaData.getTime()) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			log.error("Erro ao converter : " + value);
		}

		return false;
	}

	/**
	 * Criar uma lista com os objetos do tipo AnaliseCodigoHGBean
	 */
	public void listarInfos() {
		try {
			dao = new Analise_SonarDAO();
			listaAnalise = dao.listar();
			listaAnalise.sort(Analise_Sonar.getComparadorPorDataCaptura());
			total = listaAnalise.size();
			log.info("Lista Atualizada");
		} catch (Exception e) {
			log.error("Erro ao lista informações da Analise Em Testes", e);
		}
	}

	/**
	 * Exclui as analises selecionadas
	 */
	public void excluirAnalisesSelecionadas() {
		String idPMessages = "analises";
		if (listaAnaliseSelecionada.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(idPMessages, new FacesMessage("Nenhuma analise selecionada!"));
		} else {
			try {
				Analise_SonarDAO dao = new Analise_SonarDAO();
				for (Analise_Sonar analise : listaAnaliseSelecionada) {
					dao.excluir(analise);
				}
				listarInfos();
				FacesContext.getCurrentInstance().addMessage(idPMessages,
						new FacesMessage("Exclusão realizada com sucesso!"));
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(idPMessages,
						new FacesMessage("Ocorreu um erro ao excluir as analises selecionadas!"));
			}
		}
	}

	/**
	 * 
	 * Envia o e-mail de acordo com o tipo de Entrega
	 * 
	 */
	public void enviaEmailGenerico() {
		try {
			dao = new Analise_SonarDAO();
			Date dataCaptura = filtroDeDataCaptura;
			String tipoEntrega = valorEntregaTexto;
			String titulo = "Inspeção " + tipoEntrega;
			StringBuilder conteudoEmailEmHtml = new StringBuilder();

			List<Analise_Sonar> listaParaSerEnviadaPorEmail = dao.listarPorTipoEntregaDataCaptura(tipoEntrega,
					dataCaptura);

			for (Analise_Sonar analise : listaParaSerEnviadaPorEmail) {
				conteudoEmailEmHtml.append(conteudoAnaliseEmHtml(analise));
			}

			String tabelaDeEnvioEmail = criaTabelaECabecalho(conteudoEmailEmHtml.toString());

			Email email = new Email(tabelaDeEnvioEmail, titulo, chaveEmail, origemEmail, destinoEmail);

			EnviarEmail.enviarInspecoesGenericas(email);

		} catch (Exception e) {
			log.error("Erro ao enviar o e-mail : " + e.getStackTrace());
		}

	}

	private String conteudoAnaliseEmHtml(Analise_Sonar analise) {
		StringBuilder conteudoEmailEmHtml = new StringBuilder();
		conteudoEmailEmHtml.append("<tr style='height:12.5pt'>");
		conteudoEmailEmHtml.append(formataTDParaEmail(38.0, analise.getSigla()));
		conteudoEmailEmHtml.append(formataTDParaEmail(150.0, analise.getModulo()));
		conteudoEmailEmHtml.append(formataTDParaEmail(62.0, defineMilhar(analise.getLinhaCodigo())));
		conteudoEmailEmHtml.append(formataTDBlockerParaEmail(42.0, analise.getBlockers()));
		conteudoEmailEmHtml.append(formataTDCriticalsParaEmail(42.0, analise.getCriticals()));
		conteudoEmailEmHtml.append(formataTDParaEmail(42.0, defineMilhar(analise.getMajors())));
		conteudoEmailEmHtml.append(formataTDNotaParaEmail(42.0, analise));
		conteudoEmailEmHtml.append(formataTDParaEmail(42.0, analise.getCobertura()));
		conteudoEmailEmHtml.append(formataTDParaEmail(42.0, analise.getNovaCobertura()));
		conteudoEmailEmHtml
				.append(formataTDParaEmail(70.0, defineMilhar(Integer.parseInt(analise.getDebitoTecnicoEmMinutos()))));
		conteudoEmailEmHtml.append(formataTDLinkParaEmail(34.0, analise.getUrl()));
		conteudoEmailEmHtml.append(formataTDDataCapturaParaEmail(42.0, analise.getDataCaptura()));
		conteudoEmailEmHtml.append("</tr>");
		return conteudoEmailEmHtml.toString();
	}

	private Object formataTDDataCapturaParaEmail(Double tamanhoTD, Date dataCaptura) {

		return incluiTdfoParaAnaliseDoEmail(tamanhoTD,
				incluiParagrafoParaAnaliseDoEmail(incluiSpanParaAnaliseDoEmail(tratarDataHoraTexto(dataCaptura))));
	}

	public String tratarDataHoraTexto(Date dataCaptura) {

		LocalDate retorno = dataCaptura.toInstant().atZone(ZoneId.of("Etc/GMT+3")).toLocalDate();

		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		return retorno.format(formatador);
	}

	private String formataTDLinkParaEmail(Double tamanhoTD, String url) {
		String tagA = "<a href='" + url
				+ "' target='_blank' rel='noopener noreferrer' data-auth='NotApplicable'>Link</a>";
		return incluiTdfoParaAnaliseDoEmail(tamanhoTD,
				incluiParagrafoParaAnaliseDoEmail(incluiSpanComTextoCor("rgb(5, 99, 193)", tagA)));
	}

	private String formataTDNotaParaEmail(Double tamanhoTD, Analise_Sonar analise) {

		Integer resultado;

		if (analise.getLinhaCodigo() == 1) {
			resultado = 0;
		} else {
			double blocker;
			double critical;
			double major;
			int linhaCodigo;
			blocker = analise.getBlockers();
			critical = analise.getCriticals();
			major = analise.getMajors();
			linhaCodigo = analise.getLinhaCodigo();
			blocker = ((blocker / linhaCodigo) * 10);
			critical = ((critical / linhaCodigo) * 5);
			major = (major / linhaCodigo);
			double soma = blocker + critical + major;
			double nota = ((1 - soma) * 100);
			if (nota < 0) {
				resultado = 0;
			} else {

				DecimalFormat df = new DecimalFormat("###,###");
				if (soma >= 0) {
					resultado = Integer.parseInt(df.format(nota));

				} else {
					resultado = 0;
				}
			}
		}
		return incluiTdfoParaAnaliseDoEmail(tamanhoTD, incluiParagrafoParaAnaliseDoEmail(
				incluiSpanComTextoCor("rgb(0, 112, 192)", (resultado < 0 ? "0" : resultado) + "%")));
	}

	private String formataTDCriticalsParaEmail(Double tamanhoTD, Integer criticals) {
		return incluiTdfoParaAnaliseDoEmail(tamanhoTD,
				incluiParagrafoParaAnaliseDoEmail(incluiSpanComTextoCor("rgb(255, 192, 0)", defineMilhar(criticals))));
	}

	private String formataTDBlockerParaEmail(Double tamanhoTD, Integer blockers) {
		return incluiTdfoParaAnaliseDoEmail(tamanhoTD,
				incluiParagrafoParaAnaliseDoEmail(incluiSpanComTextoCor("red", defineMilhar(blockers))));
	}

	private String incluiSpanComTextoCor(String cor, String texto) {
		return "<span style='font-size: 10pt; font-family: Arial, sans-serif, serif, EmojiFont; color: " + cor + ";'>"
				+ texto + "</span>";
	}

	private String formataTDParaEmail(Double tamanhoTD, String texto) {
		return incluiTdfoParaAnaliseDoEmail(tamanhoTD,
				incluiParagrafoParaAnaliseDoEmail(incluiSpanParaAnaliseDoEmail(texto)));
	}

	private String incluiTdfoParaAnaliseDoEmail(Double width, String texto) {
		return "<td nowrap='' valign='bottom' style='width:" + width.toString()
				+ "pt; border-top:none; border-left:solid windowtext 1.0pt; border-bottom:solid windowtext 1.0pt; border-right:solid windowtext 1.0pt; padding:0cm 3.5pt 0cm 3.5pt; height:12.5pt'>"
				+ texto + "</td>";

	}

	private String incluiParagrafoParaAnaliseDoEmail(String texto) {
		return "<p class='x_MsoNormal'>" + texto + "</p>";
	}

	private String incluiSpanParaAnaliseDoEmail(String texto) {
		return "<span style='font-size: 10pt; font-family: Arial, sans-serif, serif, EmojiFont;'>" + texto + "</span>";
	}

	private String defineMilhar(Integer numeroInteiro) {
		Double numeroConvertidoDouble = Double.parseDouble(numeroInteiro.toString());
		DecimalFormat df = new DecimalFormat("###,###,###,###,###");
		return df.format(numeroConvertidoDouble);
	}

	public String criaTabelaECabecalho(String corpoTabela) {
		StringBuilder conteudoEmailEmHtml = new StringBuilder();
		conteudoEmailEmHtml.append("<table border='0' cellspacing='0' cellpadding='0' style='border-collapse:collapse'>"
				+ "<tbody>" + "<tr>");
		String[] cabecalhos = { "Siglas", "Nome do Projeto", "Linhas. Cód", "Blocker", "Critical", "Major", "Nota", "Cobertura","Nova Cobertura", "Deb. Téc. em Minutos",
				"Sonar", "Data Captura" };

		for (String cabecalho : cabecalhos) {
			conteudoEmailEmHtml.append("<td style='border:solid windowtext 1.0pt; background:#5B9BD5'>"
					+ "<p align='center' style='text-align:center'><span style='font-family: Arial, sans-serif, serif, EmojiFont; color: white'>");
			conteudoEmailEmHtml.append(cabecalho);
			conteudoEmailEmHtml.append("</span></p></td>");
		}
		conteudoEmailEmHtml.append("</tr>");
		conteudoEmailEmHtml.append(corpoTabela);
		conteudoEmailEmHtml.append("</tbody>");
		conteudoEmailEmHtml.append("</table>");
		return conteudoEmailEmHtml.toString();
	}

	// Get e Set
	// ------------------------------------------------------------------------------------------------------------------------------------------------------

	public void setListaAnaliseSelecionada(List<Analise_Sonar> listaAnaliseSelecionada) {
		this.listaAnaliseSelecionada = listaAnaliseSelecionada;
	}

	public List<Analise_Sonar> getListaAnaliseSelecionada() {
		return listaAnaliseSelecionada;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Analise_Sonar> getListaAnalise() {
		return listaAnalise;
	}

	public void setListaAnalise(List<Analise_Sonar> listaAnalise) {
		this.listaAnalise = listaAnalise;
	}

	public String getOrigemEmail() {
		return origemEmail;
	}

	public void setOrigemEmail(String origemEmail) {
		this.origemEmail = origemEmail;
	}

	public String getChaveEmail() {
		return chaveEmail;
	}

	public void setChaveEmail(String chaveEmail) {
		this.chaveEmail = chaveEmail;
	}

	public String getDestinoEmail() {
		return destinoEmail;
	}

	public void setDestinoEmail(String destinoEmail) {
		this.destinoEmail = destinoEmail;
	}

	public String getValorEntregaTexto() {
		return valorEntregaTexto;
	}

	public void setValorEntregaTexto(String valorEntregaTexto) {
		this.valorEntregaTexto = valorEntregaTexto;
	}

	public Date getFiltroDeDataCaptura() {
		return filtroDeDataCaptura;
	}

	public void setFiltroDeDataCaptura(Date filtroDeDataCaptura) {
		this.filtroDeDataCaptura = filtroDeDataCaptura;
	}

}