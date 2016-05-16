from flask import Flask, jsonify, request
from flask import send_file
from pymongo import MongoClient
from bson.json_util import dumps
import base64
import os
import gridfs
import re
import pymongo
import uuid

from gcm import *

app = Flask(__name__,static_url_path='/static')

def getPropertyId():
	return str(uuid.uuid1())

def getCollection(collectionName):
        client = MongoClient('127.0.0.1', 27017)
        db = client['Shelter']
        collection = db[collectionName]
        return collection
        
def notify(tokenId,count,search_name):
        gcm = GCM("AIzaSyBCVyH_fKL_rXaad0GJ-CfukwbX1X3JQOo")
        data = {"the_message": "We have "+str(count)+" new results for "+search_name}
        #client = MongoClient('127.0.0.1',27017)
        #db = client['Shelter']
        #collection = db['token']
        #results = collection.find({"key":"token"})
        #for record in results:
        #         tokenId = record['tokenId']
        #         print tokenId
        #reg_id = 'e6LPXuJb0Fk:APA91bEwguI3nz9G_VVHtp3BlgI8cnl-3wA35VbjirdpGBBbAjUnCiHAD_dhvjTlxxTOREI-wbnjbbI-ZrX1P8CWmg0CaaL4Fk53bgtIeDp7xjG1OwPA894I5BMCjCI7WTIoZKfizBCG'
        #reg_id = 'tokenfEflMpztF6M:APA91bGXTK409MAf9xbrCuuMON23QyX17zxfnVmsVh_j2zul9_bK2NTpKjSpHK-Suq6QWT8mL0EFeAiFgm4QmF8sImUiDumoBtgqfvisCN0-iDYOleZc6ibuj96KlW6U7JLME__6Sprd'
        reg_id = tokenId
        gcm.plaintext_request(registration_id=reg_id, data=data)

def pushNotification(property_id, owner_id):
	print 'in push notification,'
        postings = getCollection('postings')
        searches = getCollection('searches')
        for prop in postings.find({'property_id':property_id,'owner_id':owner_id},{'_id':False}):
                properTy = prop
                break
	print properTy
        if properTy is not None:
		print 'property not none'
		print properTy
                for indSearch in searches.find({'frequency':'Realtime'},{'_id':False}):
                        search_criteria = {}
                        shouldNotify = True
                        if indSearch['haskeyword'] == True:
                                keyWrd = indSearch['keyword']
                                pattern = '.*'+keyword+'.*'
                                regex = re.compile(pattern,re.IGNORECASE)
                                if(len(re.findall(regex,keyWrd))>=1):
                                        shouldNotify = shouldNotify and True
                                else:
                                     shouldNotify = shouldNotify and False

			print 'after keyword matching'
			print shouldNotify

                        if indSearch['hascity'] == True:
				propCity = properTy['address'][0]['city']
				indCity = indSearch['city']
                                if propCity == indCity:
                                        shouldNotify = shouldNotify and True
                                else:
                                        shouldNotify = shouldNotify and False
			print 'after city'
			print shouldNotify

                        if indSearch['haszipcode'] == True:
                                if properTy['address'][0]['zipcode'] == indSearch['zipcode']:
                                        shouldNotify = shouldNotify and True
                                else:
                                        shouldNotify = shouldNotify and False

                                # search_criteria['address.zipcode']=int(indSearch['zipcode'])
			print 'after zipcode'
			print shouldNotify
			
			print indSearch['hasminrent']

                        if indSearch['hasminrent'] == True and indSearch['hasmaxrent']:
                                if properTy['rent_details'][0]['rent']>=indSearch['minrent'] and properTy['rent_details'][0]['rent']>=indSearch['maxrent']:
                                        shouldNotify = shouldNotify and True
                                else:
                                        shouldNotify = shouldNotify and False
                                # search_criteria['rent_details.rent']={"$gte":int(indSearch['minrent']),"$lte":int(indSearch['maxrent'])}
                        elif indSearch['hasminrent'] == True:
                                if properTy['rent_details'][0]['rent']>=indSearch['minrent']:
                                        shouldNotify = shouldNotify and True
                                else:
                                        shouldNotify = shouldNotify and False
                                # search_criteria['rent_details.rent']={"$gte":int(indSearch['minrent'])}
                        elif indSearch['hasmaxrent'] == True:
                                if properTy['rent_details'][0]['rent']<=indSearch['maxrent']:
                                        shouldNotify = shouldNotify and True
                                else:
                                        shouldNotify = shouldNotify and False
                                # search_criteria['rent_details.rent']={"$lte":int(indSearch['maxrent'])}
                        if indSearch['haspropertyType'] == True:
                                if indSearch['propertyType']=='All' or indSearch['propertyType']==properTy['property_type']:
					shouldNotify = shouldNotify and True
				else:
					shouldNotify = shouldNotify and False
                                # search_criteria['property_type']=indSearch['propertyType']
                        	# search_criteria['is_rented_or_cancel']=False
			print shouldNotify
                        if shouldNotify:
                                token = getCollection('token')
				tokenId = ''
                                for tokenIdStruct in token.find({'user_id':indSearch['user']}):
                                        tokenId = tokenIdStruct['tokenId']
				print 'notifying'
                                notify(tokenId,1,indSearch['name'])




