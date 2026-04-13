package com.rideshare.matching_service.service;

import com.rideshare.matching_service.client.LocationServiceClient;
import com.rideshare.matching_service.dto.NearByDriverResponse;
import com.rideshare.matching_service.event.RideMatchedEvent;
import com.rideshare.matching_service.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS = 5.0; // in kilometers

    /**
     * main matchin algorithm to find nearby drivers and publish matched event to kafka topic
     * Called when RideRequestedEvent is consumed from kafka topic : ride.requested
     *
     * STEP1:
     * ASK Location service for nearby drivers using the pickup location of the ride request. The location service will return a list of nearby drivers within a specified radius.
     * STEP2:
     * Score driver and select the best driver based on factors like proximity, driver rating, and availability. This can be done using a simple scoring algorithm or a more complex machine learning model.
     * STEP3:
     * Publish a RideMatchedEvent to the Kafka topic "ride.matched" with the details of the matched driver and the ride request. This event can be consumed by the Ride service to notify the rider and the driver about the match and to proceed with the ride booking process.
     * */

    public void matchDriverForRide(RideRequestedEvent event){
        List<NearByDriverResponse> nearByDrivers = locationServiceClient.getNearbyDrivers(event.getPickupLatitude(), event.getPickupLongitude(), DEFAULT_SEARCH_RADIUS);

        if (nearByDrivers.isEmpty()){
            log.warn("No nearby drivers found for ride request: {}", event.getRideId());
            // Handle the case where no drivers are available (e.g., notify the rider, retry after some time, etc.)
            return;
        }

        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearByDrivers);

        if(bestDriver.isEmpty()){
            log.warn("No suitable driver found for ride request: {}", event.getRideId());
            // Handle the case where no suitable driver is found (e.g., notify the rider, retry after some time, etc.)
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get();

        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistance()
        );

        kafkaTemplate.send(RIDE_MATCHED_TOPIC,event.getRideId(), matchedEvent);
        log.info("Published RideMatchedEvent for rideId: {} with driverId: {}", event.getRideId(), assignedDriver.getDriverId());

    }

    /**
     * Distance = 70%
     * Ratings = 30%
     *
     * Score = (1/distance) * 0.7 + (rating) * 0.3
     */

    private Optional<NearByDriverResponse> findBestDriver(List<NearByDriverResponse> drivers) {
        double distanceWeight = 0.7;
        double ratingWeight = 0.3;

        return  drivers.stream()
                .max(Comparator.comparingDouble(driver->{
                    //Distance score (the closer the driver, the higher the score)
                    double distanceScore = 1 / (driver.getDistance() + 0.1); // Adding a small value to avoid division by zero

                    //Simulate rating between 4.0 and 5.0 (In real implementation, you would fetch the actual rating of the driver)
                    double rating = 4.0 + Math.random(); // Simulating a rating between 4.0 and 5.0

                    //Find weighted score
                    return (distanceScore * distanceWeight) + (rating * ratingWeight);
                }));
    }
}
