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

        book1 = new Book("Effective Java", List.of("Joshua Bloch"), 2018);
        copy1 = new BookCopy(book1);
    }

    @Test
    public void testConstructor() {

        assertEquals(book1.getTitle(), copy1.getBook().getTitle(), "Title should match the original book");
        assertEquals(book1.getAuthors(), copy1.getBook().getAuthors(), "Authors should match the original book");
        assertEquals(book1.getYear(), copy1.getBook().getYear(), "Year should match the original book");
        assertEquals(BookCopy.Condition.GOOD, copy1.getCondition(), "New BookCopy should have GOOD condition");
    }

    @Test
    public void testGetBookDefensiveCopy() {

        Book retrievedBook = copy1.getBook();
        assertNotSame(book1, retrievedBook, "getBook() should return a new Book object, not a reference to the original");


        List<String> authors = retrievedBook.getAuthors();
        assertThrows(UnsupportedOperationException.class, () -> authors.add("Another Author"));
    }

    @Test
    public void testSetCondition() {

        copy1.setCondition(BookCopy.Condition.DAMAGED);
        assertEquals(BookCopy.Condition.DAMAGED, copy1.getCondition(), "Condition should be DAMAGED after calling setCondition");


        copy1.setCondition(BookCopy.Condition.GOOD);
        assertEquals(BookCopy.Condition.GOOD, copy1.getCondition(), "Condition should be GOOD after calling setCondition");
    }

    @Test
    public void testToString() {

        String expectedGoodCondition = "Title :Effective Java, author [Joshua Bloch], year :2018. In good condition";
        assertEquals(expectedGoodCondition, copy1.toString(), "toString() should match the expected format");


        copy1.setCondition(BookCopy.Condition.DAMAGED);
        String expectedDamagedCondition = "Title :Effective Java, author [Joshua Bloch], year :2018. In damaged condition";
        assertEquals(expectedDamagedCondition, copy1.toString(), "toString() should change to reflect the damaged condition");
    }

    @Test
    public void testImmutabilityOfBookField() {

        List<String> authors = book1.getAuthors();
        assertThrows(UnsupportedOperationException.class, () -> authors.add("Another Author"),
            "Modifying the original book's authors list should not affect the BookCopy's book");


        assertEquals(List.of("Joshua Bloch"), copy1.getBook().getAuthors(),
            "The internal Book of BookCopy should not be affected by changes to the original Book");
    }

    @Test
    public void testCheckRepInvariant() {

        assertThrows(AssertionError.class, () -> new Book("", List.of("Author"), 2000),
            "Book with empty title should fail the rep invariant check");


        assertThrows(AssertionError.class, () -> new Book("Title", List.of(), 2000),
            "Book with an empty authors list should fail the rep invariant check");


        assertThrows(AssertionError.class, () -> new Book("Title", List.of("Author"), -2020),
            "Book with a negative year should fail the rep invariant check");
    }

}
