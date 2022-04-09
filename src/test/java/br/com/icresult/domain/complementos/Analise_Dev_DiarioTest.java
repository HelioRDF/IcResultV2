package br.com.icresult.domain.complementos;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.icresult.bean.Analise_DevDiarioBean;
import br.com.icresult.model.Captura;

public class Analise_Dev_DiarioTest{

	@Test
	public void execucoesComOsMesmosResultados() {

		int blocker = 2, critical = 5;
		Captura captura = new Captura();
		captura.setIssuesMuitoAlta(blocker);
		captura.setIssuesAlta(critical);
		captura.setLinhaCodigo(25);

		Analise_DevDiarioBean managedBean = new Analise_DevDiarioBean();
		Analise_Dev_Diario a1 = new Analise_Dev_Diario(captura);
		Analise_Dev_Diario a2 = new Analise_Dev_Diario(captura);

		a1.setBlockerReferencia(blocker);
		a2.setBlockerReferencia(blocker);

		a1.setCriticalReferencia(critical);
		a2.setCriticalReferencia(critical);

		List<Analise_Dev_Diario> listaAnalisesSemResultado = Arrays.asList(a1, a2);
		List<Analise_Dev_Diario> listaAnalisesComResultado = new ArrayList<Analise_Dev_Diario>();

		for (int posicao = 0; posicao < listaAnalisesSemResultado.size(); posicao++) {
			listaAnalisesComResultado.add(managedBean.geraResultado(listaAnalisesSemResultado, posicao,
					listaAnalisesSemResultado.get(posicao)));
		}

		for (Analise_Dev_Diario analise : listaAnalisesComResultado) {
			Assert.assertEquals(analise.getResultado(), "Liberado");
		}
	}

	@Test
	public void execucoesComResultadosDiferentes() {

		int blockerA1 = 1, criticalA1 = 5;
		int blockerA2 = 2, criticalA2 = 5;
		int blockerA3 = 0, criticalA3 = 0;

		Captura capturaA1 = new Captura();
		capturaA1.setIssuesMuitoAlta(blockerA1);
		capturaA1.setIssuesAlta(criticalA1);
		capturaA1.setLinhaCodigo(25);

		Captura capturaA2 = new Captura();
		capturaA2.setIssuesMuitoAlta(blockerA2);
		capturaA2.setIssuesAlta(criticalA2);
		capturaA2.setLinhaCodigo(0);

		Captura capturaA3 = new Captura();
		capturaA3.setIssuesMuitoAlta(blockerA3);
		capturaA3.setIssuesAlta(criticalA3);
		capturaA3.setLinhaCodigo(25);

		Analise_DevDiarioBean managedBean = new Analise_DevDiarioBean();
		Analise_Dev_Diario a1 = new Analise_Dev_Diario(capturaA1);
		Analise_Dev_Diario a2 = new Analise_Dev_Diario(capturaA2);
		Analise_Dev_Diario a3 = new Analise_Dev_Diario(capturaA3);

		a1.setBlockerReferencia(blockerA1);
		a2.setBlockerReferencia(blockerA1);
		a3.setBlockerReferencia(blockerA1);

		a1.setCriticalReferencia(criticalA1);
		a2.setCriticalReferencia(criticalA1);
		a3.setCriticalReferencia(criticalA1);

		List<Analise_Dev_Diario> listaAnalisesSemResultado = Arrays.asList(a1, a2, a3);
		List<Analise_Dev_Diario> listaAnalisesComResultado = new ArrayList<Analise_Dev_Diario>();

		for (int posicao = 0; posicao < listaAnalisesSemResultado.size(); posicao++) {
			listaAnalisesComResultado.add(managedBean.geraResultado(listaAnalisesSemResultado, posicao,
					listaAnalisesSemResultado.get(posicao)));
		}

		Assert.assertEquals(listaAnalisesComResultado.get(0).getResultado(), "Liberado");
		Assert.assertEquals(listaAnalisesComResultado.get(1).getResultado(), "?");
		Assert.assertEquals(listaAnalisesComResultado.get(2).getResultado(), "Liberado");

	}

