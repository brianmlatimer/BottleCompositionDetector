import glob
import firebase
import firebase_admin
from firebase_admin import firestore
import numpy as np
from cv2 import imread
from firebase_admin import db

# 1 - 450 are training images for glass
# 451-501 are testing images for glass

# 1 - 435 are training images for plastic
# 436 - 482 are testing images for plastic

credentials = firebase_admin.credentials.Certificate("../vandyhacks-dbd1b-firebase-adminsdk-u7z46-eb00793d1d.json")
firebase_admin.initialize_app(credentials, options= {
                                                        'databaseURL' : 'https://vandyhacks-dbd1b.firebaseio.com/',
                                                        'project-id' : 'vandyhacks-dbd1b',
                                                        'storageBucket' : 'vandyhacks-dbd1b.appspot.com'
})

database = db.reference("datasetCategories")

rulesReference = db.reference("/")

fptr = open("dataset-resized/labelSet.txt", "r")

for i in range(1, 501):
    line = fptr.readline()
    category = line[-2] # Gets the second to last line, which will be the category (hopefully not \n)
    filename = line[0:-3]
    for x in filename:
        if (x == '.'):
            x = '-'
    if (i <= 450):
        database.push({

            u'name' : line[0:-2],
            u'train' : True,
            u'type': u"BOTTLE",
        })
    else:
        database.push({
            u'name': line[0:-2],
            u'train': False,
            u'type': u"BOTTLE",
        })
        #database.collection(u'vandyhacks-dbd1b').document(line[0:-2]).set(data)

for i in range(1, 482):
    line = fptr.readline()
    category = line[-2] # Gets the second to last line, which will be the category (hopefully not \n)
    if (i <= 435):
        database.push({

            u'name' : line[0:-2],
            u'train' : True,
            u'type' : u"PLASTIC"

        })
        #database.collection(u'vandyhacks-dbd1b').document(line[0:-2]).set(data)
    else:
        database.push({
            u'name': line[0:-2],
            u'train': False,
            u'type': u"PLASTIC",
            })
        #database.collection(u'vandyhacks-dbd1b').document(line[0:-2]).set(data)

fptr.close()






