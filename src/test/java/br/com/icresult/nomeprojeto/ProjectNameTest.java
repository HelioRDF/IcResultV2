package br.com.icresult.nomeprojeto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProjectNameTest {

	@Test
	public void deveRetornarNomeConcatenadoComSiglaAmbienteRepositorioENomeSistema() {
		ProjectName nomeProjetoPadronizado = new ProjectName("BOL", "Boletos Comex", Repositorio.GIT,
				Ambiente.DESENVOLVIMENTO);
		assertEquals("BOL-HG-GIT-BOLETOS_COMEX", nomeProjetoPadronizado.getPadraoNomeProjeto());
	}

	@Test
	public void deveRetornarNomeConcatenadoComAmbienteRepositorioENomeSistema() {
		ProjectName nomeProjetoPadronizado = new ProjectName("", "Boletos Comex", Repositorio.GIT,
				Ambiente.DESENVOLVIMENTO);
		assertEquals("HG-GIT-BOLETOS_COMEX", nomeProjetoPadronizado.getPadraoNomeProjeto());
	}

	@Test(expected = RuntimeException.class)
	public void deveCausarUmErroPoisASiglaENulla() {
		ProjectName nomeProjetoPadronizado = new ProjectName(null, "Boletos Comex", Repositorio.GIT,
				Ambiente.DESENVOLVIMENTO);
		assertEquals("", nomeProjetoPadronizado.getPadraoNomeProjeto());
	}
	
	@Test(expected = RuntimeException.class)
	public void deveCausarUmErroPoisONomeDoProjetoTemMenosQueDoisDigitos() {
		ProjectName nomeProjetoPadronizado = new ProjectName("BOL", "B", Repositorio.GIT,
				Ambiente.DESENVOLVIMENTO);
		assertEquals("", nomeProjetoPadronizado.getPadraoNomeProjeto());
	}

}
