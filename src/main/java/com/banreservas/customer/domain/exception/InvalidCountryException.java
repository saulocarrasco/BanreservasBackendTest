package com.banreservas.customer.domain.exception;

public class InvalidCountryException extends RuntimeException {
    public InvalidCountryException(String code) {
        super("Country code is not valid: " + code);
    }
}
