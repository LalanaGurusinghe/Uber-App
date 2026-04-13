package com.rideshare.controller;

import com.rideshare.dto.DriverLocationRequest;
import com.rideshare.dto.NearByDriverResponse;
import com.rideshare.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    //driver phone calls this every 3 seconds
    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest driverLocationRequest){
        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok("Location updated successfully");
    }

    //rider phone calls this when they want to find nearby drivers(Matching service call this when ride is required)
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(@RequestParam double latitude, @RequestParam double longitude , @RequestParam (defaultValue = "5.0") double radius){
        List<NearByDriverResponse> nearByDrivers = locationService.findNearByDrivers(latitude, longitude , radius);
        return ResponseEntity.ok(nearByDrivers);
    }

    //When driver goes offline or app is closed, we can call this API to remove the driver from Redis
    @DeleteMapping("/drivers/{driverID}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverID){
        locationService.removeDriver(driverID);
        return ResponseEntity.ok("Driver removed successfully");
    }
}
