package br.com.icresult.util;

import java.util.concurrent.ThreadFactory;

/**
 * Cria uma Fabrica de Thread, utilizada para controlar as execuções de capturas e dos scanners
 * @author andre.graca
 *
 */
public class YourThreadFactory implements ThreadFactory {

	private String nomeTarefa;
	
	public YourThreadFactory(Long idTarefa) {
		this.nomeTarefa = idTarefa.toString();
	}

	public YourThreadFactory(String tarefa) {
		this.nomeTarefa = tarefa;
	}

	public String retornaNomeTarefa() {
		return this.nomeTarefa;
	}

	public Thread newThread(Runnable r) {
		System.out.println("\n\n-------------------------------------------------------");
		System.out.println("Nome da tarefa:"+this.nomeTarefa);
		System.out.println("\n\n-------------------------------------------------------\n\n");
		return new Thread(r, this.nomeTarefa);
	}
}
