// Imports the Google Cloud client library

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;
import com.google.protobuf.ByteString;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TesteSrt {

    /**
     * Demonstrates using the Speech API to transcribe an audio file.
     */
    String GOOGLE_APPLICATION_CREDENTIALS = "C:\\Users\\Greg\\Documents\\GoogleSpeech\\Legendit-ed02e700db97.json";

    public static void main(String... args) throws Exception {
        // Instantiates a client
        try (SpeechClient speechClient = SpeechClient.create()) {

            System.out.println("entrou no try");

            // The path to the audio file to transcribe
            String fileName = "C:\\Users\\Greg\\Molico-Que-tal-repensar-o-leite-que-você-toma.flac";

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .clearEncoding()
                    .setEncoding(AudioEncoding.FLAC)
                    .setSampleRateHertz(44100)
                    .setEnableWordTimeOffsets(true)
                    .setLanguageCode("pt-BR")
                    .setAudioChannelCount(2)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Use non-blocking call for getting file transcription
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response
                    = speechClient.longRunningRecognizeAsync(config, audio);
            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();

            FileWriter arquivoSrt = new FileWriter("d:\\testeSrt.srt");
            PrintWriter gravarArq = new PrintWriter(arquivoSrt);
            
            String legendaFinal;

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.              
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

                for (WordInfo wordInfo : alternative.getWordsList()) {
                    System.out.printf(
                            "\t%s.%s sec - %s.%s sec\n",
                            wordInfo.getStartTime().getSeconds(),
                            wordInfo.getStartTime().getNanos() / 100000000,
                            wordInfo.getEndTime().getSeconds(),
                            wordInfo.getEndTime().getNanos() / 100000000);
                    
                    System.out.println(wordInfo.getWord());
                    gravarArq.printf(wordInfo.getWord(), true);
                }
                //System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }

            arquivoSrt.close();

        }
    }
}
