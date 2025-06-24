package dev.quilla;

public record GptResponseUsage(int prompt_tokens, int completion_tokens, int total_tokens, Object prompt_tokens_details, Object completion_tokens_details) {
}
