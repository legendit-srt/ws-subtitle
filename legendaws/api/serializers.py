from rest_framework import serializers
from .models import TranscriptionData

class TranscriptionDataSerializer(serializers.Serializer):
    id = serializers.ReadOnlyField()
    nome = serializers.CharField()
    url = serializers.CharField()
    
    def create(self, validate_data):
        instance = TranscriptionData()
        instance.nome = validate_data.get('nome')
        instance.url = validate_data.get('url')
        return instance
    
    