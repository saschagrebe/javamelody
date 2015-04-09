package net.bull.javamelody;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * La configuration de JMX.
 * @author Sascha Grebe
 */
@XmlRootElement(name = "jmxConfig")
class JmxConfig {

	// the jmx objects
	private List<JmxObject> object = new ArrayList<JmxObject>();

	public List<JmxObject> getObject() {
		return object;
	}

	public void setObject(List<JmxObject> object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "JmxConfig [object=" + object + "]";
	}

}
