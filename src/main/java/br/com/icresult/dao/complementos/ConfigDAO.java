package br.com.icresult.dao.complementos;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Config;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v3.0.0
 * @since 11-06-2019
 * 
 */

public class ConfigDAO extends GenericDAO<Config> {

	private static final long serialVersionUID = -1237735069072150482L;

	public Config buscarPorConfiguracaoSelecionada() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Config.class);
			consulta.add(Restrictions.eq("padrao", true));
			Config configuracaoSelecionada = (Config) consulta.uniqueResult();
			return configuracaoSelecionada;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	
}