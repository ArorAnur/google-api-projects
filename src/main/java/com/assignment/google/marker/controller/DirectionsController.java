package com.assignment.google.marker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.google.marker.rest.StopFinderService;
import com.google.maps.model.LatLng;

@RestController
public class DirectionsController {
	
	
	@Autowired
	private StopFinderService service;
	
	@GetMapping("/getStops")
	public List<LatLng> getStops(@RequestParam String source, @RequestParam String dest) {
		
		
		
		//Using the Service layer to retrieve the stops
		List<LatLng> stops = service.getStops(source, dest);
		
		return stops;
	}
	
	
	
	
	

}
