package br.com.icresult.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import br.com.icresult.util.HibernateUtil;

/**
 * Classe de configuração de Contexto hibernate. //Criar ou fecha a sessão de
 * acordo com os eventos do tomcat
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 * 
 */

public class HibernateContexto implements ServletContextListener {

	/**
	 * contextDestroyed
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		HibernateUtil.getFabricadeSessoes().close();
	}

	/**
	 * contextInitialized
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		HibernateUtil.getFabricadeSessoes().openSession();
	}
}
