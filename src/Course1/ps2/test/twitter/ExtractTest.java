package Course1.ps2.test.twitter;

import org.junit.Test;
import Course1.ps2.src.twitter.Extract;
import Course1.ps2.src.twitter.Timespan;
import Course1.ps2.src.twitter.Tweet;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

public class ExtractTest {

    /*
     * Testing strategy:
     * Partitioning based on Interval
     * 1. Same timestamps
     * 2. Different timestamps
     *
     * Partitioning based on quantity of tweets:
     * 1. Empty set of tweets
     * 2. Single tweet
     * 3. More than one tweet
     *
     * Partitioning based on mentions
     * 1. No mentions
     * 2. Single mention
     * 3. More than a mention
     * 4. Invalid mention ie. a mention preceded or followed by one of the following
     *    characters A-Z, a-z, 0-9, _, -
     * Partitioning based on the position
     * 1. At the beginning of the tweet
     * 2. At the middle
     * 3. At the end
     * Partitioning based on case
     * 1. lowercase mention
     * 2. uppercase mention
     * 3. toggle case mention
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
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testMultipleTweetsWithDistinctTimestamps() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "First tweet", Instant.parse("2024-12-01T10:15:30Z")),
            new Tweet(2, "user2", "Second tweet", Instant.parse("2024-12-02T11:45:00Z")),
            new Tweet(3, "user3", "Third tweet", Instant.parse("2024-12-03T09:00:00Z"))
        );

        Timespan timespan = Extract.getTimespan(tweets);
        assertEquals(Instant.parse("2024-12-01T10:15:30Z"), timespan.getStart());
        assertEquals(Instant.parse("2024-12-03T09:00:00Z"), timespan.getEnd());
    }

    @Test
    public void testSingleTweet(){
        Instant d1 = Instant.parse("2024-12-02T11:45:00Z");
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Only tweet", d1)
        );
        Timespan timespan = Extract.getTimespan(tweets);
        assertEquals(d1, timespan.getStart());
        assertEquals(d1, timespan.getEnd());
    }

    @Test
    public void testMultipleTweetsWithIdenticalTimestamps(){
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "First tweet", Instant.parse("2024-12-02T11:45:00Z")),
            new Tweet(2, "user2", "Duplicate tweet", Instant.parse("2024-12-02T11:45:00Z")),
            new Tweet(3, "user3", "Another tweet", Instant.parse("2024-12-02T11:45:00Z"))
        );

        Timespan timespan = Extract.getTimespan(tweets);
        assertEquals(Instant.parse("2024-12-02T11:45:00Z"), timespan.getStart());
        assertEquals(Instant.parse("2024-12-02T11:45:00Z"), timespan.getEnd());
    }


    @Test
    public void testTimestampsInUnorderedOrder(){
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Middle tweet", Instant.parse("2024-12-02T11:45:00Z")),
            new Tweet(2, "user2", "Earliest tweet", Instant.parse("2024-12-01T10:15:30Z")),
            new Tweet(3, "user3", "Latest tweet", Instant.parse("2024-12-03T09:00:00Z"))
        );
        Timespan timespan = Extract.getTimespan(tweets);
        assertEquals(Instant.parse("2024-12-01T10:15:30Z"), timespan.getStart());
        assertEquals(Instant.parse("2024-12-03T09:00:00Z"), timespan.getEnd());
    }

    @Test
    public void testEmptyList(){
        List<Tweet> tws = List.of();
        assertThrows(IllegalArgumentException.class, () -> Extract.getTimespan(tws));
    }

    @Test
    public void testLargeList(){
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Tweet 1", Instant.parse("2024-12-12T10:00:00Z")),
            new Tweet(2, "user2", "Tweet 2", Instant.parse("2024-12-10T09:00:00Z")),
            new Tweet(3, "user3", "Tweet 3", Instant.parse("2024-12-15T22:30:00Z")),
            new Tweet(4, "user4", "Tweet 4", Instant.parse("2024-12-08T11:00:00Z")),
            new Tweet(5, "user5", "Tweet 5", Instant.parse("2024-12-20T06:45:00Z")),
            new Tweet(6, "user6", "Tweet 6", Instant.parse("2024-12-06T12:00:00Z"))
        );
        Timespan timespan = Extract.getTimespan(tweets);
        assertEquals(Instant.parse("2024-12-06T12:00:00Z"), timespan.getStart());
        assertEquals(Instant.parse("2024-12-20T06:45:00Z"), timespan.getEnd());
    }

    // Second method Test
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(List.of(tweet1));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testBasicMention() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Hey @user2, how's it going?", Instant.now())
        );
        Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        assertEquals(Set.of("user2"), mentionedUsers);
    }

    @Test
    public void testMultipleMentions() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "@user2 Hey, @user3! How are you @user2?", Instant.now())
        );
        Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        assertEquals(Set.of("user2", "user3"), mentionedUsers);
    }

    @Test
    public void testEmailWithMention() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Contact me at bitdiddle@mit.edu for @user2's updates.", Instant.now())
        );
        Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        assertEquals(Set.of("user2"), mentionedUsers);
    }

    @Test
    public void testCaseInsensitiveMention() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Hey @USER2, do you want to join the meeting?", Instant.now())
        );
        Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        assertEquals(Set.of("user2"), mentionedUsers);
    }

    @Test
    public void testNoMentions() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "This is a tweet with no mentions at all.", Instant.now())
        );
        Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        assertTrue(mentionedUsers.isEmpty());
    }

    @Test
    public void testOnlyAtCharacter() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "user1", "Just @", Instant.now())
        );
        Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        assertTrue(mentionedUsers.isEmpty());
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
