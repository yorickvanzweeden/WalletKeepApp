import pyrebase
import json
import requests


config = {
  "apiKey": "AIzaSyCBuQXqQangAFbPD2z4gmCBTmxQ8ZfkGF0 ",
  "authDomain": "adept-comfort-192710.firebaseapp.com",
  "databaseURL": "https://adept-comfort-192710.firebaseio.com/",
  "storageBucket": "adept-comfort-192710.appspot.com",
  "serviceAccount": "credentials.json"
}

firebase = pyrebase.initialize_app(config)


class DataObject:
  symbol = []
  price_usd = []
  price_btc = []
  price_eur = []
  percent_change_1h = []
  percent_change_24h = []
  percent_change_7d = []

def getCMCData():
  cmc_url = "https://api.coinmarketcap.com/v1/ticker/?convert=EUR&limit=2000"
  json_response = requests.get(url=cmc_url).content.decode("UTF-8")
  with open("cmc_output.txt", "w") as f:
    f.write(json_response)

def getCMCDataFromFile():
  return json.load(open('cmc_output.txt'))

def parse(json):
  # Arrays
  data = DataObject()

  for item in json:
    if item['symbol'] is None:
      print("Skipped: {} | Grounds: No ticker".format(item['symbol']))
      continue
    
    invalid_chars = set('.#$/[]')
    if any((c in invalid_chars) for c in item['symbol']):
      print("Skipped: {} | Grounds: Invalid symbol for key".format(item['symbol']))
      continue

    data.symbol.append(item['symbol'])
    data.price_eur.append(          0               if item['price_eur'] is None          else item['price_eur'])
    data.price_usd.append(          0               if item['price_usd'] is None          else item['price_usd'])
    data.price_btc.append(          0               if item['price_btc'] is None          else item['price_btc'])
    data.percent_change_1h.append(  0               if item['percent_change_1h'] is None  else item['percent_change_1h'])
    data.percent_change_24h.append( 0               if item['percent_change_24h'] is None else item['percent_change_24h'])
    data.percent_change_7d.append(  0               if item['percent_change_7d'] is None  else item['percent_change_7d'])

  return data

def createUpdateObject(dataObject):
  data = {}

  for i in range(0, len(dataObject.symbol)):
    childNode = {}
    childNode['price_usd'] = dataObject.price_usd[i]
    childNode['price_eur'] = dataObject.price_eur[i]
    childNode['price_btc'] = dataObject.price_btc[i]
    childNode['percent_change_1h'] = dataObject.percent_change_1h[i]
    childNode['percent_change_24h'] = dataObject.percent_change_24h[i]
    childNode['percent_change_7d'] = dataObject.percent_change_7d[i]

    data['prices/' + dataObject.symbol[i] + '/'] = childNode
    
  return data


db = firebase.database()
getCMCData()
jsonData = createUpdateObject(parse(getCMCDataFromFile()))
db.update(jsonData)