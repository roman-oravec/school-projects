/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

import com.mycompany.common.DBUtils;
import com.mycompany.common.IllegalEntityException;
import com.mycompany.common.ServiceFailureException;
import com.mycompany.common.ValidationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Viki
 */
public class BookManagerImpl implements BookManager{
    
    private static final Logger LOGGER = Logger
            .getLogger(BookManagerImpl.class.getName()); 
    private DataSource dataSource;
    
    public BookManagerImpl(){
        
    }

    public BookManagerImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }  
    
    @Override
    public void addBook(Book book){
        checkDataSource();
        validate(book);
        if (book.getId() != null){
            throw new IllegalEntityException("book id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                "INSERT INTO Book (name,author,pages,copies) VALUES (?,?,?,?)", 
                    Statement.RETURN_GENERATED_KEYS);
            
            st.setString(1, book.getName());
            st.setString(2, book.getAuthor());
            st.setInt(3, book.getPages());
            st.setInt(4, book.getCopies());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, true);
            
            Long id = DBUtils.getId(st.getGeneratedKeys());
            book.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when adding book into db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Book getBookById(Long id) throws ServiceFailureException{
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, author, pages, copies FROM Book WHERE id=?");
            st.setLong(1, id);
            return executeQueryForSingleBook(st);
        } catch (SQLException ex) {
            String msg = "Error while getting book with id: " + id + "from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Book> getAllBooks() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, author, pages, copies FROM Book");
            return executeQueryForMultipleBooks(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all books from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Book> getBooksByAuthor(String author) {
        checkDataSource();
        if (author == null) {
            throw new IllegalArgumentException("author is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, author, pages, copies FROM Book WHERE author=?");
            st.setString(3, author);
            return executeQueryForMultipleBooks(st);
        } catch (SQLException ex) {
            String msg = "Error while getting book/books by author: " + author + "from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Book> getBooksByName(String name) {
        checkDataSource();
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, author, pages, copies FROM Book WHERE name=?");
            st.setString(1, name);
            return executeQueryForMultipleBooks(st);
        } catch (SQLException ex) {
            String msg = "Error while getting book/books with name: " + name + "from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateBook(Book book) {
        checkDataSource();
        validate(book);
        if (book.getId() == null){
            throw new ValidationException("book id is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Book SET name=?, author=?, pages=?, copies=? WHERE id=?");
            st.setString(1, book.getName());
            st.setString(2, book.getAuthor());
            st.setInt(3, book.getPages());
            st.setInt(4, book.getCopies());
            st.setLong(5, book.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating book in db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteBook(Book book) {
        checkDataSource();
        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (book.getId() == null) {
            throw new IllegalEntityException("book id is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Book WHERE id=?");
            st.setLong(1, book.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting book from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }
    
    private void validate(Book book) throws ValidationException{
        if (book == null){
            throw new IllegalArgumentException(" book is null");
        }
        if (book.getAuthor() == null){
            throw new ValidationException("author is null");
        }
        if (book.getName() == null){
            throw new ValidationException("book name is null");
        }
        if (book.getPages() <= 0){
            throw new ValidationException("book has zero or negative pages");
        }
        if (book.getCopies() <= 0){
            throw new ValidationException("book has zero or negative copies");
        }
    }
    
        static List<Book> executeQueryForMultipleBooks(PreparedStatement st) throws SQLException {
        ResultSet result = st.executeQuery();
        List<Book> rs = new ArrayList<>();
        while (result.next()) {
            rs.add(resultBook(result));
        }
        return rs;
    }
    
    private static Book resultBook(ResultSet result) throws SQLException {
        Book rs = new Book();
        rs.setId(result.getLong("id"));
        rs.setName(result.getString("name"));
        rs.setAuthor(result.getString("author"));
        rs.setPages(result.getInt("pages"));
        rs.setCopies(result.getInt("copies"));
        return rs;
    }
    
        static Book executeQueryForSingleBook(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Book result = resultBook(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Integrity error: more books with same id found");
            }
            return result;
        } else {
            return null;
        }
    }
    
}
