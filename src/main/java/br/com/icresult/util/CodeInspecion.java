package br.com.icresult.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

/**
 * Classe para representar o schema da API DA RSI
 * 
 * @author x205451
 * 
 */
/**
 * @author x205451
 *
 */
public class CodeInspecion {
	
	

	/**
	 * ID do componente cadastrado no GZen
	 */
	private String componentCode;

	/**
	 * Data de execução
	 */
	private String executionDate;

	/**
	 * Blocker issues
	 */
	private Integer bugQuantityHigh;

	/**
	 * Criticals issues
	 */
	private Integer bugQuantityMediumHigh;

	/**
	 * Major issues
	 */
	private Integer bugQuantityMedium;

	/**
	 * Minor issues
	 */
	private Integer bugQuantityLow;

	/**
	 * Vulnerabilidades
	 */
	private Integer securityQuantity;

	/**
	 * Code Smell
	 */
	private Integer codeSmellQuantity;

	/**
	 * Cobertura de testes
	 */
	private Double unitTestCoverage;

	/**
	 * Nota
	 */
	private Double aplicationScore;

	/**
	 * Teve alteração?
	 */
	private boolean modified;

	/**
	 * Data de commit atual
	 */
	private String currentCommitDate;

	/**
	 * Data de commit anterior
	 */
	private String beforeCommitDate;

	/**
	 * Linhas de código
	 */
	private Integer loc;

	/**
	 * String de retorno da API quando enviamos uma nova inspeção
	 */
	private String codeInspectionID;

	public static class Builder {
		
		private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");

		/**
		 * ID do componente cadastrado no GZen
		 */
		private String componentCode;

		/**
		 * Data de execução
		 */
		
		private String executionDate;

		/**
		 * Blocker issues
		 */
		private Integer bugQuantityHigh;

		/**
		 * Criticals issues
		 */
		private Integer bugQuantityMediumHigh;

		/**
		 * Major issues
		 */
		private Integer bugQuantityMedium;

		/**
		 * Minor issues
		 */
		private Integer bugQuantityLow;

		/**
		 * Vulnerabilidades
		 */
		private Integer securityQuantity;

		/**
		 * Code Smell
		 */
		private Integer codeSmellQuantity;

		/**
		 * Cobertura de testes
		 */
		private Double unitTestCoverage;

		/**
		 * Nota
		 */
		private Double aplicationScore;

		/**
		 * Teve alteração?
		 */
		private boolean modified;

		/**
		 * Data de commit atual
		 */
		private String currentCommitDate;

		/**
		 * Data de commit anterior
		 */
		private String beforeCommitDate;

		/**
		 * Linhas de código
		 */
		private Integer loc;

		/**
		 * String de retorno da API quando enviamos uma nova inspeção
		 */
		private String codeInspectionID;

		public String getComponentCode() {
			return componentCode;
		}

		public Builder withComponentCode(String componentCode) {
			this.componentCode = componentCode;
			return this;
		}

		public String getExecutionDate() {
			return executionDate;
		}

		public Builder withExecutionDate(Date executionDate) {
			this.executionDate =  formatter.format(executionDate);
			return this;
		}

		public Integer getBugQuantityHigh() {
			return bugQuantityHigh;
		}

		public Builder withBugQuantityHigh(Integer bugQuantityHigh) {
			this.bugQuantityHigh = bugQuantityHigh;
			return this;
		}

		public Integer getBugQuantityMediumHigh() {
			return bugQuantityMediumHigh;
		}

		public Builder withBugQuantityMediumHigh(Integer bugQuantityMediumHigh) {
			this.bugQuantityMediumHigh = bugQuantityMediumHigh;
			return this;
		}

		public Integer getBugQuantityMedium() {
			return bugQuantityMedium;
		}

		public Builder withBugQuantityMedium(Integer bugQuantityMedium) {
			this.bugQuantityMedium = bugQuantityMedium;
			return this;
		}

		public Integer getBugQuantityLow() {
			return bugQuantityLow;
		}

		public Builder withBugQuantityLow(Integer bugQuantityLow) {
			this.bugQuantityLow = bugQuantityLow;
			return this;
		}

		public Integer getSecurityQuantity() {
			return securityQuantity;
		}

		public Builder withSecurityQuantity(Integer securityQuantity) {
			this.securityQuantity = securityQuantity;
			return this;
		}

		public Integer getCodeSmellQuantity() {
			return codeSmellQuantity;
		}

