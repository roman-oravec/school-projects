/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

import java.util.List;

/**
 *
 * @author roman
 */
public interface CustomerManager {
    
    /**
     * 
     * @param customer 
     */
    void addCustomer(Customer customer);
    
    /**
     * 
     * @param id
     * @return 
     */
    Customer getCustomerById(Long id);
    
    /**
     * 
     * @return 
     */
    List<Customer> getAllCustomers();
    
    /**
     * 
     * @param name
     * @return 
     */
    List<Customer> getCustomersByName(String name);
    /**
     * 
     * @param customer 
     */
    void deleteCustomer(Customer customer);
    
    /**
     * 
     * @param customer 
     */
    void updateCustomer(Customer customer);
}
