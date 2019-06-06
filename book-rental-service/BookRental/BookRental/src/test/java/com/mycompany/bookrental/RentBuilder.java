/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This is builder for the Book class to make tests better readable.
 *
 * @author Viki
 */
public class RentBuilder {
    
    private Long id;
    private Long customerId;
    private Long bookId;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    
    public RentBuilder id(Long id) {
        this.id = id;
        return this;
    }
    
    public RentBuilder customerId(Long customerId){
        this.customerId = customerId;
        return this;
    }
    
    public RentBuilder bookId(Long bookId) {
        this.bookId = bookId;
        return this;
    }
    
    public RentBuilder startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }
    
    public RentBuilder expectedEndDate(LocalDate exEndDate) {
        this.expectedEndDate = exEndDate;
        return this;
    }
    
    public Rent build() {
        Rent rent = new Rent();
        rent.setBook(bookId);
        rent.setCustomer(customerId);
        rent.setId(id);
        rent.setStartDate(LocalDate.now());
        rent.setExpectedEndDate(rent.getStartDate().plus(30, DAYS));
        return rent;
    }
    
    
}
