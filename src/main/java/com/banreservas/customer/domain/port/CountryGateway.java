package com.banreservas.customer.domain.port;

public interface CountryGateway {
    /**
     * Returns the English masculine demonym for the given ISO 3166 alpha-2 code.
     * Throws InvalidCountryException if the code is unknown.
     * Throws CountryServiceUnavailableException if the external API cannot be reached.
     */
    String getDemonym(String isoAlpha2Code);
}