		public Builder withCodeSmellQuantity(Integer codeSmellQuantity) {
			this.codeSmellQuantity = codeSmellQuantity;
			return this;
		}

		public Double getUnitTestCoverage() {
			return unitTestCoverage;
		}

		public Builder withUnitTestCoverage(Double unitTestCoverage) {
			this.unitTestCoverage = unitTestCoverage;
			return this;
		}

		public Double getAplicationScore() {
			return aplicationScore;
		}

		public Builder withAplicationScore(Double aplicationScore) {
			this.aplicationScore = aplicationScore;
			return this;
		}

		public boolean isModified() {
			return modified;
		}

		public Builder withModified(boolean modified) {
			this.modified = modified;
			return this;
		}

		public String getCurrentCommitDate() {
			return currentCommitDate;
		}

		public Builder withCurrentCommitDate(Date currentCommitDate) {
			this.currentCommitDate =  formatter.format(currentCommitDate);
			return this;
		}

		public String getBeforeCommitDate() {
			return beforeCommitDate;
		}

		public Builder withBeforeCommitDate(Date beforeCommitDate) {
			this.beforeCommitDate = formatter.format(beforeCommitDate);
			return this;
		}

		public Integer getLoc() {
			return loc;
		}

		public Builder withLoc(Integer loc) {
			this.loc = loc;
			return this;
		}

		public String getCodeInspectionID() {
			return codeInspectionID;
		}

		public CodeInspecion build() {
			return new CodeInspecion(this);
		}

	}

	private CodeInspecion(Builder builder) {
		this.componentCode = builder.getComponentCode();
		this.executionDate = builder.getExecutionDate();
		this.bugQuantityHigh = builder.getBugQuantityHigh();
		this.bugQuantityMediumHigh = builder.getBugQuantityMediumHigh();
		this.bugQuantityMedium = builder.getBugQuantityMedium();
		this.bugQuantityLow = builder.getBugQuantityLow();
		this.securityQuantity = builder.getSecurityQuantity();
		this.codeSmellQuantity = builder.getCodeSmellQuantity();
		this.unitTestCoverage = builder.getUnitTestCoverage();
		this.aplicationScore = builder.getAplicationScore();
		this.modified = builder.isModified();
		this.currentCommitDate = builder.getCurrentCommitDate();
		this.beforeCommitDate = builder.getBeforeCommitDate();
		this.loc = builder.getLoc();
	}

	public String getComponentCode() {
		return componentCode;
	}

	public String getExecutionDate() {
		return executionDate;
	}

	public Integer getBugQuantityHigh() {
		return bugQuantityHigh;
	}

	public Integer getBugQuantityMediumHigh() {
		return bugQuantityMediumHigh;
	}

	public Integer getBugQuantityMedium() {
		return bugQuantityMedium;
	}

	public Integer getBugQuantityLow() {
		return bugQuantityLow;
	}

	public Integer getSecurityQuantity() {
		return securityQuantity;
	}

	public Integer getCodeSmellQuantity() {
		return codeSmellQuantity;
	}

	public Double getUnitTestCoverage() {
		return unitTestCoverage;
	}

	public Double getAplicationScore() {
		return aplicationScore;
	}

	public boolean isModified() {
		return modified;
	}

	public String getCurrentCommitDate() {
		return currentCommitDate;
	}

	public String getBeforeCommitDate() {
		return beforeCommitDate;
	}

	public Integer getLoc() {
		return loc;
	}

	public String getCodeInspectionID() {
		return codeInspectionID;
	}

	public void setCodeInspectionID(String codeInspectionID) {
		this.codeInspectionID = codeInspectionID;
	}

	public JSONObject toJSON() {

		JSONObject obj = new JSONObject(this);

		return obj;
	}

	@Override
	public String toString() {
		return "CodeInspecion [componentCode=" + componentCode + ", executionDate=" + executionDate
				+ ", bugQuantityHigh=" + bugQuantityHigh + ", bugQuantityMediumHigh=" + bugQuantityMediumHigh
				+ ", bugQuantityMedium=" + bugQuantityMedium + ", bugQuantityLow=" + bugQuantityLow
				+ ", securityQuantity=" + securityQuantity + ", codeSmellQuantity=" + codeSmellQuantity
				+ ", unitTestCoverage=" + unitTestCoverage + ", aplicationScore=" + aplicationScore + ", modified="
				+ modified + ", currentCommitDate=" + currentCommitDate + ", beforeCommitDate=" + beforeCommitDate
				+ ", loc=" + loc + ", codeInspectionID=" + codeInspectionID + "]";
	}

}
