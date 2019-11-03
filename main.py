from __future__ import absolute_import, division, print_function, unicode_literals
import tensorflow as tf
import numpy as np
import firebase_admin
from firebase_admin import firestore
from firebase_admin import db
from firebase_admin import storage
import cv2
import glob
import os
import threading
from firebase_admin import messaging

os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="../vandyhacks-dbd1b-firebase-adminsdk-u7z46-eb00793d1d.json"


# Initialize our connection with Firebase
#credentials = firebase_admin.credentials.ApplicationDefault() # NOTE: only uncomment if using the Google Cloud Shell
credentials = firebase_admin.credentials.Certificate("../vandyhacks-dbd1b-firebase-adminsdk-u7z46-eb00793d1d.json")
firebase_admin.initialize_app(credentials, options= {
                                                        'project-id' : 'vandyhacks-dbd1b',
                                                        'storageBucket' : 'vandyhacks-dbd1b.appspot.com',
                                                        'databaseURL' : 'https://vandyhacks-dbd1b.firebaseio.com'
})

# Subscribe to the topic used for communication
topic = 'bcdMessaging' # bcd stands for our project, BottleCompositionDetector

registrationToken = 'AAAArSnKi2M:APA91bGEnpiiHpihogal7C5A34sLBcr8fPThAGHh1lr-s_DnWhAlm2_ODdWaoFmHdqVqX3KmElJBX_rKVjf_tsmQKBaduyNFfbPbF4B1c4gfZA-LgKruYgN7Eg0zPnBBbH0VcAGhMN3j'

subResponse = messaging.subscribe_to_topic(registrationToken, topic)

ref = db.reference('datasetCategories')
print(ref.get())

model = tf.keras.applications.VGG16(weights="")

model.compile(optimizer="SGD", loss="sparse_categorical_crossentropy", metrics=["accuracy"])

prediction = model.predict(inputFile) # Prediction that comes from our pre-trained neural network

# Send data over to Android App
message = messaging.Message(
    data = {
        'isPlastic' : prediction
    },
    topic = topic,
)

dataSend = messaging.send(message)