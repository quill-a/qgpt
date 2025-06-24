package dev.quilla;

public record GptResponseChoice(String text, int index, Object logprobs, String finish_reason) {
}
