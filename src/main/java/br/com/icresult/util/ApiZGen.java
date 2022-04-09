package br.com.icresult.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiZGen {

	private static Logger LOG = LoggerFactory.getLogger(ApiZGen.class);

	public String getBearer() {

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("prbc01.gsb", 80));
		OkHttpClient client = new OkHttpClient().newBuilder().proxy(proxy).build();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody
				.create("{\n\t\"email\": \"inspecao@rsinet.com.br\",\n\t\"password\": \"Rsi123456\"\n}", mediaType);
		Request request = new Request.Builder()
				.url("http://35.175.254.16:8082/api/account/login?customerName=Santander%20HOM").post(body)
				.addHeader("content-type", "application/json").addHeader("cache-control", "no-cache")
				.addHeader("postman-token", "9beb3376-76bc-87ce-8de9-f2f146add96b").build();

		try {
			Response response = client.newCall(request).execute();
			StringBuilder resultGet = new StringBuilder();
			try (BufferedReader rdGet = new BufferedReader(new InputStreamReader(response.body().byteStream()));) {
				String lineGet = "";
				while ((lineGet = rdGet.readLine()) != null) {
					resultGet.append(lineGet);
				}
				JSONObject obj = new JSONObject(resultGet.toString());
				return obj.get("token").toString();
			} catch (Exception e) {
				LOG.error(e.getCause().toString());
			}
		} catch (IOException e) {
			LOG.error(e.getCause().toString());
		}
		return "";

	}

}
