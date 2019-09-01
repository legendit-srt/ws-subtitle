
from __future__ import print_function
import time
import boto3
import requests
transcribe = boto3.client('transcribe')
job_name = "legendit11"
job_uri = "s3://legendit/Molico-Que-tal-repensar-o-leite-que-você-toma.flac"
transcribe.start_transcription_job(
    TranscriptionJobName=job_name,
    Media={'MediaFileUri': job_uri},
    MediaSampleRateHertz=44100,
    MediaFormat='flac',
    LanguageCode='pt-BR'
)
while True:
    response = transcribe.get_transcription_job(TranscriptionJobName=job_name)
    if response['TranscriptionJob']['TranscriptionJobStatus'] in ['COMPLETED', 'FAILED']:
        break
    print("Quase pronto...")
    time.sleep(5)

#Retorna o Json da legenda criada
result = requests.get(str(response["TranscriptionJob"]["Transcript"]["TranscriptFileUri"]))
transcript = result.text

#Imprime o Json na tela
print(transcript)

#Salva o arquivo Json na maquina (Nao fucniona ainda)
#arquivo = open("C:\legenda.json", "w")
#arquivo.write(transcript)
#arquivo.close()
