package mil.nga.giat.geowave.services.rest;

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

@Path("/files")
public interface FileUpload
{

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/plain")
	@Path("/upload/stageToHdfs")
	public Response stageToHdfs(
			@Context HttpServletRequest request );

	@POST
	@Produces("text/plain")
	@Path("/stageToHdfs")
	public Response stageToHdfs(
			@FormParam("basePath") String basePath );

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/plain")
	@Path("/upload/hdfsIngest")
	public Response hdfsIngest(
			@Context HttpServletRequest request );

	@POST
	@Produces("text/plain")
	@Path("/hdfsIngest")
	public Response hdfsIngest(
			@FormParam("basePath") String basePath,
			@FormParam("namespace") String namespace );

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/plain")
	@Path("/upload/localIngest")
	public Response localIngest(
			@Context HttpServletRequest request );

	@POST
	@Produces("text/plain")
	@Path("/localIngest")
	public Response localIngest(
			@FormParam("basePath") String basePath,
			@FormParam("namespace") String namespace );

	@POST
	@Produces("text/plain")
	@Path("/clearNamespace")
	public Response clearNamespace(
			@FormParam("namespace") String namespace );

	@POST
	@Produces("text/plain")
	@Path("/postStage")
	public Response postStage(
			@FormParam("namespace") String namespace );

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/plain")
	@Path("/upload/loadStyle")
	public Response uploadStyle(
			@Context HttpServletRequest request );

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/plain")
	@Path("/upload/updateStyle")
	public Response updateStyle(
			@Context HttpServletRequest request );
}
