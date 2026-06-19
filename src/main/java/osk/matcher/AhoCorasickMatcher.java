package osk.matcher;

import java.util.ArrayList;
import java.util.List;

public class AhoCorasickMatcher implements Matchable {
    private AhoCorasickTrie rootTrie;

    public AhoCorasickMatcher(AhoCorasickTrie trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Trie should not be null");
        }
        if (!trie.isRoot()) {
            throw new IllegalArgumentException("Trie should be a root");
        }
        this.rootTrie = trie;
    }

    @Override
    public List<MatchResult> getMatches(String input) {
        if (input == null) {
            return new ArrayList<>();
        }

        int currentIndex = 0;
        AhoCorasickTrie currentTrie = rootTrie;
        AhoCorasickTrie currentExitTrie = rootTrie;
        List<MatchResult> results = new ArrayList<>();

        while (currentIndex < input.length()) {
            currentTrie = currentTrie.getNext(input.charAt(currentIndex++));
            currentExitTrie = currentTrie;
            while (currentExitTrie.hasValue()) {
                results.add(new MatchResult(currentIndex - currentExitTrie.getValue().length() + 1,
                        currentExitTrie.getValue()));
                currentExitTrie = currentExitTrie.getExitLink();
            }
        }
        return results;
    }

    @Override
    public boolean hasMatch(String input) {
        if (input == null) {
            return false;
        }

        int currentIndex = 0;
        AhoCorasickTrie currentTrie = rootTrie;

        while (currentIndex < input.length()) {
            currentTrie = currentTrie.getNext(input.charAt(currentIndex++));
            if (currentTrie.hasValue()) {
                return true;
            }
        }
        return false;
    }
}
