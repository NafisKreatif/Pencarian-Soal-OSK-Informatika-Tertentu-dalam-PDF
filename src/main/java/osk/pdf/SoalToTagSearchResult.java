package osk.pdf;

import java.util.List;

public class SoalToTagSearchResult {
    public int number;
    public List<String> tags;
    
    public SoalToTagSearchResult(int number, List<String> tags) {
        this.number = number;
        this.tags = tags;
    }
}
