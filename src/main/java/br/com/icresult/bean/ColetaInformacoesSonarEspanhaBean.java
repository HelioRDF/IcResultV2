package br.com.icresult.bean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.icresult.model.Captura;
import jxl.common.Logger;

/**
 * 
 * @author andre.graca
 *
 *         Classe que se comunica com um Sonar Espanha
 */

public class ColetaInformacoesSonarEspanhaBean {

	private static Logger LOG = Logger.getLogger(ColetaInformacoesSonarEspanhaBean.class);
	private String URL_API_SONAR = "http://sonarqube.isban.gs.corp/";
	private final String LOGIN_PAULA_RTC = "XB201520";
	private final String PASS_PAULA_RTC = "Rsi#2019A";
	private final static String COMPONENT_KEYS = "api/issues/search?componentKeys=";
	private final static String TOTAL = "total";

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
	 * Inicia a captura de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser capturada
	 * @return - retorna um objeto do tipo Captura que contem as informações do
	 *         painel
	 */
	public Captura getSonarApi(String chavePainel) {
		ColetaInformacoesSonarEspanhaBean coletor = new ColetaInformacoesSonarEspanhaBean();
		Captura automacao = new Captura();
		try {

			if (coletor.getInfo(automacao, chavePainel) == null) {
				return null;
			}
			coletor.getIssues(automacao, chavePainel);
			coletor.getVulnerabilidadesPorSeveridade(automacao, chavePainel);

		} catch (Exception e) {
			LOG.error("Erro ao capturar informações do painel " + chavePainel, e);
			return null;
		}
		automacao.setLinhaCodigo(1);
		return automacao;
	}

	/**
	 * Coleta todas as vulnerabilidades de um painel do Sonar por severidade
	 * 
	 * @param automacao  - objeto do tipo Captura que contem algumas informações
	 *                   sobre um painel Sonar e que recebe o valor de suas
	 *                   vulnerabilidades
	 * @param chaveTeste - chave do painel a ser coletado as vulnerabilidades
	 * @return - retorna um objeto do tipo Captura com as vulnerabilidades inseridas
	 */
	private Captura getVulnerabilidadesPorSeveridade(Captura automacao, String chaveTeste) {
		try {
			automacao.setVulnerabilityMuitoAlta(getVulnerabilidadeMuitoAlta(chaveTeste));
			automacao.setVulnerabilityAlta(getVulnerabilidadeAlta(chaveTeste));
			automacao.setVulnerabilityMedia(getVulnerabilidadeMedia(chaveTeste));
			automacao.setVulnerabilityBaixa(getVulnerabilidadeBaixa(chaveTeste));
			automacao.setVulnerabilityMuitoBaixa(getVulnerabilidadeMuitoBaixa(chaveTeste));
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidades por severidade", e);
			try {
				Thread.sleep(1000);
				automacao = getVulnerabilidadesPorSeveridade(automacao, chaveTeste);
			} catch (Exception e2) {
				e2.getStackTrace();
			}
		}

		return automacao;
	}

