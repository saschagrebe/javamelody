/*
 * Copyright 2008-2015 by Emeric Vernat
 *
 *     This file is part of Java Melody.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
