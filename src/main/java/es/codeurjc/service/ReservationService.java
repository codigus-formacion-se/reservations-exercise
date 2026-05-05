package es.codeurjc.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import es.codeurjc.model.Event;
import es.codeurjc.model.Reservation;
import es.codeurjc.model.User;
import es.codeurjc.repository.EventRepository;
import es.codeurjc.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final PaymentGateway paymentGateway;

    public ReservationService(ReservationRepository reservationRepository,
            EventRepository eventRepository,
            PaymentGateway paymentGateway) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.paymentGateway = paymentGateway;
    }

    public Reservation reserve(Long eventId, User user, int tickets) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.isOpen()) {
            throw new IllegalStateException("Event is closed");
        }

        if (tickets <= 0) {
            throw new IllegalArgumentException("Invalid tickets");
        }

        if (tickets > 10) {
            throw new IllegalArgumentException("Too many tickets");
        }

        if (event.getAvailableSeats() < tickets) {
            throw new IllegalStateException("Not enough seats");
        }

        double basePrice = tickets * event.getTicketPrice();
        double serviceFee = basePrice * 0.05;
        double totalPrice = basePrice + serviceFee;

        boolean paymentAccepted;
        try {
            paymentAccepted = paymentGateway.charge(user.getId(), totalPrice);
        } catch (Exception e) {
            return Reservation.failed(user, event, "Payment error");
        }

        if (!paymentAccepted) {
            return Reservation.failed(user, event, "Payment rejected");
        }

        event.setAvailableSeats(event.getAvailableSeats() - tickets);
        eventRepository.save(event);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setTickets(tickets);
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus(Reservation.Status.CONFIRMED);
        reservation.setCreationDate(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    public String buildReservationReport(User user, int year, boolean includeFailed) {

        if (user == null) {
            return "No user provided";
        }

        int totalTickets = 0;
        double totalAmount = 0.0;
        int confirmedCount = 0;
        int failedCount = 0;
        String report = "Report for user " + user.getId() + "\n";

        for (Reservation reservation : reservationRepository.findAll()) {
            if (reservation.getUser() == null) {
                continue;
            }

            if (reservation.getUser().getId().equals(user.getId())) {
                if (reservation.getCreationDate().getYear() == year) {
                    if (includeFailed) {
                        if (reservation.getStatus() == Reservation.Status.FAILED) {
                            failedCount = failedCount + 1;
                            totalAmount = totalAmount + reservation.getTotalPrice();
                            report = report + "Failed reservation: " + reservation.getId() + "\n";
                        }
                    }
                    if (reservation.getStatus() == Reservation.Status.CONFIRMED) {
                        confirmedCount = confirmedCount + 1;
                        totalTickets = totalTickets + reservation.getTickets();
                        totalAmount = totalAmount + reservation.getTotalPrice();
                        report = report + "Confirmed reservation: " + reservation.getId() + " ("
                                + reservation.getTickets() + " tickets)\n";
                    }
                }
            }
        }

        if (confirmedCount == 0 && failedCount == 0) {
            report = report + "No reservations found for " + year + "\n";
        }

        report = report + "Total tickets: " + totalTickets + "\n";
        report = report + "Total amount: " + totalAmount + "\n";
        report = report + "Confirmed: " + confirmedCount + "\n";
        report = report + "Failed: " + failedCount + "\n";

        if (totalAmount > 1000.0) {
            report = report + "VIP client\n";
        } else {
            report = report + "Regular client\n";
        }

        if (includeFailed == true) {
            report = report + "Including failed reservations\n";
        } else {
            report = report + "Excluding failed reservations\n";
        }

        return report;
    }

}
