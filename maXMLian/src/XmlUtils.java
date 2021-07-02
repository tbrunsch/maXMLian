import javax.xml.namespace.QName;

class XmlUtils
{
	static String getQualifiedName(QName name) {
		String prefix = name.getPrefix();
		String localPart = name.getLocalPart();
		return prefix == null || "".equals(prefix) ? localPart : prefix + ":" + localPart;
	}
}
