package br.com.icresult.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ComunicadorAPIRSI {

	public String getBearer(boolean comProxy) {
		
		System.out.println("Chamou API RSI");

		String email = "inspecao@rsinet.com.br";
		String acesso = "Rsi123456";

		JSONObject objetoAcesso = new JSONObject();
		objetoAcesso.put("email", email);
		objetoAcesso.put("password", acesso);
		OkHttpClient client = null;
		if (comProxy) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("prbc01.gsb", 80));
			client = new OkHttpClient().newBuilder().proxy(proxy).build();
		} else {
			client = new OkHttpClient().newBuilder().build();
		}

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(objetoAcesso.toString(), mediaType);
		// http://35.175.254.16:8082/api/account/login?customerName=Santander%20HOM
		// http://35.175.254.16:8081/api/users/login?customerName=Santander HOM

		Request request = new Request.Builder()
				.url("http://35.175.254.16:8082/api/account/login?customerName=Santander%20HOM").post(body)
				.addHeader("content-type", "application/json").addHeader("cache-control", "no-cache").build();

		try {
			Response response = client.newCall(request).execute();
			System.out.println(response);
			StringBuilder resultGet = new StringBuilder();
			try (BufferedReader rdGet = new BufferedReader(new InputStreamReader(response.body().byteStream()));) {
				
				String lineGet = "";
				while ((lineGet = rdGet.readLine()) != null) {
					resultGet.append(lineGet);
				}
				JSONObject object = new JSONObject(resultGet.toString());
				return object.get("token").toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	public String chamadaCodeInspection(CodeInspecion codeInspection, boolean comProxy) {

		StringBuilder resultGet = new StringBuilder();

		OkHttpClient client;

		if (comProxy) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("prbc01.gsb", 80));
			client = new OkHttpClient().newBuilder().proxy(proxy).build();
		} else {
			client = new OkHttpClient().newBuilder().build();
		}

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(codeInspection.toJSON().toString(), mediaType);

		Request request = new Request.Builder().url("http://35.175.254.16:8082/api/CodeInspection").post(body)
				.addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer " + getBearer(comProxy)).addHeader("cache-control", "no-cache")
				.build();

		try {
			Response response = client.newCall(request).execute();

			try (BufferedReader rdGet = new BufferedReader(new InputStreamReader(response.body().byteStream()));) {
				String lineGet = "";
				while ((lineGet = rdGet.readLine()) != null) {
					resultGet.append(lineGet);
				}
				JSONObject object = new JSONObject(resultGet.toString());
				return "JSON: " + object.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Result:" + resultGet.toString();

	}
}
