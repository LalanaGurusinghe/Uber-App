package com.rideshare.ride_service.service;

import com.rideshare.ride_service.event.RideMatchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {

    private final RiderService riderService;

    @KafkaListener(topics = "ride.matched", groupId = "ride-service-group")
    public void consumeRideMatchedEvent(RideMatchedEvent event) {
        log.info("Received RideMatchedEvent: {}", event);
        // Deserialize the message to RideMatchedEvent
        // Update the ride status and notify the rider
        riderService.updatedRideWithDriver(event.getRideId(), event.getDriverId());
    }
}
