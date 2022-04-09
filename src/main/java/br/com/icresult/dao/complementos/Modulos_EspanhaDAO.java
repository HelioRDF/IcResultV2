package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Modulos_Espanha;
import br.com.icresult.domain.complementos.SiglasGit;
import br.com.icresult.util.HibernateUtil;

public class Modulos_EspanhaDAO extends GenericDAO<Modulos_Espanha>{

	private static final long serialVersionUID = 2526817322723385356L;
	
	@SuppressWarnings("unchecked")
	public void preencherCampoSelecionado() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria update = sessao.createCriteria(SiglasGit.class);
			update.add(Restrictions.isNull("selecionado"));
			List<Modulos_Espanha> listaControle = update.list();
			for(Modulos_Espanha controle : listaControle) {
				controle.setSelecionado("ui-icon-blank");
				editar(controle);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
	
	

}
