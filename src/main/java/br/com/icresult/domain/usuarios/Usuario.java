package br.com.icresult.domain.usuarios;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.icresult.domain.complementos.GenericDomain;

/**
 * [ Detalhes... ] -Classe POJO Usuário, e entity do banco de dados via
 * Hibernate. -Entity - Diz que a classe é uma entidade do hibernate
 * 
 * -Column( | length = Tamanho do campo | name = define o nome no banco |
 * nullable = Diz se o campo pode ou não ser nulo "True ou False"); | precision
 * = Define quantidade de digitos | scale= Define quantos digitos ficam após a
 * virgula
 * 
 * -JoinColumn - Permite personalizar colunas que são chaves estrangeiras
 * 
 * -Temporal(TemporalType.param) |DATE=Data |Time=Hora |TIMESTAMP=Data e hora
 * 
 * -MD5 - A senha está utilizando MD5 para criptografia.
 * 
 * -Atributos |dataCadastro - status - nome - senha - cpf - rg - dataNascimento
 * - email - curriculo
 * 
 * @author helio.franca
 * @version v1.7
 * @since N/A
 * 
 */

@SuppressWarnings("serial")
@Entity
public class Usuario extends GenericDomain {

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date ultimoLogin = Date.from(LocalDateTime.now().atZone(ZoneId.of("Etc/GMT+3")).toInstant());

	@Column(nullable = false)
	private Boolean status;

	@Column(nullable = false)
	private Boolean admin;

	@Column(length = 60, nullable = false)
	private String nome;

	@Column(length = 32)
	private String senha;

	@Transient
	private String senhaSemCriptografia;

	@Column(length = 100, nullable = false, unique = true)
	private String email;

	@Column(length = 250)
	private String descricao;

	@Column(nullable = true)
	private Boolean perfilDev;

	@Column(nullable = true)
	private Boolean perfilHk;

	// -----------------------------------------------------------------------------

	public Boolean getStatus() {
		return status;
	}

	public String getSenhaSemCriptografia() {
		return senhaSemCriptografia;
	}

	public void setSenhaSemCriptografia(String senhaSemCriptografia) {
		this.senhaSemCriptografia = senhaSemCriptografia;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Date getUltimoLogin() {
		return ultimoLogin;
	}

	public void setUltimoLogin(Date ultimoLogin) {
		this.ultimoLogin = ultimoLogin;
	}

	public Boolean getPerfilDev() {
		return perfilDev;
	}

	public void setPerfilDev(Boolean perfilDev) {
		this.perfilDev = perfilDev;
	}

	public Boolean getPerfilHk() {
		return perfilHk;
	}

	public void setPerfilHk(Boolean perfilHk) {
		this.perfilHk = perfilHk;
	}

	@Override
	public String toString() {
		return "Usuario [dataCadastro=" + dataCadastro + ", ultimoLogin=" + ultimoLogin + ", status=" + status
				+ ", admin=" + admin + ", nome=" + nome + ", senha=" + senha + ", senhaSemCriptografia="
				+ senhaSemCriptografia + ", email=" + email + ", descricao=" + descricao + ", perfilDev=" + perfilDev
				+ ", perfilHk=" + perfilHk + "]";
	}
}
