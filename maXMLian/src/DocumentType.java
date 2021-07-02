import java.util.Map;

public interface DocumentType extends Node
{
	String getName();
	Map<String, Entity> getEntities();
	Map<String, Notation> getNotations();
	String getPublicId();
	String getSystemId();
	String getInternalSubset();
}
