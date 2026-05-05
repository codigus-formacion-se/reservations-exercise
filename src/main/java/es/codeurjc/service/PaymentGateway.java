package es.codeurjc.service;

public interface PaymentGateway {

    boolean charge(Long userId, double amount);
}