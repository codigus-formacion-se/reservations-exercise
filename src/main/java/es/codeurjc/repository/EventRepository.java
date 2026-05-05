package es.codeurjc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}