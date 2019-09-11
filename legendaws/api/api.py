from __future__ import print_function
from django.shortcuts import render
from django.http import HttpResponse
import boto3
import requests
import json
from rest_framework.response import Response
from .serializers import TranscriptionDataSerializer
from rest_framework.views import APIView
from rest_framework import status
import asyncio

class TranscriptionAPI(APIView):
    def post(self,request):
        dataObject = TranscriptionDataSerializer(data = request.data)
        s3Url = "s3://legendit/"
        if dataObject.is_valid():
            
            transcribe = boto3.client('transcribe', region_name='us-west-2')
            job_name = dataObject.data['nome']
            job_uri = s3Url + job_name            
            job_media = job_name.split(".")[1]
            job_name = job_name.split(".")[0]
            
            transcribe.start_transcription_job(
                TranscriptionJobName=job_name,
                Media={'MediaFileUri': job_uri},
                MediaFormat=job_media,
                LanguageCode='pt-BR'
            )
            while True:
                resultado = transcribe.get_transcription_job(TranscriptionJobName=job_name)
                if resultado['TranscriptionJob']['TranscriptionJobStatus'] in ['COMPLETED', 'FAILED']:
                    break
                asyncio.sleep(5)
                
            #Retorna o Json da legenda criada
            result = requests.get(str(resultado["TranscriptionJob"]["Transcript"]["TranscriptFileUri"]))
            #Tive que colocar pra retornar o json pra string e depois converter pra json novament, pois estava retornando com "\" no meio ¯\_(ツ)_/¯.
            transcript = result.text
            retorno = json.loads(transcript)
                                          
            return Response(retorno, status = 200)
        else:
            return Response(dataObject.data, status = 400)       
    
        
           
          