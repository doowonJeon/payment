package com.pay.repository;

import org.springframework.data.repository.CrudRepository;

import com.pay.model.Payment;

public interface PaymentRepository extends CrudRepository<Payment, String> {

}
