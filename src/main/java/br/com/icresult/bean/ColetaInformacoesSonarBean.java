package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.icresult.dao.complementos.ConfigDAO;
import br.com.icresult.domain.complementos.Config;
import br.com.icresult.model.Captura;
import br.com.icresult.util.MetodosUteis;

/**
 * 
 * @author andre.graca
 * 
 */

public class ColetaInformacoesSonarBean {

	private static Logger LOG = LoggerFactory.getLogger(ColetaInformacoesSonarBean.class);
	private final static String USER_AGENT = "Chrome/33.0.1750.117";
	private static String URL_API_SONAR;
	private static String USER;
	private static String PASS;
	private final static String COMPONENT_KEYS = "api/issues/search?componentKeys=";
	private final static String TOTAL = "total";
	private final static String BRANCH_MASTER = "master";
	private final static String BRANCH_DEVELOP = "develop";

	public ColetaInformacoesSonarBean() {
		Config configuracoesSonarSelecionada = new ConfigDAO().buscarPorConfiguracaoSelecionada();
		ColetaInformacoesSonarBean.USER = configuracoesSonarSelecionada.getLogin();
		ColetaInformacoesSonarBean.PASS = configuracoesSonarSelecionada.getAcessoSonar();
		ColetaInformacoesSonarBean.URL_API_SONAR = configuracoesSonarSelecionada.getUrl();
	}

	public ColetaInformacoesSonarBean(Config config) {
		ColetaInformacoesSonarBean.USER = config.getLogin();
		ColetaInformacoesSonarBean.PASS = config.getAcessoSonar();
		ColetaInformacoesSonarBean.URL_API_SONAR = config.getUrl();
	}

	/**
	 * 
	 * Informa a data execu????o de um painel
	 * 
	 * @param chave - String com a chave do painel Sonar
	 * @return - retorna a data de execu????o em que est?? o painel.
	 * 
	 */

	public Date getDataSonar(String chave) {
		Date dataExecucao = null;
		try {
			Captura captura = getInfo(new Captura(), chave, BRANCH_MASTER);
			dataExecucao = captura == null ? null : captura.getDataSonar();
		} catch (Exception e) {
			LOG.error("Erro ao capturar data do painel sonar", e);
		}
		return dataExecucao;
	}

	/**
	 * 
	 * Informa a versao da execu????o de um painel
	 * 
	 * @param chave - String com a chave do painel Sonar
	 * @return - retorna a vers??o em que est?? o painel.
	 * 
	 */

	public String getVersao(String chave) {
		String versao = null;
		try {
			Captura captura = getInfo(new Captura(), chave, BRANCH_MASTER);
			if (captura != null)
				versao = captura.getVersao();
		} catch (Exception e) {
			LOG.error("Erro ao capturar vers??o do sonar", e);
		}
		return versao;
	}

	/**
	 * Inicia a captura de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser capturada
	 * @return - retorna um objeto do tipo Captura que contem as informa????es do
	 *         painel
	 */
	public Captura getSonarApi(String chavePainel) {
		ColetaInformacoesSonarBean teste = new ColetaInformacoesSonarBean();
		Captura automacao = new Captura();
		String branch = BRANCH_DEVELOP;
		try {

			if (teste.getInfo(automacao, chavePainel, branch) == null) {
				branch = BRANCH_MASTER;
				if (teste.getInfo(automacao, chavePainel, branch) == null) {
					return null;
				}
			}
			automacao.setBranch(branch);
			teste.getIssues(automacao, chavePainel, branch);
			teste.getVulnerabilidadesPorSeveridade(automacao, chavePainel, branch);

		} catch (Exception e) {
			LOG.error("Erro ao capturar informa????es do painel " + chavePainel, e);
			return null;
		}
		if (automacao.getLinhaCodigo() == 0) {
			automacao.setLinhaCodigo(1);
		}
		return automacao;
	}