	/**
	 * Coleta nome, versão e data de execução de um painel Sonar
	 * 
	 * @param automacao   - objeto do tipo Captura onde será inserido as informações
	 * @param chavePainel - chave do painel para se localizar as informações
	 * @return - retorna um objeto do tipo Captura com as informações inseridas
	 */
	private Captura getInfo(Captura automacao, String chavePainel) throws InterruptedException {

		try {

			String url = URL_API_SONAR + "api/resources?resource=" + chavePainel + "&format=json";

			JSONObject auxJObj = executaRequisicao(url);

			if (auxJObj != null) {
				JSONObject jObj = new JSONObject(auxJObj.toString());
				String versao = jObj.getString("version");
				String nome = jObj.getString("name");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				automacao.setDataSonar(Date.from(LocalDateTime
						.parse(jObj.getString("date").replaceAll("\\+\\d{4}", "").replaceAll("T", " "), formatter)
						.atZone(ZoneId.of("Etc/GMT-2")).toInstant()));
				automacao.setVersao(versao);
				automacao.setNomeProjeto(nome);
				automacao.setUrl(URL_API_SONAR + "overview?id=" + chavePainel);
				automacao.setDataCaptura(Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
			} else {
				return null;
			}

		} catch (Exception e) {
			LOG.error("Erro ao capturar informações", e);
			Thread.sleep(1000);
			automacao = getInfo(automacao, chavePainel);

		}
		return automacao;

	}

	/**
	 * Coleta as issues de um painel Sonar
	 * 
	 * @param automacao   - objeto do tipo Captura onde será inserido os valores das
	 *                    issues
	 * @param chavePainel - chave do painel Sonar a ser localizado as issues
	 * @return - retorna um objeto do tipo Captura onde foi inserido as issues
	 */
	private Captura getIssues(Captura automacao, String chavePainel) throws InterruptedException {

		try {
			String url = URL_API_SONAR + "api/measures/component?componentKey=" + chavePainel
					+ "&metricKeys=bugs,ncloc,code_smells,vulnerabilities,blocker_violations,critical_violations,major_violations,minor_violations,info_violations,sqale_index,reliability_remediation_effort,security_remediation_effort,coverage,alert_status";
			JSONObject jObj = executaRequisicao(url);
			JSONObject component = getComponent(jObj);
			JSONArray measures = getMeasues(component);
			int tamanhoArrayObjetos = measures.length();
			for (int i = 0; i < tamanhoArrayObjetos; i++) {
				String metrica = measures.getJSONObject(i).get("metric").toString();
				String valor = measures.getJSONObject(i).get("value").toString();
				switch (metrica) {
				case "bugs":
					automacao.setBugs(Integer.parseInt(valor));
					break;
				case "ncloc":
					automacao.setLinhaCodigo(Integer.parseInt(valor));
					break;
				case "vulnerabilities":
					automacao.setVulnerabilidades(Integer.parseInt(valor));
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
					Double cobertura = Double.valueOf(valor);
					automacao.setCobertura(cobertura + "%");
					break;
				default:
					break;
				}

			}

			automacao.setCodeSmell(getCodeSmells(chavePainel));

		} catch (Exception e) {
			LOG.error("Erro ao capturar issues", e);
			Thread.sleep(1000);
			automacao = getIssues(automacao, chavePainel);

		}

		return automacao;

	}

	/**
	 * Tranformas as horas do débito técnico em (dias/horas/minutos)
	 * 
	 * @param esforcoEmMinutos - débito técnico em minutos
	 * @return - os débito técnico em (dias/horas/minutos)
	 */
	private String validaEsforcos(int esforcoEmMinutos) {
		String esforcoValidado = null;
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
	 */
	public int getVulnerabilidadeMuitoBaixa(String chavePainel) {
		int vulnerabilidadesMuitoBaixa = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=INFO&types=VULNERABILITY&resolved=false&format=json";
			JSONObject jObj = executaRequisicao(url);
			if (jObj != null) {
				vulnerabilidadesMuitoBaixa = jObj.getInt(TOTAL);
			}
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade muito baixa", e);
			vulnerabilidadesMuitoBaixa = getVulnerabilidadeMuitoBaixa(chavePainel);
		}
		return vulnerabilidadesMuitoBaixa;
	}

	/**
	 * Coleta as vulnerabilidades consideradas baixa de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade baixa
	 * @return - retorna as vulnerabilidades baixa de um painel
	 */
	public int getVulnerabilidadeBaixa(String chavePainel) {
		int vulnerabilidadesBaixa = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=MINOR&types=VULNERABILITY&resolved=false";
			JSONObject jObj = executaRequisicao(url);
			if (jObj != null) {
				vulnerabilidadesBaixa = jObj.getInt(TOTAL);
			}
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade baixa", e);
			vulnerabilidadesBaixa = getVulnerabilidadeBaixa(chavePainel);
		}
		return vulnerabilidadesBaixa;
	}

