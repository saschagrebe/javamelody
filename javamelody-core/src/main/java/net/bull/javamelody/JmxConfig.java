package net.bull.javamelody;

class JmxConfig {

	private final String objectName;

	private final String attributeName;

	public JmxConfig(String jmxPath) {
		objectName = jmxPath.substring(0, jmxPath.lastIndexOf("@"));
		attributeName = jmxPath.substring(jmxPath.lastIndexOf("@") + 1);
	}

	public String getObjectName() {
		return objectName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getJRobinCounterName() {
		return stripSpecialChars(objectName) + "_" + stripSpecialChars(attributeName);
	}

	private String stripSpecialChars(String value) {
		if (value == null) {
			return "null";
		}
		return value.replace(".", "_").replaceAll("[^a-zA-Z0-9_]+", "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result + ((objectName == null) ? 0 : objectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JmxConfig other = (JmxConfig) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (objectName == null) {
			if (other.objectName != null)
				return false;
		} else if (!objectName.equals(other.objectName))
			return false;
		return true;
	}
}
