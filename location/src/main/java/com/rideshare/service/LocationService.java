package com.rideshare.service;

import com.rideshare.dto.DriverLocationRequest;
import com.rideshare.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String DRIVERS_GEO_KEY = "drivers:locations";

    /**
     * Update driver location in Redis
     * Called every 3 seconds by driver's phone
     * Maps to Redis GEOADD command
     */
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {

        log.info("Updating location for driver: {}", driverLocationRequest.getDriverId());

        Point driverPoint = new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );

        redisTemplate.opsForGeo().add(
                DRIVERS_GEO_KEY,
                driverPoint,
                driverLocationRequest.getDriverId()
        );

        log.info("Location updated for driver: {}", driverLocationRequest.getDriverId());
    }

    /**
     * Find nearby drivers within given radius (in KM)
     */
    public List<NearByDriverResponse> findNearByDrivers(double latitude,
                                                        double longitude,
                                                        double radius) {

        log.info("Finding nearby drivers for location: ({}, {}) with radius: {} km",
                latitude, longitude, radius);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radius, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        DRIVERS_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs
                                .newGeoRadiusArgs()
                                .includeCoordinates()   // ← THIS LINE WAS MISSING (caused the NPE)
                                .includeDistance()
                                .sortAscending()
                                .limit(10)
                );

        List<NearByDriverResponse> nearbyDrivers = new ArrayList<>();

        if (results != null) {
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {

                RedisGeoCommands.GeoLocation<String> location = result.getContent();

                nearbyDrivers.add(
                        new NearByDriverResponse(
                                location.getName(),                    // driverId
                                location.getPoint().getY(),           // latitude
                                location.getPoint().getX(),           // longitude
                                result.getDistance().getValue()       // distance
                        )
                );
            }
        }

        log.info("Found {} nearby drivers", nearbyDrivers.size());

        return nearbyDrivers;
    }

    public void removeDriver(String driverID) {
        log.info("Removing driver: {} from Redis", driverID);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, driverID);
        log.info("Driver: {} removed successfully", driverID);
    }
}