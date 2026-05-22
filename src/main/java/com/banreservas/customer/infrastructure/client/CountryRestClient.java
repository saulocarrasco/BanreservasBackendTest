package com.banreservas.customer.infrastructure.client;

import com.banreservas.customer.infrastructure.client.dto.CountryApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "country-api")
@Path("/v3.1")
public interface CountryRestClient {

    @GET
    @Path("/alpha/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    CountryApiResponse getByCode(@PathParam("code") String code,
                                  @QueryParam("fields") String fields);
}