	/**
	 * Coleta todas as vulnerabilidades de um painel do Sonar por severidade
	 * 
	 * @param automacao  - objeto do tipo Captura que contem algumas informa????es
	 *                   sobre um painel Sonar e que recebe o valor de suas
	 *                   vulnerabilidades
	 * @param chaveTeste - chave do painel a ser coletado as vulnerabilidades
	 * @param branch
	 * @return - retorna um objeto do tipo Captura com as vulnerabilidades inseridas
	 */
	private Captura getVulnerabilidadesPorSeveridade(Captura automacao, String chaveTeste, String branch) {
		try {
			automacao.setVulnerabilityMuitoAlta(getVulnerabilidadeMuitoAlta(chaveTeste, branch));
			automacao.setVulnerabilityAlta(getVulnerabilidadeAlta(chaveTeste, branch));
			automacao.setVulnerabilityMedia(getVulnerabilidadeMedia(chaveTeste, branch));
			automacao.setVulnerabilityBaixa(getVulnerabilidadeBaixa(chaveTeste, branch));
			automacao.setVulnerabilityMuitoBaixa(getVulnerabilidadeMuitoBaixa(chaveTeste, branch));
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidades por severidade", e);
			try {
				Thread.sleep(1000);
				automacao = getVulnerabilidadesPorSeveridade(automacao, chaveTeste, branch);
			} catch (Exception e2) {
				e2.getStackTrace();
			}
		}

		return automacao;
	}

	/**
	 * Coleta nome, vers??o e data de execu????o de um painel Sonar
	 * 
	 * @param automacao   - objeto do tipo Captura onde ser?? inserido as informa????es
	 * @param chavePainel - chave do painel para se localizar as informa????es
	 * @param branch
	 * @return - retorna um objeto do tipo Captura com as informa????es inseridas
	 */
	private Captura getInfo(Captura automacao, String chavePainel, String branch) throws InterruptedException {

		try {

			String url = URL_API_SONAR + "api/components/show?component=" + chavePainel + parametroBranch(branch);

			String requisicao = executaRequisicao(url);
			JSONObject jObj = new JSONObject(requisicao);
			if (jObj.toString().contains("errors")) {
				return null;
			}
			String versao = new String();
			JSONObject component = getComponent(jObj);
			if (component.has("version")) {
				versao = component.getString("version");
			}
			String nome = component.getString("name");
			if (component.has("analysisDate")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				automacao
						.setDataSonar(
								Date.from(LocalDateTime
										.parse(component.getString("analysisDate").replaceAll("[+-]\\d{4}", "")
												.replaceAll("T", " "), formatter)
										.atZone(ZoneId.systemDefault()).toInstant()));
			} else {
				return null;
			}
			automacao.setVersao(versao);
			automacao.setNomeProjeto(nome);
			String urlComHttps = new MetodosUteis().verificaLinkSONAR(URL_API_SONAR);
			// TODO VERIFICAR PARAMETRO BRANCH
			automacao.setUrl(urlComHttps + "dashboard?id=" + chavePainel + parametroBranch(branch));
			Date dataExecucao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
			automacao.setDataCaptura(dataExecucao);
		} catch (Exception e) {
			LOG.error("Erro ao capturar informa????es", e);
			Thread.sleep(1000);
			automacao = getInfo(automacao, chavePainel, branch);
		}
		return automacao;

	}

	private String parametroBranch(String branch) {
		return "&branch=" + branch;
	}

	/**
	 * Retorna um JSONObject com nome de component
	 * 
	 * @param jObj = objeto Json que contem o component
	 * 
	 */

	private JSONObject getComponent(JSONObject jObj) {
		return jObj.getJSONObject("component");
	}

	/**
	 * Retorna um JSONArray com nome de measures
	 * 
	 * @param jObj = objeto Json que contem o measures
	 * 
	 */

	private JSONArray getMeasues(JSONObject jObj) {
		return jObj.getJSONArray("measures");
	}

