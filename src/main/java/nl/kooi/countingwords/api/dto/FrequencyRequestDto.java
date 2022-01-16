package nl.kooi.countingwords.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FrequencyRequestDto {
    @NotNull(message = "field [text] can't be null")
    private String text;
}
