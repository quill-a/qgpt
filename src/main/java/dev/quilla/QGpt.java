package dev.quilla;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Scanner;

public class QGpt {
    public static void main(String[] args) throws IOException, InterruptedException {



        String keyStr = getKey();
//        System.out.println(keyStr);

        // Initialize Scanner for user input.
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string to search for: ");
        String searchString = scanner.nextLine();

        // Instantiate the GptRequest object and convert it to a string for HttpRequest.
        ObjectMapper objectMapper = new ObjectMapper();
        GptRequest gptRequest = new GptRequest("gpt-4o-mini", searchString, 1, 100);
        String userInput = objectMapper.writeValueAsString(gptRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/completions"))  //https://api.github.com/users/gpt-4o-mini/repos
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " +  keyStr)
                .POST(HttpRequest.BodyPublishers.ofString(userInput))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {

            // Instantiate a response object and grab the response that is at the last index, which is
            // assumed to be the most relevant.
            var GptResponse = objectMapper.readValue(response.body(), GptResponse.class);
            String answer = GptResponse.choices()[GptResponse.choices().length - 1].text();

            if(!answer.isEmpty()){

                // Remove any newline characters given in the response.
                answer = answer.replace("\n", "");

                // TODO Figure out how to trim GPT's responses to remove the '**A ' and everything after the last whitespace.

                System.out.println(answer);
            }

        } else {

            // Print out the error code and response if there is an issue retrieving the response.
            System.out.println(response.statusCode());
            System.out.println(response.body());
        }
    }

    public static String getKey() throws IOException {

        // Take key.txt and convert it to String object for interpolation in main
        File keyFile = new File(".env/key.txt");
        return new String(Files.readAllBytes(keyFile.toPath()));
    }

}
