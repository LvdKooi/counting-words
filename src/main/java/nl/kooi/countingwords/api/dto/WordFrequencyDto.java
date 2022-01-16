package nl.kooi.countingwords.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WordFrequencyDto extends FrequencyDto {
    private String word;

    public WordFrequencyDto word(String word) {
        this.word = word;
        return this;
    }

    public WordFrequencyDto frequency(int frequency) {
        super.frequency = frequency;
        return this;
    }
}
