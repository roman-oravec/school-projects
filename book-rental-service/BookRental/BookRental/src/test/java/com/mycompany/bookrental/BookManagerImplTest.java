/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bookrental;
import com.mycompany.common.*;
import com.mycompany.common.ValidationException;
import java.sql.SQLException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import javax.sql.DataSource;
import org.junit.After;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author roman
 */
public class BookManagerImplTest {
    
    private BookManagerImpl manager;
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
        DBUtils.executeSqlScript(ds,BookManager.class.getResource("createTables.sql"));
        manager = new BookManagerImpl();
        manager.setDataSource( ds);
    }
    
    /**
     *
     * @throws SQLException
     */
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript( ds,BookManager.class.getResource("dropTables.sql"));
    }
    
    private BookBuilder sampleWiedzminBookBuilder(){
        return new BookBuilder()
                .name("Wiedzmin: Czas pogardy")
                .author("Andrzsrej Sapkowski")
                .copies(2)
                .pages(352);
        
    }
    
    private BookBuilder sampleBarvaKouzelBookBuilder(){
        return new BookBuilder()
                .name("Barva Kouzel")
                .author("Terry Pratchett")
                .copies(4)
                .pages(290);
    }
    
    @Test
    public void addBook(){
        Book book = sampleWiedzminBookBuilder().build();
        manager.addBook(book);
        
        Long bookId = book.getId();
        assertThat(manager.getBookById(bookId))
                .isNotSameAs(book)
                .isEqualToComparingFieldByField(book);
                
    }
    
    @Test
    public void findAllBooks(){
        assertThat(manager.getAllBooks()).isEmpty();
        
        Book wiedzmin = sampleWiedzminBookBuilder().build();
        Book barvaKouzel = sampleBarvaKouzelBookBuilder().build();
        
        manager.addBook(wiedzmin);
        manager.addBook(barvaKouzel);
        
        assertThat(manager.getAllBooks())
                .usingFieldByFieldElementComparator()
                .containsOnly(wiedzmin, barvaKouzel);
        assert(manager.getAllBooks().size() == 2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullBook(){
        manager.addBook(null);
    }
    
    @Test
    public void createBookWithExistingId(){
        Book book = sampleWiedzminBookBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.addBook(book);
    }
    
    @Test
    public void createBookWithNullName(){
        Book book = sampleWiedzminBookBuilder()
                .name(null)
                .build();
        assertThatThrownBy(()-> manager.addBook(book))
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void createBookWithNullAuthor(){
        Book book = sampleBarvaKouzelBookBuilder().author(null).build();
        assertThatThrownBy(()-> manager.addBook(book))
                .isInstanceOf(ValidationException.class);
    }
    
    @Test 
    public void createBookWithZeroPages(){
        Book book = sampleWiedzminBookBuilder()
                .pages(0)
                .build();
        assertThatThrownBy(() -> manager.addBook(book))
                .isInstanceOf(ValidationException.class);
    }
        
    @Test 
    public void createBookWithLessThanZeroPages(){      
        Book book = sampleBarvaKouzelBookBuilder()
                .pages(-2)
                .build();
    assertThatThrownBy(() -> manager.addBook(book))
                .isInstanceOf(ValidationException.class);
    }
    
    @Test 
    public void createBookWithZeroCopies(){
        Book book = sampleWiedzminBookBuilder()
                .copies(0)
                .build();
        assertThatThrownBy(() -> manager.addBook(book))
                .isInstanceOf(ValidationException.class);
    }
    
    @Test 
    public void createBookWithLessThanZeroCopies(){      
        Book book = sampleBarvaKouzelBookBuilder()
                .copies(-2)
                .build();
    assertThatThrownBy(() -> manager.addBook(book))
                .isInstanceOf(ValidationException.class);
    }
    
    //--------------------------------------------------------------------------
    // Tests for BookManager.updateBook(Book) operation
    //--------------------------------------------------------------------------
    
    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }
    
    private void testUpdateBook(Operation<Book> updateOperation) {
        Book bookForUpdate = sampleWiedzminBookBuilder().build();
        Book anotherBook = sampleBarvaKouzelBookBuilder().build();
        
        manager.addBook(bookForUpdate);
        manager.addBook(anotherBook);
        
        updateOperation.callOn(bookForUpdate);
        
        manager.updateBook(bookForUpdate);
        assertThat(manager.getBookById(bookForUpdate.getId()))
                .isEqualToComparingFieldByField(bookForUpdate);
        assertThat(manager.getBookById(anotherBook.getId()))
                .isEqualToComparingFieldByField(anotherBook);
    }
    
    @Test
    public void updateAuthor(){
        testUpdateBook((book) -> book.setAuthor("Dimitrij Glukhovski"));
    }
    
    @Test
    public void updatePages(){
        testUpdateBook((book) -> book.setPages(300));
    }
    
    public void updateName(){
        testUpdateBook((book) -> book.setName("New name"));
    }
    
    public void updateCopies(){
        testUpdateBook((book) -> book.setCopies(9));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateNullBook(){
        manager.updateBook(null);
    }
    
    @Test
    public void updateBookWithNullId(){
        Book book = sampleBarvaKouzelBookBuilder().id(null).build();
        expectedException.expect(ValidationException.class);
        manager.updateBook(book);
    }
    
    @Test
    public void updateNonExistingBook(){
        Book book = sampleWiedzminBookBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.updateBook(book);
    }
    
    @Test
    public void updateBookWithNullName(){
        Book book = sampleWiedzminBookBuilder().build();
        manager.addBook(book);
        book.setName(null);
        expectedException.expect(ValidationException.class);
        manager.updateBook(book);
    }
    
    @Test
    public void updateBookWithZeroPages(){
        Book book = sampleWiedzminBookBuilder().build();
        manager.addBook(book);
        book.setPages(0);
        expectedException.expect(ValidationException.class);
        manager.updateBook(book);
    }
    
    @Test
    public void updateBookWithZeroCopies(){
        Book book = sampleWiedzminBookBuilder().build();
        manager.addBook(book);
        book.setCopies(0);
        expectedException.expect(ValidationException.class);
        manager.updateBook(book);
    }
    //--------------------------------------------------------------------------
    // Tests for BookManager.deleteBook(Book) operation
    //--------------------------------------------------------------------------

    @Test 
    public void deleteBook(){
        Book wiedzmin = sampleWiedzminBookBuilder().build();
        Book barva = sampleBarvaKouzelBookBuilder().build();
        manager.addBook(wiedzmin);
        manager.addBook(barva);
        
        assertThat(manager.getBookById(wiedzmin.getId())).isNotNull();
        assertThat(manager.getBookById(barva.getId())).isNotNull();
        
        manager.deleteBook(wiedzmin);
        
        assertThat(manager.getBookById(wiedzmin.getId())).isNull();
        assertThat(manager.getBookById(barva.getId())).isNotNull();
        assert((manager.getAllBooks()).size() == 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteNullBook(){
        manager.deleteBook(null);
    }
    
    @Test 
    public void deleteNonExistingBook(){
        Book book = sampleWiedzminBookBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteBook(book);
    }
    
    //--------------------------------------------------------------------------
    // Tests if BookManager methods throws ServiceFailureException in case of
    // DB operation failure
    //--------------------------------------------------------------------------
    
    @Test
    public void createBookWithSqlExceptionThrown() throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        
        manager.setDataSource(failingDataSource);
        Book book = sampleWiedzminBookBuilder().build();
        assertThatThrownBy(() -> manager.addBook(book))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }
    
    private void testExpectedServiceFailureException(Operation<BookManager> operation) throws SQLException {
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
        Book book = sampleWiedzminBookBuilder().build();
        manager.addBook(book);
        testExpectedServiceFailureException((bookManager) -> bookManager.updateBook(book));
    }

    @Test
    public void getBookWithSqlExceptionThrown() throws SQLException {
        Book book = sampleWiedzminBookBuilder().build();
        manager.addBook(book);
        testExpectedServiceFailureException((bookManager) -> bookManager.getBookById(book.getId()));
    }

    @Test
    public void deleteBookWithSqlExceptionThrown() throws SQLException {
        Book book = sampleWiedzminBookBuilder().build();
        manager.addBook(book);
        testExpectedServiceFailureException((bookManager) -> bookManager.deleteBook(book));
    }

    @Test
    public void getAllBooksWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((bookManager) -> bookManager.getAllBooks());
    }
}
