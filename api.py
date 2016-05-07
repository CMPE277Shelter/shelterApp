from flask import Flask, jsonify, request
from pymongo import MongoClient
from bson.json_util import dumps
import re

app = Flask(__name__,static_url_path='/static')

def getPropertyId():
        return 1


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

@app.route('/all/', methods=['GET'])
def getAllPostings():
        postings=getCollection('postings')
        results = postings.find({},{'_id': False})
        return dumps(results)

def getCollection(collectionName):
        client = MongoClient('127.0.0.1', 27017)
        db = client['Shelter']
        collection = db[collectionName]
        return collection


@app.route('/')
def getIndex():
        return "Shelter API"

if __name__ == '__main__':
        app.run(debug=True, host='0.0.0.0')