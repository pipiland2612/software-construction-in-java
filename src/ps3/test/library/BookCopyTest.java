package ps3.test.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ps3.src.library.Book;
import ps3.src.library.BookCopy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookCopyTest {

    private Book book1;
    private BookCopy copy1;

    @BeforeEach
    public void setUp() {
        // Set up a fresh book and book copy for each test
        book1 = new Book("Effective Java", List.of("Joshua Bloch"), 2018);
        copy1 = new BookCopy(book1);
    }

    @Test
    public void testConstructor() {
        // Verify that a new copy has the correct book and the condition is GOOD
        assertEquals(book1.getTitle(), copy1.getBook().getTitle(), "Title should match the original book");
        assertEquals(book1.getAuthors(), copy1.getBook().getAuthors(), "Authors should match the original book");
        assertEquals(book1.getYear(), copy1.getBook().getYear(), "Year should match the original book");
        assertEquals(BookCopy.Condition.GOOD, copy1.getCondition(), "New BookCopy should have GOOD condition");
    }

    @Test
    public void testGetBookDefensiveCopy() {
        // Verify that getBook() returns a defensive copy, not the actual reference
        Book retrievedBook = copy1.getBook();
        assertNotSame(book1, retrievedBook, "getBook() should return a new Book object, not a reference to the original");

        // Modifying the retrieved copy should not affect the original
        List<String> authors = retrievedBook.getAuthors();
        assertThrows(UnsupportedOperationException.class, () -> authors.add("Another Author"));
    }

    @Test
    public void testSetCondition() {
        // Change condition from GOOD to DAMAGED
        copy1.setCondition(BookCopy.Condition.DAMAGED);
        assertEquals(BookCopy.Condition.DAMAGED, copy1.getCondition(), "Condition should be DAMAGED after calling setCondition");

        // Change condition back to GOOD
        copy1.setCondition(BookCopy.Condition.GOOD);
        assertEquals(BookCopy.Condition.GOOD, copy1.getCondition(), "Condition should be GOOD after calling setCondition");
    }

    @Test
    public void testToString() {
        // Verify the string representation of a BookCopy
        String expectedGoodCondition = "Title :Effective Java, author [Joshua Bloch], year :2018. In good condition";
        assertEquals(expectedGoodCondition, copy1.toString(), "toString() should match the expected format");

        // Change the condition and check again
        copy1.setCondition(BookCopy.Condition.DAMAGED);
        String expectedDamagedCondition = "Title :Effective Java, author [Joshua Bloch], year :2018. In damaged condition";
        assertEquals(expectedDamagedCondition, copy1.toString(), "toString() should change to reflect the damaged condition");
    }

    @Test
    public void testImmutabilityOfBookField() {
        // Verify that changes to the original book do not affect the BookCopy
        List<String> authors = book1.getAuthors();
        assertThrows(UnsupportedOperationException.class, () -> authors.add("Another Author"),
            "Modifying the original book's authors list should not affect the BookCopy's book");

        // Check if internal copy is unaffected
        assertEquals(List.of("Joshua Bloch"), copy1.getBook().getAuthors(),
            "The internal Book of BookCopy should not be affected by changes to the original Book");
    }

    @Test
    public void testCheckRepInvariant() {
        // Check for title being empty (should fail)
        assertThrows(AssertionError.class, () -> new Book("", List.of("Author"), 2000),
            "Book with empty title should fail the rep invariant check");

        // Check for authors list being empty (should fail)
        assertThrows(AssertionError.class, () -> new Book("Title", List.of(), 2000),
            "Book with an empty authors list should fail the rep invariant check");

        // Check for year being negative (should fail)
        assertThrows(AssertionError.class, () -> new Book("Title", List.of("Author"), -2020),
            "Book with a negative year should fail the rep invariant check");
    }

}
