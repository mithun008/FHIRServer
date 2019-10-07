package com.amazonaws.lab.resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Enumerations.ResourceType;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;

import com.amazonaws.lab.LambdaHandler;
import com.amazonaws.serverless.proxy.internal.jaxrs.AwsProxySecurityContext.CognitoUserPoolPrincipal;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;

@Path("/Bundle")

@io.swagger.annotations.Api(description = "the Bundle API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-07-17T16:45:16.134-07:00")
public class BundleResource implements IResourceProvider {
	@Context
	SecurityContext securityContext;



	static final Logger log = LogManager.getLogger(BundleResource.class);

	
	private static final String FHIR_META_TABLE = System.getenv("FHIR_RESOURCE_META_TABLE");
	private static final String FHIR_INSTANCE_BUCKET = System.getenv("FHIR_INSTANCE_BUCKET");
	
	private static final String BUNDLE_TABLE = System.getenv("FHIR_BUNDLE_TABLE");

	private static final String VALIDATE_FHIR_RESOURCE = System.getenv("VALIDATE_FHIR_RESOURCE");
	private static final String COGNITO_ENABLED = System.getenv("COGNITO_ENABLED");

	@POST
	@Consumes({ "application/fhir+json", "application/xml+fhir" })
	@Produces({ "application/fhir+json", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Create a new type ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 201, message = "Succesfully created a new type ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request - Resource cound not be parsed or failed basic FHIR validation rules ", response = Void.class),

			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found - resource type not support, or not a FHIR validation rules ", response = Void.class) })

	public Response pOSTBundle(@Context SecurityContext securityContext, String bundleBlob) {
		String userId = null;
		OperationOutcome opOutCome = null;
		
		if(COGNITO_ENABLED.equals("true")) {
			CognitoUserPoolPrincipal cognitoPrin = 
					securityContext.getUserPrincipal()!=null?(CognitoUserPoolPrincipal)securityContext.getUserPrincipal():null;
			userId = 
					cognitoPrin!=null?cognitoPrin.getClaims().getUsername():null;
		}
		
		log.debug("Before Validation started .."+userId);
		//ValidationResult result = FhirContext.forDstu3().newValidator().validateWithResult(patientBlob);
		if(VALIDATE_FHIR_RESOURCE.equals("true")) {
			ValidationResult result = LambdaHandler.getFHIRValidator().validateWithResult(bundleBlob);
			if (result.getMessages().size() > 0) {
				log.debug("Validation failed ..");
				// The result object now contains the validation results
				for (SingleValidationMessage next : result.getMessages()) {
					log.debug("Validation message : " + next.getLocationString() + " " + next.getMessage());
				}
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}
		Bundle bundle = LambdaHandler.getFHIRContext().newJsonParser().parseResource(Bundle.class, bundleBlob);
		
		String id = this.createBundle(bundle,userId!=null?userId:"Unknown");
		
		List<BundleEntryComponent> list = bundle.getEntry();
		
		String patientId = null;
		//Patient patient = null;
		String patientFullUrl = null;
		for(BundleEntryComponent entry : list) {
			
			String fhirType = entry.getResource().fhirType();
			//System.out.println(entry.getResource().fhirType());
			
			if(fhirType.equals(ResourceType.PATIENT.getDisplay())) {
				Patient patient = (Patient)entry.getResource();
				patientFullUrl = entry.getFullUrl();
				log.debug("The patient name "+patient.getName());
				PatientResource patResource = new PatientResource();
				patientId = patResource.createPatient(patient,userId!=null?userId:"Unknown");
				
			}else if(fhirType.equals(ResourceType.OBSERVATION.getDisplay())){
				Observation obs = (Observation)entry.getResource();
				log.trace("The observation resource :"+obs.fhirType());
				Reference ref = new Reference(patientFullUrl);
				//assuming the patient comes first so patient would be created
				ref.setId(UUID.randomUUID().toString());
				//ref.setReference(patientId);
				
				obs.setSubject(ref);
				log.trace("The patient reference id "+obs.getSubject().getReference());
				ObservationResource obsResource = new ObservationResource();
				obsResource.createObservation(obs,userId!=null?userId:"Unknown");
			}else if(fhirType.equals(ResourceType.CONDITION.getDisplay())){
				Condition cond = (Condition)entry.getResource();
				log.trace("The observation resource :"+cond.fhirType());
				Reference ref = new Reference(patientFullUrl);
				//assuming the patient comes first so patient would be created
				ref.setId(UUID.randomUUID().toString());
				//ref.setReference(patientId);
				
				cond.setSubject(ref);
				log.trace("The patient reference id "+cond.getSubject().getReference());
				ConditionResource condResource = new ConditionResource();
				condResource.createCondition(cond,userId!=null?userId:"Unknown");
			}
			
		}

		//load attachment to S3
		
		AmazonS3 s3Client = LambdaHandler.getS3Client();
		String s3Key = id+"_V1";
		s3Client.putObject(FHIR_INSTANCE_BUCKET,s3Key, bundleBlob);
		
		//load meta info to Dyanamo DB
		
		HashMap<String, AttributeValue> attValues = new HashMap<String,AttributeValue>();
		attValues.put("ResourceType", new AttributeValue("Bundle"));
		attValues.put("id",new AttributeValue(id));
		attValues.put("BucketName",new AttributeValue(FHIR_INSTANCE_BUCKET));
		attValues.put("Key",new AttributeValue(s3Key));
		
		//metaTable.putItem(FHIR_META_TABLE,)
		AmazonDynamoDB ddbClient = LambdaHandler.getDDBClient();
		ddbClient.putItem(FHIR_META_TABLE, attValues);
		
		opOutCome = new OperationOutcome();
		opOutCome.setId(new IdType("Patient", id, "1"));
		//opOutCome.fhirType();
		Narrative narrative = new Narrative();
		narrative.setStatus(NarrativeStatus.GENERATED);
		narrative.setDivAsString("<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><h1>Operation Outcome</h1>"
				+ "<table border=\\\"0\\\"><tr><td style=\\\"font-weight: bold;\\\">INFORMATION</td><td>[]</td>"
				+ "<td><pre>Successfully created resource \\\"Patient/"+id+"/_history/1\\\" in 36ms</pre>"
				+ "</td>\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t</tr>\\n\\t\\t\\t<tr>\\n\\t\\t\\t\\t<td style=\\\"font-weight: bold;\\\">INFORMATION</td>"
				+ "\\n\\t\\t\\t\\t<td>[]</td>\\n\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\t\\t<td>"
				+ "<pre>No issues detected during validation</pre></td>\\n\\t\\t\\t\\t\\t\\n\\t\\t\\t\\t\\n\\t\\t\\t</tr>\\n\\t\\t</table>\\n\\t</div>");
		opOutCome.setText(narrative);
		ArrayList<OperationOutcomeIssueComponent> outcomelist = new ArrayList<OperationOutcomeIssueComponent>();
		
		OperationOutcomeIssueComponent issue = new OperationOutcomeIssueComponent();
		issue.setSeverity(IssueSeverity.INFORMATION);
		issue.setCode(IssueType.INFORMATIONAL);
		issue.setDiagnostics("Successfully created resource Bundle/"+id+"/_history/1");
		outcomelist.add(issue);
		
		issue = new OperationOutcomeIssueComponent();
		issue.setSeverity(IssueSeverity.INFORMATION);
		issue.setCode(IssueType.INFORMATIONAL);
		issue.setDiagnostics("No issues detected during validation");
		outcomelist.add(issue);
		opOutCome.addContained(bundle);
		
		opOutCome.setIssue(outcomelist);
		// return Response.status(201).entity(newOrder).build();

		log.debug("End of function...");
		System.out.println("End of function from system out....");

		return Response.status(Response.Status.CREATED).entity(LambdaHandler
				.getFHIRContext().newJsonParser()
				.encodeResourceToString(opOutCome)).build();
	}


	
	/**
	 * method to create Patient record using a Patient object
	 * @param pat
	 * @return
	 */
	
	public String createBundle(Bundle bundle,String userId) {
		log.debug("Executing bundle create.. ");
		log.debug("The security context object.." + securityContext);

		// Ask the context for a validator

		String id = bundle.getId();
		if(id == null) {
			id = UUID.randomUUID().toString();
			bundle.setId(id);
		}
		
		// log.debug("Executing dynamo db..");
		log.debug("Execute Dynamo DB with id" +id);
		// ddbMapper.save(patient, new DynamoDBMapperConfig(new
		// DynamoDBMapperConfig.TableNameOverride(PATIENT_TABLE)));
		DynamoDB dynamodb = new DynamoDB(LambdaHandler.getDDBClient());
		Table myTable = dynamodb.getTable(BUNDLE_TABLE);

		// Make sure your object includes the hash or hash/range key
		String myJsonString = LambdaHandler.getFHIRContext().newJsonParser().encodeResourceToString(bundle);
		

		// Convert the JSON into an object
		Item myItem = Item.fromJSON(myJsonString);

		//add user id
		myItem.withString("userid", userId);
		// Insert the Object
		myTable.putItem(myItem);
		
		return id;
	}


	
	public void testBundle()throws IOException {
		
		String bundleBlob = this.getFile("Doretha289_Bayer639_4480d762-f8c4-4691-bbe9-3dabe66496eb.json");
				
		Bundle bundle = LambdaHandler.getFHIRContext().newJsonParser().parseResource(Bundle.class, bundleBlob);
		
		List<BundleEntryComponent> list = bundle.getEntry();
		
		for(BundleEntryComponent entry : list) {
			String fhirType = entry.getResource().fhirType();
			//System.out.println(entry.getResource().fhirType());
			
			if(fhirType.equals(ResourceType.PATIENT.getDisplay())) {
				Patient pat = (Patient)entry.getResource();
				log.debug("The patient name "+pat.getName());
				List<HumanName> namelist = pat.getName();
				for(HumanName name : namelist) {
					log.debug("The patient name : "+ name.getGivenAsSingleString());
				}
				
			}	
		}
		
	}
	
	private String getFile(String fileName) {

		StringBuilder result = new StringBuilder("");

		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return result.toString();

	}
	
    public Class<Bundle> getResourceType() {
        return Bundle.class;
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
