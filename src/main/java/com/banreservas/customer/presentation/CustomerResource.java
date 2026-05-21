package com.banreservas.customer.presentation;

import com.banreservas.customer.application.CustomerService;
import com.banreservas.customer.application.command.CreateCustomerCommand;
import com.banreservas.customer.application.command.UpdateCustomerCommand;
import com.banreservas.customer.domain.model.Customer;
import com.banreservas.customer.presentation.dto.CreateCustomerRequest;
import com.banreservas.customer.presentation.dto.CustomerResponse;
import com.banreservas.customer.presentation.dto.PagedResponse;
import com.banreservas.customer.presentation.dto.UpdateCustomerRequest;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @POST
    public Response create(@Valid CreateCustomerRequest request) {
        CreateCustomerCommand cmd = new CreateCustomerCommand(
            request.firstName, request.middleName, request.lastName, request.secondLastName,
            request.email, request.address, request.phone, request.country
        );
        Customer customer = customerService.create(cmd);
        return Response.status(Response.Status.CREATED)
            .entity(CustomerResponse.from(customer))
            .build();
    }

    @GET
    public Response getAll(@QueryParam("page") Integer page,
                           @QueryParam("size") Integer size,
                           @QueryParam("country") String country) {
        if (page != null && size != null) {
            return Response.ok(
                PagedResponse.from(
                    customerService.findAllPaged(page, size, country)
                        .map(CustomerResponse::from)
                )
            ).build();
        }
        List<CustomerResponse> list = (country != null)
            ? customerService.findByCountry(country).stream().map(CustomerResponse::from).toList()
            : customerService.findAll().stream().map(CustomerResponse::from).toList();
        return Response.ok(list).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Customer customer = customerService.findById(id);
        return Response.ok(CustomerResponse.from(customer)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid UpdateCustomerRequest request) {
        UpdateCustomerCommand cmd = new UpdateCustomerCommand(
            request.email, request.address, request.phone, request.country
        );
        Customer customer = customerService.update(id, cmd);
        return Response.ok(CustomerResponse.from(customer)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        customerService.delete(id);
        return Response.noContent().build();
    }
}
