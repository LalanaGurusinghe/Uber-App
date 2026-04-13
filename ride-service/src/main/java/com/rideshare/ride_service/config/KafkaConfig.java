package com.rideshare.ride_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    //Topic where Ride Requests are published
    //Matching Service listens to this topic to find drivers for the ride requests

    @Bean
    public NewTopic rideRequestTopic() {
        return TopicBuilder.name("ride.requested")
                .partitions(3) // Number of partitions for scalability
                .replicas(1) // Number of replicas for fault tolerance
                .build();
    }

    //Topic where matching service publishes match results (which driver accepted the ride request)
    //Ride Service listens to this topic to update the ride status and notify the rider
    @Bean
    public NewTopic rideMatchTopic() {
        return TopicBuilder.name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
