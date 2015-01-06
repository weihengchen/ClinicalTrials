import sys
import urllib
import urllib2
import json
import socket

def getLatLng(content):
    st = content.find('{');
    en = content.rfind('}');
    content = content[st:en+1];
    tmp = json.loads(content);
    if tmp[u'primary'] == None or tmp[u'primary'][u'GeocodeFulfillments'] == None or len(tmp[u'primary'][u'GeocodeFulfillments']) == 0:
        return None;
    if len(tmp[u'primary'][u'GeocodeFulfillments'][0][u'QueryParseResults']) == 0:
        return None;
    geocoding = tmp[u'primary'][u'GeocodeFulfillments'][0][u'QueryParseResults'][0][u'GeocodingResults'];
    if len(geocoding) == 0:
        return None;
    pos = geocoding[0][u'BestLocation'][u'Coordinates'];
    lat = pos[u'Latitude'];
    lng = pos[u'Longitude'];

    return [lat, lng];

if __name__=='__main__':
    url_head = 'https://maps.googleapis.com/maps/api/geocode/json?';
    url_head = 'http://www.bing.com/maps/search.ashx?'
    url_tail = '&wh=&n=11&mkt=%22en-us%22&cp=%22-30.955450479819675%2C119.59677505493164%22&si=0&ob=&r=80&md=%22999%2C159%22&z=6&qh=&ep=&oj=&ai=%22eal%22&ca=&cid=&jsonso=r83&jsonp=microsoftMapsNetworkCallback&culture=%22en-us%22&token=AnsYKp7hJUXcBGx19mDT6fOTh6n-Bxlcjw-FRuT3sAGvL7JY4GNL_Z_b_eJnnnwI'

    fin = open(sys.argv[1]);
    cnt = int(sys.argv[2]);
    fout = open(sys.argv[3],"a", 0);
    ferr = open(argv[4],"a", 0);
    for line in fin.readlines():
        if cnt > 0:
            cnt-=1;
            continue;
        line = line.strip('\n').strip('\r');
        info = {};
        info['q'] = line
        url = url_head + urllib.urlencode(info) + url_tail;
        fileurl = None;
        print url;
        while True:
            try:
                fileurl = urllib2.urlopen(url);
            except urllib2.URLError as e:
                sys.stderr.write('[URLError]\t'+line+'\n');
                continue;
            except urllib2.HTTPError as e:
                sys.stderr.write('[HTTPError]\t'+line+'\n');
            except socket.error as e:
                sys.stderr.write('[SocketError]\t'+line+'\n');
                continue;
            break;
        content = fileurl.read();
        coord = getLatLng(content);
        if coord == None:
            ferr.write(line+'\n');
        else:
            line = line+'\t'+str(coord[0])+'\t'+str(coord[1]);
            fout.write(line+'\n');
    fin.close();
    ferr.close();
    fout.close();

