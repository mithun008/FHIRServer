package com.amazonaws.lab.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;


import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;

/**
 * This class is a mock class that provides annotation for the interactions that 
 * have been implemented for various resources.
 * The actual implementations remain in the main resource classes.
 * @author mithumal
 *
 */
		


@io.swagger.annotations.Api(description = "the CapabilityStatementResource API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-07-17T16:45:16.134-07:00")
public class CapabilityStatementResource{





	@Context
	SecurityContext securityContext;
	
	
	

	static final Logger log = LogManager.getLogger(CapabilityStatementResource.class);
	

	
	public CapabilityStatementResource() {
	
		
	}
	
	@Read(type=Patient.class)
	public Bundle getPatient(@IdParam IdType id) {
		//dummy methods
		
		return new Bundle();
	}
	
	@Search(type=Patient.class)
	public Bundle searchPatient() {
		//dummy methods
		return new Bundle();
	}
	
	@Create
	public MethodOutcome createPatient(@ResourceParam Patient thePatient) {
		//dummy methods
		return new MethodOutcome();
	}
	
	@Create
	public MethodOutcome createBundle(@ResourceParam Bundle theBundle) {
		//dummy methods
		return new MethodOutcome();
	}
	
	@Search(type=Observation.class)
	public Bundle searchObservation() {
		//dummy methods
		return new Bundle();
	}
}