	@Test
	public void execucoesComAumentoDeBlockers() {

		int blockerA1 = 1, criticalA1 = 5;
		int blockerA2 = 2, criticalA2 = 5;
		int blockerA3 = 5, criticalA3 = 0;

		Captura capturaA1 = new Captura();
		capturaA1.setIssuesMuitoAlta(blockerA1);
		capturaA1.setIssuesAlta(criticalA1);
		capturaA1.setLinhaCodigo(25);

		Captura capturaA2 = new Captura();
		capturaA2.setIssuesMuitoAlta(blockerA2);
		capturaA2.setIssuesAlta(criticalA2);
		capturaA2.setLinhaCodigo(40);

		Captura capturaA3 = new Captura();
		capturaA3.setIssuesMuitoAlta(blockerA3);
		capturaA3.setIssuesAlta(criticalA3);
		capturaA3.setLinhaCodigo(45);

		Analise_DevDiarioBean managedBean = new Analise_DevDiarioBean();
		Analise_Dev_Diario a1 = new Analise_Dev_Diario(capturaA1);
		Analise_Dev_Diario a2 = new Analise_Dev_Diario(capturaA2);
		Analise_Dev_Diario a3 = new Analise_Dev_Diario(capturaA3);

		a1.setBlockerReferencia(blockerA1);
		a2.setBlockerReferencia(blockerA1);
		a3.setBlockerReferencia(blockerA1);

		a1.setCriticalReferencia(criticalA1);
		a2.setCriticalReferencia(criticalA1);
		a3.setCriticalReferencia(criticalA1);

		List<Analise_Dev_Diario> listaAnalisesSemResultado = Arrays.asList(a1, a2, a3);
		List<Analise_Dev_Diario> listaAnalisesComResultado = new ArrayList<Analise_Dev_Diario>();

		for (int posicao = 0; posicao < listaAnalisesSemResultado.size(); posicao++) {
			listaAnalisesComResultado.add(managedBean.geraResultado(listaAnalisesSemResultado, posicao,
					listaAnalisesSemResultado.get(posicao)));
		}

		Assert.assertEquals(listaAnalisesComResultado.get(0).getResultado(), "Liberado");
		Assert.assertEquals(listaAnalisesComResultado.get(1).getResultado(), "Bloqueado");
		Assert.assertEquals(listaAnalisesComResultado.get(2).getResultado(), "Bloqueado");

	}

	@Test
	public void execucoesComDiminuicaoDeBlockers() {

		int blockerA1 = 5, criticalA1 = 5;
		int blockerA2 = 6, criticalA2 = 5;
		int blockerA3 = 3, criticalA3 = 0;

		Captura capturaA1 = new Captura();
		capturaA1.setIssuesMuitoAlta(blockerA1);
		capturaA1.setIssuesAlta(criticalA1);
		capturaA1.setLinhaCodigo(25);

		Captura capturaA2 = new Captura();
		capturaA2.setIssuesMuitoAlta(blockerA2);
		capturaA2.setIssuesAlta(criticalA2);
		capturaA2.setLinhaCodigo(40);

		Captura capturaA3 = new Captura();
		capturaA3.setIssuesMuitoAlta(blockerA3);
		capturaA3.setIssuesAlta(criticalA3);
		capturaA3.setLinhaCodigo(45);

		Analise_DevDiarioBean managedBean = new Analise_DevDiarioBean();
		Analise_Dev_Diario a1 = new Analise_Dev_Diario(capturaA1);
		Analise_Dev_Diario a2 = new Analise_Dev_Diario(capturaA2);
		Analise_Dev_Diario a3 = new Analise_Dev_Diario(capturaA3);

		a1.setBlockerReferencia(blockerA1);
		a2.setBlockerReferencia(blockerA1);
		a3.setBlockerReferencia(blockerA1);

		a1.setCriticalReferencia(criticalA1);
		a2.setCriticalReferencia(criticalA1);
		a3.setCriticalReferencia(criticalA1);

		List<Analise_Dev_Diario> listaAnalisesSemResultado = Arrays.asList(a1, a2, a3);
		List<Analise_Dev_Diario> listaAnalisesComResultado = new ArrayList<Analise_Dev_Diario>();

		for (int posicao = 0; posicao < listaAnalisesSemResultado.size(); posicao++) {
			listaAnalisesComResultado.add(managedBean.geraResultado(listaAnalisesSemResultado, posicao,
					listaAnalisesSemResultado.get(posicao)));
		}

		Assert.assertEquals(listaAnalisesComResultado.get(0).getResultado(), "Liberado");
		Assert.assertEquals(listaAnalisesComResultado.get(1).getResultado(), "Bloqueado");
		Assert.assertEquals(listaAnalisesComResultado.get(2).getResultado(), "Liberado");

	}

	@Test
	public void primeiraExecucao() {

		Integer blockerA1 = 5, criticalA1 = 5;

		Captura capturaA1 = new Captura();
		capturaA1.setIssuesMuitoAlta(blockerA1);
		capturaA1.setIssuesAlta(criticalA1);
		capturaA1.setLinhaCodigo(25);

		Analise_DevDiarioBean managedBean = new Analise_DevDiarioBean();
		Analise_Dev_Diario a1 = new Analise_Dev_Diario(capturaA1);

		a1.setBlockerReferencia(blockerA1);

		a1.setCriticalReferencia(criticalA1);

		List<Analise_Dev_Diario> listaAnalisesSemResultado = Arrays.asList(a1);
		List<Analise_Dev_Diario> listaAnalisesComResultado = new ArrayList<Analise_Dev_Diario>();

		for (int posicao = 0; posicao < listaAnalisesSemResultado.size(); posicao++) {
			listaAnalisesComResultado.add(managedBean.geraResultado(listaAnalisesSemResultado, posicao,
					listaAnalisesSemResultado.get(posicao)));
		}

		Assert.assertEquals(listaAnalisesComResultado.get(0).getResultado(), "Liberado");
		Assert.assertEquals(listaAnalisesComResultado.get(0).getIssuesMuitoAltaAnterior(), blockerA1);

	}

}
