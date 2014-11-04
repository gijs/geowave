package mil.nga.giat.geowave.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mil.nga.giat.geowave.services.GeoserverService;
import mil.nga.giat.geowave.services.utils.ServiceUtils;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Produces(MediaType.APPLICATION_JSON)
@Path("/geoserver")
public class GeoserverServiceImpl implements
		GeoserverService
{
	private final static Logger log = Logger.getLogger(GeoserverServiceImpl.class);

	private final String geoserverUrl;
	private final String geoserverUser;
	private final String geoserverPass;
	private final String defaultWorkspace;

	public GeoserverServiceImpl(
			@Context final ServletConfig servletConfig ) {
		final Properties props = ServiceUtils.loadProperties(servletConfig.getServletContext().getResourceAsStream(
				servletConfig.getInitParameter("config.properties")));

		geoserverUrl = ServiceUtils.getProperty(
				props,
				"geoserver.url");

		geoserverUser = ServiceUtils.getProperty(
				props,
				"geoserver.username");

		geoserverPass = ServiceUtils.getProperty(
				props,
				"geoserver.password");

		defaultWorkspace = ServiceUtils.getProperty(
				props,
				"geoserver.workspace");
	}

	@Override
	public Response createWorkspace(
			final FormDataMultiPart multiPart ) {

		final String workspace = multiPart.getField(
				"workspace").getValue();

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		return target.path(
				"geoserver/rest/workspaces").request().post(
				Entity.entity(
						"{'workspace':{'name':'" + workspace + "'}}",
						MediaType.APPLICATION_JSON));
	}

	@Override
	public Response getStyles() {

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		final Response resp = target.path(
				"geoserver/rest/styles.json").request().get();

		if (resp.getStatus() == Status.OK.getStatusCode()) {

			resp.bufferEntity();

			final Pattern p = Pattern.compile("name\".\"([^\"]+)");
			final Matcher m = p.matcher(resp.readEntity(String.class));

			final ArrayList<String> styleNames = new ArrayList<String>();
			while (m.find()) {
				styleNames.add(m.group(1));
			}

			return Response.ok(
					styleNames.toArray(new String[styleNames.size()])).build();
		}

		return resp;
	}

	@Override
	public Response getStyle(
			@PathParam("styleName") final String styleName ) {

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		final Response resp = target.path(
				"geoserver/rest/styles/" + styleName + ".sld").request().get();

		if (resp.getStatus() == Status.OK.getStatusCode()) {
			final InputStream inStream = (InputStream) resp.getEntity();

			return Response.ok(
					inStream,
					MediaType.APPLICATION_XML).header(
					"Content-Disposition",
					"attachment; filename=\"" + styleName + ".sld\"").build();
		}

		return resp;
	}

	@Override
	public Response publishStyle(
			final FormDataMultiPart multiPart ) {

		// read the list of files & upload to geoserver services
		for (final FormDataBodyPart field : multiPart.getFields("file")) {
			final String filename = field.getFormDataContentDisposition().getFileName();
			if (filename.endsWith(".sld") || filename.endsWith(".xml")) {
				final String styleName = filename.substring(
						0,
						filename.length() - 4);
				final InputStream inStream = field.getValueAs(InputStream.class);

				final Client client = ClientBuilder.newClient().register(
						HttpAuthenticationFeature.basic(
								geoserverUser,
								geoserverPass));
				final WebTarget target = client.target(geoserverUrl);

				// create a new geoserver style
				target.path(
						"geoserver/rest/styles").request().post(
						Entity.entity(
								// "<style><name>" + styleName +
								// "</name><filename>" + styleName +
								// ".sld</filename></style>",
								"{'style':{'name':'" + styleName + "','filename':'" + styleName + ".sld'}}",
								MediaType.APPLICATION_JSON));

				// upload the style to geoserver
				final Response resp = target.path(
						"geoserver/rest/styles/" + styleName).request().put(
						Entity.entity(
								inStream,
								"application/vnd.ogc.sld+xml"));

				if (resp.getStatus() != Status.OK.getStatusCode()) {
					return resp;
				}
			}
		}

		return Response.ok().build();
	}

	@Override
	public Response getDatastores(
			@DefaultValue("") @QueryParam("workspace") String customWorkspace ) {

		customWorkspace = (customWorkspace.equals("")) ? defaultWorkspace : customWorkspace;

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		final Response resp = target.path(
				"geoserver/rest/workspaces/" + customWorkspace + "/datastores.json").request().get();

		if (resp.getStatus() == Status.OK.getStatusCode()) {

			resp.bufferEntity();

			final Pattern p = Pattern.compile("name\".\"([^\"]+)");
			final Matcher m = p.matcher(resp.readEntity(String.class));

			final ArrayList<String> datastoreNames = new ArrayList<String>();
			while (m.find()) {
				datastoreNames.add(m.group(1));
			}

			return Response.ok(
					datastoreNames.toArray(new String[datastoreNames.size()])).build();
		}

		return resp;
	}

	@Override
	public Response getDatastore(
			@PathParam("datastoreName") final String datastoreName,
			@DefaultValue("") @QueryParam("workspace") String customWorkspace ) {

		customWorkspace = (customWorkspace.equals("")) ? defaultWorkspace : customWorkspace;

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		final Response resp = target.path(
				"geoserver/rest/workspaces/" + customWorkspace + "/datastores/" + datastoreName + ".json").request().get();

		if (resp.getStatus() == Status.OK.getStatusCode()) {
			final InputStream inStream = (InputStream) resp.getEntity();

			return Response.ok(
					inStream).header(
					"Content-Disposition",
					"attachment; filename=\"" + datastoreName + "\"").build();
		}

		return resp;
	}

	@Override
	public Response publishDatastore(
			final FormDataMultiPart multiPart ) {

		final String zookeeperUrl = multiPart.getField(
				"zookeeperUrl").getValue();

		final String username = multiPart.getField(
				"username").getValue();

		final String password = multiPart.getField(
				"password").getValue();

		final String instance = multiPart.getField(
				"instance").getValue();

		final String namespace = multiPart.getField(
				"namespace").getValue();

		final String lockMgmt = (multiPart.getField("lockMgmt") != null) ? multiPart.getField(
				"lockMgmt").getValue() : "memory";

		final String authMgmtPrvdr = (multiPart.getField("authMgmtPrvdr") != null) ? multiPart.getField(
				"authMgmtPrvdr").getValue() : "empty";

		final String authDataUrl = (multiPart.getField("authDataUrl") != null) ? multiPart.getField(
				"authDataUrl").getValue() : "";

		final String customWorkspace = (multiPart.getField("workspace") != null) ? multiPart.getField(
				"workspace").getValue() : defaultWorkspace;

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));

		final WebTarget target = client.target(geoserverUrl);

		final String dataStoreJson = createDatastoreJson(
				zookeeperUrl,
				username,
				password,
				instance,
				namespace,
				lockMgmt,
				authMgmtPrvdr,
				authDataUrl,
				true);

		// create a new geoserver style
		final Response resp = target.path(
				"geoserver/rest/workspaces/" + customWorkspace + "/datastores").request().post(
				Entity.entity(
						dataStoreJson,
						MediaType.APPLICATION_JSON));

		if (resp.getStatus() == Status.CREATED.getStatusCode()) {
			return Response.ok().build();
		}

		return resp;
	}

	private String createDatastoreJson(
			final String zookeeperUrl,
			final String username,
			final String password,
			final String instance,
			final String namespace,
			final String lockMgmt,
			final String authMgmtProvider,
			final String authDataUrl,
			final boolean enabled ) {

		final JSONObject dataStore = new JSONObject();
		dataStore.put(
				"name",
				namespace);
		dataStore.put(
				"type",
				"GeoWave Datastore");
		dataStore.put(
				"enabled",
				Boolean.toString(enabled));

		final JSONObject connParams = new JSONObject();
		connParams.put(
				"ZookeeperServers",
				zookeeperUrl);
		connParams.put(
				"UserName",
				username);
		connParams.put(
				"Password",
				password);
		connParams.put(
				"InstanceName",
				instance);
		connParams.put(
				"Namespace",
				namespace);
		connParams.put(
				"Lock Management",
				lockMgmt);
		connParams.put(
				"Authorization Management Provider",
				authMgmtProvider);
		if (!authMgmtProvider.equals("empty")) {
			connParams.put(
					"Authorization Data URL",
					authDataUrl);
		}

		dataStore.put(
				"connectionParameters",
				connParams);

		final JSONObject jsonObj = new JSONObject();
		jsonObj.put(
				"dataStore",
				dataStore);

		return jsonObj.toString();
	}

	@Override
	public Response getLayers() {

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		final Response resp = target.path(
				"geoserver/rest/layers.json").request().get();

		if (resp.getStatus() == Status.OK.getStatusCode()) {

			resp.bufferEntity();

			final Pattern p = Pattern.compile("name\".\"([^\"]+)");
			final Matcher m = p.matcher(resp.readEntity(String.class));

			final ArrayList<String> layerNames = new ArrayList<String>();
			while (m.find()) {
				layerNames.add(m.group(1));
			}

			return Response.ok(
					layerNames.toArray(new String[layerNames.size()])).build();
		}

		return resp;
	}

	@Override
	public Response getLayer(
			@PathParam("layerName") final String layerName ) {

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		final Response resp = target.path(
				"geoserver/rest/layers/" + layerName + ".json").request().get();

		if (resp.getStatus() == Status.OK.getStatusCode()) {
			final InputStream inStream = (InputStream) resp.getEntity();

			return Response.ok(
					inStream).header(
					"Content-Disposition",
					"attachment; filename=\"" + layerName + "\"").build();
		}

		return resp;
	}

	@Override
	public Response publishLayer(
			final FormDataMultiPart multiPart ) {

		final String datastore = multiPart.getField(
				"datastore").getValue();

		final String defaultStyle = multiPart.getField(
				"defaultStyle").getValue();

		final String customWorkspace = (multiPart.getField("workspace") != null) ? multiPart.getField(
				"workspace").getValue() : defaultWorkspace;

		String jsonString;
		try {
			jsonString = IOUtils.toString(multiPart.getField(
					"featureType").getValueAs(
					InputStream.class));
		}
		catch (final IOException e) {
			throw new WebApplicationException(
					Response.status(
							Status.BAD_REQUEST).entity(
							"Layer Creation Failed - Unable to parse featureType").build());
		}

		final String layerName = JSONObject.fromObject(
				jsonString).getJSONObject(
				"featureType").getString(
				"name");

		final Client client = ClientBuilder.newClient().register(
				HttpAuthenticationFeature.basic(
						geoserverUser,
						geoserverPass));
		final WebTarget target = client.target(geoserverUrl);

		// upload the style to geoserver
		Response resp = target.path(
				"geoserver/rest/workspaces/" + customWorkspace + "/datastores/" + datastore + "/featuretypes").request().post(
				Entity.entity(
						jsonString,
						MediaType.APPLICATION_JSON));

		if (resp.getStatus() != Status.CREATED.getStatusCode()) {
			return resp;
		}

		resp = target.path(
				"geoserver/rest/layers/" + layerName).request().put(
				Entity.entity(
						"{'layer':{'defaultStyle':{'name':'" + defaultStyle + "'}'}}",
						MediaType.APPLICATION_JSON));

		return Response.ok().build();
	}
}