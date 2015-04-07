package net.bull.javamelody;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

class JmxObject {

	private String name;

	private List<JmxValue> jmxValue = new ArrayList<JmxValue>();

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JmxValue> getJmxValue() {
		return jmxValue;
	}

	public void setJmxValue(List<JmxValue> jmxValue) {
		this.jmxValue = jmxValue;
	}

	@Override
	public String toString() {
		return "JmxObject [name=" + name + ", jmxValue=" + jmxValue + "]";
	}

}
