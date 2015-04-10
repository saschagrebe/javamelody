package net.bull.javamelody;

/**
 * MBean for testing purposes.
 * @author Sascha Grebe
 */
public class TestMBean implements TestMXBean {

	private int test = 10;

	/**
	 * Get a test value.
	 */
	@Override
	public int getValue() {
		return ++test;
	}
}
