package com.pay.repository;

import org.springframework.data.repository.CrudRepository;

import com.pay.model.Company;

public interface CompanyRepository extends CrudRepository<Company, String> {

}
