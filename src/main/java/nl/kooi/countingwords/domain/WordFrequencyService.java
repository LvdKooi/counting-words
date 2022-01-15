package nl.kooi.countingwords.domain;


import lombok.extern.slf4j.Slf4j;
import nl.kooi.countingwords.exception.WordProcessingException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class WordFrequencyService implements WordFrequencyAnalyzer {
    private static final String NON_WORD_REGEX = "[^A-Za-z]+";
    private static final String WORD_REGEX = "[A-Za-z]+";
    private static final String EXACT_WORD_REGEX = "(?i).*?\\b%s\\b.*?";

    @Override
    public int calculateHighestFrequency(String text) {
        verifyText(text);
        return countGroupedByWord(text).values().stream().mapToInt(Long::intValue).max().orElse(0);
    }

    private boolean isStringEmpty(String text) {
        return text == null || "".equals(text);
    }

    private Map<String, Long> countGroupedByWord(String text) {
        return getWordStreamByText(text)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Stream<String> getWordStreamByText(String text) {
        return isStringEmpty(text) ?
                Stream.empty() :
                Arrays.stream(text.split(NON_WORD_REGEX))
                        .map(String::toLowerCase);
    }

    @Override
    public int calculateFrequencyForWord(String text, String word) {
        verifyWord(word);
        verifyText(text);

        var pattern = Pattern.compile(String.format(EXACT_WORD_REGEX, word));

        return Math.toIntExact(pattern.matcher(text.toLowerCase()).results().count());
    }

    private void verifyText(String text) {
        if (text == null) {
            throw new WordProcessingException("Input text is null. Null texts cannot be analyzed.");
        }
    }

    private void verifyWord(String word) {
        var pattern = Pattern.compile(WORD_REGEX);

        if (isStringEmpty(word) || !pattern.matcher(word).matches()) {
            throw new WordProcessingException(String.format("Word %s doesn't contain the required letters " +
                    "(a-z or A-Z).", word));
        }
    }

    @Override
    public WordFrequency[] calculateMostFrequentNWords(String text, int n) {
        verifyText(text);

        var frequencyDescWordAscComparator =
                Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey());

        return countGroupedByWord(text).entrySet().stream()
                .sorted(frequencyDescWordAscComparator)
                .limit(Math.max(n, 0))
                .map(e -> WordFrequencyInfo.of(e.getKey(), e.getValue().intValue()))
                .toArray(WordFrequency[]::new);
    }
}
