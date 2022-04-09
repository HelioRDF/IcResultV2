package br.com.icresult.dao.complementos;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.ControleGitHK;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ]
 * 
 * -Classe DAO ControleGitHK Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.8
 * @since 13-07-2018
 * 
 */

public class ControleGitHKDAO extends GenericDAO<ControleGitHK> {

	private static final long serialVersionUID = -1479068389462822849L;

	/**
	 * Busca o commit mais recente por sigla, nome do sistema...
	 * 
	 * @param sigla - String
	 * @return - Retorna uma String com a Data
	 */
	public String buscarDataCommit(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitHK.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleGitHK resultado = (ControleGitHK) consulta.uniqueResult(); // Utilizado para retornar um unico
			// resultado

			System.out.println("-- Achou:" + resultado.getSigla());
			return resultado.getDataCommit().toString();
		} catch (RuntimeException erro) {
			System.out.println("\n --- XXXX --- Objeto não encontrado: " + sigla);
			System.out.println(erro + "\n ---- XXXX ---");
			return "N/A";

		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca o commit mais recente por sigla, nome do sistema...
	 * 
	 * @param sigla - String
	 * @return - Retorna uma String com tipo Legado/Novo
	 */
	public String buscarAlteracaoCommit(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitHK.class);
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("dataCommit"));
			ControleGitHK resultado = (ControleGitHK) consulta.uniqueResult(); // Utilizado para retornar um unico
			String alteracao = "N/A";

			if (resultado.isAlteracao()) {
				alteracao = "Novo";
			} else {
				alteracao = "Legado";
			}

			System.out.println("-- Achou:" + resultado.getSigla());
			return alteracao;
		} catch (RuntimeException erro) {
			System.out.println("\n --- XXXX --- Objeto não encontrado: " + sigla);
			System.out.println(erro + "\n ---- XXXX ---");
			return "N/A";

		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca ordenada por alteração
	 * 
	 * @return - Retorna uma lista de ControleGitHK
	 */

	@SuppressWarnings("unchecked")
	public List<ControleGitHK> listarOrdenandoAlteracao() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitHK.class);
			// consulta.add(Restrictions.eq("alteracao", true));
			consulta.addOrder(Order.desc("alteracao"));
			List<ControleGitHK> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void validarChave() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(ControleGitHK.class);
			consulta.add(Restrictions.isNull("chave"));
			List<ControleGitHK> resultado = consulta.list();
			for (ControleGitHK c : resultado) {
				Optional<RelacaoProjetoSiglaGestor> controlePesquisado = new RelacaoProjetoSiglaGestorDAO().listar()
						.stream().filter(r -> r.getNome_Projeto().equals(c.getNomeSistema())).findFirst();
				if (controlePesquisado.isPresent()) {
					c.setChave(controlePesquisado.get().getChave());
					editar(c);
				}

			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
}