package net.bull.javamelody;

import java.io.Serializable;

class JmxInformation implements Serializable {

	private static final long serialVersionUID = 4581224399754365782L;

	private JmxConfig jmxConfig;

	private double value;

	public JmxInformation(JmxConfig jmxConfig, double value) {
		super();
		this.jmxConfig = jmxConfig;
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public JmxConfig getJmxConfig() {
		return jmxConfig;
	}

	public void setJmxConfig(JmxConfig jmxConfig) {
		this.jmxConfig = jmxConfig;
	}

}
