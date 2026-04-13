package com.rideshare.ride_service.controller;


import com.rideshare.ride_service.dto.RideRequest;
import com.rideshare.ride_service.dto.RideResponse;
import com.rideshare.ride_service.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

    private final RiderService riderService;

    @PostMapping("/request")
    public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody RideRequest rideRequest) {
        log.info("Ride request received from rider: {}", rideRequest.getRiderId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(riderService.requestRide(rideRequest));

    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String rideId){
        return ResponseEntity.ok(riderService.getRideById(rideId));
    }

    @GetMapping("/rider/{riderId}/rides")
    public ResponseEntity<List<RideResponse>> getRidesByRider(@PathVariable String riderId){
        return ResponseEntity.ok(riderService.getRidesByRider(riderId));
    }

    //Driver start the ride

    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideResponse> startRide(@PathVariable String rideId){
        return ResponseEntity.ok(riderService.startRide(rideId));
    }

    @PutMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId){
        return ResponseEntity.ok(riderService.completeRide(rideId));

    }

    @PutMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancleRide(@PathVariable String rideId){
        return ResponseEntity.ok(riderService.cancleRide(rideId));

    }


}