	/**
	 * Coleta as issues de um painel Sonar
	 * 
	 * @param automacao   - objeto do tipo Captura onde ser?? inserido os valores das
	 *                    issues
	 * @param chavePainel - chave do painel Sonar a ser localizado as issues
	 * @param branch      -
	 * @return - retorna um objeto do tipo Captura onde foi inserido as issues
	 */
	private Captura getIssues(Captura automacao, String chavePainel, String branch) throws InterruptedException {

		try {
			String url = URL_API_SONAR + "api/measures/component?componentKey=" + chavePainel + "&metricKeys=bugs,"
					+ "ncloc," + "code_smells," + "vulnerabilities," + "critical_violations," + "major_violations,"
					+ "minor_violations," + "info_violations," + "sqale_index," + "coverage," + "blocker_violations,"
					+ "new_blocker_violations," + "new_critical_violations," + "new_major_violations," + "new_lines,"
					+ "new_coverage" + parametroBranch(branch);

			JSONObject jObj = new JSONObject(executaRequisicao(url));
			JSONObject component = getComponent(jObj);
			JSONArray measures = getMeasues(component);
			int tamanhoArrayObjetos = measures.length();
			for (int i = 0; i < tamanhoArrayObjetos; i++) {
				String metrica = measures.getJSONObject(i).get("metric").toString();
				String valor = new String();

				if (measures.getJSONObject(i).has("value")) {
					valor = measures.getJSONObject(i).get("value").toString();
				} else {
					JSONArray array = (JSONArray) measures.getJSONObject(i).getJSONArray("periods");
					JSONObject objetoQueContemValor = (JSONObject) array.get(0);
					valor = objetoQueContemValor.getString("value");
				}

				Float valorEmFloat = 0f;
				switch (metrica) {
				case "bugs":
					automacao.setBugs(Integer.parseInt(valor));
					break;
				case "ncloc":
					Integer ncloc = Integer.parseInt(valor);
					automacao.setLinhaCodigo(ncloc);
					break;
				case "new_lines":
					Integer newLines = Integer.parseInt(valor);
					automacao.setNovasLinhasCodigo(newLines);
					break;
				case "code_smells":
					automacao.setCodeSmell(Integer.parseInt(valor));
					break;
				case "vulnerabilities":
					automacao.setVulnerabilidades(Integer.parseInt(valor));
					break;
				case "blocker_violations":
					automacao.setIssuesMuitoAlta(Integer.parseInt(valor));
					break;
				case "new_blocker_violations":
					automacao.setNovasIssuesMuitoAlta(Integer.parseInt(valor));
					break;
				case "critical_violations":
					automacao.setIssuesAlta(Integer.parseInt(valor));
					break;
				case "new_critical_violations":
					automacao.setNovasIssuesAlta(Integer.parseInt(valor));
					break;
				case "major_violations":
					automacao.setIssuesMedia(Integer.parseInt(valor));
					break;
				case "new_major_violations":
					automacao.setNovasIssuesMedia(Integer.parseInt(valor));
					break;
				case "minor_violations":
					automacao.setIssuesBaixa(Integer.parseInt(valor));
					break;
				case "info_violations":
					automacao.setIssuesMuitoBaixa(Integer.parseInt(valor));
					break;
				case "sqale_index":
					automacao.setDebitoTecnicoMinutos(valor);
					automacao.setDebitoTecnico(validaEsforcos(Integer.parseInt(valor)));
					break;
				case "coverage":
					valorEmFloat = Float.valueOf(valor);
					automacao.setCobertura(String.format("%.2f", valorEmFloat).concat("%"));
					break;
				case "new_coverage":
					valorEmFloat = Float.valueOf(valor);
					automacao.setNovaCobertura(String.format("%.2f", valorEmFloat).concat("%"));
					break;
				default:
					break;
				}

			}

		} catch (Exception e) {
			LOG.error("Erro ao capturar issues", e);
			Thread.sleep(1000);
			automacao = getIssues(automacao, chavePainel, branch);
		}
		return automacao;

	}

	/**
	 * Tranformas as horas do d??bito t??cnico em (dias/horas/minutos)
	 * 
	 * @param esforcoEmMinutos - d??bito t??cnico em minutos
	 * @return - os d??bito t??cnico em (dias/horas/minutos)
	 */
	private String validaEsforcos(int esforcoEmMinutos) {
		String esforcoValidado = new String();
		if (esforcoEmMinutos < 60) {
			esforcoValidado = esforcoEmMinutos + "min";
		} else if (esforcoEmMinutos > 479) {
			esforcoValidado = (esforcoEmMinutos / 480) + "d";
		} else {
			esforcoValidado = (esforcoEmMinutos / 60) + "h";
		}
		return esforcoValidado;
	}

