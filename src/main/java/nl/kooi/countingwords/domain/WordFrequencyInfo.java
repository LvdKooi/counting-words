package nl.kooi.countingwords.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(staticName = "of")
@Getter
@ToString
public class WordFrequencyInfo implements WordFrequency {
    private final String word;
    private final int frequency;
}
