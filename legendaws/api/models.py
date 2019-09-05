from django.db import models

# Create your models here.
class TranscriptionData(models.Model):
    nome = models.CharField(max_length=200)
    url = models.CharField(max_length=200)
    media = models.CharField(max_length=200)
    
    def publish(self):        
        self.save()

    def __str__(self):
        return self.nome