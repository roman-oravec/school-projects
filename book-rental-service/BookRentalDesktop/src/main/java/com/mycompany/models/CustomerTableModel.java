/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.models;

import com.mycompany.bookrental.Customer;
import com.mycompany.bookrental.CustomerManagerImpl;
import com.mycompany.bookrental.Main;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Viki
 */
public class CustomerTableModel extends AbstractTableModel {
    
    private CustomerManagerImpl customerManager;
    private List<Customer> customers = new ArrayList<>();
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.mycompany.bookrentaldesktop.Bundle");

    public CustomerTableModel(){
        
            customerManager = new CustomerManagerImpl();
        try {
            customerManager.setDataSource(Main.prepareDataSource());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CustomerTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
            customers = customerManager.getAllCustomers();
      
    }
    
    public void setCustomerManager(CustomerManagerImpl customerManager) {
        this.customerManager = customerManager;
    }
    
    public void setCustomers(List<Customer> customers){
        this.customers = customers;
    }

    public CustomerManagerImpl getCustomerManager() {
        return customerManager;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
    
    public void removeRow(int row){
        Customer customer = customerManager.getCustomerById((long)getValueAt(row, 0));
        customerManager.deleteCustomer(customer);
        customers = customerManager.getAllCustomers();
        fireTableDataChanged();
    }
    public void addCustomer(Customer customer) {
        customerManager.addCustomer(customer);
        customers = customerManager.getAllCustomers();
        fireTableDataChanged();
    }
    
    public void update(Customer customer){
        int i;
        //preco customers.size -1?
        for (i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId().equals(customer.getId())){
                break;
            }
        }
        customers.set(i, customer);
        fireTableRowsUpdated(i, i);
    }

    
    @Override
    public int getRowCount() {
        return customers.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        
        switch(columnIndex){
            case 0:
                return customer.getId();
            case 1:
                return customer.getName();
            case 2:
                return customer.getEmail();
            case 3:
                return customer.getMembership();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    //getcolumnclass neni
    
    @Override
    public String getColumnName(int column){
        switch(column){
             case 0:
                return bundle.getString("ID");
            case 1:
                return bundle.getString("JDialogCustomer.jLabelName.text");
            case 2:
               return bundle.getString("JDialogCustomer.jLabelEmail.text"); 
            case 3:
                return bundle.getString("JDialogCustomer.jCheckBoxMembership.text"); 
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
    switch (columnIndex) {
        case 0:
            customer.setId((Long) aValue);
            break;
        case 1:
            customer.setName((String) aValue);
            break;
        case 2:
            customer.setEmail((String) aValue);
            break;
        case 3:
            customer.setMembership((boolean)aValue);
            break;
        default:
            throw new IllegalArgumentException("columnIndex");
    }
    fireTableCellUpdated(rowIndex, columnIndex);
}
    
    
    
}
