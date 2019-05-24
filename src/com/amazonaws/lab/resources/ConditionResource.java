package com.amazonaws.lab.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleLinkComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Condition;

import com.amazonaws.lab.LambdaHandler;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

@Path("/Condition")

@io.swagger.annotations.Api(description = "the Condition API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-07-17T16:45:16.134-07:00")

public class ConditionResource {
	@Context
	SecurityContext securityContext;


	static final Logger log = LogManager.getLogger(ConditionResource.class);
	
	private static final String CONDITION_TABLE = System.getenv("FHIR_CONDITION_TABLE");	
	
	public ConditionResource() {
		

	}
	
	@GET
	@Produces({ "application/fhir+json", "application/xml+fhir" })
	@io.swagger.annotations.ApiOperation(value = "", notes = "Get Condition resources for a patient", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "Status 200", response = Void.class) })
	public Response gETCondition(
			@DefaultValue("") @QueryParam("patient-ref-id") String patientRefId,
			@Context SecurityContext securityContext)  {
		Bundle bundle = new Bundle();

		bundle.setType(BundleType.SEARCHSET);
		bundle.setId(UUID.randomUUID().toString());
		
		BundleLinkComponent bunLinkComp = new BundleLinkComponent();
		bunLinkComp.setRelation("self");
		//bunLinkComp.setUrl("http://hapi.fhir.org/baseDstu3/Patient?_pretty=true&address-country=US");
		
		ArrayList<BundleLinkComponent> bunLinkList = new ArrayList<>();
		bunLinkList.add(bunLinkComp);
		
		bundle.setLink(bunLinkList);
		
		ArrayList<BundleEntryComponent> entryList = new ArrayList<>();
		
		DynamoDB dynamoDB = LambdaHandler.getDynamoDB();
		Table table = dynamoDB.getTable(CONDITION_TABLE);
		
		Index index = table.getIndex("patientRefId-index");

		QuerySpec spec = new QuerySpec()
		    .withKeyConditionExpression("#p = :v_patRef")
		    .withNameMap(new NameMap()
		        .with("#p", "patientRefId"))
		    .withValueMap(new ValueMap()
		        .withString(":v_patRef","urn:uuid:"+patientRefId));
		        
		int resultCount = 0;
		ItemCollection<QueryOutcome> items = index.query(spec);
		Iterator<Item> iter = items.iterator(); 
		while (iter.hasNext()) {
			BundleEntryComponent comp = new BundleEntryComponent();
			Item item = iter.next();
			String obsJSON = item.toJSON();
			Condition cond = LambdaHandler.getFHIRContext().newJsonParser().parseResource(Condition.class, obsJSON);
			String condId = item.getString("id");
		    log.debug("The condition id : "+item.getString("id"));
		    
			comp.setResource(cond);
			comp.setFullUrl("http://hapi.fhir.org/baseDstu3/Condition/"+condId);
			
			entryList.add(comp);
			resultCount++;
		}
		bundle.setEntry(entryList);
		bundle.setTotal(resultCount);
		
		return Response.status(200).entity(LambdaHandler.getFHIRContext().newJsonParser().encodeResourceToString(bundle)).build();
	}

	/**
	 * method to create Condition record using a Condition object
	 * @param condition
	 * @return The id of the condition created
	 */
	
	public String createCondition(Condition condition,String userId) {
		//log.debug("Executing Observation create.. ");
		log.trace("The security context object.." + securityContext);
		log.trace("The patient reference .. "+condition.getSubject().getReference());

		String id =  UUID.randomUUID().toString();
		condition.setId(id);
		
		// log.debug("Executing dynamo db..");
		log.trace("Execute Dynamo DB with id" +id);
		// ddbMapper.save(patient, new DynamoDBMapperConfig(new
		// DynamoDBMapperConfig.TableNameOverride(PATIENT_TABLE)));
		DynamoDB dynamodb = new DynamoDB(LambdaHandler.getDDBClient());
		Table myTable = dynamodb.getTable(CONDITION_TABLE);

		// Make sure your object includes the hash or hash/range key
		String myJsonString = LambdaHandler.getFHIRContext().newJsonParser().encodeResourceToString(condition);

		// Convert the JSON into an object
		Item myItem = Item.fromJSON(myJsonString);
		myItem.withString("userid", userId);
		
		myItem.withString("patientRefId",condition.getSubject().getReference());

		// Insert the Object
		myTable.putItem(myItem);
		return id;
	}
	
    public Class<Condition> getResourceType() {
        return Condition.class;
    }
}