@app.route('/updatetoken',methods=['PUT'])
def updateToken():
	print "In update Token"
	tokenId = request.json['tokenId']
	user_id = request.json['user_id']
	print str(tokenId)
	print str(user_id)
        client = MongoClient('127.0.0.1',27017)
        db = client.Shelter
        if 'token' not in db.collection_names():
                token = db.token
		print "in 1st insert"
                updatedId = token.insert_one({'user_id':user_id,'tokenId':tokenId})
        else:
                token = getCollection('token')
		print "in else"
		if token.find({"user_id":user_id}):
                	updatedId = token.update_one({'user_id':user_id},{'$set':{'tokenId':tokenId}},upsert=True)
		else:
			updatedId = token.insert_one({"user_id":user_id,"tokenId":tokenId})
        if updatedId:
                return dumps([{'Status':'OK'}])
        else:
                return dumps([{'Error':'Error Occurred'}])

@app.route('/trendingProperty',methods=['GET'])
def getTrendingProperty():
        postings = getCollection('postings')
        maxViewCountProperty = postings.find({'is_rented_or_cancel':False},{'_id':False}).sort('view_count',pymongo.DESCENDING).limit(1)
        if maxViewCountProperty.count()>0:
                return dumps(maxViewCountProperty)
        else:
                return dumps([])


@app.route('/image',methods=['POST'])
def saveImage():
	owner = request.json['owner_id']
	property = request.json['property_id']
	file = request.json['filename']
	strByte = request.json['strByte']
        #comment below two lines in prod
        #with open("/Users/Prasanna/Documents/watch1.jpg", "rb") as imageFile:        
        #      strByte = base64.b64encode(imageFile.read())
        client = MongoClient('127.0.0.1',27017)
        db = client.Shelter
        fs = gridfs.GridFS(db)
        fileId = fs.put(strByte, filename=file, encoding='utf-8', owner_id=owner, property_id=property)
        db.properties.insert_one({'property_id':property,'owner_id':owner,'fileIdStr':str(fileId), 'fileId':fileId})

        return dumps([{"fileId":str(fileId)}])
        
@app.route('/images',methods=['GET'])
def getImages():
        properties = getCollection('properties')
        search_criteria={}
        if 'owner_id' in request.args:
                search_criteria['owner_id']=request.args['owner_id']
        if 'property_id' in request.args:
                search_criteria['property_id']=request.args['property_id']
        results=properties.find(search_criteria,{'_id':False})
	return dumps(results)

@app.route('/image',methods=['GET'])
def getImage():
        homePart = '/home/ubuntu/ShelterImages/'
        #comment below line
        #homePart = '/Users/Prasanna/Documents/'
        fileName=homePart+request.args['fileId']+'.jpg'
        client = MongoClient('127.0.0.1',27017)
        db = client.Shelter
        fs = gridfs.GridFS(db)
        search_criteria={}
        if 'fileId' in request.args:
                search_criteria['fileIdStr']=request.args['fileId']

        properties = getCollection('properties')        
        results=properties.find(search_criteria,{'_id':False})
        for record in properties.find(search_criteria,{'_id':False}):
                fileId = record['fileId']
        imageData = fs.get(fileId).read().decode('base64')
        with open(fileName,"wb") as f:
                f.write(imageData)
        return send_file(fileName)
        
