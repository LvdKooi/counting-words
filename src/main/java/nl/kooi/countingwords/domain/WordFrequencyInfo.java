package nl.kooi.countingwords.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class WordFrequencyInfo implements WordFrequency {
    private final String word;
    private final int frequency;
}
