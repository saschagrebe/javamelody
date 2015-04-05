package net.bull.javamelody;

import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class JmxInformations {

	private static final List<JmxConfig> JMX_CONFIG = new ArrayList<JmxConfig>();

	private static List<JmxConfig> getJmxConfig() {
		if (JMX_CONFIG.isEmpty()) {
			final String jmxConfig = Parameters.getParameter(Parameter.JMX_CONFIG);
			if (jmxConfig != null && !"".equals(jmxConfig)) {
				final String[] jmxPaths = jmxConfig.split(";");
				for (String nextJmxPath : jmxPaths) {
					JMX_CONFIG.add(new JmxConfig(nextJmxPath));
				}
			}
		}
		return JMX_CONFIG;
	}

	static List<JmxInformation> buildJmxInformations() {
		final List<JmxConfig> config = getJmxConfig();
		final MBeanServer beanServer = MBeans.getPlatformMBeanServer();
		final List<JmxInformation> information = new ArrayList<JmxInformation>();
		for (JmxConfig nextConfig : config) {
			try {
				final Object value = beanServer.getAttribute(
						new ObjectName(nextConfig.getObjectName()), nextConfig.getAttributeName());
				if (value instanceof Number) {
					final Number number = (Number) value;
					information.add(new JmxInformation(nextConfig, number.doubleValue()));
				}
			} catch (JMException e) {
				LOG.warn("Could not collect jmx bean info", e);
			}
		}

		return information;
	}
}
