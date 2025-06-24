package dev.quilla;

public record GptResponse(String id, String object, int created, String model, GptResponseChoice[] choices, GptResponseUsage usage, String service_tier, String system_fingerprint) {
}
