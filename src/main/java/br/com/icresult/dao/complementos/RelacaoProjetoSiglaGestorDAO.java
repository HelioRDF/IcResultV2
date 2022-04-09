package br.com.icresult.dao.complementos;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_Homologacao;
import br.com.icresult.domain.complementos.RelacaoProjetoSiglaGestor;
import br.com.icresult.util.HibernateUtil;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v2.4.4
 * @since 18-10-2018
 * 
 */

public class RelacaoProjetoSiglaGestorDAO extends GenericDAO<RelacaoProjetoSiglaGestor> {

	
	private static final long serialVersionUID = 242859631301569372L;

	/***
	 * 
	 * @param sigla - Sigla que se quer saber o nome do Gestor
	 * @return Retorna o nome do gestor de acordo com a sigla pesquisada
	 */
	public String buscarGestor(String sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(RelacaoProjetoSiglaGestor.class);
			consulta.add(Restrictions.eqOrIsNull("sigla", sigla));
			consulta.add(Restrictions.not(Restrictions.like("painelGestor", "%HK%")));
			consulta.setMaxResults(1);
			RelacaoProjetoSiglaGestor resultado = (RelacaoProjetoSiglaGestor) consulta.uniqueResult();
			return resultado.getPainelGestor();
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
	
	/**
	 * @return - lista de RelacaoProjetoSiglaGestor que são Snapshot
	 */
	@SuppressWarnings("unchecked")
	public List<RelacaoProjetoSiglaGestor> listaSnapshot(){
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(RelacaoProjetoSiglaGestor.class);
			consulta.add(Restrictions.eq("devOps", "SIM"));
			return consulta.list();
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
	
	/**
	 * Busca a mais recente analise de homologacao para uma relacaoProjetoSiglaGestor
	 * @param sigla - Objeto do tipo sigla a ser pesquisada
	 * @return - retorna a analise mais recente de uma Sigla pesquisada
	 */

	@SuppressWarnings("unchecked")
	public Analise_Homologacao buscarUltimaInspecaoPorNomePainel(RelacaoProjetoSiglaGestor relacao) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria select = sessao.createCriteria(Analise_Homologacao.class);
			select.addOrder(Order.desc("dataCaptura"));
			List<Analise_Homologacao> listaControle = select.list();
			if (!listaControle.isEmpty()) {
				Optional<Analise_Homologacao> ultimaInspecaoPainelDevOps = (Optional<Analise_Homologacao>) listaControle.stream().filter(
						analise -> analise.getNomeProjeto().toLowerCase().equals(relacao.getNome_Projeto().toLowerCase()))
						.findFirst();
				if(ultimaInspecaoPainelDevOps.isPresent()) {
					return ultimaInspecaoPainelDevOps.get();
				}
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
		return null;
	}

	public RelacaoProjetoSiglaGestor buscar(String nomePainel) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(RelacaoProjetoSiglaGestor.class);
			consulta.add(Restrictions.eq("nome_Projeto",nomePainel));
			return (RelacaoProjetoSiglaGestor)consulta.uniqueResult();
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
}
