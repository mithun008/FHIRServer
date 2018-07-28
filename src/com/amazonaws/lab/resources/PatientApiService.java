package com.amazonaws.lab.resources;


import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;


import java.util.List;


import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-07-17T16:45:16.134-07:00")
public abstract class PatientApiService {
    public abstract Response dELETEPatientid(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatient(SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientHistory(SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientSearch(SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientTags(SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientid(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientidHistory(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientidHistoryvid(String vid,String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientidHistoryvidTags(String vid,String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientidTags(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientideverything(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientidvalidate(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientmeta(SecurityContext securityContext) throws NotFoundException;
    public abstract Response gETPatientvalidate(SecurityContext securityContext) throws NotFoundException;
    public abstract Response pOSTPatient(SecurityContext securityContext) throws NotFoundException;
    public abstract Response pOSTPatientValidateid(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response pOSTPatientidHistoryvidTags(String vid,String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response pOSTPatientidHistoryvidTagsDelete(String vid,String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response pOSTPatientidTags(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response pOSTPatientidTagsDelete(String id,SecurityContext securityContext) throws NotFoundException;
    public abstract Response pUTPatientid(String id,SecurityContext securityContext) throws NotFoundException;
}
