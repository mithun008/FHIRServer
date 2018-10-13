package com.amazonaws.lab.resources;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.CapabilityStatement;

import com.amazonaws.lab.LambdaHandler;

import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;

@Path("/_meta")

@io.swagger.annotations.Api(description = "the Patient API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-07-17T16:45:16.134-07:00")

public class MetaDataResource {
	
	static final Logger log = LogManager.getLogger(MetaDataResource.class);
	
	@GET

	@Produces({ "application/json+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Get Capability statement", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })

	public Response gETCapabilityStatement(@Context HttpServletRequest request, @Context ServletContext serContext)
			throws ServletException {
		log.debug("Entering get capability statement..");
		RestfulServer server = new RestfulServer();
		List<Object> plainProviders = new ArrayList<Object>();
		plainProviders.add(new CapabilityStatementResource());
		// resourceProviders.add(new ObservationResource());
		// resourceProviders.add(new BundleResource());
		server.setPlainProviders(plainProviders);
		server.setImplementationDescription("AWS Serverless FHIR Server");
		server.setServerName("AWS Server");
		server.setFhirContext(LambdaHandler.getFHIRContext());

		String serverBaseUrl = "http://foo.com/fhir";
		server.setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBaseUrl));
		ConformanceProvider conf = new ConformanceProvider();

		server.setServerConformanceProvider(conf);

		server.init();

		CapabilityStatement cap = conf.getServerConformance(request);

		// server.setFhirContext(LambdaHandler.getFHIRContext());
		// ServerCapabilityStatementProvider provider=
		// (ServerCapabilityStatementProvider)server.getServerConformanceProvider();
		// provider.initializeOperations();
		String capStmt = LambdaHandler.getFHIRContext().newJsonParser().encodeResourceToString(cap);
		log.trace("The statement is : " + capStmt );
		log.debug("The desc is " + cap.getImplementation().getDescription());


		return Response.status(Response.Status.OK).entity(capStmt).build();
	}

}
