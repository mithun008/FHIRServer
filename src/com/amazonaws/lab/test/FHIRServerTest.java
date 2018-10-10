package com.amazonaws.lab.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;

public class FHIRServerTest {
	
	static final Logger log = LogManager.getLogger(FHIRServerTest.class);

	public static void main(String[] args) throws IOException {
		// PatientResource pat = new PatientResource();
		// pat.testBundle();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("The date :" + sdf.format(new Date()));

		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -30);

		LocalDate now = LocalDate.now();
		LocalDate thirty = now.minusDays(30);
		LocalDate sixty = now.minusDays(60);
		LocalDate ninety = now.minusDays(90);

		System.out.println("now: " + now);
		System.out.println("thirty: " + thirty);
		System.out.println("sixty: " + sixty);
		System.out.println("ninety: " + ninety);
		
		FHIRServerTest test = new FHIRServerTest();
		AuthenticationResultType authResult = test.signIn("mithunm", "FHIRService123!", "2m9c0cnsorcr4e3k2q6jct466e", "us-west-2_uzw5bY4Pp");
		//if(authResult.get)
		System.out.println("The auth result :"
				+authResult.getIdToken()+" Validity : "+authResult.getExpiresIn());

	}

	public AuthenticationResultType signIn(String userName, String password, String clientId, String userPoolId) {

		AuthenticationResultType authenticationResult = null;
		AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();

		final Map<String, String> authParams = new HashMap<>();

		authParams.put("USERNAME", userName);
		authParams.put("PASSWORD", password);

		AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();

		authRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
				.withClientId(clientId).withUserPoolId(userPoolId)
				.withAuthParameters(authParams);

		AdminInitiateAuthResult result = cognitoClient.adminInitiateAuth(authRequest);
		log.debug("The challenge type :"+result.getChallengeName());
		authenticationResult = result.getAuthenticationResult();
		return authenticationResult;

	}

	public AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
		DefaultAWSCredentialsProviderChain credProvider = new DefaultAWSCredentialsProviderChain();
		//ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

		return AWSCognitoIdentityProviderClientBuilder.standard().withCredentials(credProvider)
				.withRegion("us-west-2").build();

	}

}
