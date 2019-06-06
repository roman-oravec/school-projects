package com.mycompany.bookrental;

/**
 * This is builder for the Book class to make tests better readable.
 *
 * @author roman
 */
public class BookBuilder {

    private Long id;
    private String name;
    private String author;
    private int pages;
    private int copies;

    public BookBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public BookBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BookBuilder author(String author) {
        this.author = author;
        return this;
    }

    public BookBuilder pages(int pages) {
        this.pages = pages;
        return this;
    }

    public BookBuilder copies(int copies) {
        this.copies = copies;
        return this;
    }

    /**
     * Creates new instance of Book with configured properties.
     *
     * @return new instance of Book with configured properties.
     */
    public Book build() {
        Book book = new Book();
        book.setId(id);
        book.setName(name);
        book.setAuthor(author);
        book.setPages(pages);
        book.setCopies(copies);
        return book;
    }
}
