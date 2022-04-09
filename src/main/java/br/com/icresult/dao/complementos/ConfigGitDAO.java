package br.com.icresult.dao.complementos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ConfigGit;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author andre.graca
 * @version v3.0.0
 * @since 14-06-2019
 * 
 */

public class ConfigGitDAO extends GenericDAO<ConfigGit> {

	private static final long serialVersionUID = -1237735069072150482L;

	@SuppressWarnings("unchecked")
	public List<ConfigGit> buscaPorUsuario(ConfigGit configuracao) {

		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ConfigGit.class);
			consulta.add(Restrictions.eq("login", configuracao.getLogin()));
			consulta.add(Restrictions.eq("url", configuracao.getUrl()));
			List<ConfigGit> resultado = consulta.list();
			return resultado == null ? new ArrayList<ConfigGit>() : resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}

	}

}