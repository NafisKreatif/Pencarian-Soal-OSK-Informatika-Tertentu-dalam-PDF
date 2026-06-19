package osk.pdf;

import java.util.List;

public class TagToSoalSearchResult {
    public String tag;
    public List<Integer> numbers;

    public TagToSoalSearchResult(String tag, List<Integer> numbers) {
        this.tag = tag;
        this.numbers = numbers;
    }
}