@app.route('/drawable',methods=['GET'])
def getDrawableImage():
        homePart='/home/ubuntu/ShelterImages/'
        requestedFile=request.args['filename']
        client=MongoClient('127.0.0.1',27017)
        db = client.Drawable
        fs = gridfs.GridFS(db)
        search_criteria = {}
        search_criteria['image_name']=requestedFile
        images = db.images
        results = images.find(search_criteria,{'_id':False})
        for record in images.find(search_criteria,{'_id':False}):
                fileId = record['fileId']
        imageData = fs.get(fileId).read().decode('base64')
        with open(homePart+requestedFile,"wb") as f:
                f.write(imageData)
        return send_file(homePart+requestedFile)

        
@app.route('/postings/<owner_id>/<property_id>', methods=['GET'])
def getPostings(owner_id,property_id=""):
        postings=getCollection('postings')
        results = postings.find({"owner_id":owner_id,"property_id":property_id},{'_id': False})
        return dumps(results)

@app.route('/postings',methods=['GET'])
def searchPosting():
	postings=getCollection('postings')
	search_criteria={}
        if 'keyword' in request.args:
                keyword = str(request.args['keyword'])
                pattern = '.*'+keyword+'.*'
                regex = re.compile(pattern,re.IGNORECASE)
                search_criteria['description']={"$regex":regex}
	if 'city' in request.args:
		search_criteria['address.city']=request.args['city']
	if 'zipcode' in request.args:
		search_criteria['address.zipcode']=int(request.args['zipcode'])
	if 'min_rent' in request.args and 'max_rent' in request.args:
		search_criteria['rent_details.rent']={"$gte":int(request.args['min_rent']),"$lte":int(request.args['max_rent'])}
        elif 'min_rent' in request.args:
                search_criteria['rent_details.rent']={"$gte":int(request.args['min_rent'])}
        elif 'max_rent' in request.args:
                search_criteria['rent_details']={"$lte":int(request.args['max_rent'])}
	if 'property_type' in request.args:
		search_criteria['property_type']=request.args['property_type']
	search_criteria['is_rented_or_cancel']=False
	
	results=postings.find(search_criteria,{'_id':False})
	
	finalResults = []
	if 'owner_id' in request.args:
                user_id = request.args['owner_id']
                favorites = getCollection('favourites')
                favProperties = []
                favOwnerIds = []
                for fav in favorites.find({'user_id':user_id},{'_id':False}):
                        favDetail = fav['FavDetails']
                        for eachFav in favDetail:
                                favProperties.append(eachFav['property_id'])
                                favOwnerIds.append(eachFav['owner_id'])
                for property in postings.find(search_criteria,{'_id':False}):
                        if property['property_id'] in favProperties and property['owner_id']==favOwnerIds[favProperties.index(property['property_id'])]:
                                p = property
                                p['is_favorite']=True
                                finalResults.append(p)
                        else:
                                finalResults.append(property)
        
	alongWithImageURLs = []
	properties = getCollection('properties')
	for posting in finalResults:
                imageURLs = []
                fileIds = properties.find({'property_id':posting['property_id'],'owner_id':posting['owner_id']},{'fileIdStr':1,'_id':0})
                for fileId in fileIds:
                        imageURLs.append('http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/image?fileId='+fileId['fileIdStr'])
                p = posting
                p['images']=imageURLs
                alongWithImageURLs.append(p)
                
	return dumps(alongWithImageURLs)

