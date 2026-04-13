package com.rideshare.ride_service.service;

import com.rideshare.ride_service.dto.RideRequest;
import com.rideshare.ride_service.dto.RideResponse;
import com.rideshare.ride_service.event.RideRequestEvent;
import com.rideshare.ride_service.model.Ride;
import com.rideshare.ride_service.model.RideStatus;
import com.rideshare.ride_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiderService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String , RideRequestEvent> kafkaTemplate;

    private static final String RIDE_REQUEST_TOPIC = "ride.requested";

    /**
     * This method will handle the ride request from the rider. It will save the ride request in the database and then publish an event to Kafka to notify the driver service about the new ride request.
     */

    public RideResponse requestRide(RideRequest rideRequest) {
        log.info("New ride request received from rider: {}", rideRequest.getRiderId());
        // Save the ride request in the database
        Ride ride = new Ride();
        ride.setRiderId(rideRequest.getRiderId());
        // model stores coordinates as strings so convert
        ride.setPickupLatitude(rideRequest.getPickupLatitude());
        ride.setPickupLongitude(rideRequest.getPickupLongitude());
        ride.setPickupAdress(rideRequest.getPickupAddress());
        ride.setDropLatitude(rideRequest.getDropLatitude());
        ride.setDropLongitude(rideRequest.getDropLongitude());
        ride.setDropAdress(rideRequest.getDropAddress());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateFare(rideRequest));


        Ride saved = rideRepository.save(ride);

        // publish event to kafka to notify matching/driver service
        RideRequestEvent event = new RideRequestEvent(
                saved.getId(),
                saved.getRiderId(),
                saved.getPickupLatitude(),
                saved.getPickupLongitude(),
                saved.getPickupAdress(),
                saved.getDropLatitude(),
                saved.getDropLongitude(),
                saved.getDropAdress()
        );
        try {
            kafkaTemplate.send(RIDE_REQUEST_TOPIC, event);
        } catch (Exception e) {
            log.warn("Failed to publish ride request event to kafka: {}", e.getMessage());
        }

        saved.setStatus(RideStatus.MATCHING);
        rideRepository.save(saved);
        return toResponse(saved);
    }

    public void updatedRideWithDriver(String rideId, String driverId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    private double calculateFare(RideRequest rideRequest) {
        //Simple fare calculation based on distance. In real application, this would be more complex and take into account factors like traffic, time of day, etc.
        double lat1 = Math.toRadians(Double.parseDouble(rideRequest.getPickupLatitude()));
        double lon1 = Math.toRadians(Double.parseDouble(rideRequest.getPickupLongitude()));
        double lat2 = Math.toRadians(Double.parseDouble(rideRequest.getDropLatitude()));
        double lon2 = Math.toRadians(Double.parseDouble(rideRequest.getDropLongitude()));

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        //Haversine formula to calculate distance between two points on the Earth
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371 * c; // Distance in kilometers
        double baseFare = 5.0; // Base fare in dollars
        double perKmRate = 2.0; // Rate per kilometer in dollars
        return baseFare + (distance * perKmRate);
    }

    public RideResponse getRideById(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        return toResponse(ride);
    }

    public List<RideResponse> getRidesByRider(String riderId){
        return rideRepository.findByRiderId(riderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RideResponse startRide(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        if(ride.getStatus() != RideStatus.ACCEPTED){
            throw new RuntimeException("Ride cannot be started. Current status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDate.now());
        Ride updated = rideRepository.save(ride);
        return toResponse(updated);
    }

    public RideResponse completeRide(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        if(ride.getStatus() != RideStatus.RIDE_STARTED){
            throw new RuntimeException("Ride cannot be completed. Current status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDate.now());
        ride.setActualFare(ride.getEstimatedFare());
        Ride updated = rideRepository.save(ride);
        return toResponse(updated);
    }

    public RideResponse cancleRide(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus(RideStatus.CANCELLED);
        Ride updated = rideRepository.save(ride);
        return toResponse(updated);
    }

    private RideResponse toResponse(Ride ride){
        RideResponse resp = new RideResponse();
        resp.setId(ride.getId());
        resp.setRiderId(ride.getRiderId());
        resp.setDriverId(ride.getDriverId());
        resp.setPickupLatitude(ride.getPickupLatitude());
        resp.setPickupLongitude(ride.getPickupLongitude());
        resp.setPickupAdress(ride.getPickupAdress());
        resp.setDropLatitude(ride.getDropLatitude());
        resp.setDropLongitude(ride.getDropLongitude());
        resp.setDropAdress(ride.getDropAdress());
        resp.setStatus(ride.getStatus());
        resp.setFare(ride.getEstimatedFare());
        resp.setActualFare(ride.getActualFare());
        resp.setCreatedAt(ride.getCreatedAt());
        resp.setUpdatedAt(ride.getUpdatedAt());
        resp.setStartedAt(ride.getStartedAt());
        resp.setCompletedAt(ride.getCompletedAt());
        return resp;
    }

}
