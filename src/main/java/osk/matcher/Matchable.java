package osk.matcher;

import java.util.List;

public interface Matchable {
    public List<MatchResult> getMatches(String input);
}
