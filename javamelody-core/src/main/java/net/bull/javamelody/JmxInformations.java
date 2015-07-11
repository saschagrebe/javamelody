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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Classe Helper à la JMX recueillir des informations auprès des MBeans configurés.
 * @author Sascha Grebe
 */
class JmxInformations {

	// caching the loaded jmx config 
	static JmxConfig JMX_CONFIG = null;

	private static JmxConfig getJmxConfig(ServletContext servletContext) {
		if (JMX_CONFIG == null) {
			// get the path to the jmx config file
			final String jmxConfig = Parameters.getParameter(Parameter.JMX_CONFIG);
			if (jmxConfig != null && !"".equals(jmxConfig)) {
				try {
					// unmarshall the xml config
					final JAXBContext context = JAXBContext.newInstance(JmxConfig.class);
					final Unmarshaller unmarshaller = context.createUnmarshaller();
					final URL configFileUrl;
					if (servletContext != null) {
						configFileUrl = servletContext.getResource(jmxConfig);
					} else {
						configFileUrl = JmxInformations.class.getResource(jmxConfig);
					}

					JMX_CONFIG = (JmxConfig) unmarshaller.unmarshal(configFileUrl);

				} catch (MalformedURLException e) {
					// can not handle it anyway
					throw new RuntimeException(e);

				} catch (JAXBException e) {
					// can not handle it anyway
					throw new RuntimeException(e);

				}

				// add the defined labels to be used in the graphs
				for (JmxObject nextObject : JMX_CONFIG.getObject()) {
					for (JmxValue nextJmxValue : nextObject.getJmxValue()) {
						final String counterName = nextJmxValue
								.getCounterName(nextObject.getName());
						I18N.addTranslation(counterName, nextJmxValue.getLabel());
					}
				}
			}
		}

		return JMX_CONFIG;
	}

	/**
	 * Collecter des informations JMX.
	 * @param servletContext Le contexte de la surveillance servlet.
	 * @return Les informations recueillies.
	 */
	static List<JmxInformation> buildJmxInformations(ServletContext servletContext) {
		final JmxConfig config = getJmxConfig(servletContext);

		final List<JmxInformation> information = new ArrayList<JmxInformation>();
		if (config != null) {
			final MBeanServer beanServer = MBeans.getPlatformMBeanServer();
			for (JmxObject nextObject : config.getObject()) {
				for (JmxValue nextValue : nextObject.getJmxValue()) {
					try {
						final Object value = loadValue(beanServer, nextObject, nextValue);
						if (value instanceof Number) {
							final String counterName = nextValue.getCounterName(nextObject
									.getName());
							final Number number = (Number) value;

							information.add(new JmxInformation(counterName, number));
						}

					} catch (MalformedObjectNameException e) {
						LOG.warn("Could not collect jmx bean info", e);
					}

				}
			}
		}

		return information;
	}

	private static Object loadValue(final MBeanServer beanServer, final JmxObject nextObject,
			final JmxValue nextValue) throws MalformedObjectNameException {
		// try loading the value
		final ObjectName objectName = new ObjectName(nextObject.getName());
		try {
			return beanServer.getAttribute(objectName, nextValue.getName());

		} catch (JMException e) {
			LOG.warn("Could not collect jmx bean info", e);

		}

		return null;
	}
}
