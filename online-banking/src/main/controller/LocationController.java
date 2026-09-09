package com.bank.controller;


import org.springframework.web.bind.annotation.*;

import com.bank.service.LocationService;



@RestController
@RequestMapping("/location")
@CrossOrigin("*")
public class LocationController {



private final LocationService service;



public LocationController(
        LocationService service){

    this.service = service;

}




@GetMapping("/countries")
public String countries(){

    return service.getCountries();

}




@GetMapping("/states/{country}")
public String states(
@PathVariable String country){

    return service.getStates(country);

}




@GetMapping("/cities/{country}/{state}")
public String cities(

@PathVariable String country,

@PathVariable String state){

    return service.getCities(
        country,
        state
    );

}


}