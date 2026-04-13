package com.rideshare.ride_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    //rider who requested a ride
    @Column(nullable = false)
    private String riderId;

    private String driverId;

    @Column(nullable = false)
    private String pickupLongitude;
    @Column(nullable = false)
    private String pickupLatitude;
    @Column(nullable = false)
    private String pickupAdress;
    @Column(nullable = false)
    private String dropLongitude;
    @Column(nullable = false)
    private String dropLatitude;
    @Column(nullable = false)
    private String dropAdress;

    //Ride status - tracks the lifecycle of the ride
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    private double estimatedFare;
    private double actualFare;

    //Timestamps for tracking the ride lifecycle
    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;

    private LocalDate startedAt;
    private LocalDate completedAt;
}
