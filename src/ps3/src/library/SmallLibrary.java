package ps3.src.library;

import java.util.*;

/**
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // rep
    private Set<BookCopy> inLibrary;
    private Set<BookCopy> checkedOut;

    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set

    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out

    public SmallLibrary() {
        inLibrary = new HashSet<>();
        checkedOut = new HashSet<>();
        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        assert Collections.disjoint(inLibrary, checkedOut);
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy copy = new BookCopy(book);
        inLibrary.add(copy);
        checkRep();
        return copy;
    }

    @Override
    public void checkout(BookCopy copy) {
        if (!inLibrary.contains(copy))
            throw new IllegalArgumentException("Book is not available");
        inLibrary.remove(copy);
        checkedOut.add(copy);
        checkRep();
    }

    @Override
    public void checkin(BookCopy copy) {
        if (!checkedOut.contains(copy))
            throw new IllegalArgumentException("Book was not checked out");
        checkedOut.remove(copy);
        inLibrary.add(copy);
        checkRep();
    }

    @Override
    public boolean isAvailable(BookCopy copy) {
        return inLibrary.contains(copy);
    }

    @Override
    public Set<BookCopy> allCopies(Book book) {
        Set<BookCopy> allCopies = new HashSet<>();
        for (BookCopy copy : inLibrary) {
            if (copy.getBook().equals(book)) {
                allCopies.add(copy);
            }
        }
        for (BookCopy copy : checkedOut) {
            if (copy.getBook().equals(book)) {
                allCopies.add(copy);
            }
        }
        return Collections.unmodifiableSet(allCopies);
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        Set<BookCopy> availableCopies = new HashSet<>();
        for (BookCopy copy : inLibrary) {
            if (copy.getBook().equals(book)) {
                availableCopies.add(copy);
            }
        }
        return Collections.unmodifiableSet(availableCopies);
    }

    @Override
    public List<Book> find(String query) {
        Set<Book> matchingBooks = new HashSet<>();

        findMatchingBooks(inLibrary, matchingBooks, query);
        findMatchingBooks(checkedOut, matchingBooks, query);

        List<Book> dict = new ArrayList<>(matchingBooks);
        Collections.sort(dict);

        return dict;
    }

    /**
     * Mutates matchingBooks with matches founded in the books present in
     * the set of bookCopies
     * @param bookSet the set of book copies
     * @param matchingBooks the list of matching books
     * @param query the search term
     */
    private void findMatchingBooks(Set<BookCopy> bookSet, Set<Book> matchingBooks, String query) {
        for(BookCopy copy: bookSet) {
            Book book = copy.getBook();
            if(!matchingBooks.contains(book) && (isTitleMatching(book, query) || isAuthorMatching(book, query)) )
                matchingBooks.add(book);
        }
        checkRep();
    }

    /**
     * Returns whether the book's title is the same as the query(case-sensitive)
     * @param book the book to query on
     * @param query the search term
     * @return true if the book's title matches query, false otherwise
     */
    private boolean isTitleMatching(Book book, String query) {
        return book.getTitle().equals(query);
    }

    /**
     * Returns whether one of the book's authors' names is
     * the same as the query(case-sensitive)
     * @param book the book to query on
     * @param query the search term
     * @return true if the one of the book's author's names matches query,
     * false otherwise
     */
    private boolean isAuthorMatching(Book book, String query) {
        for (String author: book.getAuthors()) {
            if (author.equals(query)) return true;
        }
        return false;
    }

    @Override
    public void lose(BookCopy copy) {
        if (inLibrary.contains(copy)) {
            inLibrary.remove(copy);
        } else if (checkedOut.contains(copy)) {
            checkedOut.remove(copy);
        } else {
            throw new IllegalArgumentException("Book not found in library or checked out");
        }
        checkRep();
    }

    // Uncomment the following methods if you need to implement equals and hashCode,
    // or delete them if you don't

    // @Override
    // public boolean equals(Object that) {
    //     throw new RuntimeException("not implemented yet");
    // }
    //
    // @Override
    // public int hashCode() {
    //     throw new RuntimeException("not implemented yet");
    // }
}
