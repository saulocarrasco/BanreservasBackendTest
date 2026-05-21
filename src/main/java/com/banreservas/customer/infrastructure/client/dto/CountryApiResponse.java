package com.banreservas.customer.infrastructure.client.dto;

import java.util.Map;

public class CountryApiResponse {

    public Map<String, DemonymEntry> demonyms;

    public static class DemonymEntry {
        public String f;
        public String m;
    }
}
