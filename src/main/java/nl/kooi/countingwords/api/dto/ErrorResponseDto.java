package nl.kooi.countingwords.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ErrorResponseDto {
    private String reason;
    private UUID reference;

    public ErrorResponseDto reason(String reason) {
        this.reason = reason;
        return this;
    }

    public ErrorResponseDto reference(UUID uuid) {
        this.reference = uuid;
        return this;
    }
}

