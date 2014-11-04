package mil.nga.giat.geowave.services.rest.data;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BoundingBox
{
	private final static Logger LOGGER = Logger.getLogger(BoundingBox.class);
	
	private String title;
	private double minx;
	private double maxx;
	private double miny;
	private double maxy;
	private String crs;
	
	final static String MINX = "minx";
	final static String MAXX = "maxx";
	final static String MINY = "miny";
	final static String MAXY = "maxy";
	final static String CRS = "crs";
	
	public BoundingBox() {
		super();
		
		minx = -180;
		maxx = 180;
		miny = -90;
		maxy = 90;
		crs = "";
	}

	public BoundingBox(String crs) {
		this();
		this.crs = crs;
	}
	
	public double getMinx() {
		return minx;
	}

	public void setMinx(
			double minx ) {
		this.minx = minx;
	}

	public double getMaxx() {
		return maxx;
	}

	public void setMaxx(
			double maxx ) {
		this.maxx = maxx;
	}

	public double getMiny() {
		return miny;
	}

	public void setMiny(
			double miny ) {
		this.miny = miny;
	}

	public double getMaxy() {
		return maxy;
	}

	public void setMaxy(
			double maxy ) {
		this.maxy = maxy;
	}

	public String getCrs() {
		return crs;
	}

	public void setCrs(
			String crs ) {
		this.crs = crs;
	}

	public Node toXML(
			Document document ) {
		Element rootElement = document.createElement(title);
		try {
			
			// root element
			document.appendChild(rootElement);
						
			// minx element
			Element minxElement = document.createElement(MINX);
			minxElement.appendChild(document.createTextNode(Double.toString(getMinx())));
			rootElement.appendChild(minxElement);
			
			// maxx element
			Element maxxElement = document.createElement(MAXX);
			maxxElement.appendChild(document.createTextNode(Double.toString(getMaxx())));
			rootElement.appendChild(maxxElement);
			
			// minx element
			Element minyElement = document.createElement(MINY);
			minyElement.appendChild(document.createTextNode(Double.toString(getMiny())));
			rootElement.appendChild(minyElement);
			
			// maxy element
			Element maxyElement = document.createElement(MAXY);
			maxyElement.appendChild(document.createTextNode(Double.toString(getMaxy())));
			rootElement.appendChild(maxyElement);
			
			// crs element
			Element crsElement = document.createElement(CRS);
			crsElement.appendChild(document.createTextNode(getCrs()));
			rootElement.appendChild(crsElement);
		}
		catch (DOMException e) {
			LOGGER.error(e.getMessage());
		}
		return rootElement;
	}

}
