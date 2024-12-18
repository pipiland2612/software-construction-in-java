package ps3.test.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ps3.src.library.Book;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book1;
    private Book book2;
    private Book book3;

    @BeforeEach
    public void setUp() {
        book1 = new Book("The Great Gatsby", List.of("F. Scott Fitzgerald"), 1925);
        book2 = new Book("To Kill a Mockingbird", List.of("Harper Lee"), 1960);
        book3 = new Book("The Great Gatsby", List.of("F. Scott Fitzgerald"), 1925); // identical to book1
    }

    /**
     * Test that the constructor creates a valid Book object.
     */
    @Test
    public void testConstructorValidInputs() {
        assertDoesNotThrow(() -> new Book("1984", List.of("George Orwell"), 1949));
    }

    /**
     * Test that the constructor throws an exception when given invalid inputs.
     */
    @Test
    public void testConstructorInvalidInputs() {
        assertThrows(AssertionError.class, () -> new Book("", List.of("Author"), 2000), "Title must not be empty");
        assertThrows(AssertionError.class, () -> new Book("Title", Collections.emptyList(), 2000), "Authors list must not be empty");
        assertThrows(AssertionError.class, () -> new Book("Title", List.of(""), 2000), "Author name must not be empty");
        assertThrows(AssertionError.class, () -> new Book("Title", List.of("Author"), -1), "Year must be nonnegative");
    }

    /**
     * Test the getTitle method.
     */
    @Test
    public void testGetTitle() {
        assertEquals("The Great Gatsby", book1.getTitle());
        assertEquals("To Kill a Mockingbird", book2.getTitle());
    }

    /**
     * Test the getAuthors method.
     */
    @Test
    public void testGetAuthors() {
        List<String> authors = book1.getAuthors();
        assertEquals(1, authors.size());
        assertEquals("F. Scott Fitzgerald", authors.get(0));
    }

    /**
     * Test that getAuthors returns a copy and does not allow mutation of the original list.
     */
    @Test
    public void testGetAuthorsImmutability() {
        List<String> authors = book1.getAuthors();
        assertThrows(UnsupportedOperationException.class, () -> authors.add("Another Author"), "Authors list should be immutable");
    }

    /**
     * Test the getYear method.
     */
    @Test
    public void testGetYear() {
        assertEquals(1925, book1.getYear());
        assertEquals(1960, book2.getYear());
    }

    /**
     * Test the toString method.
     */
    @Test
    public void testToString() {
        String expected1 = "Title :The Great Gatsby, author [F. Scott Fitzgerald], year :1925";
        String expected2 = "Title :To Kill a Mockingbird, author [Harper Lee], year :1960";
        assertEquals(expected1, book1.toString());
        assertEquals(expected2, book2.toString());
    }

    /**
     * Test the equals method.
     */
    @Test
    public void testImmutability() {
        List<String> externalAuthors = List.of("Mutable Author");
        Book book = new Book("Title", externalAuthors, 2000);
        externalAuthors.set(0, "Changed Author"); // External change should not affect the book's authors
        assertEquals("Mutable Author", book.getAuthors().get(0), "Book's internal state should not be affected by changes to external inputs");
    }
}
