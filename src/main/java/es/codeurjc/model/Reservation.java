package es.codeurjc.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    private int tickets;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime creationDate;
    private String failureReason; // For failed reservations

    public enum Status {
        CONFIRMED, FAILED
    }

    // Constructors
    public Reservation() {
    }

    public Reservation(User user, Event event, int tickets, double totalPrice, Status status,
            LocalDateTime creationDate) {
        this.user = user;
        this.event = event;
        this.tickets = tickets;
        this.totalPrice = totalPrice;
        this.status = status;
        this.creationDate = creationDate;
    }

    // Static factory method for failed reservations
    public static Reservation failed(User user, Event event, String reason) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setStatus(Status.FAILED);
        reservation.setFailureReason(reason);
        reservation.setCreationDate(LocalDateTime.now());
        return reservation;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}