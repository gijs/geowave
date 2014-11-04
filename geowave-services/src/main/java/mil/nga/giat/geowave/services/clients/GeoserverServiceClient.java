package mil.nga.giat.geowave.services.clients;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mil.nga.giat.geowave.services.GeoserverService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

public class GeoserverServiceClient
{
	private final static Logger log = Logger.getLogger(GeoserverServiceClient.class);

	private final GeoserverService geoserverService;

	public GeoserverServiceClient(
			final String baseUrl ) {

		geoserverService = WebResourceFactory.newResource(
				GeoserverService.class,
				ClientBuilder.newBuilder().register(
						MultiPartFeature.class).build().target(
						baseUrl));
	}

	public boolean createWorkspace(
			final String workspace ) {
		final FormDataMultiPart multiPart = new FormDataMultiPart();

		multiPart.field(
				"workspace",
				workspace);

		final Response resp = geoserverService.createWorkspace(multiPart);
		return resp.getStatus() == Status.CREATED.getStatusCode();
	}

	public String[] getStyles() {
		final Response resp = geoserverService.getStyles();
		resp.bufferEntity();
		return resp.readEntity(String[].class);
	}

	public InputStream getStyle(
			final String styleName ) {
		return (InputStream) geoserverService.getStyle(
				styleName).getEntity();
	}

	public boolean publishStyle(
			final File[] styleFiles )
			throws FileNotFoundException {
		final FormDataMultiPart multiPart = new FormDataMultiPart();

		for (final File styleFile : styleFiles) {
			multiPart.bodyPart(new FileDataBodyPart(
					"file",
					styleFile));
		}

		final Response resp = geoserverService.publishStyle(multiPart);
		return resp.getStatus() == Status.OK.getStatusCode();
	}

	public String[] getDatastores() {
		return getDatastores("");
	}

	public String[] getDatastores(
			final String workspace ) {
		final Response resp = geoserverService.getDatastores(workspace);
		resp.bufferEntity();
		return resp.readEntity(String[].class);
	}

	public InputStream getDatastore(
			final String datastoreName ) {
		return getDatastore(
				datastoreName,
				"");
	}

	public InputStream getDatastore(
			final String datastoreName,
			final String workspace ) {
		return (InputStream) geoserverService.getDatastore(
				datastoreName,
				workspace).getEntity();
	}

	public boolean publishDatastore(
			final String zookeeperUrl,
			final String username,
			final String password,
			final String instance,
			final String namespace ) {
		return publishDatastore(
				zookeeperUrl,
				username,
				password,
				instance,
				namespace,
				null,
				null,
				null,
				null);
	}

	public boolean publishDatastore(
			final String zookeeperUrl,
			final String username,
			final String password,
			final String instance,
			final String namespace,
			final String lockMgmt,
			final String authMgmtProvider,
			final String authDataUrl,
			final String workspace ) {
		final FormDataMultiPart multiPart = new FormDataMultiPart();

		multiPart.field(
				"zookeeperUrl",
				zookeeperUrl);

		multiPart.field(
				"username",
				username);

		multiPart.field(
				"password",
				password);

		multiPart.field(
				"instance",
				instance);

		multiPart.field(
				"namespace",
				namespace);

		if (lockMgmt != null) {
			multiPart.field(
					"lockMgmt",
					lockMgmt);
		}

		if (authMgmtProvider != null) {
			multiPart.field(
					"authMgmtPrvdr",
					authMgmtProvider);

			if (authDataUrl != null) {
				multiPart.field(
						"authDataUrl",
						authDataUrl);
			}
		}

		if (workspace != null) {
			multiPart.field(
					"workspace",
					workspace);
		}

		final Response resp = geoserverService.publishDatastore(multiPart);
		return resp.getStatus() == Status.OK.getStatusCode();
	}

	public String[] getLayers() {
		final Response resp = geoserverService.getLayers();
		resp.bufferEntity();
		return resp.readEntity(String[].class);
	}

	public InputStream getLayer(
			final String layerName ) {
		return (InputStream) geoserverService.getLayer(
				layerName).getEntity();
	}

	public boolean publishLayer(
			final String datastore,
			final String defaultStyle,
			final SimpleFeatureType featureType ) {
		return publishLayer(
				datastore,
				defaultStyle,
				featureType,
				null);
	}

	public boolean publishLayer(
			final String datastore,
			final String defaultStyle,
			final SimpleFeatureType featureType,
			final String workspace ) {
		final FormDataMultiPart multiPart = new FormDataMultiPart();

		multiPart.field(
				"datastore",
				datastore);

		multiPart.field(
				"defaultStyle",
				defaultStyle);

		if (workspace != null) {
			multiPart.field(
					"workspace",
					workspace);
		}

		final String json = createFeatureTypeJson(featureType);

		multiPart.field(
				"featureType",
				json);

		final Response resp = geoserverService.publishLayer(multiPart);
		return resp.getStatus() == Status.OK.getStatusCode();
	}

	private String createFeatureTypeJson(
			final SimpleFeatureType featureType ) {

		final JSONObject featTypeJson = new JSONObject();

		featTypeJson.put(
				"name",
				featureType.getTypeName());
		featTypeJson.put(
				"nativeName",
				featureType.getTypeName());
		featTypeJson.put(
				"title",
				featureType.getTypeName());
		featTypeJson.put(
				"srs",
				"EPSG:4326");

		final JSONObject attribsJson = new JSONObject();
		final JSONArray attribArray = new JSONArray();
		for (final AttributeDescriptor attribDesc : featureType.getAttributeDescriptors()) {

			final JSONObject attribJson = new JSONObject();
			attribJson.put(
					"name",
					attribDesc.getLocalName());
			attribJson.put(
					"binding",
					attribDesc.getType().getBinding().getName());

			attribArray.add(attribJson);
		}

		attribsJson.put(
				"attribute",
				attribArray);

		featTypeJson.put(
				"attributes",
				attribsJson);

		final JSONObject jsonObj = new JSONObject();
		jsonObj.put(
				"featureType",
				featTypeJson);

		return jsonObj.toString();
	}
}
