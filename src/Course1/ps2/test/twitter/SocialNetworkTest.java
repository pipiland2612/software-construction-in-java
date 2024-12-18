package Course1.ps2.test.twitter;

import org.junit.Test;
import Course1.ps2.src.twitter.SocialNetwork;
import Course1.ps2.src.twitter.Tweet;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SocialNetworkTest {

    /*
     * TODO: Testing strategy
     * guessFollowsGraph:
     * Partitioning based on quantity of tweets:
     *  1. No Tweets
     *  2. Exactly one tweet
     *  3. More than one tweet
     *  Partitioning based on authors:
     *  1. Author not being a follower
     *  2. Author being a followee
     * Partitioning based on cases:
     *  1. Uppercase
     *  2. Lowercase
     *  3. ToggleCase
     *
     *  influencers:
     *  Partition based on quantity of follows
     *  1. Empty graph
     *  2. One member graph
     *  3. More than one member graph
     *
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testSingleMention() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "Ernie", "Hello @Bert!", Instant.now())
        );

        Map<String, Set<String>> result = SocialNetwork.guessFollowsGraph(tweets);
        Map<String, Set<String>> expected = new HashMap<>();
        expected.put("Ernie", Set.of("bert"));

        assertEquals(expected, result);
    }

    @Test
    public void testMultipleMentionsByDifferentUsers() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "Ernie", "Hello @Bert, how are you?", Instant.now()),
            new Tweet(2, "Bert", "I'm doing well, thanks @Ernie!", Instant.now()),
            new Tweet(3, "Ernie", "@Bert let's catch up soon.", Instant.now())
        );

        Map<String, Set<String>> result = SocialNetwork.guessFollowsGraph(tweets);
        Map<String, Set<String>> expected = new HashMap<>();
        expected.put("Ernie", Set.of("bert"));
        expected.put("Bert", Set.of("ernie"));

        assertEquals(expected, result);
    }

    @Test
    public void testNoMentions() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "Ernie", "Hello world!", Instant.now()),
            new Tweet(2, "Bert", "Just relaxing today.", Instant.now())
        );

        Map<String, Set<String>> result = SocialNetwork.guessFollowsGraph(tweets);
        Map<String, Set<String>> expected = new HashMap<>();

        assertEquals(expected, result);
    }

    @Test
    public void testMultipleMentionsFromSingleUser() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "Ernie", "Hey @Bert, how are you? @Bert I hope you're doing well!", Instant.now()),
            new Tweet(2, "Ernie", "Let's catch up soon @Bert.", Instant.now())
        );

        Map<String, Set<String>> result = SocialNetwork.guessFollowsGraph(tweets);
        Map<String, Set<String>> expected = new HashMap<>();
        expected.put("Ernie", Set.of("bert"));

        assertEquals(expected, result);
    }


    @Test
    public void testMentionsWithDifferentUsers() {
        List<Tweet> tweets = List.of(
            new Tweet(1, "Ernie", "@Bert I think @Alice should join us.", Instant.now()),
            new Tweet(2, "Bert", "@Alice is a great addition!", Instant.now()),
            new Tweet(3, "Alice", "@Ernie I agree, @Bert will love it.", Instant.now())
        );

        Map<String, Set<String>> result = SocialNetwork.guessFollowsGraph(tweets);
        Map<String, Set<String>> expected = new HashMap<>();
        expected.put("Ernie", Set.of("bert", "alice"));
        expected.put("Bert", Set.of("alice"));
        expected.put("Alice", Set.of("ernie, bert"));

        assertEquals(expected, result);
    }

    //Method 2
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersBasic() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put("Alice", new HashSet<>(Set.of("Bob")));
        followsGraph.put("Bob", new HashSet<>(Set.of("Alice")));
        followsGraph.put("Charlie", new HashSet<>(Set.of("Alice")));

        List<String> result = SocialNetwork.influencers(followsGraph);

        List<String> expected = List.of("Alice", "Bob", "Charlie");

        assertEquals(expected, result);
    }

    @Test
    public void testInfluencersSameFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put("Alice", new HashSet<>(Set.of("Bob", "Charlie")));
        followsGraph.put("Bob", new HashSet<>(Set.of("Alice", "Charlie")));
        followsGraph.put("Charlie", new HashSet<>(Set.of("Alice", "Bob")));

        List<String> result = SocialNetwork.influencers(followsGraph);

        List<String> expected = List.of("Alice", "Bob", "Charlie");

        assertEquals(expected, result);
    }

    @Test
    public void testInfluencersNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put("Alice", new HashSet<>());
        followsGraph.put("Bob", new HashSet<>());
        followsGraph.put("Charlie", new HashSet<>());

        List<String> result = SocialNetwork.influencers(followsGraph);

        List<String> expected = List.of("Alice", "Bob", "Charlie");

        assertEquals(expected, result);
    }

    @Test
    public void testInfluencersEmptyNetwork() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        List<String> result = SocialNetwork.influencers(followsGraph);

        List<String> expected = new ArrayList<>();

        assertEquals(expected, result);
    }

    @Test
    public void testInfluencersSingleUserMultipleFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put("Alice", new HashSet<>(Set.of("Bob", "Charlie", "David")));
        followsGraph.put("Bob", new HashSet<>());
        followsGraph.put("Charlie", new HashSet<>());
        followsGraph.put("David", new HashSet<>());

        List<String> result = SocialNetwork.influencers(followsGraph);

        List<String> expected = List.of("Alice", "Bob", "Charlie", "David");

        assertEquals(expected, result);
    }

    @Test
    public void testInfluencersCaseInsensitive() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put("Alice", new HashSet<>(Set.of("bob", "Charlie")));
        followsGraph.put("Bob", new HashSet<>(Set.of("alice", "charlie")));
        followsGraph.put("Charlie", new HashSet<>(Set.of("alice", "bob")));

        List<String> result = SocialNetwork.influencers(followsGraph);

        List<String> expected = List.of("Alice", "Bob", "Charlie");

        assertEquals(expected, result);
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
