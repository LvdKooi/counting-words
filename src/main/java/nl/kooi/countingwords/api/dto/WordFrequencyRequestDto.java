package nl.kooi.countingwords.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class WordFrequencyRequestDto extends FrequencyRequestDto {
    @NotNull(message = "field [word] can't be null")
    private String word;
}
