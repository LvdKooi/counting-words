package nl.kooi.countingwords.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.kooi.countingwords.api.dto.*;
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

    /**
     * POST /rest/word-count/highest-frequency : Calculate which word has the highest frequency in a text.
     *
     * @param body The FrequencyRequestDto object
     * @return The word frequency information object (status code 200)
     * or Bad request (status code 400)
     * or Server side problem (status code 500)
     */
    @ApiOperation(value = "Calculate the highest frequency of one word in a text.", nickname = "calculateHighestFrequency", response = FrequencyDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The frequency information", response = FrequencyDto.class),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorResponseDto.class),
            @ApiResponse(code = 500, message = "Server side problem", response = ErrorResponseDto.class)})
    @PostMapping(value = "/highest-frequency")
    public FrequencyDto calculateHighestFrequency(@RequestBody @Valid FrequencyRequestDto body) {
        return new FrequencyDto().frequency(service.calculateHighestFrequency(body.getText()));
    }

    /**
     * POST /rest/word-count/frequency-for-word : Calculate the frequency of a certain word in a text.
     *
     * @param body The FrequencyRequestDto object
     * @return The word frequency information object (status code 200)
     * or Bad request (status code 400)
     * or Server side problem (status code 500)
     */
    @ApiOperation(value = "Calculate the frequency of a certain word in a text.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The word frequency information", response = WordFrequencyDto.class),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorResponseDto.class),
            @ApiResponse(code = 500, message = "Server side problem", response = ErrorResponseDto.class)})
    @PostMapping("/frequency-for-word")
    @ResponseStatus(value = HttpStatus.OK)
    public WordFrequencyDto calculateFrequencyForWord(@RequestBody @Valid WordFrequencyRequestDto body) {
        var frequencyForWord = service.calculateFrequencyForWord(body.getText(), body.getWord());

        return new WordFrequencyDto().word(body.getWord().toLowerCase()).frequency(frequencyForWord);
    }

    /**
     * POST /rest/word-count/top-frequency : Calculate the top n highest frequency words in a text.
     *
     * @param body The TopFrequencyRequestDto object
     * @return A list of the top n word frequency information objects (status code 200)
     * or Bad request (status code 400)
     * or Server side problem (status code 500)
     */
    @ApiOperation(value = "Calculate the top n highest frequency words in a text.", nickname = "calculateTopNFrequency", response = WordFrequencyDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The word frequency information", response = WordFrequencyDto.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Bad request", response = ErrorResponseDto.class),
            @ApiResponse(code = 500, message = "Server side problem", response = ErrorResponseDto.class)})
    @PostMapping("/top-frequency")
    @ResponseStatus(value = HttpStatus.OK)
    public List<WordFrequencyDto> calculateTopNFrequency(@RequestBody @Valid TopFrequencyRequestDto body) {
        var wordFrequencies = service.calculateMostFrequentNWords(body.getText(), body.getN());

        return Arrays.stream(wordFrequencies)
                .map(Mapper::map).collect(Collectors.toList());
    }

}
