package com.amazonaws.lab.resources;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.dstu3.hapi.rest.server.ServerCapabilityStatementProvider;
import org.hl7.fhir.dstu3.model.CapabilityStatement;

/**
 * class to call the method that generates capability stament
 * @author mithumal
 *
 */

public class ConformanceProvider extends ServerCapabilityStatementProvider {
	
	

	public CapabilityStatement getServerConformance(HttpServletRequest theRequest) {
		return super.getServerConformance(theRequest);
	}
}