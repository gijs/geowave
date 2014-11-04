package mil.nga.giat.geowave.services.rest.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mil.nga.giat.geowave.services.rest.FileUpload;
import mil.nga.giat.geowave.services.rest.ingest.ClearNamespace;
import mil.nga.giat.geowave.services.rest.ingest.HdfsIngest;
import mil.nga.giat.geowave.services.rest.ingest.LocalIngest;
import mil.nga.giat.geowave.services.rest.ingest.PostStage;
import mil.nga.giat.geowave.services.rest.ingest.StageToHdfs;

import org.apache.commons.cli.ParseException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class FileUploadImpl implements FileUpload{

	private final static Logger LOGGER = Logger.getLogger(FileUploadImpl.class);	
	private final static String TMP_DIR = "geowave-upload-files";
	private final static String STYLE_NAME = "styleName";
	private final static String NAMESPACE = "namespace";
	
	private String styleName;
	private File file;
	private String namespace;

	@Override
	public Response stageToHdfs(@Context HttpServletRequest request) {
		Response response = null;
		try {
			upload(request);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
		} 
		finally {
			if (file != null)
				response = stageToHdfs(file.getAbsolutePath());
		}
		
		if (response == null)
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error occurred.").build();
		return response;

	}
	
	@Override
	public Response stageToHdfs(@FormParam("basePath")String basePath) {
		Response response = null;
		try {
			new StageToHdfs().run(basePath);
			response = Response.status(Status.OK).entity("Data staged to HDFS.").build();
		}
		catch (IOException | ParseException e) {
			LOGGER.error(e.getMessage());			
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return response;
	}
		
	@Override
	public Response hdfsIngest(@Context HttpServletRequest request) {
		Response response = null;
		try {
			upload(request);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
		} 
		finally {
			if (file != null)
				response = hdfsIngest(file.getAbsolutePath(), namespace);
		}
		
		if (response == null)
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error occurred.").build();
		return response;
	}
	
	@Override
	public Response hdfsIngest(@FormParam("basePath")String basePath, @FormParam("namespace")String namespace) {
		Response response = null;
		try {
			new HdfsIngest().run(basePath, namespace);
			response = Response.status(Status.OK).entity("Data ingested to Geowave.").build();
		}
		catch (IOException | ParseException e) {
			LOGGER.error(e.getMessage());
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return response;
	}
		
	@Override
	public Response localIngest(@Context HttpServletRequest request) {
		Response response = null;
		try {
			upload(request);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
		} 
		finally {
			if (file != null && namespace != null)
				response = localIngest(file.getAbsolutePath(), namespace);
		}
		
		if (response == null)
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error occurred.").build();
		return response;
	}
	
	@Override
	public Response localIngest(@FormParam("basePath")String basePath, @FormParam("namespace")String namespace) {
		Response response = null;
		try {
			new LocalIngest().run(basePath, namespace);
			response = Response.status(Status.OK).entity("Data ingested to Geowave.").build();
		}
		catch (IOException | ParseException e) {
			LOGGER.error(e.getMessage());
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return response;
	}
		
	@Override
	public Response clearNamespace(@FormParam("namespace")String namespace) {
		Response response = null;
		try {
			if (namespace != null && namespace.trim().length() > 0) {
				new ClearNamespace().run(namespace);
				response = Response.status(Status.OK).entity("Cleared ALL data from Geowave namespace: " + namespace + ".").build();
			}
			else
				response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Namespace is empty.").build();
		}
		catch (IOException | ParseException e) {
			LOGGER.error(e.getMessage());
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return response;
	}
		
	@Override
	public Response postStage(@FormParam("namespace")String namespace) {
		Response response = null;
		try {
			new PostStage().run(namespace);
			response = Response.status(Status.OK).entity("Ingested files into Geowave.").build();
		}
		catch (IOException | ParseException e) {
			LOGGER.error(e.getMessage());
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return response;
	}
		
	@Override
	public Response uploadStyle(@Context HttpServletRequest request) {
		Response response = null;
		try {
			upload(request);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
		} 
		finally {
			if (styleName != null && file != null) {
				//Services.publishStyle(styleName, file);
				response = Response.status(Status.CREATED).entity("Style published.").build();
			}

			try {
					Files.delete(file.toPath());
			} catch (IOException e) {
				LOGGER.warn(e);
			}
		}

		if (response == null)
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error occurred.").build();
		return response;
	}
	
	@Override
	public Response updateStyle(@Context HttpServletRequest request) {
		Response response = null;
		try {
			upload(request);
		}
		catch (Exception e) {
			LOGGER.error(e);
		}
		finally {
			if (styleName != null && file != null) {
					//Services.updateStyle(styleName, file);
					response = Response.status(Status.CREATED).entity("Style updated.").build();
			}

			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				LOGGER.warn(e);
			}
		}

		if (response == null)
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error occurred.").build();
		return response;
	}
	
	private void upload(HttpServletRequest request) throws Exception {
		// create temporary directory
		java.nio.file.Path tempDir = Files.createTempDirectory(TMP_DIR);

		//checks whether there is a file upload request or not
		if (ServletFileUpload.isMultipartContent(request)) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload fileUpload = new ServletFileUpload(factory);

			for(Object obj : fileUpload.parseRequest(request)) {
				if (obj instanceof FileItem) {
					FileItem item = (FileItem)obj;

					// check if it represents an uploaded file
					if (!item.isFormField()) {
						File file = new File(tempDir + File.separator + item.getName());
						item.write(file);
						this.file = file;
					}
					else {
						if (STYLE_NAME.equals(item.getFieldName()))
							styleName = item.getString();
						else if (NAMESPACE.equals(item.getFieldName()))
							namespace = item.getString();
					}
				}
			}
		}
	}
}
