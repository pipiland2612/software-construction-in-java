package Course1.ps3.src.library;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 */
public class BookCopy {

    // TODO: rep
    
    // TODO: rep invariant
    // TODO: abstraction function
    // TODO: safety from rep exposure argument
    
    public static enum Condition {
        GOOD, DAMAGED
    };
    
    /**
     * Make a new BookCopy, initially in good condition.
     * @param book the Book of which this is a copy
     */
    private final Book copy;
    private Condition condition;

    public BookCopy(Book book) {
        copy = new Book(book.getTitle(), book.getAuthors(), book.getYear());
        condition = Condition.GOOD;
        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert !copy.getTitle().isEmpty();
        assert !copy.getAuthors().isEmpty() && copy.getAuthors().stream().noneMatch(String::isEmpty);
        assert copy.getYear() >= 0;
    }
    
    /**
     * @return the Book of which this is a copy
     */
    public Book getBook() {
        return new Book(copy.getTitle(), copy.getAuthors(), copy.getYear());
    }
    
    /**
     * @return the condition of this book copy
     */
    public Condition getCondition() {
        return this.condition;
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is returned and a librarian inspects it.
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    
    /**
     * @return human-readable representation of this book that includes book.toString()
     *    and the words "good" or "damaged" depending on its condition
     */
    public String toString() {
        String cond = switch (this.condition){
            case GOOD -> "good";
            case DAMAGED -> "damaged";
        };
        return copy.toString() + ". In " + cond + " condition";
    }

    // uncomment the following methods if you need to implement equals and hashCode,
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


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
