package net.bull.javamelody;

import java.io.Serializable;

class JmxInformation implements Serializable {

	private static final long serialVersionUID = 4581224399754365782L;

	private final String name;

	private final Number value;

	public JmxInformation(String name, Number value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Number getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
