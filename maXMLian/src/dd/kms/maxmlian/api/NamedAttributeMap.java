package dd.kms.maxmlian.api;

public interface NamedAttributeMap extends Iterable<Attr>
{
	Attr get(String name);
	Attr get(int index);
	int size();
}
