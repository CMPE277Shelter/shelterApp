from flask import Flask, jsonify, request
from flask import send_file
from pymongo import MongoClient
from bson.json_util import dumps
import base64
import os
import gridfs


app = Flask(__name__,static_url_path='/static')

def getPropertyId():
	return 1

def getCollection(collectionName):
        client = MongoClient('127.0.0.1', 27017)
        db = client['Shelter']
        collection = db[collectionName]
        return collection

@app.route('/image',methods=['POST'])
def saveImage():
        if 'owner_id' in request.args:
                owner=int(request.args['owner_id'])
        if 'property_id' in request.args:
                property=int(request.args['property_id'])
        if 'filename' in request.args:
                file=request.args['filename']
        if 'strByte' in request.args:
                strByte = request.args['strByte']
        #comment below two lines in prod
        #with open("/Users/Prasanna/Documents/watch1.jpg", "rb") as imageFile:        
        #      strByte = base64.b64encode(imageFile.read())
        client = MongoClient('127.0.0.1',27017)
        db = client.Shelter
        fs = gridfs.GridFS(db)
        fileId = fs.put(strByte, filename=file, owner_id=owner, property_id=property)
        db.properties.insert_one({'property_id':property,'owner_id':owner,'fileIdStr':str(fileId), 'fileId':fileId})

        return dumps({"fileId":str(fileId)})
        
@app.route('/images',methods=['GET'])
def getImages():
        properties = getCollection('properties')
        search_criteria={}
        if 'owner_id' in request.args:
                search_criteria['owner_id']=int(request.args['owner_id'])
        if 'property_id' in request.args:
                search_criteria['property_id']=int(request.args['property_id'])
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
        

        
@app.route('/postings/<owner_id>/<property_id>', methods=['GET'])
def getPostings(owner_id,property_id=""):
        postings=getCollection('postings')
        results = postings.find({"owner_id":int(owner_id),"property_id":int(property_id)},{'_id': False})
        return dumps(results)

@app.route('/postings',methods=['GET'])
def searchPosting():
	postings=getCollection('postings')
	search_criteria={}
	if 'owner_id' in request.args:
		search_criteria['owner_id']=int(request.args['owner_id'])
	if 'property_id' in request.args:
		search_criteria['property_id']=int(request.args['property_id'])
	if 'desc' in request.args:
		regex=re.compile("*"+request.args['desc']+"*", re.IGNORECASE)
		search_criteria['description']={"$regex":regex}
	if 'city' in request.args:
		search_criteria['address.city']=request.args['city']
	if 'zipcode' in request.args:
		search_criteria['address.zipcode']=int(request.args['zipcode'])
	if 'min_rent' in request.args and 'max_rent' in request.args:
		search_criteria['rent_details.rent']={"$gte":int(request.args['min_rent']),"$lte":int(request.args['max_rent'])}
	if 'property_type' in request.args:
		search_criteria['property_type']=request.args['property_type']
	
	results=postings.find(search_criteria,{'_id':False})
	return dumps(results)

@app.route('/postings/',methods=['POST'])
def createPosting():
	posting={
		"property_id":getPropertyId(),
		"owner_id":request.json['owner_id'],
		"address":[
			{
				"street":request.json['address'][0]['street'],
				"city":request.json['address'][0]['city'],
				"state":request.json['address'][0]['state'],
				"zipcode":request.json['address'][0]['zipcode']
			}
		],
		"property_type":request.json['property_type'],
		"details":[
			{
				"rooms":request.json['details'][0]['rooms'],
				"bath":request.json['details'][0]['bath'],
				"floor_area":request.json['details'][0]['floor_area'],
				"unit":"square feet",
				"small_unit":"sq-ft"
			}
		],
		"rent_details":[
			{
				"rent":request.json['rent_details'][0]['rent'],
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
                "isRentedOrCancelled":False,
		"description":request.json['description'],
		"more":[
			{
				"lease_type":request.json['more'][0]['lease_type'],
				"deposit":request.json['more'][0]['deposit']
			}
		]
	}

	postings=getCollection('postings')
	insertedId=postings.insert_one(posting)
	if insertedId:
		return dumps(posting)
	else:
		return dumps({"error":"Error Occured"})
	

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
                return dumps({"error":"Error Occured"})

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
                return dumps({"Status" : "OK"})
        else:
                return dumps({"error":"Error Occured"})


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
