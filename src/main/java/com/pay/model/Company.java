package com.pay.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Company {
	@Id
    @Column(length = 20)
    private String id;

	@Column(length = 450, nullable = false)
    private String data;
}
