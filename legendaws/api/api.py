from __future__ import print_function
from django.shortcuts import render
from django.http import HttpResponse
import time
import boto3
import requests
import json
from rest_framework.response import Response
from .serializers import TranscriptionDataSerializer
from rest_framework.views import APIView
from rest_framework import status

class TranscriptionAPI(APIView):
    def post(self,request):
        dataObject = TranscriptionDataSerializer(data = request.data)
        if dataObject.is_valid():
            
            transcribe = boto3.client('transcribe')
            job_name = "legendit4"
            job_uri = "s3://legendit/Molico-Que-tal-repensar-o-leite-que-você-toma.flac"
    
            transcribe.start_transcription_job(
                TranscriptionJobName=job_name,
                Media={'MediaFileUri': job_uri},
                MediaSampleRateHertz=44100,
                MediaFormat='flac',
                LanguageCode='pt-BR'
            )
            while True:
                resultado = transcribe.get_transcription_job(TranscriptionJobName=job_name)
                if resultado['TranscriptionJob']['TranscriptionJobStatus'] in ['COMPLETED', 'FAILED']:
                    break    
            time.sleep(5)

            #Retorna o Json da legenda criada
            result = requests.get(str(resultado["TranscriptionJob"]["Transcript"]["TranscriptFileUri"]))
            #Tive que colocar pra retornar o json pra string e depois converter pra json novament, pois estava retornando com "\" no meio.
            transcript = result.text
            retorno = json.loads(transcript)
                                          
            return Response(retorno, status = 200)
        else:
            return Response(dataObject.data, status = 400)       
    
        
           
          