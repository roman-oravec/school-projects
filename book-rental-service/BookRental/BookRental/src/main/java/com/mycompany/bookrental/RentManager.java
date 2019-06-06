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
public interface RentManager {
    
    /**
     * 
     * @param rent 
     */
    void addRent(Rent rent);
    
    /**
     * 
     * @param book
     * @return 
     */
    List<Rent> getRentsByBook(Book book);
    
    /**
     * 
     * @return 
     */
    List<Rent> getAllRents();
    
    /**
     * 
     * @param customer
     * @return 
     */
    List<Rent> getRentsByCustomer(Customer customer);
    
    /**
     * 
     * @param id
     * @return 
     */
    Rent getRentById(Long id);

    /**
     * 
     * @param book
     * @return 
     */
    int getNumberOfAvailableCopies(Book book);
    
    
    /**
     * 
     * @param rent 
     */
    void deleteRent(Rent rent);
    
    /**
     * 
     * @param rent 
     */
    void updateRent(Rent rent);
    
    
}
