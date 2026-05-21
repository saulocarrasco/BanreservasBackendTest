package com.banreservas.customer.exception;

import com.banreservas.customer.domain.exception.CountryServiceUnavailableException;
import com.banreservas.customer.domain.exception.CustomerNotFoundException;
import com.banreservas.customer.domain.exception.DuplicateEmailException;
import com.banreservas.customer.domain.exception.InvalidCountryException;
import com.banreservas.customer.domain.exception.InvalidCredentialsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof CustomerNotFoundException e) {
            return problem(404, "Not Found", e.getMessage());
        }
        if (ex instanceof DuplicateEmailException e) {
            return problem(409, "Conflict", e.getMessage());
        }
        if (ex instanceof InvalidCountryException e) {
            return problem(422, "Unprocessable Entity", e.getMessage());
        }
        if (ex instanceof CountryServiceUnavailableException e) {
            return problem(503, "Service Unavailable", e.getMessage());
        }
        if (ex instanceof InvalidCredentialsException) {
            return problem(401, "Unauthorized", "Invalid or missing credentials");
        }
        return problem(500, "Internal Server Error", "An unexpected error occurred");
    }

    private Response problem(int status, String title, String detail) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "about:blank");
        body.put("title", title);
        body.put("status", status);
        body.put("detail", detail);
        return Response.status(status)
            .type("application/problem+json")
            .entity(body)
            .build();
    }
}
