package br.com.icresult.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import br.com.icresult.dao.complementos.ControleGitDevDAO;
import br.com.icresult.domain.complementos.ControleGitDev;
import jxl.common.Logger;

public class ExecutaGitPull implements Runnable {
	
	private CountDownLatch latch;
	
	public ExecutaGitPull(CountDownLatch latch) {
		this.latch = latch;
	}
	
	private static Logger LOG = Logger.getLogger(ExecutaGitPull.class);

	@Override
	public void run() {
		long idControle = Thread.currentThread().getId();
		System.out.println("\n-----------------------\nID identificado: " + idControle);
		ControleGitDevDAO dao = new ControleGitDevDAO();
		ControleGitDev entidade = dao.buscar(idControle);
		String pathSigla = "cd " + entidade.getCaminho();
		String comandoUsuario = "git config --global user.name " + entidade.getUsuarioGit();
		String comandoGit = "git -c http.sslverify=no pull >>LogGit.txt";
		String[] cmds = { pathSigla, comandoUsuario, comandoGit };
		StringBuilder log = new StringBuilder();
		log.append("\n \n");
		System.out.println(entidade);
		Boolean fatalErro = new Boolean(false);
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd", "/c", String.join("& ", cmds));
			builder.redirectErrorStream(true);
			Process p = builder.start();

			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line, erro;

			int i = 0;
			while (true) {
				i++;
				line = r.readLine();
				if (line == null) {
					break;
				}
				if (line.contains("fatal")) {
					fatalErro = new Boolean(true);
					erro = line;
					entidade.setErroGitPull(erro);
				}
				log.append(i + ": " + line + "\n");
				LOG.debug(line);
			}
		} catch (Exception e) {
			LOG.error("Erro ao executar GitPull", e);
		} finally {
			latch.countDown();
			if (!fatalErro) {
				entidade.setErroGitPull("");
			}
			dao.editar(entidade);
		}
	}

}
