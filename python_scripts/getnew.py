import json
import urllib2
from pprint import pprint

url = 'https://api.coinmarketcap.com/v1/ticker/?convert=EUR&limit=2000'
our_data = json.load(open('/home/yorick/Repositories/WalletKeepApp/app/src/main/res/raw/cryptocurrencies.json'))
cmc_data = json.load(urllib2.urlopen(url))

pprint(our_data[0]['ticker'])
pprint(cmc_data[0]['symbol'])
pprint("_________________")

for cmc_item in cmc_data:
  found = False;
  for our_item in our_data:
    if cmc_item['symbol'] in our_item['ticker']:
      found = True;
  if (not found):
    print '  {"ticker" : "%s", "name" : "%s"},' % (cmc_item['symbol'], cmc_item['name'])
