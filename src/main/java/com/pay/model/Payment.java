package com.pay.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity
public class Payment {
	@Id
    @Column(length = 20)
    private String id;

	@Column
    private int installment_months;
	
	@Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long vat;
    
    @Column(nullable = false)
    private String card_info;
    
    @Column(nullable = false)
    private Boolean cancel_flag = false;
    
    @Column(nullable = false, updatable = false)  
    @Temporal(TemporalType.TIMESTAMP)  
    @CreationTimestamp 
    private Date created_date;
}
