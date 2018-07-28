package com.amazonaws.lab.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Patient;


import com.amazonaws.lab.LambdaHandler;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import io.swagger.annotations.ApiParam;

@Path("/Patient")

@io.swagger.annotations.Api(description = "the Patient API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-07-17T16:45:16.134-07:00")
public class PatientResource {
	@Context
	SecurityContext securityContext;

	private FhirContext fhirContext;

	private final PatientApiService delegate;

	static final Logger log = LogManager.getLogger(PatientResource.class);

	private static final String PATIENT_TABLE = System.getenv("PATIENT_TABLE");

	public PatientResource() {
		// Map<String, AttributeValue> scanExpression = new HashMap();
		// scanExpression.put(":user", new
		// AttributeValue().withS(securityContext.getUserPrincipal().getName()));

		PatientApiService delegate = null;
		/**
		 * The original implementation relied on using reflection to load the delegation
		 * class on run time. Avoiding the reflection approach for a efficient lambda
		 * implementation.
		 */

		delegate = new PatientApiServiceImpl();

		this.delegate = delegate;
		this.fhirContext = FhirContext.forDstu3();
	}

	@DELETE
	@Path("/{id}")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Delete resource ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 204, message = "Succesfully deleted resource ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found - resource was not found ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "Method Not allowed - delete is not allowed ", response = Void.class) })
	public Response dELETEPatientid(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.dELETEPatientid(id, securityContext);
	}

