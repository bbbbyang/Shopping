package com.example.bing.shopping.infrastructure;

import java.util.HashMap;

public class ServiceResponse {
    private HashMap<String, String> propertyErrors;

    public ServiceResponse() {
        this.propertyErrors = new HashMap<>();
    }

    public void setPropertyErrors(String property, String errors) {
        propertyErrors.put(property, errors);
    }

    public String getPropertyError(String property) {
        return propertyErrors.get(property);
    }

    public boolean didSucceed() {
        return (propertyErrors.size() == 0);
    }

}
