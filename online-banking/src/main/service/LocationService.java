package com.bank.service;


public interface LocationService {


String getCountries();


String getStates(
        String countryCode);


String getCities(
        String countryCode,
        String stateCode);


}