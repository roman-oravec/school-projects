/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

/**
 *
 * @author Viki
 */
public class CustomerBuilder {
    
    private Long id;
    private String name;
    private String eMail;
    private boolean membershipPaid;

    public CustomerBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder eMail(String eMail) {
        this.eMail = eMail;
        return this;
    }

    public CustomerBuilder membershipPaid(boolean membershipPaid) {
        this.membershipPaid = membershipPaid;
        return this;
    }
    
    /**
     * Creates new instance of Customer with configured properties.
     *
     * @return new instance of Customer with configured properties.
     */
    public Customer build() {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setEmail(eMail);
        customer.setMembership(membershipPaid);
        return customer;
    }
    
    
    
}
