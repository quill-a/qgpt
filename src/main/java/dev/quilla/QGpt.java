package dev.quilla;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Scanner;

public class QGpt {
    public static void main(String[] args) throws IOException, InterruptedException {

        // Grab the API key from environment variable(s).
        String apiKeyStr = Optional.ofNullable(System.getenv("API_KEY")).orElseThrow(() ->
                new IllegalStateException("API_KEY env var is not defined"));

        // User input
        String prompt;
        if(args.length > 0){
            prompt = args[0];
        }  else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a string to search for: ");
            prompt = scanner.nextLine();
        }

        // Instantiate the GptRequest object and convert it to a string for HttpRequest.
        ObjectMapper objectMapper = new ObjectMapper();
        GptRequest gptRequest = new GptRequest("gpt-3.5-turbo-instruct", prompt, 1, 100);
        String userInput = objectMapper.writeValueAsString(gptRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " +  apiKeyStr)
                .POST(HttpRequest.BodyPublishers.ofString(userInput))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {

            // Instantiate a response object and grab the response that is at the last index, which is
            // assumed to be the most relevant.
            var gptResponse = objectMapper.readValue(response.body(), GptResponse.class);
            String answer = gptResponse.choices()[gptResponse.choices().length - 1].text();

            if(!answer.isEmpty()){

                // Remove any newline characters from the response and print to the console.
                System.out.println(answer.replace("\n", ""));
            }

        } else {

            // Print out the error code and response if there is an issue retrieving the response.
            System.out.println(response.statusCode());
            System.out.println(response.body());
        }
    }
}
