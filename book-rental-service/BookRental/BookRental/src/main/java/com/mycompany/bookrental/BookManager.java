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
public interface BookManager {
    
    /**
     * 
     * @param book 
     */
    void addBook(Book book);
    
    /**
     * 
     * @param id
     * @return 
     */
    Book getBookById(Long id);
    
    /**
     * 
     * @return 
     */
    List<Book>  getAllBooks();
    
    /**
     * 
     * @param author
     * @return 
     */
    List<Book> getBooksByAuthor(String author);
    
    /**
     * 
     * @param name
     * @return 
     */
    List<Book> getBooksByName(String name);
    
    /**
     * 
     * @param book 
     */
    void updateBook(Book book);
    
    /**
     * 
     * @param book 
     */
    void deleteBook(Book book);
}
