package br.com.icresult.dao.complementos;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ModulosRTCDEV;
import br.com.icresult.util.HibernateUtil;

@SuppressWarnings("serial")
public class ModulosRTCDEVDAO extends GenericDAO<ModulosRTCDEV> {
	
	/**
	 * 
	 * @param chave - String
	 * @return - Retorna um objeto filtrado por código
	 */
	public ModulosRTCDEV buscarPorChave(String chave) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ModulosRTCDEV.class);
			consulta.add(Restrictions.eq("chave",chave)); // Realiza uma consulta baseada na Chave.
			ModulosRTCDEV resultado = (ModulosRTCDEV) consulta.uniqueResult(); // Utilizado para retornar um unico resultado
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
			Criteria update = sessao.createCriteria(ModulosRTCDEV.class);
			update.add(Restrictions.isNull("selecionado"));
			List<ModulosRTCDEV> listaControle = update.list();
			for(ModulosRTCDEV controle : listaControle) {
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
