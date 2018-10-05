package com.amazonaws.lab.test;

import java.io.IOException;

import com.amazonaws.lab.resources.PatientResource;

public class FHIRServerTest {
	
	public static void main(String[] args)throws IOException {
		PatientResource pat = new PatientResource();
		pat.testBundle();
				
	}

}
