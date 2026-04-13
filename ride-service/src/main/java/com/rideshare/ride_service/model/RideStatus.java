package com.rideshare.ride_service.model;

public enum RideStatus {
    REQUESTED, // Ride has been requested by the rider
    MATCHING,   // System is trying to find a driver for the ride
    ACCEPTED,  // Ride has been accepted by a driver
    DRIVER_ARRIVING, // Driver is on the way to the pickup location
    RIDE_STARTED, // Rider has been picked up and the ride is in progress
    COMPLETED, // Ride has been completed successfully
    CANCELLED   // Ride has been cancelled by either the rider or driver
}
