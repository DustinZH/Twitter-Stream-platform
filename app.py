import json
from kafka import SimpleProducer, KafkaClient
import tweepy
import configparser

import sys  
reload(sys)  
sys.setdefaultencoding('utf-8')

class TweeterStreamListener(tweepy.StreamListener):
    """ A class to read the twitter stream and push it to Kafka"""

    def __init__(self, api):
        self.api = api
        super(tweepy.StreamListener, self).__init__()
        client = KafkaClient("localhost:9092")
        self.producer = SimpleProducer(client, async = True,
                          batch_send_every_n = 1000,
                          batch_send_every_t = 10)
        
        # self.producer = KafkaProducer(value_serializer=lambda m: json.dumps(m).encode('ascii'))

    def on_status(self, status):
        """ This method is called whenever new data arrives from live stream.
        We asynchronously push this data to kafka queue"""
        # msg =  status.text.encode('utf-8')
        msg =  self.get_message(status)
        try:
            self.producer.send_messages(b'twitterstream', msg)
            # self.producer.send('twitterstream', msg)
        except Exception as e:
            print(e)
            return False
        return True

    # msg: (text, favorite, retweet, country, city, state, longitude, latitude)
    def get_message(self, status):
        msg = str(status.text) + '<*****>' + str(status.favorite_count) + '<*****>' + str(status.retweet_count)
        if (status.place is not None):
            if (status.place.country is not None):
                country = status.place.country
                msg += '<*****>' + str(country).encode('utf-8')
           
            if (status.place.full_name is not None):
                full_name = status.place.full_name
                full_name = full_name.encode("ASCII", 'ignore')
                full_name = full_name.split(",")
                if (len(full_name) == 2):
                    city = full_name[0]
                    state = full_name[1][1:]
                    msg += '<*****>' + str(city) + '<*****>' + str(state)
            
            if (status.place.bounding_box is not None):
                bounding_box = status.place.bounding_box
                if (bounding_box.coordinates is not None):
                    coordinates = bounding_box.coordinates[0][0]
                    lng = coordinates[0]
                    lat = coordinates[1]

                    msg += '<*****>' + str(lng) + '<*****>' + str(lat)
           
        return msg 

    def on_error(self, status_code):
        #print("Error received in kafka producer")
        #return True # Don't kill the stream
        print(status_code)

    def on_timeout(self):
        return True # Don't kill the stream

if __name__ == '__main__':

    # Read the credententials from 'twitter-app-credentials.txt' file
    config = configparser.ConfigParser()
    config.read('twitter-app-credentials.txt')
    consumer_key = config['DEFAULT']['consumerKey']
    consumer_secret = config['DEFAULT']['consumerSecret']
    access_key = config['DEFAULT']['accessToken']
    access_secret = config['DEFAULT']['accessTokenSecret']
    

    # Create Auth object
    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_key, access_secret)
    api = tweepy.API(auth)

    # Create stream and bind the listener to it
    stream = tweepy.Stream(auth, listener = TweeterStreamListener(api))

    #Custom Filter rules pull all traffic for those filters in real time.
    #stream.filter(track = ['love', 'hate'], languages = ['en'])
    stream.filter(locations=[-180,-90,180,90], languages = ['en'])
