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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author roman
 */
public class RentManagerImpl implements RentManager {

    private final DataSource dataSource;
    private static final Logger LOGGER = Logger.getLogger(
            RentManagerImpl.class.getName());

    @SuppressWarnings("WeakerAccess")
    public RentManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void addRent(Rent rent) {
        BookManagerImpl bookManager = new BookManagerImpl();
        checkDataSource();
        bookManager.setDataSource(dataSource);
        validate(rent);
        if (rent.getId() != null) {
            throw new IllegalEntityException("rent id already set");
        }
        if (getNumberOfAvailableCopies((bookManager.getBookById(rent.getBookId()))) <= 0 ){
            throw new ValidationException("No available copies of this book right now");
        }
        rent.setStartDate(LocalDate.now());
        rent.setExpectedEndDate(rent.getStartDate().plus(30, DAYS));
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Rent (bookId, customerId, startDate, expectedEndDate) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, rent.getBookId());
            st.setLong(2, rent.getCustomerId());
            st.setObject(3, toSqlDate(rent.getStartDate()));
            st.setObject(4,toSqlDate(rent.getExpectedEndDate()));
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, rent, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            rent.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when adding rent into database";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Rent getRentById(Long id) {
        checkDataSource();
        if (id == null){
            throw new IllegalArgumentException("id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, customerId, bookId, startDate, expectedEndDate FROM Rent WHERE id=?");
                    st.setLong(1, id);
                    Rent res = executeQueryForSingleRent(st);
                    if (res == null){
                        throw new IllegalEntityException("rent not in DB");
                    }
                    return res;
        } catch(SQLException ex){
            String msg = "Error when getting rent with id " + id + " from DB";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> getRentsByBook(Book book) {
        checkDataSource();
        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (book.getId() == null){
            throw new IllegalEntityException("bookId is null");
        }
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, customerId, bookId, startDate, expectedEndDate FROM Rent WHERE bookId=?");
            st.setLong(1, book.getId());
            return executeQueryForMultipleRents(st);
        } catch (SQLException ex) {
            String msg = "Error while getting book with name: " + book.getName() + " from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }  
    }

    @Override
    public List<Rent> getAllRents() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, customerId, bookId, startDate, expectedEndDate, realEndDate FROM Rent");
            return executeQueryForMultipleRents(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all rents";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> getRentsByCustomer(Customer customer) {
        checkDataSource();
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null){
            throw new IllegalEntityException("customerId is null");
        }
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, customerId, bookId, startDate, expectedEndDate FROM Rent WHERE customerId=?");
            st.setLong(1, customer.getId());
            return executeQueryForMultipleRents(st);
        } catch (SQLException ex) {
            String msg = "Error while getting customer with name: " + customer.getName() + " from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }  
    }

    @Override
    public void deleteRent(Rent rent) {
        checkDataSource();
        if (rent == null){
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getId() == null){
            throw new IllegalEntityException("rent id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Rent WHERE id=?");
            st.setLong(1, rent.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, rent, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting rent from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateRent(Rent rent) {
        checkDataSource();
        validate(rent);
        if (rent.getId() == null){
            throw new ValidationException("rent is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Rent SET expectedEndDate=? WHERE id=?");
            st.setObject(1, toSqlDate(rent.getExpectedEndDate().plus(30, DAYS)));
            st.setLong(2, rent.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, rent, false);
            conn.commit();
            
        } catch (SQLException ex){
            String msg = "Error when updating rent in db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public int getNumberOfAvailableCopies(Book book) {
        return (book.getCopies() - getRentsByBook(book).size());
    }
    
    private static void validate(Rent rent) {
        
        if (rent == null) {
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getBookId() == null) {
            throw new IllegalEntityException("book id is null");
        }
        if (rent.getCustomerId() == null) {
            throw new IllegalEntityException("customer id is null");
        }
        
    }

    private Rent executeQueryForSingleRent(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Rent result = resultRent(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Integrity error: more rents with same id found");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Rent> executeQueryForMultipleRents(PreparedStatement st) throws SQLException {
        ResultSet result = st.executeQuery();
        List<Rent> rs = new ArrayList<>();
        while (result.next()) {
            rs.add(resultRent(result));
        }
        return rs;
    }

    private static Rent resultRent(ResultSet result) throws SQLException {
        Rent rs = new Rent();
        rs.setId(result.getLong("id"));
        rs.setBook(result.getLong("bookId"));
        rs.setCustomer(result.getLong("customerId"));
        rs.setStartDate(toLocalDate(result.getDate("startDate")));
        rs.setExpectedEndDate(toLocalDate(result.getDate("expectedEndDate")));
        return rs;
    }
    
    private static Date toSqlDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }
    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
    
}
