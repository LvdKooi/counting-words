package nl.kooi.countingwords.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Positive;

@Data
@EqualsAndHashCode(callSuper = true)
public class TopFrequencyRequestDto extends FrequencyRequestDto {
    @Positive(message = "field [n] can't be smaller than 1")
    private int n;
}
