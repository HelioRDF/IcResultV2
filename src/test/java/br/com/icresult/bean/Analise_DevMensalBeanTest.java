package br.com.icresult.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.icresult.nomeprojeto.Repositorio;

public class Analise_DevMensalBeanTest {
	
	@Test
	public void deveRetornarRepositorioGit() {
		Analise_DevMensalBean analiseBean = new Analise_DevMensalBean();
		Repositorio repositorio = analiseBean.buscaRepositorio("CPT-CATALOGO-PRODUTOS-TESOURARIA");
		assertEquals(Repositorio.GIT, repositorio);
	}
	
	@Test
	public void deveRetornarRepositorioIndeterminado() {
		Analise_DevMensalBean analiseBean = new Analise_DevMensalBean();
		Repositorio repositorio = analiseBean.buscaRepositorio("Liferay-IBPF-25-Tributos");
		assertEquals(Repositorio.INDETERMINADA, repositorio);
	}

}
