package com.banreservas.customer.domain.exception;

public class CountryServiceUnavailableException extends RuntimeException {
    public CountryServiceUnavailableException() {
        super("Country service is currently unavailable");
    }
}
