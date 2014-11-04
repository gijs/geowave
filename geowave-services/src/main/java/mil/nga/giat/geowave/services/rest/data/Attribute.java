package mil.nga.giat.geowave.services.rest.data;

public class Attribute
{

	private String name;
	private int minOccurs;
	private int maxOccurs;
	private boolean nillable;
	private Class<?> binding;
	
	final static String ATTRIBUTE = "attribute";
	final static String NAME = "name";
	final static String MINOCCURS = "minOccurs";
	final static String MAXOCCURS = "maxOccurs";
	final static String NILLABLE = "nillable";
	final static String BINDING = "binding";

	public Attribute() {}

	public String getName() {
		return name;
	}

	public void setName(
			String name ) {
		this.name = name;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(
			int minOccurs ) {
		this.minOccurs = minOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(
			int maxOccurs ) {
		this.maxOccurs = maxOccurs;
	}

	public boolean isNillable() {
		return nillable;
	}

	public void setNillable(
			boolean nillable ) {
		this.nillable = nillable;
	}

	public Class<?> getBinding() {
		return binding;
	}

	public void setBinding(
			Class<?> binding ) {
		this.binding = binding;
	}
}
