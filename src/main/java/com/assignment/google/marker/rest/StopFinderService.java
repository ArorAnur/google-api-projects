package com.assignment.google.marker.rest;

import java.util.List;

import com.google.maps.model.LatLng;

public interface StopFinderService {
	
	public List<LatLng> getStops(String source, String destination);

}
