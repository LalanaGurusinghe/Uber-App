package com.rideshare.ride_service.dto;

import com.rideshare.ride_service.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {

    private String id;

    private String riderId;

    private String driverId;

    private String pickupLongitude;

    private String pickupLatitude;

    private String pickupAdress;

    private String dropLongitude;

    private String dropLatitude;

    private String dropAdress;

    //Ride status - tracks the lifecycle of the ride
    private RideStatus status;

    private double fare;
    private double actualFare;

    //Timestamps for tracking the ride lifecycle
    private LocalDate createdAt;

    private LocalDate updatedAt;

    private LocalDate startedAt;
    private LocalDate completedAt;
}
