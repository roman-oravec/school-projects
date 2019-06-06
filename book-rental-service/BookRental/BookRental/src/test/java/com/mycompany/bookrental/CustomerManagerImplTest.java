/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;

import com.mycompany.common.DBUtils;
import com.mycompany.common.IllegalEntityException;
import com.mycompany.common.ServiceFailureException;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Viki
 */
public class CustomerManagerImplTest {
    
    private CustomerManagerImpl manager;
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
        DBUtils.executeSqlScript(ds, CustomerManager.class.getResource("createTables.sql"));
        manager = new CustomerManagerImpl();
        manager.setDataSource(ds);        
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, CustomerManager.class.getResource("dropTables.sql"));
    }
    
    private CustomerBuilder sampleBoyCustomerBuilder() {
        return new CustomerBuilder()
                .name("Roman O")
                .eMail("roman@xy.com")
                .membershipPaid(true);
    }
    
    public CustomerBuilder sampleGirlCustomerBuilder(){
        return new CustomerBuilder()
                .name("Viktoria S")
                .eMail("viktoria@xy.com")
                .membershipPaid(false);
    }

    //--------------------------------------------------------------------------
    // Tests for operations for creating and fetching customer
    //--------------------------------------------------------------------------
    
    @Test
    public void addCustomer() {
        Customer customer = sampleBoyCustomerBuilder().build();
        manager.addCustomer(customer);
        
        Long customerId = customer.getId();
        assertThat(manager.getCustomerById(customerId))
                .isNotSameAs(customer)
                .isEqualToComparingFieldByField(customer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullCustomer(){
        manager.addCustomer(null);
    }
    
    @Test
    public void addCustomerWithExistingId() {
        Customer customer = sampleBoyCustomerBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.addCustomer(customer);
    }
    
    public void addCustomerWithNullName(){
        Customer customer = sampleBoyCustomerBuilder()
                .name(null)
                .build();
        assertThatThrownBy(() -> manager.addCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }
    
    public void addCustomerWithNullEmail() {
        Customer customer = sampleBoyCustomerBuilder()
                .eMail(null)
                .build();
        assertThatThrownBy(() -> manager.addCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }
    

    @Test
    public void getAllCustomers() {
        assertThat(manager.getAllCustomers()).isEmpty();
        
        Customer roman = sampleBoyCustomerBuilder().build();
        Customer viktoria = sampleGirlCustomerBuilder().build();
        
        manager.addCustomer(roman);
        manager.addCustomer(viktoria);
        
        assertThat(manager.getAllCustomers())
                .usingFieldByFieldElementComparator()
                .containsOnly(roman,viktoria);
        assert(manager.getAllCustomers().size() == 2);
    }

    @Test
    public void getCustomersByName() {
        Customer roman = sampleBoyCustomerBuilder().build();
        Customer viktoria = sampleGirlCustomerBuilder().build();
        
        manager.addCustomer(roman);
        manager.addCustomer(viktoria);
        
        assertThat(manager.getCustomersByName("Roman O"))
                .usingFieldByFieldElementComparator()
                .containsOnly(roman);
        assert(manager.getCustomersByName("Roman O").size() == 1);
    }
    
    //--------------------------------------------------------------------------
    // Tests for CustomerManager.updateCustomer(Customer) operation
    //--------------------------------------------------------------------------
    
    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }
    
    private void testUpdateCustomer(Operation<Customer> updateOperation) {
        Customer customerForUpdate = sampleBoyCustomerBuilder().build();
        Customer anotherCustomer = sampleGirlCustomerBuilder().build();
        manager.addCustomer(customerForUpdate);
        manager.addCustomer(anotherCustomer);
        
        updateOperation.callOn(customerForUpdate);
        
        manager.updateCustomer(customerForUpdate);
        assertThat(manager.getCustomerById(customerForUpdate.getId()))
                .isEqualToComparingFieldByField(customerForUpdate);
        assertThat(manager.getCustomerById(anotherCustomer.getId()))
                .isEqualToComparingFieldByField(anotherCustomer);
    }
    
    @Test
    public void updateName() {
        testUpdateCustomer((customer) -> customer.setName("Roman P"));
    }
    
    public void updateEmail() {
        testUpdateCustomer((customer) -> customer.setEmail("roman@yz.com"));
    }
    
    public void updateMembership() {
        testUpdateCustomer((customer) -> customer.setMembership(false));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateNullCustomer() {
        manager.updateCustomer(null);
    }
    
    @Test
    public void updateCustomerWithNullId() {
        Customer customer = sampleBoyCustomerBuilder().id(null).build();
        expectedException.expect(com.mycompany.common.ValidationException.class);
        manager.updateCustomer(customer);
    }
    
    @Test
    public void updateNonExistingCustomer(){
        Customer customer = sampleBoyCustomerBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.updateCustomer(customer);
    }
    
    @Test
    public void updateNonExistingNameCustomer() {
        Customer customer = sampleBoyCustomerBuilder().name("Zdeno N").build();
        expectedException.expect(com.mycompany.common.ValidationException.class);
        manager.updateCustomer(customer);   
    }
    
    //--------------------------------------------------------------------------
    // Tests for BookManager.deleteBook(Book) operation
    //--------------------------------------------------------------------------
    
    @Test
    public void deleteCustomer() {
        Customer roman = sampleBoyCustomerBuilder().build();
        Customer viktoria = sampleGirlCustomerBuilder().build();
        manager.addCustomer(viktoria);
        manager.addCustomer(roman);
        
        assertThat(manager.getCustomerById(roman.getId())).isNotNull();
        assertThat(manager.getCustomerById(viktoria.getId())).isNotNull();
        
        manager.deleteCustomer(roman);
        assertThat(manager.getCustomerById(roman.getId())).isNull();
        assertThat(manager.getCustomerById(viktoria.getId())).isNotNull();
        assert(manager.getAllCustomers().size() == 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteNullCustomer() {
        manager.deleteCustomer(null);
    }
    
    @Test
    public void deleteCustomerWithZeroId() {
        Customer customer = sampleBoyCustomerBuilder().id(0L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteCustomer(customer);
    }
    
    @Test
    public void deleteNonExistingCustomer(){
        Customer customer = sampleBoyCustomerBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteCustomer(customer);
    }
    
    @Test
    public void deleteNonExistingNameCustomer(){
        Customer customer = sampleBoyCustomerBuilder().name("Zdeno N").build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteCustomer(customer);
    }
    
    //--------------------------------------------------------------------------
    // Tests if CustomerManager methods throws ServiceFailureException in case of
    // DB operation failure
    //--------------------------------------------------------------------------
    
    @Test
    public void createBookWithSqlExceptionThrown() throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        
        manager.setDataSource(failingDataSource);
        Customer customer = sampleBoyCustomerBuilder().build();
        assertThatThrownBy(() -> manager.addCustomer(customer))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }
    
    private void testExpectedServiceFailureException(CustomerManagerImplTest.Operation<CustomerManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        manager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.callOn(manager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }
    
    @Test
    public void updateBookWithSqlExceptionThrown() throws SQLException {
        Customer customer = sampleBoyCustomerBuilder().build();
        manager.addCustomer(customer);
        testExpectedServiceFailureException((customerManager) -> customerManager.updateCustomer(customer));
    }
    
     @Test
    public void getBookWithSqlExceptionThrown() throws SQLException {
        Customer customer = sampleBoyCustomerBuilder().build();
        manager.addCustomer(customer);
        testExpectedServiceFailureException((customerManager) -> customerManager.getCustomerById(customer.getId()));
    }

    @Test
    public void deleteBookWithSqlExceptionThrown() throws SQLException {
        Customer customer = sampleBoyCustomerBuilder().build();
        manager.addCustomer(customer);
        testExpectedServiceFailureException((customerManager) -> customerManager.deleteCustomer(customer));
    }

    @Test
    public void getAllBooksWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((customerManager) -> customerManager.getAllCustomers());
    }
}