@app.route('/ownerpostings',methods=['GET'])
def searchownerPosting():
	postings=getCollection('postings')
	search_criteria={}
	if 'owner_id' in request.args:
                search_criteria['owner_id']=request.args['owner_id']
        if 'keyword' in request.args:
                keyword = str(request.args['keyword'])
                pattern = '.*'+keyword+'.*'
                regex = re.compile(pattern,re.IGNORECASE)
                search_criteria['description']={"$regex":regex}
	if 'city' in request.args:
		search_criteria['address.city']=request.args['city']
	if 'zipcode' in request.args:
		search_criteria['address.zipcode']=int(request.args['zipcode'])
	if 'min_rent' in request.args and 'max_rent' in request.args:
		search_criteria['rent_details.rent']={"$gte":int(request.args['min_rent']),"$lte":int(request.args['max_rent'])}
        elif 'min_rent' in request.args:
                search_criteria['rent_details.rent']={"$gte":int(request.args['min_rent'])}
        elif 'max_rent' in request.args:
                search_criteria['rent_details']={"$lte":int(request.args['max_rent'])}
	if 'property_type' in request.args:
		search_criteria['property_type']=request.args['property_type']

	
	results=postings.find(search_criteria,{'_id':False})
	
	finalResults = []
	if 'owner_id' in request.args:
                user_id = request.args['owner_id']
                favorites = getCollection('favourites')
                favProperties = []
                favOwnerIds = []
                for fav in favorites.find({'user_id':user_id},{'_id':False}):
                        favDetail = fav['FavDetails']
                        for eachFav in favDetail:
                                favProperties.append(eachFav['property_id'])
                                favOwnerIds.append(eachFav['owner_id'])
                for property in postings.find(search_criteria,{'_id':False}):
                        if property['property_id'] in favProperties and property['owner_id']==favOwnerIds[favProperties.index(property['property_id'])]:
                                p = property
                                p['is_favorite']=True
                                finalResults.append(p)
                        else:
                                finalResults.append(property)
        
	alongWithImageURLs = []
	properties = getCollection('properties')
	for posting in finalResults:
                imageURLs = []
                fileIds = properties.find({'property_id':posting['property_id'],'owner_id':posting['owner_id']},{'fileIdStr':1,'_id':0})
                for fileId in fileIds:
                        imageURLs.append('http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/image?fileId='+fileId['fileIdStr'])
                p = posting
                p['images']=imageURLs
                alongWithImageURLs.append(p)
                
	return dumps(alongWithImageURLs)

@app.route('/postings/',methods=['POST'])
def createPosting():
        if 'property_id' in request.json:
		print 'Updating'
                property_id = request.json['property_id']
                owner_id = request.json['owner_id']
                property_name=request.json['property_name']
                street=request.json['address'][0]['street']
                city=request.json['address'][0]['city']
                state=request.json['address'][0]['state']
                zipcode=int(request.json['address'][0]['zipcode'])
                property_type=request.json['property_type']
                rooms=int(request.json['details'][0]['rooms'])
                bath=int(request.json['details'][0]['bath'])
                floor_area=int(request.json['details'][0]['floor_area'])
                rent=int(request.json['rent_details'][0]['rent'])
                phone_number=int(request.json['owner_contact_info'][0]['phone_number'])
                display_phone=request.json['owner_contact_info'][0]['display_phone']
                email=request.json['owner_contact_info'][0]['email']
                description=request.json['description']
                
                postings=getCollection('postings')
                updateId = postings.update_one({"property_id":property_id,"owner_id":owner_id},{"$set": {"property_name":property_name,"address.0.street":street,"address.0.city":city,"address.0.state":state,"address.0.zipcode":zipcode,"property_type":property_type,"details.0.rooms":rooms,"details.0.bath":bath,"details.0.floor_area":floor_area,"rent_details.0.rent":rent,"owner_contact_info.0.phone_number":phone_number,"owner_contact_info.0.display_phone":display_phone,"owner_contact_info.0.email":email,"description":description}})
                
                if updateId:
                        return dumps([{"property_id":property_id}])
                else:
                        return dumps([{"error":"Error Occured"}])
                
        else:
                property_id =  getPropertyId()
                posting={
                        "property_id":property_id,
                        "property_name":request.json['property_name'],
                        "owner_id":request.json['owner_id'],
                        "view_count":0,
                        "images":[],
                        "address":[
                                {
                                        "street":request.json['address'][0]['street'],
                                        "city":request.json['address'][0]['city'],
                                        "state":request.json['address'][0]['state'],
                                        "zipcode":int(request.json['address'][0]['zipcode'])
                                }
                        ],
                        "property_type":request.json['property_type'],
                        "details":[
                                {
                                        "rooms":int(request.json['details'][0]['rooms']),
                                        "bath":int(request.json['details'][0]['bath']),
                                        "floor_area":int(request.json['details'][0]['floor_area']),
                                        "unit":"square feet",
                                        "small_unit":"sq-ft"
                                }
                        ],
                        "rent_details":[
                                {
                                        "rent":int(request.json['rent_details'][0]['rent']),
                                        "unit":"US dollar",
                                        "small_unit":"USD"
                                }
                        ],
                        "owner_contact_info":[
                                {
                                        "phone_number":int(request.json['owner_contact_info'][0]['phone_number']),
                                        "display_phone":request.json['owner_contact_info'][0]['display_phone'],
                                        "email":request.json['owner_contact_info'][0]['email']
                                }
                        ],
                        "is_favorite":False,
                        "is_rented_or_cancel":False,
                        "description":request.json['description'],
                        "more":[
                                {
                                        "lease_type":request.json['more'][0]['lease_type'],
                                        "deposit":int(request.json['more'][0]['deposit'])
                                }
                        ]
                }
                
                postings=getCollection('postings')
                insertedId=postings.insert_one(posting)
                if insertedId:
                        pushNotification(property_id,request.json['owner_id'])
                        return dumps([posting])
                else:
                        return dumps([{"error":"Error Occured"}])

