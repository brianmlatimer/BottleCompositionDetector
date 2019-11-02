from __future__ import absolute_import, division, print_function, unicode_literals
import tensorflow as tf
import numpy as np
import firebase_admin
from firebase_admin import firestore
from firebase_admin import db
from firebase_admin import storage
from cv2 import imread
import glob
import os

os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="../vandyhacks-dbd1b-firebase-adminsdk-u7z46-eb00793d1d.json"


# Initialize our connection with Firebase
#credentials = firebase_admin.credentials.ApplicationDefault() # NOTE: only uncomment if using the Google Cloud Shell
credentials = firebase_admin.credentials.Certificate("../vandyhacks-dbd1b-firebase-adminsdk-u7z46-eb00793d1d.json")
firebase_admin.initialize_app(credentials, options= {
                                                        'project-id' : 'vandyhacks-dbd1b',
                                                        'storageBucket' : 'vandyhacks-dbd1b.appspot.com',
                                                        'databaseURL' : 'https://vandyhacks-dbd1b.firebaseio.com'
})

ref = db.reference('datasetCategories')
print(ref.get())

yTrain = ref.order_by_child('train').equal_to(True).get()
yTest = ref.order_by_child('train').equal_to(False).get()

# Access the Firebase storage and grab all relevant images
bucket = storage.bucket()

# Get the file locations for the images
xTrainBlob = bucket.blob("xTrain/")
xTestBlob = bucket.blob("xTest/")

# Download the xTrain and xTest files onto the local machine
xTrainBlob.download_to_filename("xTrainFile")
xTestBlob.download_to_filename("xTestFile")

# Access the xTrain and xTest files
xTrainGlob = glob.glob("xTrainFile/*.jpg")
xTestGlob = glob.glob("xTestFile/*.jpg")

# Get placeholders for the xTrain and xTest
xTrain = np.zeros(0)
xTest = np.zeros(0)

# Access each xTrain and xTest image and add them to a uint8 array of RGB images (this is so tensorflow can process them).
for xTrainFile in xTrainGlob:
    xTrainImg = imread(xTrainFile)
    np.append(xTrain, xTrainImg)

for xTestFile in xTestGlob:
    xTestImg = imread(xTestFile)
    np.append(xTest, xTestImg)


xTrain, xTest = xTrain / 255.0, xTest / 255.0

# Change the training labels to one-hot encoding
yTrain = tf.keras.utils.to_categorical(yTrain)
yTest = tf.keras.utils.to_categorical(yTest)

#model = tf.keras.models.Sequential([
  #tf.keras.layers.Flatten(input_shape=(32, 32, 3)),
  #tf.keras.layers.Conv2D(16, (2, 2), input_shape=(32, 32, 3), padding='same', activation='sigmoid'),
  #tf.keras.layers.Dense(128, activation='softmax'),
  #tf.keras.layers.Dropout(0.2),
  #tf.keras.layers.Dense(10, activation='sigmoid')
#])

#model.compile(optimizer='adam',
#              loss='sparse_categorical_crossentropy',
#              metrics=['accuracy'])

# Input nodes
#inputNodes = tf.Variable(tf.zeros(shape=(32*32)), name="inputNodes")

# First convolution / max pooling round
#hiddenLayer = tf.nn.conv2d(inputNodes, (3, 3, 1, 16), padding="same")  # hiddenLayer will be used repeatedly to save memory
#hiddenLayer = tf.nn.bias_add(hiddenLayer, 16)

model = tf.keras.applications.VGG16()

model.compile(optimizer="SGD", loss="sparse_categorical_crossentropy", metrics=["accuracy"])

model.fit(xTrain, yTrain, epochs=10)

model.evaluate(xTest,  yTest, verbose=2)