package br.com.icresult.dao.usuarios;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.icresult.dao.complementos.GenericDAO;
import br.com.icresult.domain.usuarios.Usuario;
import br.com.icresult.util.HibernateUtil;

/**
 * -Classe Dao de usuário. [ Detalhes... ] Referencia.
 * http://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */
public class UsuarioDAO extends GenericDAO<Usuario> {

	
	private static final long serialVersionUID = 389626404734210262L;
	
	private static final String EMAIL="email";
	/**
	 * Autentica usuário do sistema
	 * 
	 * @param email - String
	 * @return - Retorna um objeto do tipo usuário
	 */
	public Usuario localizarUsuario(String email) {

		// Abre uma sessão com Hibernate
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Usuario.class);
			consulta.add(Restrictions.eq(EMAIL, email));
			Usuario resultado = (Usuario) consulta.uniqueResult();
			return resultado;
		} catch (Exception e) {
			return null;
		} finally {
			sessao.close();
		}
	}
	
	
	/**
	 * Autentica usuário do sistema
	 * 
	 * @param email - String
	 * @param senha - String
	 * @return - Retorna um objeto do tipo usuário
	 */
	public Usuario autenticar(String email, String senha) {

		// Abre uma sessão com Hibernate
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Usuario.class);
			consulta.add(Restrictions.eq(EMAIL, email));
			SimpleHash hash = new SimpleHash("md5", senha);
			consulta.add(Restrictions.eq("senha", hash.toHex()));
			Usuario resultado = (Usuario) consulta.uniqueResult();
			return resultado;
		} catch (Exception e) {
			return null;
		} finally {
			sessao.close();
		}
	}

	/**
	 * Busca usuário por ID
	 * 
	 * @param id - Long
	 * @return - Retorna um objeto do tipo usuário
	 */
	public Usuario buscarUsuarioId(Long id) {
		// Abre uma sessão com Hibernate
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Usuario.class);
			consulta.add(Restrictions.eq("codigo", id));
			Usuario resultado = (Usuario) consulta.uniqueResult();
			return resultado;
		} catch (Exception e) {
			return null;
		} finally {
			sessao.close();
		}
	}

	/**
	 * Valida email passando email
	 * @param email - String
	 * @return - Retorna um boolean
	 */
	public Boolean validarEmail(String email) {
		Boolean permitir = false;
		// Abre uma sessão com Hibernate
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		Criteria consulta = sessao.createCriteria(Usuario.class);
		consulta.add(Restrictions.eq(EMAIL, email));
		Usuario resultado = (Usuario) consulta.uniqueResult();
		if (resultado == null) {
			permitir = true;
		}
		return permitir;
	}
/**
 * Valida Email passando email e id
 * @param email - String
 * @param id - Long
 * @return - Retorna um boolean
 */
	public Boolean validarEmail(String email, Long id) {
		Boolean permitir = false;
		// Abre uma sessão com Hibernate
		Session sessao = HibernateUtil.getFabricadeSessoes().openSession();
		Criteria consulta = sessao.createCriteria(Usuario.class);
		consulta.add(Restrictions.eq(EMAIL, email));
		Usuario resultado = (Usuario) consulta.uniqueResult();
		if (resultado == null || id == resultado.getCodigo()) {
			permitir = true;
		}
		return permitir;

	}

}
