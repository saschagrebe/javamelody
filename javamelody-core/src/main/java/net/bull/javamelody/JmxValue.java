package net.bull.javamelody;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Les valeurs JMX configurées qui doivent être collectées.
 * @author Sascha Grebe
 */
class JmxValue {

	// The attribute name.
	private String name;

	// the custom label used in the graph
	private String label;

	private Boolean attribute = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "JmxValue [name=" + name + ", label=" + label + "]";
	}

	@XmlTransient
	public Boolean getAttribute() {
		return attribute;
	}

	public void setAttribute(Boolean attribute) {
		this.attribute = attribute;
	}

}
