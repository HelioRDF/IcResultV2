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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

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
import br.com.icresult.dao.complementos.SonarPainelDAO;
import br.com.icresult.domain.complementos.Config;
import br.com.icresult.domain.complementos.SonarPainel;
import br.com.icresult.model.SonarAtributos;
import br.com.icresult.util.MetodosUteis;

/**
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 12-06-2019
 * 
 */

@ManagedBean
@RequestScoped
public class SonarAPIBean {

	private Logger LOG = LoggerFactory.getLogger(SonarAPIBean.class);
	private final static String USER_AGENT = "Chrome/33.0.1750.117";
	private final static String COMPONENT_KEYS_ISSUES = "api/issues/search?component=";
	private final static String COMPONENT_KEYS_MEASURES = "api/measures/component?component=";
	private final static String PROJETOS = "api/projects/search?ps=500&p=";
	private final static String TOTAL = "total";
	private final static String BRANCH_MASTER = "master";
	private final static String BRANCH_DEVELOP = "develop";

	private static int projetosSonar;
	private static int paginasProjetosSonar;

	public Config getConfiguracaoSonar() {
		return new ConfigDAO().buscarPorConfiguracaoSelecionada();
	}

	/**
	 * 
	 * Informa a data execução de um painel
	 * 
	 * @param chave - String com a chave do painel Sonar
	 * @return - retorna a data de execução em que está o painel.
	 * 
	 */

	public Date getDataSonar(String chave) {
		Date dataExecucao = null;
		String branch = BRANCH_MASTER;
		try {
			dataExecucao = getInfo(new SonarAtributos(), chave, branch).getDataSonar();
		} catch (Exception e) {
			LOG.error("Erro ao capturar data do painel sonar", e);
		}
		return dataExecucao;
	}

	/**
	 * Inicia a captura de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser capturada
	 * @return - retorna um objeto do tipo Captura que contem as informações do
	 *         painel
	 */
	public SonarAtributos getSonarApi(String chavePainel) {
		SonarAPIBean sonarAPIBean = new SonarAPIBean();
		SonarAtributos automacao = new SonarAtributos();
		String branch = BRANCH_DEVELOP;
		try {

			if (sonarAPIBean.getInfo(automacao, chavePainel, branch) == null) {
				branch = BRANCH_MASTER;
				if (sonarAPIBean.getInfo(automacao, chavePainel, branch) == null) {
					return null;
				}
			}
			sonarAPIBean.getIssues(automacao, chavePainel, branch);
			automacao.setNovasIssuesAlta(sonarAPIBean.getNovasIssuesAlta(chavePainel, branch));
			automacao.setNovasIssuesMuitoAlta(sonarAPIBean.getNovasIssuesMuitoAlta(chavePainel, branch));
			automacao.setNovasIssuesMedia(sonarAPIBean.getNovasIssuesMedia(chavePainel, branch));
			automacao.setLinkIntegracaoContinua(sonarAPIBean.getLink("ci", chavePainel));
			Thread.sleep(1000);

		} catch (Exception e) {
			LOG.error("Erro ao capturar informações do painel " + chavePainel, e);
			return null;
		}
		if (automacao.getLinhaCodigo() == 0) {
			automacao.setLinhaCodigo(1);
		}
		return automacao;
	}

	private String getLink(String type, String chavePainel) {
		try {
			String url = this.getConfiguracaoSonar().getUrl() + "api/project_links/search?projectKey=" + chavePainel;
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			JSONArray arrayDeLinks = jObj.getJSONArray("links");
			for (int c = 0; c < arrayDeLinks.length(); c++) {
				JSONObject link = arrayDeLinks.getJSONObject(c);
				if (link.getString("type").equals(type)) {
					return link.getString("url");
				}
			}

		} catch (Exception e) {
			LOG.error("Erro ao capturar link", e);
			getLink(type, chavePainel);
		}
		return new String();
	}

	/**
	 * Coleta Todos os Projetos do SonarQube
	 * 
	 */

