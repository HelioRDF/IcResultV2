package br.com.icresult.bean;

import java.util.List;

import org.junit.Test;
import org.testng.Assert;

import br.com.icresult.bean.Analise_DevDiarioBean;
import br.com.icresult.dao.complementos.ControleGitDevDAO;
import br.com.icresult.dao.complementos.ControleRtcDevDAO;
import br.com.icresult.domain.complementos.Analise_Dev_Diario;
import br.com.icresult.domain.complementos.ControleGitDev;
import br.com.icresult.domain.complementos.ControleRtcDev;

public class Analise_DevDiarioBeanTest {

	@Test
	public void validaSeTodosASeremEnviadosNaDiariaEstaoBloqueados() {
		Analise_DevDiarioBean analise = new Analise_DevDiarioBean();
		List<ControleGitDev> listaModulosBloqueadosGit = new ControleGitDevDAO().listarModulosNaoLiberados();
		List<ControleRtcDev> listaModulosBloqueadosRTC = new ControleRtcDevDAO().listarModulosNaoLiberados();
		List<Analise_Dev_Diario> listaObj = analise.buscaModulosBloqueados(listaModulosBloqueadosGit,
				listaModulosBloqueadosRTC);
		int modulosBloqueados = 0;
		for (Analise_Dev_Diario modulo : listaObj) {
			if (modulo.getResultado().equals("Bloqueado")) {
				modulosBloqueados++;
			}
		}
		Assert.assertEquals(modulosBloqueados,listaObj.size());
	}

}
