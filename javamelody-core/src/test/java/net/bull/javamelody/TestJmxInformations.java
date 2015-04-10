package net.bull.javamelody;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Test;

/**
 * Test the jmx information retrieval.
 * @author Sascha Grebe
 *
 */
public class TestJmxInformations {

	/** Test.
	 * @throws JMException e */
	@Test
	public void testCollectJMXInformations() throws JMException {
		final MBeanServer mBeanServer = MBeans.getPlatformMBeanServer();
		final List<ObjectName> mBeans = new ArrayList<ObjectName>();
		try {
			final String objectName = "JavaMelody:type=TestMBean";
			mBeans.add(mBeanServer.registerMBean(new TestMBean(), new ObjectName(objectName))
					.getObjectName());
			final JmxValue value = new JmxValue();
			value.setLabel("Test");
			value.setName("Value");

			final JmxObject jmxObject = new JmxObject();
			jmxObject.setName(objectName);
			jmxObject.getJmxValue().add(value);

			JmxInformations.JMX_CONFIG = new JmxConfig();
			JmxInformations.JMX_CONFIG.getObject().add(jmxObject);

			final JavaInformations informations = new JavaInformations(null, true);
			final Collection<JmxInformation> jmxInformations = informations.getJmxInformations();
			assertEquals(1, jmxInformations.size());
			final JmxInformation jmxInfo = jmxInformations.iterator().next();
			assertEquals(11, jmxInfo.getValue());
		} finally {
			JmxInformations.JMX_CONFIG = null;
			for (final ObjectName registeredMBean : mBeans) {
				mBeanServer.unregisterMBean(registeredMBean);
			}
		}
	}
}