def strToBool(val):
	if val == 'True':
	        return True
	else:
		return False

def toInt(val):
        if val=='':
                return -1
        else:
                return int(val)

@app.route('/savesearch/',methods=['POST'])
def saveSearch():
	search={
		"id":request.json['id'],
                "user":request.json['user'],
                "name":request.json['name'],
                "frequency":request.json['frequency'],
                "keyword":request.json['keyword'],
                "city":request.json['city'],
                "zipcode":toInt(request.json['zipcode']),
                "minrent":toInt(request.json['minrent']),
                "maxrent":toInt(request.json['maxrent']),
                "staticmapurl":request.json['staticmapurl'],
                "propertyType":request.json['propertyType'],
                "haskeyword":strToBool(str(request.json['haskeyword'])),
                "hascity":strToBool(str(request.json['hascity'])),
                "haszipcode":strToBool(str(request.json['haszipcode'])),
                "hasminrent":strToBool(str(request.json['hasminrent'])),
                "hasmaxrent":strToBool(str(request.json['hasmaxrent'])),
                "haspropertyType":strToBool(str(request.json['haspropertyType']))
	}

	searches=getCollection('searches')
	insertedId=searches.insert_one(search)
	if insertedId:
		return dumps([search])
	else:
		return dumps([{"error":"Error Occured"}])

@app.route('/getsearch',methods=['GET'])
def createSearch():
	searches=getCollection('searches')
	search_criteria={}
	if 'user' in request.args:
		search_criteria['user']=request.args['user']
	if 'id' in request.args:
                search_criteria['id']=request.args['id']
        
	results=searches.find(search_criteria,{'_id':False})
	return dumps(results)

@app.route('/createuser/',methods=['POST'])
def createUser():
        user={
                "user_id":request.json['user_id'],
                "name":request.json['name']
        }
        userDetails = getCollection('user')
        insertedId = userDetails.insert_one(user)
        if insertedId:
                return dumps(user)
        else:
                return dumps({"error":"Error Occured"})

@app.route('/favorites',methods=['GET'])
def getfavorites():
	search_criteria={}
	if 'user_id' in request.args:
		user_id = str(request.args['user_id'])
		search_criteria['user_id']=user_id
		favorites = getCollection('favourites')
		postings = getCollection('postings')
		userFavList= []
		favDetails=[]
		for userFavoriteInfo in favorites.find(search_criteria,{'_id':False}):
			favDetails =  userFavoriteInfo['FavDetails']
		for favorite in favDetails:
			property_id = favorite['property_id']
			owner_id = favorite['owner_id']
			for posting in postings.find({'property_id':property_id,'owner_id':owner_id},{'_id':False}):
				p = posting
				p['is_favorite']= True
				userFavList.append(p)
		return dumps(userFavList)
	else:
		return dumps([{'Error':'User not found'}])
		
