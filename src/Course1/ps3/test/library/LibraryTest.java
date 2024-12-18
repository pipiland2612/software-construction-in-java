package Course1.ps3.test.library;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import Course1.ps3.src.library.Book;
import Course1.ps3.src.library.BookCopy;
import Course1.ps3.src.library.Library;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] {
            "Course1.ps3.src.library.SmallLibrary",
            "Course1.ps3.src.library.BigLibrary"
        };
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Testing strategy
     * ==================
     *
     * buy():
     *  A new bookcopy
     *  already existing bookcopy
     * checkout():
     *  available copies - 1, n
     * checkIn():
     *  available copies - 1, n
     * isAvailable():
     *  Buy - before and after
     *  Checkout - before and after
     *  Checkin - before and after
     * allCopies():
     *  copies - 0, 1 and n
     *  Checkout - before and after
     *  Checkin - before and after
     * availableCopies():
     *  copies - 0, 1 and n
     *  Checkout - before and after
     *  Checkin - before and after
     * find():
     *  title - case sensitivity
     *  same book copy and different book copies
     *  authors - case sensitivity
     *  searched author's position in the author's list
     *  date ordering
     * lose():
     *  available copies - 1, n
     *
     */

    Library library;
    Book book;
    BookCopy copy;

    @Before
    public void setup() {
        library = makeLibrary();
        book = new Book("What", List.of("Arthur"), 2009);
    }

    @Test
    public void testBuyNewBookCopy() {
        BookCopy bookCopy = library.buy(book);

        assertTrue(library.isAvailable(bookCopy));
        assertEquals(1, library.allCopies(book).size());
        assertEquals(1, library.availableCopies(book).size());
    }

    @Test
    public void testBuyExistingCopy() {
        BookCopy bookCopy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        assertTrue(library.isAvailable(bookCopy));
        assertTrue(library.isAvailable(anotherCopy));

        assertEquals(2, library.allCopies(book).size());
        assertEquals(2, library.availableCopies(book).size());
    }

    @Test
    public void testCheckOutSingleBookAvailable() {
        BookCopy copy = library.buy(book);

        assertEquals(1, library.allCopies(book).size());
        assertEquals(1, library.availableCopies(book).size());

        library.checkout(copy);
        assertEquals(1, library.allCopies(book).size());
        assertEquals(0, library.availableCopies(book).size());
    }

    @Test
    public void testCheckOutMoreThanBookAvailable() {
        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        assertEquals(2, library.allCopies(book).size());
        assertEquals(2, library.availableCopies(book).size());

        library.checkout(copy);
        assertEquals(2, library.allCopies(book).size());
        assertEquals(1, library.availableCopies(book).size());

        library.checkout(anotherCopy);
        assertEquals(2, library.allCopies(book).size());
        assertEquals(0, library.availableCopies(book).size());
    }

    @Test
    public void testCheckInSingleCopyAvailable() {
        BookCopy copy = library.buy(book);
        library.checkout(copy);

        assertEquals(1, library.allCopies(book).size());
        assertEquals(0, library.availableCopies(book).size());

        library.checkin(copy);
        assertEquals(1, library.allCopies(book).size());
        assertEquals(1, library.availableCopies(book).size());
    }

    @Test
    public void testCheckInMultipleCopiesAvailable() {
        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);
        library.checkout(copy);

        assertEquals(1, library.availableCopies(book).size());
        assertEquals(2, library.allCopies(book).size());

        library.checkin(copy);
        assertEquals(2, library.availableCopies(book).size());
        assertEquals(2, library.allCopies(book).size());


        library.checkout(anotherCopy);
        library.checkin(anotherCopy);
        assertEquals(2, library.availableCopies(book).size());
        assertEquals(2, library.allCopies(book).size());
    }

    @Test
    public void testIsAvailableBeforeAndAfterBuy() {
        BookCopy copy = null;
        assertFalse(library.isAvailable(copy));
        copy = library.buy(book);
        assertTrue(library.isAvailable(copy));
    }

    @Test
    public void testIsAvailableBeforeAndAfterCheckout() {
        BookCopy copy = library.buy(book);
        assertTrue(library.isAvailable(copy));

        library.checkout(copy);
        assertFalse(library.isAvailable(copy));
    }

    @Test
    public void testIsAvailableBeforeAndAfterCheckin() {
        BookCopy copy = library.buy(book);

        library.checkout(copy);
        assertFalse(library.isAvailable(copy));

        library.checkin(copy);
        assertTrue(library.isAvailable(copy));
    }

    @Test
    public void testAllCopiesEmptySet() {
        assertEquals(Collections.emptySet(), library.allCopies(book));
    }

    @Test
    public void testAllCopiesSingletonSet() {
        BookCopy copy = library.buy(book);
        Set<BookCopy> availableCopies = new HashSet<>(Collections.singletonList(copy));
        assertEquals(availableCopies, library.allCopies(book));
    }

    @Test
    public void testAllCopiesMoreThanOneCopy() {
        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        Set<BookCopy> availableCopies = new HashSet<>(Arrays.asList(copy, anotherCopy));
        assertEquals(availableCopies, library.allCopies(book));
    }

    @Test
    public void testAllCopiesBeforeAndAfterCheckout() {
        BookCopy copy = library.buy(book);
        Set<BookCopy> availableCopies = new HashSet<>(Collections.singletonList(copy));

        assertEquals(availableCopies, library.allCopies(book));
        library.checkout(copy);
        assertEquals(availableCopies, library.allCopies(book));
    }

    @Test
    public void testAllCopiesBeforeAndAfterCheckin() {
        BookCopy copy = library.buy(book);
        Set<BookCopy> availableCopies = new HashSet<>(Collections.singletonList(copy));

        library.checkout(copy);
        assertEquals(availableCopies, library.allCopies(book));
        library.checkin(copy);
        assertEquals(availableCopies, library.allCopies(book));
    }

    @Test
    public void testAvailableCopiesEmptySet() {
        assertEquals(Collections.emptySet(), library.availableCopies(book));
    }

    @Test
    public void testAvailableCopiesSingletonSet() {
        BookCopy copy = library.buy(book);
        Set<BookCopy> availableCopies = new HashSet<>(Collections.singletonList(copy));
        assertEquals(availableCopies, library.availableCopies(book));
    }

    @Test
    public void testAvailableCopiesMoreThanOneCopy() {
        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        Set<BookCopy> availableCopies = new HashSet<>(Arrays.asList(copy, anotherCopy));
        assertEquals(availableCopies, library.availableCopies(book));
    }


    @Test
    public void testAvailableCopiesBeforeAndAfterCheckout() {
        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        Set<BookCopy> availableCopies = new HashSet<>(Arrays.asList(copy, anotherCopy));
        assertEquals(availableCopies, library.availableCopies(book));

        library.checkout(copy);
        availableCopies.remove(copy);
        assertEquals(availableCopies, library.availableCopies(book));

        library.checkout(anotherCopy);
        assertEquals(Collections.emptySet(), library.availableCopies(book));
    }

    @Test
    public void testAvailableCopiesBeforeAndAfterCheckin() {
        BookCopy copy = library.buy(book);

        Set<BookCopy> availableCopies = new HashSet<>(Collections.singletonList(copy));

        library.checkout(copy);
        availableCopies.remove(copy);
        assertEquals(availableCopies, library.availableCopies(book));

        library.checkin(copy);
        availableCopies.add(copy);
        assertEquals(availableCopies, library.availableCopies(book));
    }


    @Test
    public void testFindExactTitleMatch() {
        BookCopy copy = library.buy(book);
        Book anotherBook = new Book("What", List.of("John"), 2004);
        BookCopy anotherBookCopy = library.buy(anotherBook);

        List<Book> expectedBooks = Arrays.asList(book, anotherBook);
        List<Book> results = library.find(anotherBook.getTitle());

        assertEquals(2, results.size());
        assertTrue(results.contains(book));
        assertTrue(results.contains(anotherBook));
    }

    @Test
    public void testFindExactTitleMatchSameBook() {
        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        List<Book> expectedBooks = Collections.singletonList(book);
        assertEquals(1, library.find(book.getAuthors().get(0)).size());
        assertEquals(expectedBooks, library.find(book.getTitle()));
    }

    @Test
    public void testFindInexactTitleMatch() {
        library.buy(book);
        assertEquals(Collections.emptyList(), library.find(book.getTitle().toLowerCase()));
        assertEquals(Collections.emptyList(), library.find(book.getTitle().toUpperCase()));
        assertEquals(Collections.emptyList(), library.find("WhAT"));
    }

    @Test
    public void testFindExactAuthorMatch() {
        Book anotherBook = new Book("Nothing", List.of("Arthur"), 2004);
        Book yetAnotherBook = new Book("Locked", Arrays.asList("Jon", "Joseph"), 2000);

        BookCopy copy = library.buy(book);
        BookCopy anotherBookCopy = library.buy(anotherBook);
        BookCopy yetAnotherCopy = library.buy(yetAnotherBook);

        List<Book> expectedBooks = Arrays.asList(book, anotherBook);
        List<Book> actualBooks = library.find(anotherBook.getAuthors().get(0));
        assertTrue(actualBooks.contains(book));
        assertTrue(actualBooks.contains(anotherBook));
    }

    @Test
    public void testFindExactAuthorMatchSameBook() {
        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        List<Book> expectedBooks = Collections.singletonList(book);
        assertEquals(1, library.find(book.getAuthors().get(0)).size());
        assertEquals(expectedBooks, library.find(book.getAuthors().get(0)));
    }

    @Test
    public void testFindInexactAuthor() {
        library.buy(book);
        String author = book.getAuthors().get(0);
        assertEquals(Collections.emptyList(), library.find(author.toLowerCase()));
        assertEquals(Collections.emptyList(), library.find(author.toUpperCase()));
        assertEquals(Collections.emptyList(), library.find("ARthUr"));
    }

    @Test
    public void testFindAuthorMatchFirstPosition() {
        BookCopy copy = library.buy(book);

        List<Book> matches = Collections.singletonList(book);
        assertEquals(matches, library.find(book.getAuthors().get(0)));
    }

    @Test
    public void testFindAuthorMatchLastPosition() {
        Book newBook = new Book("How to fly", Arrays.asList("Vic", "Kumar", "Arthur"), 2014);
        BookCopy newBookCopy = library.buy(newBook);

        List<Book> matches = List.of(newBook);
        assertEquals(matches, library.find(newBook.getAuthors().get(2)));
    }

    @Test
    public void testFindSameTitleAuthorDifferentPublicationDates() {
        BookCopy copy = library.buy(book);
        Book anotherBook = new Book("What", List.of("Arthur"), 2015);
        BookCopy anotherBookCopy = library.buy(anotherBook);

        List<Book> matches = Arrays.asList(anotherBook, book);
        assertEquals(matches, library.find("What"));
    }

    @Test
    public void testLoseSingleCopyAvailable() {

        BookCopy copy = library.buy(book);
        Set<BookCopy> copies = new HashSet<>(Collections.singletonList(copy));

        assertTrue(library.isAvailable(copy));
        assertEquals(copies, library.allCopies(book));
        assertEquals(copies, library.availableCopies(book));

        library.lose(copy);

        assertFalse(library.isAvailable(copy));
        assertEquals(Collections.emptySet(), library.allCopies(book));
        assertEquals(Collections.emptySet(), library.availableCopies(book));
    }


    @Test
    public void testLoseMultipleCopiesAvailable() {

        BookCopy copy = library.buy(book);
        BookCopy anotherCopy = library.buy(book);

        Set<BookCopy> copies = new HashSet<>(Arrays.asList(copy, anotherCopy));

        assertTrue(library.isAvailable(copy));
        assertEquals(copies, library.allCopies(book));
        assertEquals(copies, library.availableCopies(book));

        library.lose(copy);
        copies.remove(copy);

        assertFalse(library.isAvailable(copy));
        assertEquals(copies, library.allCopies(book));
        assertEquals(copies, library.availableCopies(book));
    }

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}