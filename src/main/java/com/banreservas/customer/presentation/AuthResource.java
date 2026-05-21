package com.banreservas.customer.presentation;

import com.banreservas.customer.application.AuthService;
import com.banreservas.customer.presentation.dto.AuthRequest;
import com.banreservas.customer.presentation.dto.TokenResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth/token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    public Response token(@Valid AuthRequest request) {
        String token = authService.generateToken(request.username, request.password);
        return Response.ok(new TokenResponse(token)).build();
    }
}
