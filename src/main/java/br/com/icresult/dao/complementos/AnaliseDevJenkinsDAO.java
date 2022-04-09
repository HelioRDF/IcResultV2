package br.com.icresult.dao.complementos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.domain.complementos.Analise_Dev_Jenkins;
import br.com.icresult.domain.complementos.Analise_Dev_Mensal;
import br.com.icresult.util.HibernateUtil;
import jxl.common.Logger;

/**
 * 
 * [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author andre.graca
 * @version v1.0
 * @since 20-03-2020
 * 
 */

public class AnaliseDevJenkinsDAO extends GenericDAO<Analise_Dev_Jenkins> {

	private static final long serialVersionUID = -126164611692514074L;
	private static Logger LOG = Logger.getLogger(AnaliseDevJenkinsDAO.class);

	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal, aonde a nota é nula
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> listaNotaVazia() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("notaProjeto"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal, aonde o gestor nvl1 é nulo
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> gestorNvl1Null() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("gestorNivel1"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> listaResultadoVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("resultado"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> listaDebitoTecnico() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("debitoTecnicoMinutos"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> listaCoeficiente() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("coeficiente"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista de Analise_Dev_Mensal
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> listaTipoVazio() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("codigoAlterado"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @param codigo  - int
	 * @param sigla   - String
	 * @param projeto - String
	 * @return - Retorna uma objeto AnaliseCodigoHG
	 */
	public Analise_Dev_Jenkins buscarAnterior(int codigo, String sigla, String projeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			// consulta.add(Restrictions.idEq(codigo)); // Realiza uma consulta baseada no
			// ID.
			consulta.add(Restrictions.lt("id", codigo));
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.add(Restrictions.eq("nomeProjeto", projeto));
			consulta.setMaxResults(1);
			consulta.addOrder(Order.desc("id"));
			Analise_Dev_Jenkins resultado = (Analise_Dev_Jenkins) consulta.uniqueResult(); // Utilizado para
																							// retornar um
																							// unico
			// resultado
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @return - Retorna uma lista Analise_Dev_Mensal com dataCommit = Null
	 */
	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> listarParaDataCommit() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("dataCommit"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	// -----------------------------------------------------------------------------------
	/**
	 * 
	 * @param codigo  - int
	 * @param sigla   - string
	 * @param projeto - string
	 * @return - Retorna a quantidade na lista
	 */
	public int qtdList(int codigo, String sigla, String projeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Mensal.class);
			consulta.add(Restrictions.lt("id", codigo));
			consulta.add(Restrictions.eq("sigla", sigla));
			consulta.add(Restrictions.eq("nomeProjeto", projeto));
			@SuppressWarnings("unchecked")
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado.size();
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	/**
	 * @return - Retorna uma lista de Analise_HK_Poc_URA, aonde a nota é nula
	 */
	@SuppressWarnings("unchecked")
	public void limparSelecaoNovasAnalises() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("selecionado"));
			for (Analise_Dev_Jenkins analise : (List<Analise_Dev_Jenkins>) consulta.list()) {
				analise.setSelecionado("ui-icon-blank");
				editar(analise);
			}

		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> gestorEntregaNull() {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("painelGestor"));
			consulta.addOrder(Order.desc("id"));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

	public void buscaUltimaInspecaoComNomeProjeto(String nomeProjeto, String nomeProjetoPadronizado, String repositorio,
			String ambiente) {
		// Lista todas as inspeçãos com mesma chave passada e retorna a com a data de
		// captura mais recente
		try {
			Analise_Dev_Jenkins resultado = buscaUltimaInspecao(nomeProjeto);
			if (resultado == null) {
				LOG.error("Não foi encontrada nenhuma inspeção com a chave " + nomeProjeto);
			} else {
				Analise_Dev_Jenkins analiseEncontrada = resultado;
				analiseEncontrada
						.setDataCaptura(Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant()));
				analiseEncontrada.setDescricao("Inspeção copiada da inspeção de ID = " + analiseEncontrada.getId());
				analiseEncontrada.setGestorNivel1(null);
				analiseEncontrada.setPainelGestor(null);
				analiseEncontrada.setAmbiente(ambiente);
				analiseEncontrada.setRepositorio(repositorio);
				analiseEncontrada.setPadraoNomeSonar(nomeProjetoPadronizado);
				analiseEncontrada.setRevisarAnalise(null);
				salvar(analiseEncontrada);
				LOG.info(analiseEncontrada);
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "finally" })
	public Analise_Dev_Jenkins buscaUltimaInspecao(String nomeProjeto) {
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		List<Analise_Dev_Jenkins> resultado = new ArrayList<Analise_Dev_Jenkins>();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.eq("nomeProjeto", nomeProjeto));
			consulta.addOrder(Order.desc("id"));
			consulta.setMaxResults(1);
			resultado = consulta.list();
			return (resultado == null || resultado.isEmpty()) ? null : resultado.get(0);
		} catch (RuntimeException erro) {
			throw erro;

		} finally {
			sessao.close();
			return (resultado == null || resultado.isEmpty()) ? null : resultado.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Analise_Dev_Jenkins> analisesComRepositorioAmbienteNulos() {
		// Lista todas as analises com repositorio e ambiente nulo apartir do mês 10 de
		// 2019
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Analise_Dev_Jenkins.class);
			consulta.add(Restrictions.isNull("ambiente"));
			consulta.add(Restrictions.isNull("repositorio"));
			consulta.add(Restrictions.gt("dataCaptura",
					Date.from(LocalDate.of(2019, 10, 01).atStartOfDay(ZoneId.systemDefault()).toInstant())));
			List<Analise_Dev_Jenkins> resultado = consulta.list();
			return resultado;
		} catch (RuntimeException erro) {
			throw erro;
		} finally {
			sessao.close();
		}
	}

}