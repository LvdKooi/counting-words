package nl.kooi.countingwords.domain.service;

import nl.kooi.countingwords.domain.WordFrequency;

public interface WordFrequencyAnalyzer {
    int calculateHighestFrequency(String text);

    int calculateFrequencyForWord(String text, String word);

    WordFrequency[] calculateMostFrequentNWords(String text, int n);
}