@app.route('/addfavourite/',methods=['POST'])
def addFavourite():
        user_id = request.json['user_id']
        fav_details =[]
        fav = request.json['property_id']
        owner_id = request.json['owner_id']
        dict = {"property_id":fav,"owner_id":owner_id}
        favourites = getCollection('favourites')
        results = favourites.find({"user_id":user_id},{'_id': False})
        for record in results:
                user = record['user_id']
                print user
                if user == user_id:
                        fav_details = record['FavDetails']
        fav_details.append(dict)
        user = {
                "user_id":user_id,
                "FavDetails":fav_details
        }
        if len(fav_details) == 0:
                favourites.insert_one(user)
        else:
                updateId = favourites.update_one({"user_id":user_id},{"$set": {"FavDetails":fav_details}},upsert = True)
        print updateId
        if updateId:
                return dumps(user)
        else:
                return dumps([{"error":"Error Occured"}])

@app.route('/removefavourite/<user_id>/<property_id>/<owner_id>',methods=['DELETE'])
def deleteFavourite(user_id,property_id,owner_id=""):
        favourites = getCollection('favourites')
        search_criteria = {}
        if 'user_id' in request.args:
                search_criteria['user_id'] = user_id
        if 'property_id' in request.args:
                search_criteria['FavDetails.property_id'] = property_id
        if 'owner_id' in request.args:
                search_criteria['FavDetails.owner_id'] = owner_id
        results=favourites.find(search_criteria,{'_id':False})
        dict = {"property_id":property_id,"owner_id":owner_id}
        for record in results:
                user = record['user_id']
                print user
                if user == user_id:
                        fav_details = record['FavDetails']
                        if len(fav_details) == 0:
                                favourites.remove({"user_id":record['user_id']})
        if dict in fav_details:
                fav_details.remove(dict)
        updateId = favourites.update_one({"user_id":user_id},{"$set": {"FavDetails":fav_details}},upsert = True)
        if updateId:
                return dumps([{"Status" : "OK"}])
        else:
                return dumps([{"error":"Error Occured"}])

@app.route('/isrented/',methods=['PUT'])
def isRented():
        postings = getCollection('postings')
        isPropRented = request.json['isRented']
        search_criteria={}
        owner_id = request.json['owner_id']
        search_criteria['owner_id']=request.json['owner_id']
        property_id = request.json['property_id']
        search_criteria['property_id']=request.json['property_id']

        results=postings.find(search_criteria,{'_id':False})
        for record in results:
                print record
                fowner = record['owner_id']
                fprop = record['property_id']
        updateId = postings.update_one({"owner_id":fowner,"property_id":fprop},{"$set": {"is_rented_or_cancel":isPropRented}},upsert = True)

        if updateId:
                return dumps([{"Status" : "OK"}])
        else:
                return dumps([{"error":"Error Occured"}])       
        
@app.route('/incrementViewCount/',methods=['PUT'])
def incrementViewCount():
        postings = getCollection('postings')
        search_criteria = {}
        owner_id = request.json['owner_id']
        search_criteria['owner_id']=request.json['owner_id']
        property_id = request.json['property_id']
        search_criteria['property_id']=request.json['property_id']
        updateId = postings.update_one({"owner_id":owner_id,"property_id":property_id},{"$inc": {"view_count":1}},upsert = False)
        if updateId:
                return dumps([{"Status" : "OK"}])
        else:
                return dumps([{"error":"Error Occured"}])


@app.route('/all/', methods=['GET'])
def getAllPostings():
        postings=getCollection('postings')
        results = postings.find({},{'_id': False})
        return dumps(results)

@app.route('/')
def getIndex():
        return "Shelter API"

if __name__ == '__main__':
        app.run(debug=True, host='0.0.0.0')

