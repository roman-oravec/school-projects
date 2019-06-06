/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author roman
 */
public class Rent {
    
    private Long customerId;
    private Long bookId;
    private Long id;
    private LocalDate startDate;
    private LocalDate expectedEndDate;

    public Long getCustomerId() {
        return customerId;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }
    

    public void setCustomer(Long customerId) {
        this.customerId = customerId;
    }

    public void setBook(Long bookId) {
        this.bookId = bookId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setExpectedEndDate(LocalDate expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (int) (53 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rent other = (Rent) obj;
        return (Objects.equals(this.id, other.id));
    }

    @Override
    public String toString() {
        return "Rent{" + "customer=" + customerId + ", book=" + bookId + ", id=" 
                + id + ", startDate=" + startDate + ", expectedEndDate=" 
                + expectedEndDate + '}';
    }
    
    
    
}
