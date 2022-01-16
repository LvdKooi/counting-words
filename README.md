**Counting words REST service**


A web service to:
- Calculate the most occurences of one word in a text.
- Calculate the frequency of a certain word in a text.
- Calculate the top N frequencies of words in a text.

When running locally, rest documentation is available at
http://localhost:8080/swagger-ui.html#/word-count-controller

**Interfaces**

This web service revolves around 2 interfaces

```
interface WordFrequency {
    String getWord();

    int getFrequency();
}
```
which is implemented in the **nl.kooi.countingwords.domain** package, and

```
interface WordFrequencyAnalyzer {
    int calculateHighestFrequency(String text);

    int calculateFrequencyForWord(String text, String word);

    WordFrequency[] calculateMostFrequentNWords(String text, int n);
}
```

which is implementend in the **nl.kooi.countingwords.domain.service** package.

**More information**

For more information contact the developer.

_Developer: Laurens van der Kooi (www.laurensvanderkooi.nl)_