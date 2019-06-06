/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.models;

import com.mycompany.bookrental.Book;
import com.mycompany.bookrental.BookManagerImpl;
import com.mycompany.bookrental.Customer;
import com.mycompany.bookrental.CustomerManagerImpl;
import com.mycompany.bookrental.Main;
import com.mycompany.bookrental.Rent;
import com.mycompany.bookrental.RentManagerImpl;
import com.mycompany.common.ServiceFailureException;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author roman
 */
public class RentsTableModel extends AbstractTableModel {

    private List<Rent> rentList = new ArrayList<>();

    private BookManagerImpl bookManager;
    private CustomerManagerImpl customerManager;
    private RentManagerImpl rentManager;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.mycompany.bookrentaldesktop.Bundle");

    private final DateTimeFormatter formatter;

    public RentsTableModel(){
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try {
            DataSource ds = Main.prepareDataSource();
            bookManager = new BookManagerImpl(ds);
            customerManager = new CustomerManagerImpl(ds);
            rentManager = new RentManagerImpl(ds);

            rentList = rentManager.getAllRents();

        } catch (ServiceFailureException ex) {
            Logger.getLogger(BooksTableModel.class.getName()).log(Level.SEVERE, "Failed when creating RentsTableModel", ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RentsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Rent> getRentList() {
        return rentList;
    }

    public void setRentList(List<Rent> rentList) {
        this.rentList = rentList;
    }

    public BookManagerImpl getBookManager() {
        return bookManager;
    }

    public void setBookManager(BookManagerImpl bookManager) {
        this.bookManager = bookManager;
    }

    public CustomerManagerImpl getCustomerManager() {
        return customerManager;
    }

    public void setCustomerManager(CustomerManagerImpl customerManager) {
        this.customerManager = customerManager;
    }

    public RentManagerImpl getRentManager() {
        return rentManager;
    }

    public void setRentManager(RentManagerImpl rentManager) {
        this.rentManager = rentManager;
    }

    public void removeRow(int row) {
        Rent rent = rentManager.getRentById((long) getValueAt(row, 0));
        rentManager.deleteRent(rent);
        rentList = rentManager.getAllRents();
        fireTableDataChanged();
    }

    public void addRent(Rent rent) {
        
        rentManager.addRent(rent);
        rentList = rentManager.getAllRents();
        
        
    }

    public void update(Rent rent) {
        int i;
        for (i = 0; i < rentList.size() - 1; i++) {
            if (Objects.equals(rentList.get(i).getId(), rent.getId())) {
                break;
            }
        }
        rentManager.updateRent(rent);
        rentList = rentManager.getAllRents();
        fireTableRowsUpdated(i, i);
    }

    @Override
    public int getRowCount() {
        return rentList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {

        switch (column) {
            case 0:
                return bundle.getString("ID");
            case 1:
                return bundle.getString("BooksFrame.jPanel2.TabConstraints.tabTitle");
            case 2:
                return bundle.getString("BooksFrame.jPanel1.TabConstraints.tabTitle");
            case 3:
                return bundle.getString("Start");
            case 4:
                return bundle.getString("End");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Rent rent = rentList.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return rent.getId();
            case 1:
                return customerManager.getCustomerById(rent.getCustomerId()).getName();
            case 2:
                return bookManager.getBookById(rent.getBookId()).getName();
            case 3:
                return rent.getStartDate().format(formatter);
            case 4:
                return rent.getExpectedEndDate().format(formatter);
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

}
