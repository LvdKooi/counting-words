package nl.kooi.countingwords.domain;

import nl.kooi.countingwords.WordProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(WordFrequencyService.class)
class WordFrequencyServiceTest {

    @Autowired
    private WordFrequencyAnalyzer wordFrequencyAnalyzer;

    private static String TEXT = "Laurens, Laurens, Laurens, Ordina, Ordina, Assessment, Assessment";

    @Test
    void calculateHighestFrequency() {
        assertThat(wordFrequencyAnalyzer.calculateHighestFrequency(TEXT)).isEqualTo(3);
    }

    @Test
    void calculateHighestFrequency_BlankText() {
        assertThat(wordFrequencyAnalyzer.calculateHighestFrequency("")).isEqualTo(0);
    }

    @Test
    void calculateHighestFrequency_Nullext() {
        var errorMessage = assertThrows(WordProcessingException.class,
                () -> wordFrequencyAnalyzer.calculateHighestFrequency(null)).getMessage();
        assertThat(errorMessage).isEqualTo("Input text is null. Null texts cannot be analyzed.");
    }

    @Test
    void calculateFrequencyForWord_WordMatchesFully() {
        assertThat(wordFrequencyAnalyzer.calculateFrequencyForWord(TEXT, "Laurens")).isEqualTo(3);
    }

    @Test
    void calculateFrequencyForWord_WordMatchesPartially() {
        assertThat(wordFrequencyAnalyzer.calculateFrequencyForWord(TEXT, "Lau")).isEqualTo(0);
    }

    @Test
    void calculateFrequencyForWord_NullText() {
        var errorMessage = assertThrows(WordProcessingException.class,
                () -> wordFrequencyAnalyzer.calculateFrequencyForWord(null, "Lau")).getMessage();
        assertThat(errorMessage).isEqualTo("Input text is null. Null texts cannot be analyzed.");
    }

    @Test
    void calculateFrequencyForWord_BlankText() {
        assertThat(wordFrequencyAnalyzer.calculateFrequencyForWord("", "Lau")).isEqualTo(0);
    }

    @Test
    void calculateFrequencyForWord_NoWord() {
        var errorMessage = assertThrows(WordProcessingException.class,
                () -> wordFrequencyAnalyzer.calculateFrequencyForWord(TEXT, "123456!!++^^"))
                .getMessage();

        assertThat(errorMessage).isEqualTo("Word 123456!!++^^ doesn't contain the required letters (a-z or A-Z).");
    }

    @Test
    void calculateMostFrequentNWords() {
    }
}