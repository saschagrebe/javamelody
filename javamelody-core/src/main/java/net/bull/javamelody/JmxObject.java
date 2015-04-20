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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Les objets JMX, qui sont identifiés par le nom de l'objet.
 * @author Sascha Grebe
 */
class JmxObject {

	// the unique name of the MXBean
	private String name;

	// the configured values to be collected
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
