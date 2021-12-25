package com.assignment.google.marker.rest.entity;

import com.google.maps.model.LatLng;


// Java Class to convert degrees into metric coordinates.




public class Coordinates {
	
	public double x;
	public double y;
	
	//For every 90 degrees of LangLat distance, we 10^7 in meter. This is the conversion factor
	//at the equator of the earth.
	private final static double FACTOR = 111111.111111;
	
    public Coordinates(LatLng l) {
    	x = FACTOR * l.lat;
    	y = FACTOR * l.lng;
    }
    
    
    public LatLng coordinatesToLatLng() {
    	
    	return new LatLng(x/FACTOR,y/FACTOR);
    	
    }

}
