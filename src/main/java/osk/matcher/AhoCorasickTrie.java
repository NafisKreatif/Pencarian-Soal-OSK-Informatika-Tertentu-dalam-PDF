package osk.matcher;

import java.util.List;
import java.util.Map;

public class AhoCorasickTrie {
    private Character parentChar;
    private String value;
    private AhoCorasickTrie parent;
    private AhoCorasickTrie failureLink;
    private AhoCorasickTrie exitLink;
    private AhoCorasickTrie[] nextMap;

    private AhoCorasickTrie(char parentChar) {
        this.parentChar = parentChar;
        this.value = null;
        this.parent = null;
        this.failureLink = null;
        this.exitLink = null;
        this.nextMap = new AhoCorasickTrie[128];
    }

    private AhoCorasickTrie(char parentChar, String value) {
        this.parentChar = parentChar;
        this.value = value;
        this.parent = null;
        this.failureLink = null;
        this.exitLink = null;
        this.nextMap = new AhoCorasickTrie[128];
    }

    public AhoCorasickTrie(List<String> patterns) {
        this.parentChar = null;
        this.value = null;
        this.parent = null;
        this.failureLink = null;
        this.exitLink = null;
        this.nextMap = new AhoCorasickTrie[128];
        for (String pattern : patterns) {
            addPattern(pattern);
        }
    }

    public AhoCorasickTrie(String value, List<String> patterns) {
        this.parentChar = null;
        this.value = value;
        this.parent = null;
        this.failureLink = null;
        this.exitLink = null;
        this.nextMap = new AhoCorasickTrie[128];
        for (String pattern : patterns) {
            addPattern(pattern);
        }
    }

    private void addPattern(String pattern) {
        AhoCorasickTrie current = this;
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (current.nextMap[ch] == null) {
                if (i < pattern.length() - 1) {
                    current.nextMap[ch] = new AhoCorasickTrie(ch);
                } else {
                    current.nextMap[ch] = new AhoCorasickTrie(ch, pattern);
                }
            }
            current = current.nextMap[ch];
        }
    }

    public AhoCorasickTrie getNext(char nextChar) {
        if (nextMap[nextChar] != null) {
            return nextMap[nextChar];
        } else {
            if (isRoot())
                return this;
            else
                return getFailureLink().getNext(nextChar);
        }
    }

    public AhoCorasickTrie getFailureLink() {
        if (failureLink == null) {
            if (isRoot()) {
                failureLink = this;
            } else if (parent.isRoot()) {
                failureLink = parent;
            } else {
                failureLink = parent.getFailureLink().getNext(parentChar);
            }
        }
        return failureLink;
    }

    public AhoCorasickTrie getExitLink() {
        if (exitLink == null) {
            AhoCorasickTrie current = this;
            while (!current.isRoot() && !current.hasValue()) {
                current = current.getFailureLink();
            }
            exitLink = current;
        }
        return exitLink;
    }

    public boolean hasValue() {
        return value != null;
    }

    public String getValue() {
        return value;
    }

    public boolean isRoot() {
        return parent == null;
    }
}