	/**
	 * Coleta as vulnerabilidades consideradas média de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade média
	 * @return - retorna as vulnerabilidades média de um painel
	 */
	public int getVulnerabilidadeMedia(String chavePainel) {
		int vulnerabilidadesMedia = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=MAJOR&types=VULNERABILITY&resolved=false";

			JSONObject jObj = executaRequisicao(url);
			if (jObj != null) {
				vulnerabilidadesMedia = jObj.getInt(TOTAL);
			}
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade média", e);
			vulnerabilidadesMedia = getVulnerabilidadeMedia(chavePainel);
		}
		return vulnerabilidadesMedia;
	}

	/**
	 * Coleta as vulnerabilidades consideradas alta de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade alta
	 * @return - retorna as vulnerabilidades alta de um painel
	 */
	public int getVulnerabilidadeAlta(String chavePainel) {
		int vulnerabilidadesAlta = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=CRITICAL&types=VULNERABILITY&resolved=false";
			JSONObject jObj = executaRequisicao(url);
			if (jObj != null) {
				vulnerabilidadesAlta = jObj.getInt(TOTAL);
			}
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade alta", e);
			vulnerabilidadesAlta = getVulnerabilidadeAlta(chavePainel);
		}
		return vulnerabilidadesAlta;
	}

	/**
	 * Coleta as vulnerabilidades consideradas muito alta de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado a vulnerabilidade muito
	 *                    alta
	 * @return - retorna as vulnerabilidades muito alta de um painel
	 */
	public int getVulnerabilidadeMuitoAlta(String chavePainel) {
		int vulnerabilidadesMuitoAlta = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel
					+ "&severities=BLOCKER&types=VULNERABILITY&resolved=false&format=json";
			JSONObject jObj = executaRequisicao(url);
			if (jObj != null) {
				vulnerabilidadesMuitoAlta = jObj.getInt(TOTAL);
			}
		} catch (Exception e) {
			LOG.error("Erro ao capturar vulnerabilidade muito alta", e);
			vulnerabilidadesMuitoAlta = getVulnerabilidadeMuitoAlta(chavePainel);
		}
		return vulnerabilidadesMuitoAlta;
	}

	/**
	 * Coleta a quantidade de CODESMELLS de um painel do Sonar
	 * 
	 * @param chavePainel - chave do painel a ser localizado o valor de CODESMELL
	 * @return - retorna a quantidade de CODESMELLS de um painel do Sonar
	 */
	public int getCodeSmells(String chavePainel) {
		int vulnerabilidadesMuitoAlta = 0;
		try {
			String url = URL_API_SONAR + COMPONENT_KEYS + chavePainel + "&types=CODE_SMELL&resolved=false&format=json";
			JSONObject jObj = executaRequisicao(url);
			if (jObj != null) {
				vulnerabilidadesMuitoAlta = jObj.getInt(TOTAL);
			}
		} catch (Exception e) {
			LOG.error("Erro ao capturar CodeSmell", e);
			vulnerabilidadesMuitoAlta = getCodeSmells(chavePainel);
		}
		return vulnerabilidadesMuitoAlta;
	}

	/**
	 * Realiza o login na API e realiza uma requisição
	 * 
	 * @param urlApi - url utilizada para compor a requisição à API
	 * @return - retorna o resultado das requisições feita à API do Sonar
	 * @throws Exception - pode lançar excessões referentes a falha de conexão com o
	 *                   Sonar
	 */
	private JSONObject executaRequisicao(String urlApi) {

		JSONObject jObj = null;

		try {
			URL obj = new URL(urlApi);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			conn.setRequestMethod("GET");

			String userpass = LOGIN_PAULA_RTC + ":" + PASS_PAULA_RTC;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);

			String data = "{\"format\":\"json\",\"pattern\":\"#\"}";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			StringBuilder response = new StringBuilder();
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			StringBuilder tratamentoJSON = null;
			if (response.charAt(0) == '[') {
				tratamentoJSON = new StringBuilder(response.toString().replaceAll("\\[", ""));
				tratamentoJSON = new StringBuilder(tratamentoJSON.toString().replaceAll("\\]", ""));
			} else {
				tratamentoJSON = new StringBuilder(response.toString());
			}

			jObj = new JSONObject(tratamentoJSON.toString());

		} catch (Exception e) {
			LOG.error("Erro ao execucao a requisição para o SONAR Espanha : " + e.getMessage());
		}

		if (jObj.toString().contains("errors")) {
			return null;
		} else {
			return jObj;
		}

	}

}
