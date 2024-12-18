package Course1.ps3.test.library;

import Course1.ps3.src.library.BigLibrary;
import Course1.ps3.src.library.Book;
import Course1.ps3.src.library.BookCopy;
import Course1.ps3.src.library.Library;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for BigLibrary's stronger specs.
 */
public class BigLibraryTest {

    /*
     * NOTE: use this file only for tests of BigLibrary.find()'s stronger spec.
     * Tests of all other Library operations should be in LibraryTest.java
     */

    /*
     * Testing strategy
     * ==================
     *
     * BasicAndOrder test
     *  - empty library
     *  - one book in library, search matches title, author, partial title, partial author and nothing
     *  - >1 books in library with >1 matches and a multiple copies
     *  - 4 copies of a book with the same title and author but different years, in random order added to library,
     *          and another book with less matches (checks date ordering and numberofmatches ordering)
     *  - check alphabetical order, first title then date
     *
     * BeforeAndAfter test
     *  - BEFORE and AFTER modifiers give correct results and can be placed anywhere in the searchquery,
     *          and they can be combined.
     *  - Wrong usage of BEFORE and AFTER does not give any errors:
     *
     * Quotations test
     *  - Test quotation marks searches for exact matchings of the phrase inside the titles
     *  - same for authors
     *
     * NoCopiesAvailableOrder
     *  - One of the two copies of one book checked out, and also all (one in this case) of the copies of another book
     *          the last book should appear at the bottom and the first one should appear at usual place
     *  - Now all copies of both books are checked out, so these two should appear at the bottom while still
     *          ordered as before, and other books that appeared in results that are still available appear at top
     *  - then all copies checked back in should give normal result
     *
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }



    @Test
    public void testFindBasicAndOrder() {
        // empty library should give no matches
        Library library = new BigLibrary();
        assertEquals(0, library.find("Harry Potter").size());

        // one book in library, search matches title, author, partial title, partial author and nothing
        Book book1 = new Book("Harry Potter", List.of("J.K. Rowling"), 1997);
        library.buy(book1);
        assertEquals(List.of(book1), library.find("Harry Potter"));
        assertEquals(List.of(book1), library.find("J.K. Rowling"));
        assertEquals(List.of(book1), library.find("Harry"));
        assertEquals(List.of(book1), library.find("Rowling"));
        assertEquals(0, library.find("Henk").size());

        // >1 books in library with >1 matches and a multiple copies
        Book book2 = new Book("Song of Ice and Fire", List.of("George R.R. Martin"), 1995);
        Book book3 = new Book("Game of Thrones", List.of("George R.R. Martin"), 1999);
        library.buy(book2);
        library.buy(book2);
        library.buy(book3);
        List<Book> searchResult = library.find("George R.R. Martin");
        assertEquals(2, searchResult.size()); // book2 should only appear once
        assertEquals(new HashSet<>(searchResult), new HashSet<>(List.of(book2, book3))); // First convert the set!

        // now 4 copies of a book with the same title and author but different years, in random order added to library
        // and another book with less matches, and a two books with same number of matches and author, but title has
        // lower and higher alphabetical place
        Book book1_2 = new Book("Harry Potter", List.of("J.K. Rowling"), 1998);
        Book book1_3 = new Book("Harry Potter", List.of("J.K. Rowling"), 1999);
        Book book1_4 = new Book("Harry Potter", List.of("J.K. Rowling"), 2001);
        Book book4 = new Book("Harry Pasta", List.of("Fredje"), 2000);
        Book book5 = new Book("ZZZ Harry Potter", List.of("J.K. Rowling"), 2000);
        Book book6 = new Book("AAA Harry Potter", List.of("J.K. Rowling"), 2000);
        library.buy(book1_4);
        library.buy(book4);
        library.buy(book6);
        library.buy(book5);
        library.buy(book1_2);
        library.buy(book1_3);
        assertEquals(library.find("Harry Potter"), List.of(book6,book1_4, book1_3, book1_2, book1,book5,book4));
    }


    @Test
    public void testFindBeforeAfter() {
        Library library = new BigLibrary();
        Book book1_1 = new Book("Harry Potter", List.of("J.K. Rowling"), 1997);
        Book book1_2 = new Book("Harry Potter", List.of("J.K. Rowling"), 1998);
        Book book1_3 = new Book("Harry Potter", List.of("J.K. Rowling"), 1999);
        Book book1_4 = new Book("Harry Potter", List.of("J.K. Rowling"), 2001);
        library.buy(book1_1);
        library.buy(book1_4);
        library.buy(book1_2);
        library.buy(book1_3);

        // BEFORE and AFTER modifiers give correct results and can be placed anywhere in the searchquery, and they
        // can be combined.
        assertEquals(library.find("Harry Potter AFTER 2000"), List.of(book1_4));
        assertEquals(library.find("Harry AFTER 1998 Potter"), List.of(book1_4, book1_3));
        assertEquals(library.find("Harry Potter BEFORE 2000"), List.of(book1_3, book1_2, book1_1));
        assertEquals(library.find("Harry BEFORE 1999 Potter"), List.of(book1_2, book1_1));
        assertEquals(library.find("AFTER 1997 Harry BEFORE 1999 Potter"), List.of(book1_2));
        // Wrong usage of BEFORE and AFTER does not give any errors:
        assertTrue(library.find("Harry BEFORE Potter AFTER a2000").size() > -1);
    }


    @Test
    public void testFindQuotations() {
        Library library = new BigLibrary();
        Book book1 = new Book("Harry Potter", List.of("J.K. Rowling"), 1997);
        Book book2 = new Book("Azkaban", List.of("J.K.F. Rowling"), 1998);
        Book book3 = new Book("Harry and Potter", List.of("J.K.F. Rowling"), 2001);
        library.buy(book1);
        library.buy(book3);
        library.buy(book2);

        // Test quotation marks searches for exact matchings of the phrase inside the titles
        assertEquals(library.find("Harry Potter"), List.of(book1,book3)); // note the alphabetical ordering of titles
        assertEquals(library.find("\"Harry Potter\""), List.of(book1));
        // same for authors
        assertEquals(3, library.find("J.K. Rowling").size());
        assertEquals(library.find("\"J.K. Rowling\""), List.of(book1));
    }

    @Test
    public void testFindNoCopiesAvailableOrder() {
        Library library = new BigLibrary();
        Book book1_1 = new Book("Harry Potter", List.of("J.K. Rowling"), 1997);
        Book book1_2 = new Book("Harry Potter", List.of("J.K. Rowling"), 1998);
        Book book1_3 = new Book("Harry Potter", List.of("J.K. Rowling"), 1999);
        Book book1_4 = new Book("Harry Potter", List.of("J.K. Rowling"), 2001);
        library.buy(book1_1);
        BookCopy copy1_2 = library.buy(book1_2);
        BookCopy copy1_4_1 = library.buy(book1_4);
        BookCopy copy1_4_2 = library.buy(book1_4);
        library.buy(book1_3);
        library.buy(book1_3);

        // All copies are available, we should get the normal result
        assertEquals(library.find("Harry Potter"), List.of(book1_4, book1_3, book1_2, book1_1));

        // Now one of the two copies of 1_4 are checked out, and also all (one in this case) of the copies of 1_2,
        // so 1_2 should appear at the bottom
        library.checkout(copy1_4_2);
        library.checkout(copy1_2);
        assertEquals(library.find("Harry Potter"), List.of(book1_4, book1_3, book1_1, book1_2));

        // Now all copies of both 1_4 and 1_2 are checked out, so these should appear at the bottom while still
        // ordered as before
        library.checkout(copy1_4_1);
        assertEquals(library.find("Harry Potter"), List.of(book1_3, book1_1, book1_4, book1_2));

        // Now all copies are brought back
        library.checkin(copy1_2);
        library.checkin(copy1_4_1);
        library.checkin(copy1_4_2);
        assertEquals(library.find("Harry Potter"), List.of(book1_4, book1_3, book1_2, book1_1));
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}