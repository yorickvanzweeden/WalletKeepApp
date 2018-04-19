from google.cloud import firestore
from urllib2 import urlopen
import json


class DataObject:
  symbol = []
  name = []
  price_usd = []
  price_btc = []
  price_eur = []
  percent_change_1h = []
  percent_change_24h = []
  percent_change_7d = []

class FsClient(object):
    __instance = None

    def __new__(cls):
        if cls.__instance == None:
            cls.__instance = firestore.Client.from_service_account_json('credentials.json')
        return cls.__instance


def commitData(data_object):
  db = FsClient()
  batch = db.batch()

  for i, ticker in enumerate(data_object.symbol):
    data = {
        u'name': data_object.name[i],
        u'ticker': ticker,
    }
    # Add a new doc in collection 'cities' with ID 'LA'
    db.collection(u'currencies').document(ticker).set(data)

  # Commit the batch
  batch.commit()

def getCMCData():
  cmc_url = "https://api.coinmarketcap.com/v1/ticker/?convert=EUR&limit=2000"
  return json.load(urlopen(cmc_url))

def getCMCDataFromFile():
  return json.load(open('cmc_output.json'))

def parse(json):
  # Arrays
  data = DataObject()

  for item in json:
    data.symbol.append(item['symbol'])
    data.name.append(item['name'])
    data.price_eur.append(item['price_eur'])
    data.price_usd.append(item['price_usd'])
    data.price_btc.append(item['price_btc'])
    data.percent_change_1h.append(item['percent_change_1h'])
    data.percent_change_24h.append(item['percent_change_24h'])
    data.percent_change_7d.append(item['percent_change_7d'])

  return data


commitData(parse(getCMCDataFromFile()))