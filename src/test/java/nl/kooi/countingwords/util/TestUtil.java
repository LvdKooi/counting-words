package nl.kooi.countingwords.util;

import nl.kooi.countingwords.api.dto.FrequencyRequestDto;
import nl.kooi.countingwords.api.dto.TopFrequencyRequestDto;
import nl.kooi.countingwords.api.dto.WordFrequencyRequestDto;

public class TestUtil {

    public static final String HIGHEST_FREQUENCY_ENDPOINT = "/rest/word-count/highest-frequency";
    public static final String HIGHEST_FREQUENCY_FOR_WORD_ENDPOINT = "/rest/word-count/frequency-for-word";
    public static final String TOP_FREQUENCY_ENDPOINT = "/rest/word-count/top-frequency";

    public static FrequencyRequestDto getFrequencyRequestDto(String text) {
        var dto = new FrequencyRequestDto();
        dto.setText(text);
        return dto;
    }

    public static WordFrequencyRequestDto getWordFrequencyRequestDto(String text, String word) {
        var dto = new WordFrequencyRequestDto();
        dto.setText(text);
        dto.setWord(word);
        return dto;
    }

    public static TopFrequencyRequestDto getToprequencyRequestDto(String text, int n) {
        var dto = new TopFrequencyRequestDto();
        dto.setText(text);
        dto.setN(n);
        return dto;
    }

}
