package Course1.ps3.src.library;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigLibrary implements Library {

    // rep
    private final Map<Book, Set<BookCopy>> allBooks;
    private final Map<Book, Set<BookCopy>> inLibrary;
    private final Map<Book, Set<BookCopy>> checkedOut;

    // rep invariant:
    //      All bookcopies in allBooks are the same as the union of all bookcopies in inLibrary and checkedOut
    //      Also the sets all bookcopies in inLibrary and in checkedOut are disjunct
    //
    // abstraction function:
    //    represents the collection of books allBooks, with possible copies of the same book as values of this map,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    //
    // safety from rep exposure argument:
    //    the collections are private and rep is checked in all creators, modifiers etc


    public BigLibrary() {
        this.allBooks = new HashMap<>();
        this.inLibrary = new HashMap<>();
        this.checkedOut = new HashMap<>();
        this.checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        // Make sets of all bookcopies in the different maps
        Set<BookCopy> allBookCopies = new HashSet<>();
        Set<BookCopy> bookCopiesInLibrary = new HashSet<>();
        Set<BookCopy> bookCopiesCheckedOut = new HashSet<>();
        for (Set<BookCopy> bookSet : allBooks.values()) allBookCopies.addAll(bookSet);
        for (Set<BookCopy> bookSet : inLibrary.values()) bookCopiesInLibrary.addAll(bookSet);
        for (Set<BookCopy> bookSet : checkedOut.values()) bookCopiesCheckedOut.addAll(bookSet);

        // Check that union of inLibrary and checkedOut equals allBooks
        Set<BookCopy> union = new HashSet<>(bookCopiesInLibrary);
        union.addAll(bookCopiesCheckedOut);
        assert union.equals(allBookCopies);

        // Check that intersection of inLibrary and checkedOut is empty
        Set<BookCopy> intersection = new HashSet<>(bookCopiesInLibrary);
        intersection.retainAll(bookCopiesCheckedOut);
        assert intersection.isEmpty();
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy newCopy = new BookCopy(book);
        if (allBooks.containsKey(book)) {
            allBooks.get(book).add(newCopy);
            inLibrary.get(book).add(newCopy);
        } else {
            allBooks.put(book, new HashSet<>(List.of(newCopy)));
            inLibrary.put(book, new HashSet<>(List.of(newCopy)));
            checkedOut.put(book, new HashSet<>());
        }
        checkRep();
        return newCopy;
    }

    @Override
    public void checkout(BookCopy copy) {
        inLibrary.get(copy.getBook()).remove(copy);
        checkedOut.get(copy.getBook()).add(copy);
        checkRep();
    }

    @Override
    public void checkin(BookCopy copy) {
        checkedOut.get(copy.getBook()).remove(copy);
        inLibrary.get(copy.getBook()).add(copy);
        checkRep();
    }

    @Override
    public Set<BookCopy> allCopies(Book book) {
        if (allBooks.containsKey(book)) return new HashSet<>(allBooks.get(book));
        else return new HashSet<>();
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        if (inLibrary.containsKey(book)) return new HashSet<>(inLibrary.get(book));
        else return new HashSet<>();
    }

    @Override
    public boolean isAvailable(BookCopy copy) {
        if(copy == null) return false;
        if (!allBooks.containsKey(copy.getBook())) return false;
        return inLibrary.get(copy.getBook()).contains(copy);
    }

    @Override
    public List<Book> find(String query) {
        List<String> exactMatches = removeQuotes(query);
        query = exactMatches.get(0);
        exactMatches.remove(0);

        String[] removeBeforeAndGetYear = removeBefore(query);
        query = removeBeforeAndGetYear[0];
        int yearBefore = Integer.parseInt(removeBeforeAndGetYear[1]);

        String[] removeAfterAndGetYear = removeAfter(query);
        query = removeAfterAndGetYear[0];
        int yearAfter = Integer.parseInt(removeAfterAndGetYear[1]);

        ArrayList<String> words = new ArrayList<>(List.of(query.split("\\s+")));
        words.addAll(exactMatches);

        Map<Double, SortedSet<Book>> results = new TreeMap<>(Collections.reverseOrder());
        for (Book book : allBooks.keySet()) {
            if (book.getYear() >= yearBefore || book.getYear() <= yearAfter) continue;

            double ranking = 0;
            for (String word : words) {
                if (book.getTitle().contains(word)) ranking++;
                for (String author : book.getAuthors()){
                    if (author.contains(word)) ranking++;
                }
            }

            if (inLibrary.get(book).isEmpty()) ranking *= 0.0001;

            results.computeIfAbsent(ranking, k -> new TreeSet<>()).add(book);
        }

        List<Book> resultsBooks = new ArrayList<>();
        for (Double key : results.keySet()) if (key > 0) resultsBooks.addAll(results.get(key));
        return resultsBooks;
    }

    private List<String> removeQuotes(String query) {
        ArrayList<String> quotes = new ArrayList<>();
        quotes.add(query);
        Pattern pattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String match = matcher.group(0);
            quotes.add(match.substring(1, match.length() - 1));
            query = query.replace(match, " ");
            matcher = pattern.matcher(query);
        }
        return quotes;
    }

    private String[] removeBefore(String query) {
        return getStrings(query, "9999", Pattern.compile("BEFORE\\s+(\\d{1,4})"));
    }

    private String[] removeAfter(String query) {
        return getStrings(query, "0", Pattern.compile("AFTER\\s+(\\d{1,4})"));
    }

    private String[] getStrings(String query, String year, Pattern pattern) {
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String match = matcher.group(0);
            Matcher digits = Pattern.compile("\\d{1,4}").matcher(match);
            digits.find();
            year = digits.group(0);
            query = query.replace(match, " ");
        }
        return new String[]{query, year};
    }
    // asserts that the book corresponding to copy is a key in allBooks
    @Override
    public void lose(BookCopy copy) {
        allBooks.get(copy.getBook()).remove(copy);
        inLibrary.get(copy.getBook()).remove(copy);
        checkedOut.get(copy.getBook()).remove(copy);
        if (allBooks.get(copy.getBook()).isEmpty()){
            allBooks.remove(copy.getBook());
            inLibrary.remove(copy.getBook());
            checkedOut.remove(copy.getBook());
        }
        checkRep();
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}