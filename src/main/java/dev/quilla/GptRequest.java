package dev.quilla;

public record GptRequest(String model, String prompt, int temperature, int max_tokens) {
}
