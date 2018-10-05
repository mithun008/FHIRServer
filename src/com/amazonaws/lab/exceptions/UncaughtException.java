package com.amazonaws.lab.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
@Provider
public class UncaughtException extends Throwable implements ExceptionMapper<Throwable>
{
    private static final long serialVersionUID = 1L;
  
    @Override
    public Response toResponse(Throwable exception)
    {
    	exception.printStackTrace();
        return Response.status(500).entity("Something bad happened. Please try again !!").type("text/plain").build();
    }
}