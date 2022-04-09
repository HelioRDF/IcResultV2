package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ControleGitSonar;
import br.com.icresult.util.HibernateUtil;

/**
 * [ Detalhes... ]
 * 
 * -Classe DAO ControleGitSonar Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author andre.graca
 * @version v1.9
 * @since 13-06-2019
 * 
 */

public class ControleGitSonarDAO extends GenericDAO<ControleGitSonar> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1723659861366221666L;

	/***
	 * 
	 * Preenche o campo selecionado com o icone correto
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void preencherCampoSelecionado() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria update = sessao.createCriteria(ControleGitSonar.class);
			update.add(Restrictions.isNull("selecionado"));
			List<ControleGitSonar> listaControle = update.list();
			for (ControleGitSonar controle : listaControle) {
				controle.setSelecionado(true);
				editar(controle);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}