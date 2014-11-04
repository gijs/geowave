package mil.nga.giat.geowave.services.rest.data;

import java.io.File;
import java.net.HttpURLConnection;

public class GeoserverPublisher
{
	private static final String XML = "application/xml";
	private static final String SLD = "application/vnd.ogc.sld+xml";

	private final String restURL;
	private final String username;
	private final String password;
	
	public GeoserverPublisher(String restURL, String username,
			String password) {
		this.restURL = HttpUtils.decurtSlash(restURL);
		this.username = username;
		this.password = password;
	}
	
	public boolean datastoreExist(String workspace, String datastore) {
		String url = restURL + "/rest/workspaces/" + workspace
				+ "/datastores/" + datastore;
		String result = HttpUtils.get(url, XML, username, password);
		return result.length() > 0;
	}
	
	public boolean createDatastore(String workspace,
			DatastoreEncoder encoder) {
		String url = restURL + "/rest/workspaces/" + workspace
				+ "/datastores";
		return HttpURLConnection.HTTP_CREATED == HttpUtils.post(url, encoder.toString(), XML, username, password);
	}

	public boolean layerExist(String layer) {
		String url = restURL + "/rest/layers/" + layer;
		String result = HttpUtils.get(url, XML, username, password);
		return result.length() > 0;
	}

	public boolean publishLayer(String workspace, String dataStore, FeatureTypeEncoder encoder ) {
		String url = restURL + "/rest/workspaces/" + workspace + "/datastores/" + dataStore + "/featuretypes";
		return HttpURLConnection.HTTP_CREATED == HttpUtils.post(url, encoder.toString(), XML, username, password);
	}
	
	public boolean publishStyle(String styleName, String sld) {
		if (styleName != null && styleName.length() > 0) {
			String url = restURL + "/rest/styles?name=" + styleName;
			return HttpURLConnection.HTTP_CREATED == HttpUtils.post(url, sld, SLD, username, password);
		}
		else
			return false;
	}
	
	public boolean publishStyle(String styleName, File sld) {
		if (styleName != null && styleName.length() > 0) {
			String url = restURL + "/rest/styles?name=" + styleName;
			return HttpURLConnection.HTTP_CREATED == HttpUtils.post(url, sld, SLD, username, password);
		}
		else
			return false;
	}

	public boolean updateStyle(String styleName, String sld) {
		String url = restURL + "/rest/styles/" + styleName;
		return HttpURLConnection.HTTP_OK == HttpUtils.put(url, sld, SLD, username, password);
	}
	
	public boolean updateStyle(String styleName, File sld) {
		String url = restURL + "/rest/styles/" + styleName;
		return HttpURLConnection.HTTP_OK == HttpUtils.put(url, sld, SLD, username, password);
	}
}
