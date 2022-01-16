package nl.kooi.countingwords.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.kooi.countingwords.api.dto.ErrorResponseDto;
import nl.kooi.countingwords.api.dto.FrequencyRequestDto;
import nl.kooi.countingwords.api.dto.WordFrequencyDto;
import nl.kooi.countingwords.domain.WordFrequency;
import nl.kooi.countingwords.domain.WordFrequencyInfo;
import nl.kooi.countingwords.domain.service.WordFrequencyService;
import nl.kooi.countingwords.exception.WordProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static nl.kooi.countingwords.util.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(WordCountController.class)
class WordCountControllerTest {

    @Autowired
    private WordCountController controller;

    @MockBean
    private WordFrequencyService service;

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
        when(service.calculateHighestFrequency(TEXT)).thenReturn(3);

        var mvcResult = getAndVerifyResponse(HIGHEST_FREQUENCY_ENDPOINT, getFrequencyRequestDto(TEXT), status().isOk());

        var response = objectMapper.readValue(mvcResult.getContentAsString(), WordFrequencyDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getFrequency()).isEqualTo(3);
    }

    @Test
    void calculateFrequencyForWord() throws Exception {
        when(service.calculateFrequencyForWord(TEXT, "test")).thenReturn(3);

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
        var wfArray = new WordFrequency[2];

        wfArray[0] = WordFrequencyInfo.of("test", 3);
        wfArray[1] = WordFrequencyInfo.of("mockmvc", 2);


        when(service.calculateMostFrequentNWords(TEXT, 2)).thenReturn(wfArray);

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

        verify(service, never()).calculateFrequencyForWord(any(), any());
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

        verify(service, never()).calculateHighestFrequency(any());
    }

    @Test
    void calculateFrequencyForWord_ServiceThrowsWordException() throws Exception {
        var errorMessage = "Word !@#$!@#$ doesn't contain the required letters (a-z or A-Z).";

        when(service.calculateFrequencyForWord(TEXT, "!@#$!@#$"))
                .thenThrow(new WordProcessingException(errorMessage));

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