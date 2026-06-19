package osk.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher implements Matchable {
    private List<Pattern> patterns;
    
    public RegexMatcher(List<String> patterns) {
        this.patterns = patterns.stream().map((p) -> Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE)).toList();
    }

    @Override
    public List<MatchResult> getMatches(String input) {
        List<MatchResult> results = new ArrayList<>();
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                results.add(new MatchResult(matcher.start(), matcher.group()));
            }
        }
        return results;
    }

    @Override
    public boolean hasMatch(String input) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }
}
