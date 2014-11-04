package mil.nga.giat.geowave.services.rest.data;

import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FeatureTypeEncoder
{
	private final static Logger LOGGER = Logger.getLogger(FeatureTypeEncoder.class);
	
	private static final String FEATURETYPE = "featureType";
	private static final String NAME = "name";
	private static final String NATIVE_NAME = "nativeName";
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String KEYWORDS = "keywords";
	private static final String NATIVE_CRS = "nativeCRS";
	private static final String SRS = "srs";
	private static final String NATIVE_BOUNDING_BOX = "nativeBoundingBox";
	private static final String LAT_LON_BOX = "latLonBoundingBox";
	private static final String PROJECTION_POLICY = "projectionPolicy";
	private static final String ENABLED = "enabled";
	private static final String METADATA = "metadata";
	private static final String KEY = "key";
	private static final String ENTRY = "entry";
	private static final String MAX_FEATURES = "maxFeatures";
	private static final String NUM_DECIMALS = "numDecimals";
	
	private String name;
	private String nativeName;
	private Map<String, Collection<String>> keywords;
	private String title;
	private String description;
	private String nativeCRS;
	private String srs;
	private BoundingBox _native;
	private BoundingBox latLon;
	private String projectionPolicy;
	private boolean enabled;
	private Map<String, Object> metadata;
	private int maxFeatures;
	private int numDecimals;
	private Collection<mil.nga.giat.geowave.services.rest.data.Attribute> attributes;
	
	public FeatureTypeEncoder() {
		super();
		
		setDescription("GeoWave Resource");
		setNativeCRS("GEOGCS[\"WGS 84\", \n  DATUM[\"World Geodetic System 1984\", \n    SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], \n    AUTHORITY[\"EPSG\",\"6326\"]], \n  PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], \n  UNIT[\"degree\", 0.017453292519943295], \n  AXIS[\"Geodetic longitude\", EAST], \n  AXIS[\"Geodetic latitude\", NORTH], \n  AUTHORITY[\"EPSG\",\"4326\"]]");
		setSrs("EPSG:4326");
		setNative(new BoundingBox("EPSG:4326"));
		setLatLon(new BoundingBox("GEOGCS[\"WGS84(DD)\", \n  DATUM[\"WGS84\", \n    SPHEROID[\"WGS84\", 6378137.0, 298.257223563]], \n  PRIMEM[\"Greenwich\", 0.0], \n  UNIT[\"degree\", 0.017453292519943295], \n  AXIS[\"Geodetic longitude\", EAST], \n  AXIS[\"Geodetic latitude\", NORTH]]"));
		setProjectionPolicy("FORCE_DECLARED");
		setEnabled(true);
		Map<String, Object> metadata = new LinkedHashMap<String, Object>();
		metadata.put("cachingEnabled", false);
		setMetadata(metadata);
	}

	@Override
	public String toString() {
		String xml = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root element
			Document document = docBuilder.newDocument();
			Element rootElement = document.createElement(FEATURETYPE);
			document.appendChild(rootElement);
			
			// name element
			Element nameElement = document.createElement(NAME);
			nameElement.appendChild(document.createTextNode(getName()));
			rootElement.appendChild(nameElement);
						
			// native element
			Element nativeElement = document.createElement(NATIVE_NAME);
			nativeElement.appendChild(document.createTextNode(getNativeName()));
			rootElement.appendChild(nativeElement);
			
			// title element
			Element titleElement = document.createElement(TITLE);
			titleElement.appendChild(document.createTextNode(getTitle()));
			rootElement.appendChild(titleElement);
			
			// description element
			Element descriptionElement = document.createElement(DESCRIPTION);
			descriptionElement.appendChild(document.createTextNode(getDescription()));
			rootElement.appendChild(descriptionElement);
			
			// keywords element
			Element keywordsElement = document.createElement(KEYWORDS);
			for (String key : getKeywords().keySet()) {
				for(String value : getKeywords().get(key)) {
					Element keyElement = document.createElement(key);
					keyElement.appendChild(document.createTextNode(value));
					keywordsElement.appendChild(keyElement);
				}
			}
			rootElement.appendChild(keywordsElement);
			
			// native CRS element
			Element nativeCRSElement = document.createElement(NATIVE_CRS);
			nativeCRSElement.appendChild(document.createTextNode(getNativeCRS()));
			rootElement.appendChild(nativeCRSElement);
			
			// srs element
			Element srsElement = document.createElement(SRS);
			srsElement.appendChild(document.createTextNode(getSrs()));
			rootElement.appendChild(srsElement);
			
			// native bounding box
			Element nativeBoxElement = document.createElement(NATIVE_BOUNDING_BOX);
			Element minxElement = document.createElement(BoundingBox.MINX);
			minxElement.appendChild(document.createTextNode(Double.toString(getNative().getMinx())));
			Element maxxElement = document.createElement(BoundingBox.MAXX);
			maxxElement.appendChild(document.createTextNode(Double.toString(getNative().getMaxx())));
			Element minyElement = document.createElement(BoundingBox.MINY);
			minyElement.appendChild(document.createTextNode(Double.toString(getNative().getMiny())));
			Element maxyElement = document.createElement(BoundingBox.MAXY);
			maxyElement.appendChild(document.createTextNode(Double.toString(getNative().getMaxy())));
			Element crsElement = document.createElement(BoundingBox.CRS);
			crsElement.appendChild(document.createTextNode(getNative().getCrs()));
			nativeBoxElement.appendChild(minxElement);
			nativeBoxElement.appendChild(maxxElement);
			nativeBoxElement.appendChild(minyElement);
			nativeBoxElement.appendChild(maxyElement);
			nativeBoxElement.appendChild(crsElement);
			rootElement.appendChild(nativeBoxElement);
			
			// lat long bounding box
			Element latLonBoxElement = document.createElement(LAT_LON_BOX);
			minxElement = document.createElement(BoundingBox.MINX);
			minxElement.appendChild(document.createTextNode(Double.toString(getNative().getMinx())));
			maxxElement = document.createElement(BoundingBox.MAXX);
			maxxElement.appendChild(document.createTextNode(Double.toString(getNative().getMaxx())));
			minyElement = document.createElement(BoundingBox.MINY);
			minyElement.appendChild(document.createTextNode(Double.toString(getNative().getMiny())));
			maxyElement = document.createElement(BoundingBox.MAXY);
			maxyElement.appendChild(document.createTextNode(Double.toString(getNative().getMaxy())));
			crsElement = document.createElement(BoundingBox.CRS);
			crsElement.appendChild(document.createTextNode(getNative().getCrs()));
			latLonBoxElement.appendChild(minxElement);
			latLonBoxElement.appendChild(maxxElement);
			latLonBoxElement.appendChild(minyElement);
			latLonBoxElement.appendChild(maxyElement);
			latLonBoxElement.appendChild(crsElement);
			rootElement.appendChild(latLonBoxElement);
			
			// projection policy element
			Element policyElement = document.createElement(PROJECTION_POLICY);
			policyElement.appendChild(document.createTextNode(getProjectionPolicy()));
			rootElement.appendChild(policyElement);
			
			// enabled element
			Element enabledElement = document.createElement(ENABLED);
			enabledElement.appendChild(document.createTextNode(Boolean.toString(isEnabled())));
			rootElement.appendChild(enabledElement);
			
			// meta element
			Element metaElement = document.createElement(METADATA);
			for (String key : getMetadata().keySet()) {
				// entry element
				Element entryElement = document.createElement(ENTRY);
				// set attribute to entry element
				Attr attribute = document.createAttribute(KEY);
				attribute.setValue(key);
				entryElement.setAttributeNode(attribute);
				
				entryElement.appendChild(document.createTextNode(getMetadata().get(key).toString()));
				metaElement.appendChild(entryElement);
			}
			rootElement.appendChild(metaElement);

			// max feature element
			Element maxFeatureElement = document.createElement(MAX_FEATURES);
			maxFeatureElement.appendChild(document.createTextNode(Integer.toString(getMaxFeatures())));
			rootElement.appendChild(maxFeatureElement);
			
			// num decimals element
			Element numDecimalsElement = document.createElement(NUM_DECIMALS);
			numDecimalsElement.appendChild(document.createTextNode(Integer.toString(getNumDecimals())));
			rootElement.appendChild(numDecimalsElement);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			xml = writer.getBuffer().toString().replaceAll("\n|\r", "");
		}
		catch (ParserConfigurationException | TransformerException | DOMException e) {
			LOGGER.error(e.getMessage());
		}
		return xml;
	}

	public String getName() {
		return name;
	}

	public void setName(
			String name ) {
		this.name = name;
	}

	public String getNativeName() {
		return nativeName;
	}

	public void setNativeName(
			String nativeName ) {
		this.nativeName = nativeName;
	}

	public Map<String, Collection<String>> getKeywords() {
		return keywords;
	}

	public void setKeywords(
			Map<String, Collection<String>> keywords ) {
		this.keywords = keywords;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(
			String title ) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(
			String description ) {
		this.description = description;
	}

	public String getNativeCRS() {
		return nativeCRS;
	}

	public void setNativeCRS(
			String nativeCRS ) {
		this.nativeCRS = nativeCRS;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(
			String srs ) {
		this.srs = srs;
	}

	public BoundingBox getNative() {
		return _native;
	}

	public void setNative(
			BoundingBox _native ) {
		this._native = _native;
	}

	public BoundingBox getLatLon() {
		return latLon;
	}

	public void setLatLon(
			BoundingBox latLon ) {
		this.latLon = latLon;
	}

	public String getProjectionPolicy() {
		return projectionPolicy;
	}

	public void setProjectionPolicy(
			String projectionPolicy ) {
		this.projectionPolicy = projectionPolicy;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(
			boolean enabled ) {
		this.enabled = enabled;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(
			Map<String, Object> metadata ) {
		this.metadata = metadata;
	}

	public int getMaxFeatures() {
		return maxFeatures;
	}

	public void setMaxFeatures(
			int maxFeatures ) {
		this.maxFeatures = maxFeatures;
	}

	public int getNumDecimals() {
		return numDecimals;
	}

	public void setNumDecimals(
			int numDecimals ) {
		this.numDecimals = numDecimals;
	}

	public Collection<mil.nga.giat.geowave.services.rest.data.Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(
			Collection<mil.nga.giat.geowave.services.rest.data.Attribute> attributes ) {
		this.attributes = attributes;
	}
}
