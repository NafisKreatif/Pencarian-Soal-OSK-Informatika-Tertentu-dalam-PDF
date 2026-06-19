package osk.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.util.Pair;
import osk.matcher.AhoCorasickMatcher;
import osk.matcher.AhoCorasickTrie;
import osk.matcher.RegexMatcher;

public class PDFSoalSearcher {
    private Map<String, AhoCorasickMatcher> ahoCorasickMatchers;
    private Map<String, RegexMatcher> regexMatchers;
    private PDFTextStripper textStripper;

    public PDFSoalSearcher() {
        ahoCorasickMatchers = new HashMap<>();
        regexMatchers = new HashMap<>();
        textStripper = new PDFTextStripper();
    }

    public void loadKeywords(InputStream inputStream) {
        JSONObject jsonObject = parseJson(inputStream);
        if (jsonObject != null) {
            for (String key : jsonObject.keySet()) {
                try {
                    JSONArray keywords = jsonObject.getJSONArray(key);
                    ahoCorasickMatchers.put(key, new AhoCorasickMatcher(new AhoCorasickTrie(
                            keywords.toList().stream().map((o) -> (String) o).toList())));
                } catch (JSONException e) {
                    System.err.println("Invalid keyword json");
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadRegex(InputStream inputStream) {
        JSONObject jsonObject = parseJson(inputStream);
        if (jsonObject != null) {
            for (String key : jsonObject.keySet()) {
                try {
                    JSONArray regexes = jsonObject.getJSONArray(key);
                    regexMatchers.put(key, new RegexMatcher(
                            regexes.toList().stream().map((o) -> (String) o).toList()));
                } catch (JSONException e) {
                    System.err.println("Invalid regex json");
                    e.printStackTrace();
                }
            }
        }
    }

    private JSONObject parseJson(InputStream inputStream) {
        StringBuilder jsonStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject object = new JSONObject(jsonStringBuilder.toString());
            return object;
        } catch (JSONException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public Pair<List<SoalToTagSearchResult>, List<TagToSoalSearchResult>> searchSoalWithTags(
            PDDocument document,
            List<String> tags) {
        Map<Integer, List<String>> soalToTagList = new HashMap<>();
        Map<String, List<Integer>> tagToSoalList = new HashMap<>();
        textStripper.setStartPage(1);
        textStripper.setEndPage(document.getNumberOfPages());
        try {
            String text = textStripper.getText(document);
            System.out.println(text);

            Matcher startSoalMatcher = Pattern.compile("^(Soal\\s)?(\\d{1,2})(-\\d{1,2})?\\.", Pattern.MULTILINE).matcher(text);
            List<Integer> soalNumberList = new ArrayList<>();
            List<Integer> soalIndexList = new ArrayList<>();
            while (startSoalMatcher.find()) {
                try {
                    soalNumberList.add(Integer.parseInt(startSoalMatcher.group(2)));
                    soalIndexList.add(startSoalMatcher.start());
                } catch (Exception e) {
                    System.out.println("Failed to regex the start of soal");
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < soalNumberList.size(); i++) {
                int beginIndex = soalIndexList.get(i);
                int endIndex = (i + 1 == soalIndexList.size()) ? text.length() : soalIndexList.get(i + 1);
                String soalText = text.substring(beginIndex, endIndex);
                for (String tag : tags) {
                    AhoCorasickMatcher ahoCorasickMatcher = ahoCorasickMatchers.get(tag);
                    RegexMatcher regexMatcher = regexMatchers.get(tag);
                    if (ahoCorasickMatcher.hasMatch(soalText) || regexMatcher.hasMatch(soalText)) {
                        if (!soalToTagList.containsKey(soalNumberList.get(i))) {
                            soalToTagList.put(soalNumberList.get(i), new ArrayList<>());
                        }
                        soalToTagList.get(soalNumberList.get(i)).add(tag);
                        if (!tagToSoalList.containsKey(tag)) {
                            tagToSoalList.put(tag, new ArrayList<>());
                        }
                        tagToSoalList.get(tag).add(soalNumberList.get(i));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to get text");
            e.printStackTrace();
        }
        tagToSoalList.values().forEach(val -> val.sort((v1, v2) -> v1 - v2));
        List<SoalToTagSearchResult> soalToTagSearchResults = soalToTagList.entrySet().stream()
                .map(entry -> new SoalToTagSearchResult(entry.getKey(), entry.getValue())).toList();
        List<TagToSoalSearchResult> tagToSoalSearchResults = tagToSoalList.entrySet().stream()
                .map(entry -> new TagToSoalSearchResult(entry.getKey(), entry.getValue())).toList();
        return new Pair<>(soalToTagSearchResults, tagToSoalSearchResults);
    }
}
