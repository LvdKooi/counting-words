package nl.kooi.countingwords.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.kooi.countingwords.api.dto.FrequencyRequestDto;
import nl.kooi.countingwords.api.dto.TopFrequencyRequestDto;
import nl.kooi.countingwords.api.dto.WordFrequencyDto;
import nl.kooi.countingwords.api.dto.WordFrequencyRequestDto;
import nl.kooi.countingwords.domain.service.WordFrequencyService;
import nl.kooi.countingwords.mapper.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/rest/word-count")
@Validated
public class WordCountController {

    private final WordFrequencyService service;

    @PostMapping(value = "/highest-frequency")
    public WordFrequencyDto calculateHighestFrequency(@RequestBody @Valid FrequencyRequestDto body) {
        var highestFrequency = service.calculateHighestFrequency(body.getText());

        return new WordFrequencyDto().frequency(highestFrequency);
    }

    @PostMapping("/frequency-for-word")
    @ResponseStatus(value = HttpStatus.OK)
    public WordFrequencyDto calculateFrequencyForWord(@RequestBody @Valid WordFrequencyRequestDto body) {
        var frequencyForWord = service.calculateFrequencyForWord(body.getText(), body.getWord());

        return new WordFrequencyDto().word(body.getWord()).frequency(frequencyForWord);
    }

    @PostMapping("/top-frequency")
    @ResponseStatus(value = HttpStatus.OK)
    public List<WordFrequencyDto> calculateTopNFrequency(@RequestBody @Valid TopFrequencyRequestDto body) {
        var wordFrequencies = service.calculateMostFrequentNWords(body.getText(), body.getN());

        return Arrays.stream(wordFrequencies)
                .map(Mapper::map).collect(Collectors.toList());
    }

}
