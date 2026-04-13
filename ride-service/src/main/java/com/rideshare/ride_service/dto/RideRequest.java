package com.rideshare.ride_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {

    @NotBlank(message = "Rider ID is required")
    private String riderId;
    @NotNull(message = "Pickup latitude is required")
    private String pickupLatitude;
    @NotNull(message = "Pickup longitude is required")
    private String pickupLongitude;
    @NotNull(message = "Pickup address is required")
    private String pickupAddress;
    @NotNull(message = "Drop latitude is required")
    private String dropLatitude;
    @NotNull(message = "Drop longitude is required")
    private String dropLongitude;
    @NotNull(message = "Drop address is required")
    private String dropAddress;

}
