package com.assignment.google.marker.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.assignment.google.marker.rest.entity.Coordinates;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

@Service
public class StopFinderLogic {

	public List<Coordinates> getStops(List<Coordinates> points, Coordinates start, Coordinates end, double res) {

		List<Coordinates> stops = new ArrayList<Coordinates>();

		stops.add(start);
		

		int i = 0;
		Coordinates lastStop = start;
		double target = res;

		/*
		 * APPROACH
		 * 
		 * 
		 * Polyline strings are decoded to give out a list of LatLngs along the path.
		 * The LatLng polylines are assumed to create the contour of the path between
		 * the source and the destination through a series of short line segments.
		 * 
		 * In the following approach, the entire list of polyline LatLngs is looked
		 * through right from the source. To start looking for the ith stop, once the
		 * (i-1)th stop is found, the list of LatLngs is parsed, with the length of each
		 * polyline segment added up on the "track" variable, to monitor the progress.
		 * Track is suitably refreshed upon finding the ith stop.
		 * 
		 * Polylines are a series of very short line segments,lying on the path between
		 * the source and the destination. Also since only use points lying on the
		 * polylines are used in the final result, this approach should take care of
		 * path curvatures.
		 * 
		 * While parsing the list elements, if a line segment is encountered, such that
		 * its length is longer than the path required to achieve a 50m distance from
		 * the (i-1)th stop, simple line interpolation is used to find an appropriate
		 * point lying on the encountered polyline.
		 *
		 *
		 * 
		 * 
		 * 
		 */

		while (true) {

			double distance = calculateDistance(lastStop, points.get(i));

			double distanceFromEnd = calculateDistance(lastStop, end);
			
			if (distanceFromEnd <= res) {
				// stops.add(points.get(i));
				stops.add(end);
				break;
			} else {
				if (distance < target) {

					lastStop = points.get(i);
					
					// Move on to the next list polyline endpoint, but use this one to track
					// the distance
					target = target - distance;
					i++;

				} else {

					// Interpolate the polyline segment to find the ith stop.

					Coordinates newStop = interpolate(lastStop, points.get(i), target);
					stops.add(newStop);
					lastStop = newStop;
					target = res;

					if (distance == target)
						i++;

				}

			}

		}

		return stops;
	}

	// Method to find a stop target meters away from the last stop using
	// interpolation

	private Coordinates interpolate(Coordinates a, Coordinates b, double target) {
		Coordinates r = new Coordinates(new LatLng(0, 0));

		double dist = calculateDistance(a, b);

		// Use similar triangles to perform the interpolation.
		double rx = (target * (b.x - a.x)) / dist;
		double ry = (target * (b.y - a.y)) / dist;
		r.x = rx + a.x;
		r.y = ry + a.y;

		return r;
	}



	// Method to calculate distance between two coordinates

	private double calculateDistance(Coordinates a, Coordinates b) {

		return Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));

	}
}
