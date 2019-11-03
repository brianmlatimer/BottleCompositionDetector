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

# Access the xTrain and xTest files
xTrainGlob = glob.glob("xTrain/*.jpg")
xTestGlob = glob.glob("xTest/*.jpg")

# Get placeholders for the xTrain and xTest
xTrain = np.zeros(0)
xTest = np.zeros(0)
print("\n")
print("Test0")
# Access each xTrain and xTest image and add them to a uint8 array of RGB images (this is so tensorflow can process them).
t1 = threading.Thread(target=convertBlobToNPArr, args=(xTrainGlob, xTrain))
t2 = threading.Thread(target=convertBlobToNPArr, args=(xTestGlob, xTest))

# Start threading
t1.start()
t2.start()

# End threading when finished
t1.join()
t2.join()
print("Done!")
# for xTrainFile in xTrainGlob:
#     xTrainImg = cv2.imread(xTrainFile)
#     np.resize(xTrainImg, (224, 224, 3)) #TODO: TEMOPORARY
#     xTrain = np.append(xTrain, xTrainImg)
#     print(xTrainFile)
# print("Test1")
# for xTestFile in xTestGlob:
#     xTestImg = cv2.imread(xTestFile)
#     xTestImg.resize((224, 224, 3)) #TODO: TEMPORARY
#     xTest = np.append(xTest, xTestImg)
#     print(xTestFile)
# print("Test2")
yTrainReal = yTestReal = []

# Convert yTest and yTrain from nested orderedDicts to an array of classifications (0 = BOTTLE, 1 = PLASTIC)
for x in (list(yTrain.items())):
    pictureName = x[1]['name']
    if (pictureName[0] == 'p'):
        yTrainReal.append(1)
    else:
        yTrainReal.append(0)

for j, w in enumerate(list(yTrain.items())):
    isPlastic = w[1]['name']
    if (isPlastic[0] == 'p'):
        yTrainReal.append(1)
    else:
        yTrainReal.append(0)

#xTrain, xTest = xTrain / 255.0, xTest / 255.0

# Change the training labels to one-hot encoding
yTrainReal = tf.keras.utils.to_categorical(yTrainReal)
yTestReal = tf.keras.utils.to_categorical(yTestReal)


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

print(xTrain)
print(yTrainReal)
print("Test")

model.fit(xTrain, yTrainReal, epochs=10)

model.evaluate(xTest,  yTestReal, verbose=2)