	/**
	 * Coleta as vulnerabilidades consideradas muito baixa de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade muito
	 *                    baixa
	 * @return - retorna as vulnerabilidades muito baixa de um painel
	 * @throws InterruptedException
	 */
	private int getVulnerabilidadeMuitoBaixa(String chavePainel, String branch) throws InterruptedException {
		int vulnerabilidadesMuitoBaixa = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=INFO&types=VULNERABILITY&resolved=false" + parametroBranch(branch);
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			vulnerabilidadesMuitoBaixa = jObj.getInt(TOTAL);
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade muito baixa", e);
			Thread.sleep(1000);
			vulnerabilidadesMuitoBaixa = getVulnerabilidadeMuitoBaixa(chavePainel, branch);
		}
		return vulnerabilidadesMuitoBaixa;
	}

	/**
	 * Coleta as vulnerabilidades consideradas baixa de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade baixa
	 * @return - retorna as vulnerabilidades baixa de um painel
	 * @throws InterruptedException
	 */
	private int getVulnerabilidadeBaixa(String chavePainel, String branch) throws InterruptedException {
		int vulnerabilidadesBaixa = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=MINOR&types=VULNERABILITY&resolved=false" + parametroBranch(branch);
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			vulnerabilidadesBaixa = jObj.getInt(TOTAL);
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade baixa", e);
			Thread.sleep(1000);
			vulnerabilidadesBaixa = getVulnerabilidadeBaixa(chavePainel, branch);
		}
		return vulnerabilidadesBaixa;
	}

	/**
	 * Coleta as vulnerabilidades consideradas m??dia de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade m??dia
	 * @param branch
	 * @return - retorna as vulnerabilidades m??dia de um painel
	 * @throws InterruptedException
	 */
	private int getVulnerabilidadeMedia(String chavePainel, String branch) throws InterruptedException {
		int vulnerabilidadesMedia = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=MAJOR&types=VULNERABILITY&resolved=false" + parametroBranch(branch);
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			vulnerabilidadesMedia = jObj.getInt(TOTAL);
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade m??dia", e);
			Thread.sleep(1000);
			vulnerabilidadesMedia = getVulnerabilidadeMedia(chavePainel, branch);
		}
		return vulnerabilidadesMedia;
	}

	/**
	 * Coleta as vulnerabilidades consideradas alta de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade alta
	 * @param branch
	 * @return - retorna as vulnerabilidades alta de um painel
	 * @throws InterruptedException
	 */
	private int getVulnerabilidadeAlta(String chavePainel, String branch) throws InterruptedException {
		int vulnerabilidadesAlta = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=CRITICAL&types=VULNERABILITY&resolved=false" + parametroBranch(branch);
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			vulnerabilidadesAlta = jObj.getInt(TOTAL);
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade alta", e);
			Thread.sleep(1000);
			vulnerabilidadesAlta = getVulnerabilidadeAlta(chavePainel, branch);
		}
		return vulnerabilidadesAlta;
	}

	/**
	 * Coleta as vulnerabilidades consideradas muito alta de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade muito
	 *                    alta
	 * @param branch
	 * @return - retorna as vulnerabilidades muito alta de um painel
	 * @throws InterruptedException
	 */
	private int getVulnerabilidadeMuitoAlta(String chavePainel, String branch) throws InterruptedException {
		int vulnerabilidadesMuitoAlta = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=BLOCKER&types=VULNERABILITY&resolved=false" + parametroBranch(branch);
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			vulnerabilidadesMuitoAlta = jObj.getInt(TOTAL);
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade muito alta", e);
			Thread.sleep(1000);
			vulnerabilidadesMuitoAlta = getVulnerabilidadeMuitoAlta(chavePainel, branch);
		}
		return vulnerabilidadesMuitoAlta;
	}

	/**
	 * Realiza o login na API e realiza uma requisi????o
	 * 
	 * @param urlApi - url utilizada para compor a requisi????o ?? API
	 * @return - retorna o resultado das requisi????es feita ?? API do Sonar
	 * @throws Exception - pode lan??ar excess??es referentes a falha de conex??o com o
	 *                   Sonar
	 */
	private String executaRequisicao(String urlApi) throws Exception {

		String url = URL_API_SONAR + "api/authentication/login";

		HttpClient client = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(
				SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build())).build();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("login", USER));
		urlParameters.add(new BasicNameValuePair("password", PASS));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		try {
			client.execute(post);
		} catch (java.net.UnknownHostException e) {
			LOG.error("Falha ao conectar no sonar", e);
		}
		return sendGet(client, urlApi);
	}

	/**
	 * Executa a requisi????o ?? API do Sonar
	 * 
	 * @param client - objeto do tipo HttpCliente que cont??m um usu??rio j?? logado na
	 *               API
	 * @param url    - URL da requisi????o
	 * @return - retorna a resposta da requisi????o
	 */
	private String sendGet(HttpClient client, String url) {
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		StringBuilder resultGet = new StringBuilder();
		HttpResponse responseGet = null;
		try {
			responseGet = client.execute(request);
		} catch (IOException e1) {
			LOG.error("Erro ao executar requisi????o para API do Sonar", e1);
		}
		try (BufferedReader rdGet = new BufferedReader(new InputStreamReader(responseGet.getEntity().getContent()));) {
			String lineGet = "";
			while ((lineGet = rdGet.readLine()) != null) {
				resultGet.append(lineGet);
			}
		} catch (IOException e) {
			LOG.error("Erro ao validar resposta do Sonar", e);
		}
		LOG.info(responseGet.toString());
		return resultGet.toString();
	}

}