	private void ProjetosSonar() throws InterruptedException {

		SonarPainelDAO dao = new SonarPainelDAO();
		List<JSONObject> listaPaginaDePaineisSONAR = new ArrayList<>();

		try {

			for (int pagina = 1; pagina < paginasProjetosSonar + 1; pagina++) {
				String url = this.getConfiguracaoSonar().getUrl() + PROJETOS + pagina;
				System.out.println("\n \t Link Sonar:\n" + url);
				System.out.println(
						"-----------------------------------------------------------------------------------------------");
				listaPaginaDePaineisSONAR.add(new JSONObject(executaRequisicao(url)));
				Thread.sleep(500);
			}

			for (JSONObject jObj : listaPaginaDePaineisSONAR) {

				JSONArray components = getComponents(jObj);

				for (Object obj : components) {
					Thread.sleep(200);
					JSONObject jObj2 = new JSONObject(obj.toString());
					SonarPainel painel = new SonarPainel();
					painel.setNomeProjeto(jObj2.getString("name"));
					painel.setChave(jObj2.getString("key"));

					String dataSonar;
					try {
						dataSonar = jObj2.getString("lastAnalysisDate");
					} catch (Exception e) {
						dataSonar = "N/A";
					}
					System.out.println(painel);
					painel.setDataSonar(dataSonar);

					dao.salvar(painel);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Erro ao capturar informações", e);
			new SonarPainelBean().limparDB();
			Thread.sleep(1000);
			ProjetosSonar();
		}

	}

	private int PaginasProjetosSonar() throws InterruptedException {

		try {
			StringBuilder url = new StringBuilder(this.getConfiguracaoSonar().getUrl() + PROJETOS + 1);
			System.out.println("\n\n --- Link Sonar API ---:\n" + url);
			JSONObject jObj = new JSONObject(executaRequisicao(url.toString()));
			JSONObject paging = getPaging(jObj);
			projetosSonar = Integer.parseInt(paging.get("total").toString()); // Recebe o total de projetos do Sonar
			paginasProjetosSonar = (projetosSonar / 500) + ((projetosSonar % 500) > 0 ? 1 : 0);
			LOG.info("Projetos Sonar: " + projetosSonar);
			LOG.info("Paginas: " + paginasProjetosSonar);

		} catch (Exception e) {
			LOG.error("Erro ao capturar informações", e);
			Thread.sleep(1000);
		}
		Thread.sleep(500);
		return paginasProjetosSonar;

	}

	/**
	 * Coleta nome, e data de execução de um painel Sonar
	 * 
	 * @param automacao   - objeto do tipo Captura onde será inserido as informações
	 * @param chavePainel - chave do painel para se localizar as informações
	 * @param branch      - branch do repositorio que o painel está ex. master
	 * @return - retorna um objeto do tipo Captura com as informações inseridas
	 */
	private SonarAtributos getInfo(SonarAtributos automacao, String chavePainel, String branch)
			throws InterruptedException {

		try {
			StringBuilder url = new StringBuilder();
			url.append(this.getConfiguracaoSonar().getUrl());
			url.append("api/components/show?component=");
			url.append(chavePainel);
			url.append("&branch=");
			url.append(branch);

			JSONObject jObj = new JSONObject(executaRequisicao(url.toString()));
			if (jObj.toString().contains("errors")) {
				return null;
			}

			JSONObject component = getComponent(jObj);

			String nome = component.getString("name");
			if (component.has("analysisDate")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				automacao
						.setDataSonar(
								Date.from(LocalDateTime
										.parse(component.getString("analysisDate").replaceAll("[+-]\\d{4}", "")
												.replaceAll("T", " "), formatter)
										.atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
				String urlComHttps = new MetodosUteis().verificaLinkSONAR(this.getConfiguracaoSonar().getUrl());
				automacao.setUrl(urlComHttps + "dashboard?id=" + chavePainel);
			} else {
				return null;
			}

			automacao.setNomeProjeto(nome);
			automacao
					.setUrl(this.getConfiguracaoSonar().getUrl() + "dashboard?id=" + chavePainel + "&branch=" + branch);
			Date dataExecucao = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println(dataExecucao);
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");

			automacao.setDataCaptura(dataExecucao);
		} catch (Exception e) {
			LOG.error("Erro ao capturar informações", e);
			Thread.sleep(1000);
			automacao = getInfo(automacao, chavePainel, branch);
		}
		return automacao;

	}

	/**
	 * Coleta as issues de um painel Sonar
	 * 
	 * @param automacao   - objeto do tipo Captura onde será inserido os valores das
	 *                    issues
	 * @param chavePainel - chave do painel Sonar a ser localizado as issues
	 * @param branch      - branch do repositorio que o painel está ex. master
	 * @return - retorna um objeto do tipo Captura onde foi inserido as issues
	 */
	private SonarAtributos getIssues(SonarAtributos automacao, String chavePainel, String branch)
			throws InterruptedException {

		try {
			String url = this.getConfiguracaoSonar().getUrl() + COMPONENT_KEYS_MEASURES+ chavePainel
					+ "&branch=" + branch
					+ "&metricKeys=ncloc,critical_violations,major_violations,blocker_violations,coverage,new_coverage,sqale_index";
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

				case "ncloc":
					Integer ncloc = Integer.parseInt(valor);
					automacao.setLinhaCodigo(ncloc);
					break;

				case "blocker_violations":
					automacao.setIssuesMuitoAlta(Integer.parseInt(valor));
					break;
				case "critical_violations":
					automacao.setIssuesAlta(Integer.parseInt(valor));
					break;
				case "major_violations":
					automacao.setIssuesMedia(Integer.parseInt(valor));
					break;
				case "coverage":
					valorEmFloat = Float.parseFloat(valor);
					automacao.setCoberturaCodigo(String.format("%.2f", valorEmFloat).concat("%"));
					break;
				case "new_coverage":
					valorEmFloat = Float.parseFloat(valor);
					automacao.setNovaCobertura(String.format("%.2f", valorEmFloat).concat("%"));
					break;
				case "sqale_index":
					automacao.setDebitoTecnicoEmMinutos(valor);
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
	 * Coleta o total de NOVAS ISSUES consideradas muito altas de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado as novas issues muito
	 *                    altas
	 * @param branch      - branch do repositorio que o painel está ex. master
	 * @return - retorna as novas issues muito alta de um painel
	 * @throws InterruptedException
	 */
	private int getNovasIssuesMuitoAlta(String chavePainel, String branch) throws InterruptedException {
		int novasIssuesMuitoAlta = 0;
		try {
			String url = this.getConfiguracaoSonar().getUrl() + COMPONENT_KEYS_ISSUES + chavePainel + "&branch=" + branch
					+ "&severities=BLOCKER&types=VULNERABILITY,CODE_SMELL,BUG&sinceLeakPeriod=true&resolved=false";
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			System.out.println("URL NOVOS BLOCKERS : " + url);
			Integer total = jObj.getInt(TOTAL);
			novasIssuesMuitoAlta = total == null ? 0 : total;
		} catch (Exception e) {
			LOG.error("Erro ao capturar novas issues muito alta", e);
			Thread.sleep(1000);
			novasIssuesMuitoAlta = getNovasIssuesMuitoAlta(chavePainel, branch);
		}
		return novasIssuesMuitoAlta;
	}

	/**
	 * Coleta o total de NOVAS ISSUES consideradas altas de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado as novas issues altas
	 * @param branch      - branch do repositorio que o painel está ex. master
	 * @return - retorna as novas issues altas de um painel
	 * @throws InterruptedException
	 */
	private int getNovasIssuesAlta(String chavePainel, String branch) throws InterruptedException {
		int novasIssuesAlta = 0;
		try {
			String url = this.getConfiguracaoSonar().getUrl() + COMPONENT_KEYS_ISSUES + chavePainel + "&branch=" + branch
					+ "&severities=CRITICAL&types=VULNERABILITY,CODE_SMELL,BUG&sinceLeakPeriod=true&resolved=false";
			System.out.println("URL NOVOS CRITICALS : " + url);
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			Integer total = jObj.getInt(TOTAL);
			novasIssuesAlta = total == null ? 0 : total;
		} catch (Exception e) {
			LOG.error("Erro ao capturar novas issues altas", e);
			Thread.sleep(1000);
			novasIssuesAlta = getNovasIssuesAlta(chavePainel, branch);
		}
		return novasIssuesAlta;
	}

	/**
	 * Coleta o total de NOVAS ISSUES consideradas médias de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado as novas issues médias
	 * @param branch      - branch do repositorio que o painel está ex. master
	 * @return - retorna as novas issues médias de um painel
	 * @throws InterruptedException
	 */
	private int getNovasIssuesMedia(String chavePainel, String branch) throws InterruptedException {
		int novasIssuesMedia = 0;
		try {
			String url = this.getConfiguracaoSonar().getUrl() + COMPONENT_KEYS_ISSUES + chavePainel + "&branch=" + branch
					+ "&severities=MAJOR&types=VULNERABILITY,CODE_SMELL,BUG&sinceLeakPeriod=true&resolved=false";
			System.out.println("URL NOVOS MAJORS : " + url);
			JSONObject jObj = new JSONObject(executaRequisicao(url));
			Integer total = jObj.getInt(TOTAL);
			novasIssuesMedia = total == null ? 0 : total;
		} catch (Exception e) {
			LOG.error("Erro ao capturar novas issues médias", e);
			Thread.sleep(1000);
			novasIssuesMedia = getNovasIssuesMedia(chavePainel, branch);
		}
		return novasIssuesMedia;
	}

	/**
	 * Realiza o login na API e realiza uma requisição
	 * 
	 * @param urlApi - url utilizada para compor a requisição à API
	 * @return - retorna o resultado das requisições feita à API do Sonar
	 * @throws Exception - pode lançar excessões referentes a falha de conexão com o
	 *                   Sonar
	 */
	private String executaRequisicao(String urlApi) throws Exception {

		String url = this.getConfiguracaoSonar().getUrl() + "api/authentication/login";

		HttpClient client = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(
				SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build())).build();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", USER_AGENT);
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("login", getConfiguracaoSonar().getLogin()));
		urlParameters.add(new BasicNameValuePair("password", getConfiguracaoSonar().getAcessoSonar()));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		try {
			client.execute(post);
		} catch (java.net.UnknownHostException e) {
			LOG.error("Falha ao conectar no sonar", e);
		}
		return sendGet(client, urlApi);
	}

	/**
	 * Executa a requisição à API do Sonar
	 * 
	 * @param client - objeto do tipo HttpCliente que contém um usuário já logado na
	 *               API
	 * @param url    - URL da requisição
	 * @return - retorna a resposta da requisição
	 */
	private String sendGet(HttpClient client, String url) {
		System.out.println(url);
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", USER_AGENT);
		StringBuffer resultGet = new StringBuffer();
		HttpResponse responseGet = null;
		try {
			responseGet = client.execute(request);
			System.out.println(responseGet);
		} catch (IOException e1) {
			LOG.error("Erro ao executar requisição para API do Sonar", e1);
		}
		try (BufferedReader rdGet = new BufferedReader(new InputStreamReader(responseGet.getEntity().getContent()));) {
			String lineGet = "";
			while ((lineGet = rdGet.readLine()) != null) {
				resultGet.append(lineGet);
			}
		} catch (IOException e) {
			LOG.error("Erro ao validar resposta do Sonar", e);
		}
		System.out.println(resultGet.toString());
		LOG.info(resultGet.toString());
		return resultGet.toString();
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

	private JSONArray getComponents(JSONObject jObj) {
		return jObj.getJSONArray("components");
	}

	/**
	 * Retorna um JSONObject com nome de paging
	 * 
	 * @param jObj = objeto Json que contem o component
	 * 
	 */

	private JSONObject getPaging(JSONObject jObj) {
		return jObj.getJSONObject("paging");
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

	public void iniciarApi() {

		try {
			PaginasProjetosSonar();
			SonarPainelBean sonarPainelBean = new SonarPainelBean();
			sonarPainelBean.limparDB();
			ProjetosSonar();

		} catch (InterruptedException e) {
			LOG.error("Erro ao iniciar a captura de todos os paineis do SONAR : {}", e);
		}
		System.out.println("\n--- ------ ---\n");
	}

}
