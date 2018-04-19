import time
import json
import requests
import shutil
url = 'https://cryptocompare.com/media/12318415/42.png'
headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36'}    

with open('imagesJPG.txt') as fp:  
   for cnt, line in enumerate(fp):
      cc_data = json.loads(line)
      path = "imagesJPG/" + cc_data['Symbol'] + ".jpg";
      url = cc_data['ImageUrl']

      r = requests.get(url, stream=True, headers=headers)
      if r.status_code == 200:
          with open(path, 'wb') as f:
              r.raw.decode_content = True
              shutil.copyfileobj(r.raw, f)
      time.sleep(0.333)