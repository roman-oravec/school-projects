/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.models;

import com.mycompany.bookrental.Book;
import com.mycompany.bookrental.BookManagerImpl;
import com.mycompany.bookrental.Main;
import com.mycompany.bookrental.RentManagerImpl;
import com.mycompany.common.ServiceFailureException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
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
public class BooksTableModel extends AbstractTableModel{
    
    private BookManagerImpl bookManager;
    private RentManagerImpl rentManager;
    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.mycompany.bookrentaldesktop.Bundle");
    private List<Book> bookList = new ArrayList<>();
    
    public BooksTableModel(){
        try{
            DataSource ds;
            ds = Main.prepareDataSource();
            bookManager = new BookManagerImpl(ds);
            rentManager = new RentManagerImpl(ds);
            bookList = bookManager.getAllBooks();
        } catch (ServiceFailureException ex){
            Logger.getLogger(BooksTableModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BooksTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public List<Book> getBookList(){
        return bookList;
    }
    
    public void setBookList(List<Book> bookList){
        this.bookList = bookList;
    }

    public BookManagerImpl getBookManager() {
        return bookManager;
    }

    public void setBookManager(BookManagerImpl bookManager) {
        this.bookManager = bookManager;
    }
    
    public void removeRow(int row){
        Book book = bookManager.getBookById((long)getValueAt(row, 0));
        
        bookManager.deleteBook(book);
        bookList = bookManager.getAllBooks();
        fireTableDataChanged();
        
    }
    
    public void addBook(Book book){
        bookManager.addBook(book);
        bookList = bookManager.getAllBooks();
        fireTableDataChanged();
    }
    
    public void update(Book book){
        int i;
        for (i = 0; i < bookList.size() - 1; i++){
            if (Objects.equals(bookList.get(i).getId(), book.getId())){
                break;
            }
        }
        
        bookList.set(i, book);
        fireTableRowsUpdated(i, i);
    }

    @Override
    public int getRowCount() {
        return bookList.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int column){
        switch(column){
            case 0:
                return bundle.getString("ID");
            case 1:
                return bundle.getString("JDialogBook.jLabel1.text");
            case 2:
                return bundle.getString("JDialogBook.jLabel2.text");
            case 3:
                return bundle.getString("JDialogBook.jLabel3.text");
            case 4:
                return bundle.getString("AvailableCopies");
            case 5:
                return bundle.getString("AllCopies");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = bookList.get(rowIndex);
        
        switch(columnIndex){
            case 0:
                return book.getId();
            case 1:
                return book.getName();
            case 2:
                return book.getAuthor();
            case 3:
                return book.getPages();
            case 4:
                return rentManager.getNumberOfAvailableCopies(book);
            case 5:
                return book.getCopies();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    /*
    @Override
    public Class<?> getColumnClass(int columnIndex){
        switch (columnIndex) {
        case 0:
            return Long.class;
        case 1:
        case 2:
            return String.class;
        case 3:
        case 4:
        case 5:
            return Integer.class;
        default:
            throw new IllegalArgumentException("columnIndex");
        }
    }
    */
    @Override
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Book book = bookList.get(rowIndex);
    switch (columnIndex) {
        case 0:
            book.setId((Long) aValue);
            break;
        case 1:
            book.setName((String) aValue);
            break;
        case 2:
            book.setAuthor((String) aValue);
            break;
        case 3:
            book.setPages((Integer) aValue);
            break;
        case 4:
            book.setCopies((Integer) aValue);
            break;
        default:
            throw new IllegalArgumentException("columnIndex");
    }
    fireTableCellUpdated(rowIndex, columnIndex);
}
    
}
