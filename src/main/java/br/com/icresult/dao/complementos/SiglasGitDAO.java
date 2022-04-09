package br.com.icresult.dao.complementos;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.domain.complementos.ControleGitDevMensal;
import br.com.icresult.domain.complementos.SiglasGit;
import br.com.icresult.util.HibernateUtil;

public class SiglasGitDAO extends GenericDAO<SiglasGit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 
	 * Metodo que auxilia a classe SiglasGit na verificação de atualizações
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void verificaAtualizacoes(SiglasGit sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria select = sessao.createCriteria(ControleGitDevMensal.class);
			select.add(Restrictions.eq("nomeSistema", sigla.getNome_Projeto()));
			select.addOrder(Order.desc("dataCommit"));
			List<ControleGitDevMensal> listaControle = select.list();
			List<ControleGitDevMensal> listaModulosAtualizados = listaControle.stream().filter(c -> c.isAlteracao())
					.collect(Collectors.toList());
			if (!listaModulosAtualizados.isEmpty()) {
				sigla.setAlteracao(true);
			} else {
				sigla.setAlteracao(false);
			}
			if (!listaControle.isEmpty()) {
				sigla.setDataCommit(listaControle.get(0).getDataCommit());
			}
			editar(sigla);

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
			Criteria update = sessao.createCriteria(SiglasGit.class);
			update.add(Restrictions.isNull("selecionado"));
			List<SiglasGit> listaControle = update.list();
			for (SiglasGit controle : listaControle) {
				controle.setSelecionado("ui-icon-blank");
				editar(controle);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}
	
	/**
	 * Busca a mais recente analise mensal para uma sigla do git
	 * @param sigla - Objeto do tipo sigla a ser pesquisada
	 * @return - retorna a analise mais recente de uma Sigla pesquisada
	 */

	@SuppressWarnings("unchecked")
	public Analise_Dev_Mensal buscarUltimaInspecaoPorNomeSistema(SiglasGit sigla) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria select = sessao.createCriteria(Analise_Dev_Mensal.class);
			select.addOrder(Order.desc("dataCaptura"));
			List<Analise_Dev_Mensal> listaControle = select.list();
			if (!listaControle.isEmpty()) {
				Optional<Analise_Dev_Mensal> ultimaInspecaoSiglaGit = (Optional<Analise_Dev_Mensal>) listaControle.stream().filter(
						analise -> analise.getNomeProjeto().toLowerCase().equals(sigla.getNome_Projeto().toLowerCase()))
						.findFirst();
				if(ultimaInspecaoSiglaGit.isPresent()) {
					return ultimaInspecaoSiglaGit.get();
				}
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
		return null;
	}

}
