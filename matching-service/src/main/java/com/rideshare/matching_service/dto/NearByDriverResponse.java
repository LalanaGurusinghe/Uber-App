package com.rideshare.matching_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the response containing information about nearby drivers.
 * Request recieved from the location service with the nearby drivers and their details, which will be sent back to the client.
 * It may include details such as driver ID, name, vehicle information, distance from the passenger
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {
    private String driverId;
    private double latitude;
    private double longitude;
    private double distance;
}