	@GET

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Get Patient", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatient(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatient(securityContext);
	}

	@GET
	@Path("/_history")

	@Produces({ "application/atom+xml", "application/json+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatientHistory(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientHistory(securityContext);
	}

	@GET
	@Path("/_search")

	@Produces({ "application/atom+xml", "application/json+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatientSearch(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientSearch(securityContext);
	}

	@GET
	@Path("/_tags")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "get a list of tags used for the nominated resource type ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Succesfully retrieved resource ", response = Void.class) })
	public Response gETPatientTags(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientTags(securityContext);
	}

	@GET
	@Path("/{id}")
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Tried to get an unknown resource ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 410, message = "Tried to get a deleted resource ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 422, message = "Unprocessable Entity - the proposed resource violated applicable FHIR  profiles or server business rules.  This should be accompanied by an OperationOutcome resource providing additional detail. ", response = Void.class) })

	public Response gETPatientid(@Context SecurityContext securityContext,
			@ApiParam(value = "", required = true) @PathParam("id") String id, @HeaderParam("Accept") String accepted)
			throws NotFoundException {

		// System.out.println("Method call invoked..");
		log.debug("Method call invoked..");

		MediaType mediaType;
		if (accepted != null) {
			mediaType = MediaType.valueOf(accepted);
			log.debug("The incoming media type + sub type is ----- " + mediaType.getType() + "-"
					+ mediaType.getSubtype());
		} else {
			log.debug("The incoming media type is null.");
		}
		log.debug("The id received : " + id);

		AmazonDynamoDB client = LambdaHandler.getDDBClient();
		DynamoDB db = new DynamoDB(client);
		Table table = db.getTable(PATIENT_TABLE);

		Item item = table.getItem("id", id);
		String json = item.toJSON();

		
		log.debug("The json string retrieved : " + json);

		return Response.status(200).entity(json).build();

	}

	@GET
	@Path("/{id}/_history")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatientidHistory(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientidHistory(id, securityContext);
	}

	@GET
	@Path("/{id}/_history/{vid}")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatientidHistoryvid(@ApiParam(value = "", required = true) @PathParam("vid") String vid,
			@ApiParam(value = "", required = true) @PathParam("id") String id, @Context SecurityContext securityContext)
			throws NotFoundException {
		return delegate.gETPatientidHistoryvid(vid, id, securityContext);
	}

	@GET
	@Path("/{id}/_history/{vid}/_tags")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "get a list of tags used for the nominated version of the resource.  This duplicates the HTTP header entries. ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Succesfully retrieved resource ", response = Void.class) })
	public Response gETPatientidHistoryvidTags(@ApiParam(value = "", required = true) @PathParam("vid") String vid,
			@ApiParam(value = "", required = true) @PathParam("id") String id, @Context SecurityContext securityContext)
			throws NotFoundException {
		return delegate.gETPatientidHistoryvidTags(vid, id, securityContext);
	}

	@GET
	@Path("/{id}/_tags")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "get a list of tags used for the nominated resource. This duplicates the HTTP header entries ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Succesfully retrieved resource ", response = Void.class) })
	public Response gETPatientidTags(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientidTags(id, securityContext);
	}

	@GET
	@Path("/{id}/$everything")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatientideverything(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientideverything(id, securityContext);
	}

	@GET
	@Path("/{id}/$validate")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatientidvalidate(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientidvalidate(id, securityContext);
	}

	@GET
	@Path("/$meta")

	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Successfully retrieved resource(s) ", response = Void.class) })
	public Response gETPatientmeta(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientmeta(securityContext);
	}

	@GET
	@Path("/$validate")

	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETPatientvalidate(@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.gETPatientvalidate(securityContext);
	}

	@POST

	@Consumes({ "application/json+fhir", "application/xml+fhir" })
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Create a new type ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 201, message = "Succesfully created a new type ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request - Resource cound not be parsed or failed basic FHIR validation rules ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found - resource type not support, or not a FHIR validation rules ", response = Void.class) })

	public Response pOSTPatient(@Context SecurityContext securityContext, String patientBlob) throws NotFoundException {
		try {
			log.debug("Before Validation started ..");
			ValidationResult result = FhirContext.forDstu3().newValidator().validateWithResult(patientBlob);
			Patient patient = FhirContext.forDstu3().newJsonParser().parseResource(Patient.class, patientBlob);
			// Patient patient = new Patient();
			log.debug("Executing patient create.. ");
			log.debug("The security context object.." + securityContext);

			if (patient != null) {
				log.debug("The patient object received .." + patient.getName());
			} else {
				log.debug("Patient object is null..");
			}

			// Ask the context for a validator

			log.debug("After Validation  ..");
			if (result.getMessages().size() > 0) {
				log.debug("Validation failed ..");
				// The result object now contains the validation results
				for (SingleValidationMessage next : result.getMessages()) {
					log.debug("Validation message : " + next.getLocationString() + " " + next.getMessage());
				}
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			//JsonObject jsonObject = new JsonParser().parse(patientBlob).getAsJsonObject();

			//System.out.println(jsonObject.get("id").getAsString()); // John

			// log.debug("Executing dynamo db..");
			log.debug("Execute Dynamo DB");
			// ddbMapper.save(patient, new DynamoDBMapperConfig(new
			// DynamoDBMapperConfig.TableNameOverride(PATIENT_TABLE)));
			DynamoDB dynamodb = new DynamoDB(LambdaHandler.getDDBClient());
			Table myTable = dynamodb.getTable(PATIENT_TABLE);

			// Make sure your object includes the hash or hash/range key
			String myJsonString = patientBlob;

			// Convert the JSON into an object
			Item myItem = Item.fromJSON(myJsonString);

			// Insert the Object
			myTable.putItem(myItem);

			// return Response.status(201).entity(newOrder).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Failed  to create a new type").build();
		}
		log.debug("End of function...");
		System.out.println("End of function from system out....");
		return Response.status(Response.Status.CREATED).entity("Succesfully created a new type").build();
	}

	@POST
	@Path("/_validate/{id}")
	@Consumes({ "application/json+fhir", "application/xml+fhir" })
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Create a new resource ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 201, message = "Validates a type ", response = Void.class) })
	public Response pOSTPatientValidateid(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.pOSTPatientValidateid(id, securityContext);
	}

	@POST
	@Path("/{id}/_history/{vid}/_tags")
	@Consumes({ "application/json+fhir", "application/xml+fhir" })
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Affix tags in the list to the nominated verion of the resource ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 201, message = "Succesfully affix tags ", response = Void.class) })
	public Response pOSTPatientidHistoryvidTags(@ApiParam(value = "", required = true) @PathParam("vid") String vid,
			@ApiParam(value = "", required = true) @PathParam("id") String id, @Context SecurityContext securityContext)
			throws NotFoundException {
		return delegate.pOSTPatientidHistoryvidTags(vid, id, securityContext);
	}

	@POST
	@Path("/{id}/_history/{vid}/_tags/_delete")
	@Consumes({ "application/json+fhir", "application/xml+fhir" })
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Removes all tags in the provided list of tags for the nominated version of the resource ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 204, message = "Successful deletion of tags", response = Void.class) })
	public Response pOSTPatientidHistoryvidTagsDelete(
			@ApiParam(value = "", required = true) @PathParam("vid") String vid,
			@ApiParam(value = "", required = true) @PathParam("id") String id, @Context SecurityContext securityContext)
			throws NotFoundException {
		return delegate.pOSTPatientidHistoryvidTagsDelete(vid, id, securityContext);
	}

	@POST
	@Path("/{id}/_tags")
	@Consumes({ "application/json" })
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Affix tags in the list to the nominated resource ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 201, message = "Succesfully affix tags ", response = Void.class) })
	public Response pOSTPatientidTags(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.pOSTPatientidTags(id, securityContext);
	}

	@POST
	@Path("/{id}/_tags/_delete")
	@Consumes({ "application/json+fhir", "application/xml+fhir" })
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Removes all tags in the provided list of tags for the nominated resource ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 204, message = "Status 204", response = Void.class) })
	public Response pOSTPatientidTagsDelete(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.pOSTPatientidTagsDelete(id, securityContext);
	}

	@PUT
	@Path("/{id}")
	@Consumes({ "application/json+fhir", "application/xml+fhir" })
	@Produces({ "application/json+fhir", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Update an existing instance ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Succesfully updated the instance  ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 201, message = "Succesfully created the instance  ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request - Resource cound not be parsed or failed basic FHIR validation rules ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found - resource type not support, or not a FHIR validation rules ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 405, message = "Method Not allowed - the resource did not exist prior to the update, and the server does not allow client defined ids ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 409, message = "Version conflict management ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 412, message = "Version conflict management ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 422, message = "Unprocessable Entity - the proposed resource violated applicable FHIR  profiles or server business rules.  This should be accompanied by an OperationOutcome resource providing additional detail. ", response = Void.class) })
	public Response pUTPatientid(@ApiParam(value = "", required = true) @PathParam("id") String id,
			@Context SecurityContext securityContext) throws NotFoundException {
		return delegate.pUTPatientid(id, securityContext);
	}

	public static void main(String[] args) {
		AmazonDynamoDB client = LambdaHandler.getDDBClient();
		DynamoDB db = new DynamoDB(client);
		Table table = db.getTable("FHIRPatient");

		Item item = table.getItem("id", "123456789");

		String json = item.toJSON();
		log.debug("The json " + json);

		String patientBlob = "{\n" + "  \"resourceType\": \"Patient\",\n" + "  \"id\": \"example\",\n"
				+ "  \"text\": {\n" + "    \"status\": \"generated\",\n"
				+ "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n\\t\\t\\t<table>\\n\\t\\t\\t\\t<tbody>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Name</td>\\n\\t\\t\\t\\t\\t\\t<td>Peter James \\n              <b>Chalmers</b> (&quot;Jim&quot;)\\n            </td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Address</td>\\n\\t\\t\\t\\t\\t\\t<td>534 Erewhon, Pleasantville, Vic, 3999</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Contacts</td>\\n\\t\\t\\t\\t\\t\\t<td>Home: unknown. Work: (03) 5555 6473</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t\\t<tr>\\n\\t\\t\\t\\t\\t\\t<td>Id</td>\\n\\t\\t\\t\\t\\t\\t<td>MRN: 12345 (Acme Healthcare)</td>\\n\\t\\t\\t\\t\\t</tr>\\n\\t\\t\\t\\t</tbody>\\n\\t\\t\\t</table>\\n\\t\\t</div>\"\n"
				+ "  },\n" + "  \"identifier\": [\n" + "    {\n" + "      \"use\": \"usual\",\n" + "      \"type\": {\n"
				+ "        \"coding\": [\n" + "          {\n"
				+ "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" + "            \"code\": \"MR\"\n"
				+ "          }\n" + "        ]\n" + "      },\n"
				+ "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" + "      \"value\": \"12345\",\n"
				+ "      \"period\": {\n" + "        \"start\": \"2001-05-06\"\n" + "      },\n"
				+ "      \"assigner\": {\n" + "        \"display\": \"Acme Healthcare\"\n" + "      }\n" + "    }\n"
				+ "  ],\n" + "  \"active\": true,\n" + "  \"active\": false,\n" + "  \"name\": [\n" + "    {\n"
				+ "      \"use\": \"official\",\n" + "      \"family\": \"Chalmers\",\n" + "      \"given\": [\n"
				+ "        \"Peter\",\n" + "        \"James\"\n" + "      ]\n" + "    },\n" + "    {\n"
				+ "      \"use\": \"usual\",\n" + "      \"given\": [\n" + "        \"Jim\"\n" + "      ]\n"
				+ "    },\n" + "    {\n" + "      \"use\": \"maiden\",\n" + "      \"family\": \"Windsor\",\n"
				+ "      \"given\": [\n" + "        \"Peter\",\n" + "        \"James\"\n" + "      ],\n"
				+ "      \"period\": {\n" + "        \"end\": \"2002\"\n" + "      }\n" + "    }\n" + "  ],\n"
				+ "  \"telecom\": [\n" + "    {\n" + "      \"use\": \"home\"\n" + "    },\n" + "    {\n"
				+ "      \"system\": \"phone\",\n" + "      \"value\": \"(03) 5555 6473\",\n"
				+ "      \"use\": \"work\",\n" + "      \"rank\": 1\n" + "    },\n" + "    {\n"
				+ "      \"system\": \"phone\",\n" + "      \"value\": \"(03) 3410 5613\",\n"
				+ "      \"use\": \"mobile\",\n" + "      \"rank\": 2\n" + "    },\n" + "    {\n"
				+ "      \"system\": \"phone\",\n" + "      \"value\": \"(03) 5555 8834\",\n"
				+ "      \"use\": \"old\",\n" + "      \"period\": {\n" + "        \"end\": \"2014\"\n" + "      }\n"
				+ "    }\n" + "  ],\n" + "  \"gender\": \"male\",\n" + "  \"birthDate\": \"1974-12-25\",\n"
				+ "  \"_birthDate\": {\n" + "    \"extension\": [\n" + "      {\n"
				+ "        \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-birthTime\",\n"
				+ "        \"valueDateTime\": \"1974-12-25T14:35:45-05:00\"\n" + "      }\n" + "    ]\n" + "  },\n"
				+ "  \"deceasedBoolean\": false,\n" + "  \"address\": [\n" + "    {\n" + "      \"use\": \"home\",\n"
				+ "      \"type\": \"both\",\n"
				+ "      \"text\": \"534 Erewhon St PeasantVille, Rainbow, Vic  3999\",\n" + "      \"line\": [\n"
				+ "        \"534 Erewhon St\"\n" + "      ],\n" + "      \"city\": \"PleasantVille\",\n"
				+ "      \"district\": \"Rainbow\",\n" + "      \"state\": \"Vic\",\n"
				+ "      \"postalCode\": \"3999\",\n" + "      \"period\": {\n" + "        \"start\": \"1974-12-25\"\n"
				+ "      }\n" + "    }\n" + "  ],\n" + "  \"contact\": [\n" + "    {\n" + "      \"relationship\": [\n"
				+ "        {\n" + "          \"coding\": [\n" + "            {\n"
				+ "              \"system\": \"http://hl7.org/fhir/v2/0131\",\n" + "              \"code\": \"N\"\n"
				+ "            }\n" + "          ]\n" + "        }\n" + "      ],\n" + "      \"name\": {\n"
				+ "        \"family\": \"du Marché\",\n" + "        \"_family\": {\n" + "          \"extension\": [\n"
				+ "            {\n"
				+ "              \"url\": \"http://hl7.org/fhir/StructureDefinition/humanname-own-prefix\",\n"
				+ "              \"valueString\": \"VV\"\n" + "            }\n" + "          ]\n" + "        },\n"
				+ "        \"given\": [\n" + "          \"Bénédicte\"\n" + "        ]\n" + "      },\n"
				+ "      \"telecom\": [\n" + "        {\n" + "          \"system\": \"phone\",\n"
				+ "          \"value\": \"+33 (237) 998327\"\n" + "        }\n" + "      ],\n"
				+ "      \"address\": {\n" + "        \"use\": \"home\",\n" + "        \"type\": \"both\",\n"
				+ "        \"line\": [\n" + "          \"534 Erewhon St\"\n" + "        ],\n"
				+ "        \"city\": \"PleasantVille\",\n" + "        \"district\": \"Rainbow\",\n"
				+ "        \"state\": \"Vic\",\n" + "        \"postalCode\": \"3999\",\n" + "        \"period\": {\n"
				+ "          \"start\": \"1974-12-25\"\n" + "        }\n" + "      },\n"
				+ "      \"gender\": \"female\",\n" + "      \"period\": {\n" + "        \"start\": \"2012\"\n"
				+ "      }\n" + "    }\n" + "  ],\n" + "  \"managingOrganization\": {\n"
				+ "    \"reference\": \"Organization/1\"\n" + "  }\n" + "}";
		Patient patient = FhirContext.forDstu3().newJsonParser().parseResource(Patient.class, patientBlob);
		// Patient patient = new Patient();
		System.out.println("Executing patient create.. ");
		// System.out.println("The security context object.."+securityContext);

		if (patient != null) {
			System.out.println("The patient object received .." + patient.getName());
		} else {
			System.out.println("Patient object is null..");
		}
		System.out.println("Before Validation started ..");
		// Ask the context for a validator

		// Ask the context for a validator
		FhirContext ctx = FhirContext.forDstu3();
		FhirValidator validator = ctx.newValidator();
		ctx.setParserErrorHandler(new StrictErrorHandler());
		// validator.validateWithResult(arg0)(patient);

		ValidationResult result = validator.validateWithResult(patient);
		if (result.getMessages().size() > 0) {
			System.out.println("Validation failed ..");
			// The result object now contains the validation results
			for (SingleValidationMessage next : result.getMessages()) {
				System.out.println("Validation message : " + next.getLocationString() + " " + next.getMessage());
			}
			// return Response.status(Response.Status.BAD_REQUEST).build();
		}

	}
}
