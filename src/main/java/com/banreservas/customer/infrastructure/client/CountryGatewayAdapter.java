package com.banreservas.customer.infrastructure.client;

import com.banreservas.customer.domain.exception.CountryServiceUnavailableException;
import com.banreservas.customer.domain.exception.InvalidCountryException;
import com.banreservas.customer.domain.port.CountryGateway;
import com.banreservas.customer.infrastructure.client.dto.CountryApiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class CountryGatewayAdapter implements CountryGateway {

    @RestClient
    CountryRestClient restClient;

    @Override
    public String getDemonym(String isoAlpha2Code) {
        try {
            List<CountryApiResponse> results = restClient.getByCode(isoAlpha2Code, "demonyms");
            if (results == null || results.isEmpty()) {
                throw new InvalidCountryException(isoAlpha2Code);
            }
            CountryApiResponse first = results.get(0);
            if (first.demonyms == null) {
                throw new InvalidCountryException(isoAlpha2Code);
            }
            CountryApiResponse.DemonymEntry eng = first.demonyms.get("eng");
            if (eng == null || eng.m == null) {
                throw new InvalidCountryException(isoAlpha2Code);
            }
            return eng.m;
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new InvalidCountryException(isoAlpha2Code);
            }
            throw new CountryServiceUnavailableException();
        } catch (InvalidCountryException e) {
            throw e;
        } catch (Exception e) {
            throw new CountryServiceUnavailableException();
        }
    }
}
