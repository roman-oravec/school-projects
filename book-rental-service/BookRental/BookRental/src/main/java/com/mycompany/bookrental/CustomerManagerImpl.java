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
 * @author roman
 */
public class CustomerManagerImpl implements CustomerManager {

    private static final Logger LOGGER = Logger.getLogger(
            CustomerManagerImpl.class.getName());

    private DataSource dataSource;
    
    
    public CustomerManagerImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    public CustomerManagerImpl(){
        
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
    public void addCustomer(Customer customer) {
        checkDataSource();
        validate(customer);
        if (customer.getId() != null) {
            throw new IllegalEntityException("customer id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Customer (name, email, membershipPaid) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, customer.getName());
            st.setString(2, customer.getEmail());
            st.setBoolean(3, customer.getMembership());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, true);          

            Long id = DBUtils.getId(st.getGeneratedKeys());
            customer.setId(id);
            conn.commit();
            LOGGER.log(Level.INFO, "Added customer");
        } catch (SQLException ex) {
            String msg = "Error when adding customer into database";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Customer getCustomerById(Long id) {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, email, membershipPaid FROM Customer WHERE id=?");
            st.setLong(1, id);
            return executeQueryForSingleCustomer(st);
        } catch (SQLException ex) {
            String msg = "Error when getting customer with id " + id + " from DB";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, email, membershipPaid FROM Customer");
            return executeQueryForMultipleCustomers(st);
        } catch(SQLException ex){
            String msg = "Error when getting all customers from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Customer> getCustomersByName(String name) {
        checkDataSource();
        if (name == null){
            throw new IllegalArgumentException("name is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, email, membershipPaid FROM Customer WHERE name=?");
            st.setString(1, name);
            return executeQueryForMultipleCustomers(st);
        } catch(SQLException ex){
            String msg = "Error while getting customer/customers by name: " + name + " from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    
    @Override
    public void updateCustomer(Customer customer) {
        checkDataSource();
        validate(customer);
        if (customer.getId() == null){
            throw new ValidationException("customer is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        
        try{
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Customer SET name=?, email=?, membershipPaid=? WHERE id=?");
            st.setString(1, customer.getName());
            st.setString(2, customer.getEmail());
            st.setBoolean(3, customer.getMembership());
            st.setLong(4, customer.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, false);
            conn.commit();
            LOGGER.log(Level.INFO, "Edited customer");
        } catch (SQLException ex){
            String msg = "Error when updating customer in db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        }finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }
    
    @Override
    public void deleteCustomer(Customer customer) {
        checkDataSource();
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null){
            throw new IllegalEntityException("customer is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Customer WHERE id=?");
            st.setLong(1, customer.getId());
            
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, false);
            conn.commit();
            LOGGER.log(Level.INFO, "Deleted customer");
        } catch (SQLException ex) {
            String msg = "Error when deleting customer from db";
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }


    static List<Customer> executeQueryForMultipleCustomers(PreparedStatement st) throws SQLException {
        ResultSet result = st.executeQuery();
        List<Customer> rs = new ArrayList<>();
        while (result.next()) {
            rs.add(resultCustomer(result));
        }
        return rs;
    }

    private static void validate(Customer customer) throws ValidationException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getName() == null) {
            throw new ValidationException("customer.name is null");
        }
        if (customer.getEmail() == null) {
            throw new ValidationException("customer.email is null");
        }
    }

    private Customer executeQueryForSingleCustomer(PreparedStatement st) throws SQLException{
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Customer result = resultCustomer(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Integrity error: more customers with same id found");
            }
            return result;
        } else {
            return null;
        }
    }
    
    private static Customer resultCustomer(ResultSet result) throws SQLException{
        Customer rs = new Customer();
        rs.setId(result.getLong("id"));
        rs.setName(result.getString("name"));
        rs.setEmail(result.getString("email"));
        rs.setMembership(result.getBoolean("membershipPaid"));
        return rs;
        
    }

}
