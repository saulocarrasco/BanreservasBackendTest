package com.banreservas.customer.infrastructure.client;

import com.banreservas.customer.domain.exception.CountryServiceUnavailableException;
import com.banreservas.customer.domain.exception.InvalidCountryException;
import com.banreservas.customer.domain.port.CountryGateway;
import com.banreservas.customer.infrastructure.client.dto.CountryApiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CountryGatewayAdapter implements CountryGateway {

    private static final Logger LOG = Logger.getLogger(CountryGatewayAdapter.class);

    @RestClient
    CountryRestClient restClient;

    @Override
    public String getDemonym(String isoAlpha2Code) {
        try {
            CountryApiResponse result = restClient.getByCode(isoAlpha2Code, "demonyms");
            if (result == null || result.demonyms == null) {
                throw new InvalidCountryException(isoAlpha2Code);
            }
            CountryApiResponse.DemonymEntry eng = result.demonyms.get("eng");
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
            LOG.errorf(e, "Country service call failed for code '%s': %s", isoAlpha2Code, e.getMessage());
            throw new CountryServiceUnavailableException();
        }
    }
}
