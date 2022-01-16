package nl.kooi.countingwords.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.kooi.countingwords.api.ControllerExceptionAdvice;
import nl.kooi.countingwords.api.WordCountController;
import nl.kooi.countingwords.api.dto.ErrorResponseDto;
import nl.kooi.countingwords.api.dto.FrequencyDto;
import nl.kooi.countingwords.api.dto.FrequencyRequestDto;
import nl.kooi.countingwords.api.dto.WordFrequencyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static nl.kooi.countingwords.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class WordCountApplicationIntegrationTests {

    @Autowired
    private WordCountController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private static final String TEXT = "test, test, test, mockmvc, mockmvc, ordina";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(ControllerExceptionAdvice.class).build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void calculateHighestFrequency() throws Exception {

        var mvcResult = getAndVerifyResponse(HIGHEST_FREQUENCY_ENDPOINT, getFrequencyRequestDto(TEXT), status().isOk());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), FrequencyDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getFrequency()).isEqualTo(3);
    }

    @Test
    void calculateFrequencyForWord() throws Exception {

        var mvcResult =
                getAndVerifyResponse(HIGHEST_FREQUENCY_FOR_WORD_ENDPOINT,
                        getWordFrequencyRequestDto(TEXT, "test"),
                        status().isOk());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), WordFrequencyDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getFrequency()).isEqualTo(3);
        assertThat(response.getWord()).isEqualTo("test");
    }

    @Test
    void calculateTopNFrequency() throws Exception {
        var mvcResult =
                getAndVerifyResponse(TOP_FREQUENCY_ENDPOINT, getToprequencyRequestDto(TEXT, 2), status().isOk());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), new TypeReference<List<WordFrequencyDto>>() {
        });

        assertThat(response).isNotNull();
        assertThat(response).hasSize(2);

        assertThat(response.get(0).getWord()).isEqualTo("test");
        assertThat(response.get(0).getFrequency()).isEqualTo(3);
        assertThat(response.get(1).getWord()).isEqualTo("mockmvc");
        assertThat(response.get(1).getFrequency()).isEqualTo(2);
    }

    @Test
    void calculateTopNFrequency_calculateMostFrequentNWords_SameFrequencyAscAlphabeticalOrder() throws Exception {
        var text = "PYthon&Java!kotlin+java8Angular3kotlin angular";

        var mvcResult =
                getAndVerifyResponse(TOP_FREQUENCY_ENDPOINT, getToprequencyRequestDto(text, 3), status().isOk());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), new TypeReference<List<WordFrequencyDto>>() {
        });

        assertThat(response).hasSize(3);
        assertThat(response.get(0).getFrequency()).isEqualTo(2);
        assertThat(response.get(0).getWord()).isEqualTo("angular");
        assertThat(response.get(1).getFrequency()).isEqualTo(2);
        assertThat(response.get(1).getWord()).isEqualTo("java");
        assertThat(response.get(2).getFrequency()).isEqualTo(2);
        assertThat(response.get(2).getWord()).isEqualTo("kotlin");
    }

    @Test
    void calculateFrequencyForWord_NotNegativeFieldsAreNull() throws Exception {
        var mvcResult =
                getAndVerifyResponse(TOP_FREQUENCY_ENDPOINT,
                        getToprequencyRequestDto(TEXT, 0),
                        status().isBadRequest());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), ErrorResponseDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getReason())
                .isEqualTo("The following fields were invalid: field [n] can't be smaller than 1");
    }


    @Test
    void calculateFrequencyForWord_NotNullableFieldsAreNull() throws Exception {
        var mvcResult =
                getAndVerifyResponse(HIGHEST_FREQUENCY_FOR_WORD_ENDPOINT,
                        getWordFrequencyRequestDto(null, null),
                        status().isBadRequest());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), ErrorResponseDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getReason())
                .contains("The following fields were invalid:")
                .contains("field [word] can't be null")
                .contains("field [text] can't be null");
    }

    @Test
    void calculateFrequencyForWord_ServiceThrowsWordException() throws Exception {
        var errorMessage = "Word !@#$!@#$ doesn't contain the required letters (a-z or A-Z).";

        var mvcResult =
                getAndVerifyResponse(HIGHEST_FREQUENCY_FOR_WORD_ENDPOINT,
                        getWordFrequencyRequestDto(TEXT, "!@#$!@#$"),
                        status().isBadRequest());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), ErrorResponseDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getReason()).isEqualTo(errorMessage);
    }


    private <T extends FrequencyRequestDto> MockHttpServletResponse getAndVerifyResponse(String endpoint,
                                                                                         T requestDto,
                                                                                         ResultMatcher statusExpectation)
            throws Exception {

        return mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(statusExpectation)
                .andReturn()
                .getResponse();
    }

}
