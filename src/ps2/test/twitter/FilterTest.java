package ps2.test.twitter;

import org.junit.Test;
import ps2.src.twitter.Filter;
import ps2.src.twitter.Timespan;
import ps2.src.twitter.Tweet;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testNoTweetsForGivenUser(){
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Tweet by user1", Instant.now()),
            new Tweet(2, "user2", "Tweet by user2", Instant.now())
        );
        String username = "user3";
        List<Tweet> res = Filter.writtenBy(tweets, username);
        assertEquals(0, res.size());
    }

    @Test
    public void testTweetsByAUser(){
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Tweet by user1", Instant.now()),
            new Tweet(2, "user2", "Tweet by user2", Instant.now()),
            new Tweet(3, "user1", "Another tweet by user1", Instant.now())
        );
        List<Tweet> result = Filter.writtenBy(tweets, "user1");
        assertEquals(2, result.size());
        assertEquals("Tweet by user1", result.get(0).getText());
        assertEquals("Another tweet by user1", result.get(1).getText());
    }

    @Test
    public void testMultipleTweets(){
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Tweet by user1", Instant.now()),
            new Tweet(2, "user2", "Tweet by user2", Instant.now()),
            new Tweet(3, "user1", "Another tweet by user1", Instant.now()),
            new Tweet(4, "user3", "Tweet by user3", Instant.now())
        );
        List<Tweet> result = Filter.writtenBy(tweets, "user1");
        assertEquals(2, result.size());
        assertEquals("Tweet by user1", result.get(0).getText());
        assertEquals("Another tweet by user1", result.get(1).getText());
    }

    @Test
    public void testSensitiveCaseUsername(){
        List<Tweet> tweets = List.of(
            new Tweet(1, "User1", "Tweet by User1", Instant.now()),
            new Tweet(2, "user1", "Tweet by user1", Instant.now())
        );
        List<Tweet> result = Filter.writtenBy(tweets, "user1");
        assertEquals(1, result.size());
        assertEquals("Tweet by user1", result.get(0).getText());  // Only "user1" tweet should be returned
    }
    @Test
    public void testEmptyTweets(){
        List<Tweet> tweets = List.of();
        List<Tweet> result = Filter.writtenBy(tweets, "user1");
        assertEquals(0, result.size());
    }


    // Method 2
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }

    @Test
    public void testNoTweetsWithinTimespan() {
        List<Tweet> tweets = List.of(
                new Tweet(1, "user1", "Tweet 1", Instant.parse("2024-01-01T00:00:00Z")),
                new Tweet(2, "user2", "Tweet 2", Instant.parse("2024-01-01T01:00:00Z"))
        );
        Instant start = Instant.parse("2024-01-01T02:00:00Z");
        Instant end = Instant.parse("2024-01-01T03:00:00Z");

        List<Tweet> result = Filter.inTimespan(tweets, new Timespan(start, end));
        assertEquals(0, result.size());  // No tweets should be in the timespan
    }

    @Test
    public void testAllTweetsWithinTimespan() {
        List<Tweet> tweets = List.of(
                new Tweet(1, "user1", "Tweet 1", Instant.parse("2024-01-01T01:30:00Z")),
                new Tweet(2, "user2", "Tweet 2", Instant.parse("2024-01-01T02:00:00Z"))
        );
        Instant start = Instant.parse("2024-01-01T01:00:00Z");
        Instant end = Instant.parse("2024-01-01T03:00:00Z");

        List<Tweet> result = Filter.inTimespan(tweets, new Timespan(start, end));
        assertEquals(2, result.size());
        assertEquals("Tweet 1", result.get(0).getText());
        assertEquals("Tweet 2", result.get(1).getText());
    }

    @Test
    public void testTweetsAtEdgeOfTimespan() {
        List<Tweet> tweets = List.of(
                new Tweet(1, "user1", "Tweet 1", Instant.parse("2024-01-01T01:00:00Z")),  // Start of timespan
                new Tweet(2, "user2", "Tweet 2", Instant.parse("2024-01-01T03:00:00Z"))   // End of timespan
        );
        Instant start = Instant.parse("2024-01-01T01:00:00Z");
        Instant end = Instant.parse("2024-01-01T03:00:00Z");

        List<Tweet> result = Filter.inTimespan(tweets, new Timespan(start, end));
        assertEquals(2, result.size());
        assertEquals("Tweet 1", result.get(0).getText());  // Exactly at start
        assertEquals("Tweet 2", result.get(1).getText());  // Exactly at end
    }

    @Test
    public void testTweetsBeforeAndAfterTimespan() {
        List<Tweet> tweets = List.of(
                new Tweet(1, "user1", "Tweet 1", Instant.parse("2024-01-01T00:30:00Z")),  // Before timespan
                new Tweet(2, "user2", "Tweet 2", Instant.parse("2024-01-01T02:30:00Z")),  // Inside timespan
                new Tweet(3, "user3", "Tweet 3", Instant.parse("2024-01-01T03:30:00Z"))   // After timespan
        );
        Instant start = Instant.parse("2024-01-01T01:00:00Z");
        Instant end = Instant.parse("2024-01-01T03:00:00Z");

        List<Tweet> result = Filter.inTimespan(tweets, new Timespan(start, end));
        assertEquals(1, result.size());
        assertEquals("Tweet 2", result.get(0).getText());  // Only this tweet should be in the timespan
    }

    // Method 3
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testTweetsContainingWord() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "The economy is improving", Instant.now()),
            new Tweet(2, "user2", "Election season is here", Instant.now()),
            new Tweet(3, "user3", "The economy is the talk of the town", Instant.now()),
            new Tweet(4, "user4", "Programming is fun", Instant.now())
        );
        List<String> words = List.of("economy", "Election");

        List<Tweet> result = Filter.containing(tweets, words);
        assertEquals(3, result.size());  // Tweets 1, 3, and 2 should be included
    }

    @Test
    public void testTweetsContainingMultipleWords() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "The economy is improving", Instant.now()),
            new Tweet(2, "user2", "Election season is here", Instant.now()),
            new Tweet(3, "user3", "The economy is the talk of the town", Instant.now()),
            new Tweet(4, "user4", "Programming is fun", Instant.now())
        );
        List<String> words = List.of("economy", "programming");

        List<Tweet> result = Filter.containing(tweets, words);
        assertEquals(3, result.size());  // Tweets 1, 3, and 4 should be included
    }

    @Test
    public void testTweetsContainingCaseInsensitiveWord() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "The Economy is improving", Instant.now()),
            new Tweet(2, "user2", "ELECTION season is here", Instant.now()),
            new Tweet(3, "user3", "The economy is the talk of the town",Instant.now())
        );
        List<String> words = List.of("economy", "election");

        List<Tweet> result = Filter.containing(tweets, words);
        assertEquals(3, result.size());  // All tweets should be included (case insensitive)
    }

    @Test
    public void testTweetsContainingNoMatchingWords() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "The weather is nice", Instant.now()),
            new Tweet(2, "user2", "Today is a good day", Instant.now()),
            new Tweet(3, "user3", "It's a beautiful day outside",Instant.now())
        );
        List<String> words = List.of("economy", "election");

        List<Tweet> result = Filter.containing(tweets, words);
        assertEquals(0, result.size());  // No tweets should match
    }

    @Test
    public void testTweetsContainingExactWords() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "I love programming", Instant.now()),
            new Tweet(2, "user2", "Programming is fun", Instant.now()),
            new Tweet(3, "user3", "Hello world!",Instant.now())
        );
        List<String> words = List.of("programming");

        List<Tweet> result = Filter.containing(tweets, words);
        assertEquals(2, result.size());  // Tweets 1 and 2 should be included
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
