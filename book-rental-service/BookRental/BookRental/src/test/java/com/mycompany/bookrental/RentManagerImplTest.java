/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

import com.mycompany.common.DBUtils;
import com.mycompany.common.IllegalEntityException;
import java.sql.SQLException;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author viki & roman
 */
public class RentManagerImplTest {
    
    private RentManagerImpl manager;
    private BookManagerImpl bookManager;
    private CustomerManagerImpl customerManager;
    private DataSource ds;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:bookrental-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, BookManager.class.getResource("createTables.sql"));
        manager = new RentManagerImpl(ds);
        bookManager = new BookManagerImpl();
        bookManager.setDataSource(ds);
        customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(ds);
        prepareTestData();
    }
    
        @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, RentManager.class.getResource("dropTables.sql"));
    }
    
    private Book b1, b2, b3, bookNotInDB, bookNullId;
    private Customer c1, c2, c3, c4, c5, customerNotInDB, customerNullId;
    
    private void prepareTestData(){
        
        b1 = new BookBuilder().author("James Joyce").name("Ulysses")
                .copies(2).pages(300).build();
        b2 = new BookBuilder().author("Mario Puzo").name("The Godfather")
                .copies(1).pages(210).build();
        b3 = new BookBuilder().author("Joshua T. Payne").name("Effective Java")
                .copies(14).pages(754).build();
        
        c1 = new CustomerBuilder().name("Adko").eMail("adko@xy.com")
                .membershipPaid(false).build();
        c2 = new CustomerBuilder().name("Alex").eMail("alex@xy.com")
                .membershipPaid(false).build();
        c3 = new CustomerBuilder().name("Zdeno").eMail("zdencooo@xy.com")
                .membershipPaid(true).build();
        c4 = new CustomerBuilder().name("Kulo").eMail("adrique@xy.com")
                .membershipPaid(false).build();
        c5 = new CustomerBuilder().name("Lubo").eMail("kittykatty@xy.com")
                .membershipPaid(true).build();
        
        bookManager.addBook(b1);
        bookManager.addBook(b2);
        bookManager.addBook(b3);
        
        customerManager.addCustomer(c1);
        customerManager.addCustomer(c2);
        customerManager.addCustomer(c3);
        customerManager.addCustomer(c4);
        customerManager.addCustomer(c5);
        
        bookNullId = new BookBuilder().id(null).build();
        bookNotInDB = new BookBuilder().id(b3.getId() + 150).build();
        assertThat(bookManager.getBookById(bookNotInDB.getId())).isNull();
        
        customerNullId = new CustomerBuilder().id(null).build();
        customerNotInDB = new CustomerBuilder().id(c5.getId() + 150).build();
        assertThat(customerManager.getCustomerById(customerNotInDB.getId()))
                .isNull();
        
        }
    
    @Test
    public void getRentsByBook(){
        
        assertThat(manager.getRentsByBook(b1)).isEmpty();
        assertThat(manager.getRentsByBook(b2)).isEmpty();
        assertThat(manager.getRentsByBook(b3)).isEmpty();
       
        Rent rent = new RentBuilder().customerId(c1.getId()).bookId(b1.getId()).build();
        manager.addRent(rent);
        assertThat(manager.getRentsByBook(b1)).contains(rent);
        assert((manager.getRentsByBook(b1)).size() == 1);
        
        assertThat(manager.getRentsByBook(b2)).isEmpty();
        assertThat(manager.getRentsByBook(b3)).isEmpty();
        
        Rent rent2 = new RentBuilder().customerId(c2.getId()).bookId(b1.getId()).build();
        Rent rent3 = new RentBuilder().customerId(c3.getId()).bookId(b2.getId()).build();
        manager.addRent(rent2);
        manager.addRent(rent3);
        assertThat(manager.getRentsByBook(b1)).contains(rent, rent2);
        assertThat(manager.getRentsByBook(b2)).contains(rent3);
        assertThat(manager.getRentsByBook(b3)).isEmpty();
        assert((manager.getRentsByBook(b1)).size() == 2);
        assert((manager.getRentsByBook(b2)).size() == 1);
        
        }
    
    @Test(expected = IllegalArgumentException.class)
    public void addNullRent() {
        manager.addRent(null);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void addRentWithNullId() {
        Rent rent = new RentBuilder().id(null).build();
        manager.addRent(rent);
    }
    
    /*@Test (expected = IllegalEntityException.class)
    public void rentBookWithNullId(){
        Rent rent = new RentBuilder().bookId(bookNullId.).build();
        manager.addRent(rent);
    }*/
    
    @Test (expected = IllegalEntityException.class)
    public void rentBookNotInDB(){
        Rent rent = new RentBuilder().bookId(bookNotInDB.getId()).build();
        manager.addRent(rent);
    }
    
    @Test 
    public void addRentWithNullCustomer(){
        Rent rent = new RentBuilder().customerId(null).build();
        assertThatThrownBy(()->manager.addRent(rent))
                .isInstanceOf(IllegalEntityException.class);
    }
    
    /*@Test (expected = IllegalEntityException.class)
    public void rentCustomerWithNullId(){
        Rent rent = new RentBuilder().customer(customerNullId).build();
        manager.addRent(rent);
    }*/
    
    @Test (expected = IllegalEntityException.class)
    public void rentCustomerNotInDB(){
        Rent rent = new RentBuilder().customerId(customerNotInDB.getId()).build();
        manager.addRent(rent);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getRentWithNullBook(){
        manager.getRentsByBook(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void getRentWithNullIdBook(){
        manager.getRentsByBook(bookNullId);
    }
    
    @Test
    public void getAllRents(){
        assertThat(manager.getAllRents()).isEmpty();
        assert(manager.getAllRents().isEmpty());
        
        Rent rent = new RentBuilder().bookId(b1.getId()).customerId(c1.getId()).build();
        Rent rent2 = new RentBuilder().bookId(b2.getId()).customerId(c2.getId()).build();
        Rent rent3 = new RentBuilder().bookId(b3.getId()).customerId(c3.getId()).build();
        
        manager.addRent(rent);
        manager.addRent(rent2);
        manager.addRent(rent3);
        
        assertThat(manager.getAllRents()).usingFieldByFieldElementComparator()
                .containsOnly(rent, rent2, rent3);
        assert(manager.getAllRents().size() == 3);
    }
    
    
    @Test
    public void getRentsByCustomer() {
        assertThat(manager.getRentsByCustomer(c1)).isEmpty();
        assert(manager.getRentsByCustomer(c1).isEmpty());
        
        Rent rent = new RentBuilder().bookId(b1.getId()).customerId(c1.getId()).build();
        
        manager.addRent(rent);
        
        assertThat(manager.getRentsByCustomer(c1)).containsOnly(rent);
        assert(manager.getRentsByCustomer(c1).size() == 1);
        
        Rent rent2 = new RentBuilder().bookId(b2.getId()).customerId(c1.getId()).build();
        manager.addRent(rent2);
        assertThat(manager.getRentsByCustomer(c1))
                .usingFieldByFieldElementComparator().containsOnly(rent, rent2);
        assert(manager.getRentsByCustomer(c1).size() == 2);       
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void getRentsByNullCustomer() {
        manager.getRentsByCustomer(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void getRentsByNullIdCustomer() {
        manager.getRentsByCustomer(customerNullId);
    }

    
    @Test
    public void getRentById(){
        Rent rent = new RentBuilder().bookId(1L).customerId(1L).build();
        manager.addRent(rent);
        assertThat(manager.getRentById(rent.getId())).isEqualTo(rent);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void getRentNotInDB() {
        Rent rent = new RentBuilder().bookId(b1.getId()).customerId(c1.getId()).id(5L).build();
        manager.getRentById(rent.getId());
    }

    @Test
    public void getNumberOfAvailableCopies() {
        assert(manager.getNumberOfAvailableCopies(b1) == 2);
        
        Rent rent = new RentBuilder().bookId(b1.getId()).customerId(c1.getId()).build();
        manager.addRent(rent);
        assert(manager.getNumberOfAvailableCopies(b1) == 1);      
    }
    /*
    @Test
    public void rentMoreBooksThanPossible(){
        Rent rent = new RentBuilder().customerId(c1.getId()).bookId(b2.getId()).build();
        Rent rent2 = new RentBuilder().customerId(c2.getId()).bookId(b2.getId()).build();
        manager.addRent(rent);
        assertThatThrownBy(()->manager.addRent(rent2))
                .isInstanceOf(ValidationException.class);     
    }
    
    @Test
    public void returnBook() {
        Rent rent = new RentBuilder().customerId(c1.getId()).bookId(b2.getId()).build();
        manager.addRent(rent);
        manager.returnBook(b1);
        assert(b1.getCopies() == manager.getNumberOfAvailableCopies(b1));
    }
     */
    
     //--------------------------------------------------------------------------
    // Tests for RentManager.updateRent(Rent) operation
    //--------------------------------------------------------------------------
    
    @Test
    public void updateRent(){
        Rent rent = new RentBuilder().customerId(c1.getId()).bookId(b2.getId()).build();
        Rent rent2 = new RentBuilder().customerId(c2.getId()).bookId(b1.getId()).build();
        manager.addRent(rent);
        manager.addRent(rent2);
        LocalDate date = LocalDate.now().plus(60, DAYS);
        manager.updateRent(rent);
        assertThat(manager.getRentsByBook(b1))
                .usingFieldByFieldElementComparator().containsOnly(rent2);
        assert((manager.getRentById(rent.getId()).getExpectedEndDate().isEqual(date)));
    }
    
    //--------------------------------------------------------------------------
    // Tests for RentManager.deleteRent(Rent) operation
    //--------------------------------------------------------------------------
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteNullRent() {
        manager.deleteRent(null);
    }
    
    @Test (expected = IllegalEntityException.class)
    public void deleteRentNotInDB() {
        Rent rent = new RentBuilder().build();
        manager.deleteRent(rent);
    }
    
    @Test
    public void deleteOneOfOneRent() {
        Rent rent = new RentBuilder().customerId(c1.getId()).bookId(b1.getId()).build();
        manager.addRent(rent);
        manager.deleteRent(rent);
        assert(manager.getAllRents().isEmpty());
    }
    
    @Test
    public void deleteRent() {
        Rent rent = new RentBuilder().customerId(c1.getId()).bookId(b1.getId()).build();
        Rent rent2 = new RentBuilder().customerId(c2.getId()).bookId(b2.getId()).build();
        Rent rent3 = new RentBuilder().customerId(c3.getId()).bookId(b3.getId()).build();
        Rent rent4 = new RentBuilder().customerId(c4.getId()).bookId(b3.getId()).build();
        
        manager.addRent(rent);
        manager.addRent(rent2);
        manager.addRent(rent3);
        manager.addRent(rent4);
        manager.deleteRent(rent);
        manager.deleteRent(rent3);
        
        assertThat(manager.getAllRents()).usingFieldByFieldElementComparator()
                .containsOnly(rent2,rent4);
        assertThat(manager.getRentsByBook(b2))
                .usingFieldByFieldElementComparator().containsOnly(rent2);
        assertThat(manager.getRentsByBook(b3))
                .usingFieldByFieldElementComparator().containsOnly(rent4);
        assert(manager.getAllRents().size() == 2);
    }
}
