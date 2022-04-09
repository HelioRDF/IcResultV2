package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import br.com.icresult.domain.complementos.ModulosRTCHK;
import br.com.icresult.util.HibernateUtil;

/**
 * -Classe DAO RelacaoProjetoSiglaModulosRTCDAO que salva os modulos do RTC no DB.
 * 
 * @author andre.graca
 * @version v1.0
 * @since 16-11-2018
 * 
 */
public class ModulosRTCHKDAO extends GenericDAO<ModulosRTCHK> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	/**
	 * 
	 * @param chave - String
	 * @return - Retorna um objeto filtrado por código
	 */
	public ModulosRTCHK buscarPorChave(String chave) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ModulosRTCHK.class);
			consulta.add(Restrictions.eq("chave",chave)); // Realiza uma consulta baseada na Chave.
			ModulosRTCHK resultado = (ModulosRTCHK) consulta.uniqueResult(); // Utilizado para retornar um unico resultado
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/***
	 * 
	 * Preenche o campo selecionado com o icone correto
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public void preencherCampoSelecionado() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria update = sessao.createCriteria(ModulosRTCHK.class);
			update.add(Restrictions.isNull("selecionado"));
			List<ModulosRTCHK> listaControle = update.list();
			for(ModulosRTCHK controle : listaControle) {
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
