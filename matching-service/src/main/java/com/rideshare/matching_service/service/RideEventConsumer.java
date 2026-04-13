package com.rideshare.matching_service.service;

import com.rideshare.matching_service.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideEventConsumer {

    private final MatchingService matchingService;

    /**
     * listens to ride requested kafka tipics and calls matching service to find a driver for the requested ride
     * Triggered everytime Ride service publishes a RideRequestedEvent to the Kafka topic "ride.requested". This method will consume the event, extract the necessary information about the ride request, and then call the matching service to find a suitable driver for the requested ride.
     * Ride Service -> Kafka(ride.requested)-> This Consumer -> Matching Service
     */

    @KafkaListener(topics = "ride.requested", groupId = "matching-service-group")
    public void consumeRideRequestedEvent(RideRequestedEvent event){
        try{
            matchingService.matchDriverForRide(event);
        }
            catch (Exception e){
                // Handle exceptions (e.g., log the error, retry logic, etc.)
                log.error("Error processing RideRequestedEvent for rideId {}: {}", event.getRideId(), e.getMessage());
        }
    }
}
