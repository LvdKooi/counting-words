package nl.kooi.countingwords.mapper;

import nl.kooi.countingwords.api.dto.WordFrequencyDto;
import nl.kooi.countingwords.domain.WordFrequency;

public class Mapper {

    public static WordFrequencyDto map(WordFrequency wordFrequency) {
        return new WordFrequencyDto().word(wordFrequency.getWord()).frequency(wordFrequency.getFrequency());
    }
}
