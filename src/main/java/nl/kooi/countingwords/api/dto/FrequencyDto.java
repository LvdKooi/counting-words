package nl.kooi.countingwords.api.dto;

import lombok.Data;

@Data
public class FrequencyDto {
    protected int frequency;

    public FrequencyDto frequency(int frequency) {
        this.frequency = frequency;
        return this;
    }
}
