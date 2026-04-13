package com.rideshare.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event class representing a ride request. This event is published to the message broker
 * when a new ride request is created. It contains all the necessary information about the ride
 * that can be used by other services (like Driver Service) to process the request.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestEvent {
    private String rideId;
    private String riderId;
    private String pickupLatitude;
    private String pickupLongitude;
    private String pickupAddress;
    private String dropLatitude;
    private String dropLongitude;
    private String dropAddress;
}
