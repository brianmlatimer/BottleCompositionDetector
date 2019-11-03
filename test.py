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

def convertBlobToNPArr(xGlob, xSet):
    for xFile in xGlob:
        xImg = cv2.imread(xFile)
        np.resize(xImg, (224, 224, 3))  # TODO: TEMOPORARY
        xSet = np.append(xSet, xImg)



# Turn yTrain and yTest into dicts from orderedDicts, then turn into a string


# # Access the Firebase storage and grab all relevant images
# bucket = storage.bucket()
#
# # Download all images available
#
#
# # Get the file locations for the images
# xTrainBlobGlass = bucket.blob("xTrain/glass{i}.jpg")
# xTrainBlobPlastic = bucket.blob("xTrain/plastic{i}.jpg")
# xTestBlobGlass = bucket.blob("xTest/glass{k}.jpg")
# xTestBlobPlastic = bucket.blob("xTest/plastic{k}.jpg")
#
# # Download the xTrain and xTest files onto the local machine
#
# xTrainBlobGlass.download_to_filename("xTrainFileGlass.jpg")
# xTrainBlobPlastic.download_to_filename("xTrainFilePlastic.jpg")
#
# # TODO: Remove print outputs
# print('Blob {} downloaded to {}.'.format(
#         xTrainBlob,
#         "xTrainFile"))

(xTrain, yTrain), (xTest, yTest) = tf.keras.datasets.mnist.load_data()

yTrainReal = tf.keras.utils.to_categorical(yTrain)
yTestReal = tf.keras.utils.to_categorical(yTest)


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

#model = tf.keras.applications.VGG16()

#model.compile(optimizer="SGD", loss="sparse_categorical_crossentropy", metrics=["accuracy"])

print(type(xTrain))
print(xTrain.shape)

#model.fit(xTrain, yTrainReal, epochs=10)

#model.evaluate(xTest,  yTestReal, verbose=2)