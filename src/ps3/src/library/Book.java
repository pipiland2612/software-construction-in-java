package ps3.src.library;

import java.util.List;
import java.util.Objects;

/**
 * Book is an immutable type representing an edition of a book -- not the physical object,
 * but the combination of words and pictures that make up a book.  Each book is uniquely
 * identified by its title, author list, and publication year.  Alphabetic case and author
 * order are significant, so a book written by "Fred" is different from a book written by "FRED".
 */
public class Book implements Comparable<Book> {

    private final String title;
    private final List<String> authors; // List is mutable, careful to not let rep exposure happen
    private final int year;

    /**
     * Make a Book.
     * @param title Title of the book. Must contain at least one non-space character.
     * @param authors Names of the authors of the book.  Must have at least one name, and each name must contain
     * at least one non-space character.
     * @param year Year when this edition was published in the conventional (Common Era) calendar.  Must be nonnegative.
     */
    public Book(String title, List<String> authors, int year) {
        this.title = title;
        this.authors = List.copyOf(authors);
        this.year = year;
        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        assert !title.isEmpty();
        assert !authors.isEmpty() && authors.stream().noneMatch(String::isEmpty);
        assert year >= 0;
    }

    /**
     * @return the title of this book
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return the authors of this book
     */
    public List<String> getAuthors() {
        return List.copyOf(this.authors);
    }

    /**
     * @return the year that this book was published
     */
    public int getYear() {
        return this.year;
    }

    /**
     * @return human-readable representation of this book that includes its title,
     *    authors, and publication year
     */
    public String toString() {
        return "Title: " + this.title + ", Author: " + this.authors + ", Year: " + this.year;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Book other)) {
            return false;
        }
        return this.title.equals(other.title) &&
               this.authors.equals(other.authors) &&
               this.year == other.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, authors, year);
    }

    @Override
    public int compareTo(Book that) {
        return Integer.compare(that.getYear(), this.getYear());
    }
}
