// Imports the Google Cloud client library
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

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

      // Performs speech recognition on the audio file
      RecognizeResponse response = speechClient.recognize(config, audio);
      List<SpeechRecognitionResult> results = response.getResultsList();

      for (SpeechRecognitionResult result : results) {
        // There can be several alternative transcripts for a given chunk of speech. Just use the
        // first (most likely) one here.
        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
        System.out.printf("Transcription: %s%n", alternative.getTranscript());
      }
    }
  }
}