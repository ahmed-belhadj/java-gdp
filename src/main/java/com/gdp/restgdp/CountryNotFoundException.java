package com.gdp.restgdp;

public class CountryNotFoundException extends RuntimeException
{
    public CountryNotFoundException(String country)
    {
        super("Could not find " + country);
    }
}
