package com.rideshare.matching_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an event that is triggered when a ride has been successfully matched between a passenger and a driver.
 * It may contain details about the matched ride, such as the passenger's information, driver's information, pickup location, drop-off location, and estimated time of arrival.
 * This event can be used to notify the passenger and driver about the successful match and provide them with relevant information about the ride.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMatchedEvent {
    private String rideId;
    private String riderId;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private double distanceToPickup;

    // Getters and Setters
}
