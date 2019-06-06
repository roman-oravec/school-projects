/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

import java.util.Objects;

/**
 *
 * @author roman
 */
public class Customer {
    
    private Long id;
    private String name;
    private String email;
    private boolean membershipPaid;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean getMembership() {
        return membershipPaid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMembership(boolean membership) {
        this.membershipPaid = membership;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (int) (89 * hash + this.id);
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
        final Customer other = (Customer) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", name=" + name + ", email=" 
                + email + ", membership=" + membershipPaid + '}';
    }
    
    
    
    
}
