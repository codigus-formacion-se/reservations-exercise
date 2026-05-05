package es.codeurjc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}