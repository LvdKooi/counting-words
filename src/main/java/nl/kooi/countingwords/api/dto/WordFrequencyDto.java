package nl.kooi.countingwords.api.dto;

import lombok.Data;

@Data
public class WordFrequencyDto {
    private String word;
    private int frequency;

    public WordFrequencyDto word(String word) {
        this.word = word;
        return this;
    }

    public WordFrequencyDto frequency(int frequency) {
        this.frequency = frequency;
        return this;
    }
}
