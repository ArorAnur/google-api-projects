package com.assignment.google.marker.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.assignment.google.marker.rest.entity.Coordinates;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

@Service
public class StopFinderServiceImpl implements StopFinderService {

	// Key for hitting Directions API
	@Value("${key}")
	private String key;

	@Value("${resolution}")
	private double resolution;

	@Autowired
	private StopFinderLogic stopFinderLogic;

	@Override
	public List<LatLng> getStops(String source, String dest) {

		List<LatLng> stops = null;

		// Using Google API handler to retrieve the path between the source and the
		// destination.

		try {
			DirectionsResult result = DirectionsApi
					.getDirections(new GeoApiContext.Builder().apiKey(key).build(), source, dest).await();

			stops = getPoints(result);

		} catch (Exception e) {

			stops = Collections.emptyList();

		}

		return stops;

	}

	private List<LatLng> getPoints(DirectionsResult result) {

		List<Coordinates> points = extractPoints(result);

		Coordinates start = new Coordinates(result.routes[0].legs[0].startLocation);
		Coordinates end = new Coordinates(result.routes[0].legs[0].endLocation);

		// Method to generate the list of (x-y coordinates) stops on the path, spaced
		// 50m from each other

		List<Coordinates> coordinateList = stopFinderLogic.getStops(points, start, end, resolution);

		// Converting x-y coordinates back to LatLngs.

		List<LatLng> latLngList = new ArrayList<LatLng>();

		// Printing out the list of relevant LatLngs.
		for (Coordinates c : coordinateList) {
			latLngList.add(c.coordinatesToLatLng());
		}

		return latLngList;
	}

	private List<Coordinates> extractPoints(DirectionsResult result) {
		List<Coordinates> points = new ArrayList<Coordinates>();

		// LangLat coordinates of the source and destination using the appropriate JSON
		// field

		// Compiling the list of LatLangs/Coordinates using encoded polylines.

		for (DirectionsRoute route : result.routes) {
			for (DirectionsLeg leg : route.legs) {
				for (DirectionsStep step : leg.steps) {
					EncodedPolyline polyline = step.polyline;

					// Using the built-in decoding method to convert polyline strings to
					// cartesian plane coordinates.

					List<LatLng> polylinePoints = polyline.decodePath();

					for (LatLng ll : polylinePoints) {
						points.add(new Coordinates(ll));
					}
				}
			}
		}
		return points;
	}

}
