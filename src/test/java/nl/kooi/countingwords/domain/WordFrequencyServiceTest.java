package nl.kooi.countingwords.domain;

import nl.kooi.countingwords.exception.WordProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(WordFrequencyService.class)
class WordFrequencyServiceTest {

    @Autowired
    private WordFrequencyAnalyzer wordFrequencyAnalyzer;

    private static String TEXT = "Laurens, Laurens, Laurens, Ordina, Ordina, Assessment, Assessment, Nice";

    @Test
    void calculateHighestFrequency() {
        assertThat(wordFrequencyAnalyzer.calculateHighestFrequency(TEXT)).isEqualTo(3);
    }

    @Test
    void calculateHighestFrequency_BlankText() {
        assertThat(wordFrequencyAnalyzer.calculateHighestFrequency("")).isEqualTo(0);
    }

    @Test
    void calculateHighestFrequency_NullText() {
        var errorMessage = assertThrows(WordProcessingException.class,
                () -> wordFrequencyAnalyzer.calculateHighestFrequency(null)).getMessage();
        assertThat(errorMessage).isEqualTo("Input text is null. Null texts cannot be analyzed.");
    }

    @Test
    void calculateFrequencyForWord_WordMatchesFully() {
        assertThat(wordFrequencyAnalyzer.calculateFrequencyForWord(TEXT, "ordina")).isEqualTo(2);
    }

    @Test
    void calculateFrequencyForWord_WordMatchesPartially() {
        assertThat(wordFrequencyAnalyzer.calculateFrequencyForWord(TEXT, "Ord")).isEqualTo(0);
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
        var result = wordFrequencyAnalyzer.calculateMostFrequentNWords(TEXT, 2);
        assertThat(result).hasSize(2);
        assertThat(result[0].getFrequency()).isEqualTo(3);
        assertThat(result[0].getWord()).isEqualTo("laurens");
        assertThat(result[1].getFrequency()).isEqualTo(2);
        assertThat(result[1].getWord()).isEqualTo("assessment");
    }

    @Test
    void calculateMostFrequentNWords_SameFrequencyAscAlphabeticalOrder() {
        var text = "PYthon&Java!kotlin+java8Angular3kotlin angular";
        var result = wordFrequencyAnalyzer.calculateMostFrequentNWords(text, 3);

        assertThat(result).hasSize(3);
        assertThat(result[0].getFrequency()).isEqualTo(2);
        assertThat(result[0].getWord()).isEqualTo("angular");
        assertThat(result[1].getFrequency()).isEqualTo(2);
        assertThat(result[1].getWord()).isEqualTo("java");
        assertThat(result[2].getFrequency()).isEqualTo(2);
        assertThat(result[2].getWord()).isEqualTo("kotlin");
    }

    @Test
    void calculateMostFrequentNWords_LessWordsThanN() {
        var result = wordFrequencyAnalyzer.calculateMostFrequentNWords(TEXT, 10);
        assertThat(result).hasSize(4);
    }

    @Test
    void calculateMostFrequentNWords_NegativeN() {
        var result = wordFrequencyAnalyzer.calculateMostFrequentNWords(TEXT, -1);
        assertThat(result).hasSize(0);
    }

    @Test
    void calculateMostFrequentNWords_NullText() {
        var errorMessage = assertThrows(WordProcessingException.class,
                () -> wordFrequencyAnalyzer.calculateMostFrequentNWords(null, 5)).getMessage();
        assertThat(errorMessage).isEqualTo("Input text is null. Null texts cannot be analyzed.");
    }

}