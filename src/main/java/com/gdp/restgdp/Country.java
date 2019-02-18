package com.gdp.restgdp;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Country
{
    private @Id
    @GeneratedValue
    Long id;
    private String country;
    private long gdp;

    public Country()
    {
    }

    public Country(String country, long gdp)
    {
        this.country = country;
        this.gdp = gdp;
    }
}