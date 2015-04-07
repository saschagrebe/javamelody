package net.bull.javamelody;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

class JmxInformations {

	private static JmxConfig JMX_CONFIG = null;

	private static JmxConfig getJmxConfig(ServletContext servletContext) {
		if (JMX_CONFIG == null) {
			final String jmxConfig = Parameters.getParameter(Parameter.JMX_CONFIG);
			if (jmxConfig != null && !"".equals(jmxConfig)) {
				try {
					final JAXBContext context = JAXBContext.newInstance(JmxConfig.class);
					final Unmarshaller unmarshaller = context.createUnmarshaller();
					final URL configFileUrl;
					if (servletContext != null) {
						configFileUrl = servletContext.getResource(jmxConfig);
					} else {
						configFileUrl = JmxInformations.class.getResource(jmxConfig);
					}

					JMX_CONFIG = (JmxConfig) unmarshaller.unmarshal(configFileUrl);

					for (JmxObject nextObject : JMX_CONFIG.getObject()) {
						for (JmxValue nextJmxValue : nextObject.getJmxValue()) {
							final String counterName = counterName(nextObject, nextJmxValue);
							I18N.addTranslation(counterName, nextJmxValue.getLabel());
						}
					}
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);

				} catch (JAXBException e) {
					throw new RuntimeException(e);

				}
			}
		}

		return JMX_CONFIG;
	}

	static List<JmxInformation> buildJmxInformations(ServletContext servletContext) {
		final JmxConfig config = getJmxConfig(servletContext);
		final MBeanServer beanServer = MBeans.getPlatformMBeanServer();
		final List<JmxInformation> information = new ArrayList<JmxInformation>();
		for (JmxObject nextObject : config.getObject()) {
			for (JmxValue nextValue : nextObject.getJmxValue()) {
				try {
					final Object value = beanServer.getAttribute(
							new ObjectName(nextObject.getName()), nextValue.getName());
					if (value instanceof Number) {
						final String counterName = counterName(nextObject, nextValue);
						final Number number = (Number) value;

						information.add(new JmxInformation(counterName, number));
					}
				} catch (JMException e) {
					LOG.warn("Could not collect jmx bean info", e);
				}
			}
		}

		return information;
	}

	private static String counterName(JmxObject nextObject, JmxValue nextValue) {
		return stripSpecialChars(nextObject.getName()) + "_"
				+ stripSpecialChars(nextValue.getName());
	}

	private static String stripSpecialChars(String strValue) {
		if (strValue == null) {
			return "null";
		}
		return strValue.replace(".", "_").replaceAll("[^a-zA-Z0-9_]+", "");
	}
}
