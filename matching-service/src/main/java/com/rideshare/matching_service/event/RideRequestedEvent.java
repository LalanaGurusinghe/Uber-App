package com.rideshare.matching_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an event that is triggered when a ride request is made by a passenger.
 * Event consumed from kafka topic : ride.requested
 * Published by Rider service when a rider requests a ride. This event can be used to notify the matching service about the new ride request, allowing it to start the process of finding a suitable driver for the passenger.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {
    private String riderId;
    private String rideId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
}
