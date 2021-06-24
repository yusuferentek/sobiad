
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.ArrayList;

public class test {
     static void setup() {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("NQTxc2Wl059r3W14zUkAPIUZV");
        cb.setOAuthConsumerSecret("OVpKwXYKKlCA2bCGbA5xWbdSMjXGtcHxqR8U9J2WpWueFszsfO");
        cb.setOAuthAccessToken("970960034959101952-bwr7bg2rkGMMZjf9uzted0x8GSt80L6");
        cb.setOAuthAccessTokenSecret("xK8DgldHUOEhhztHVnCxKc2y7cUa84uonijb3Gf0jW2Wq");

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        Query query = new Query("sobiad");
        int numberOfTweets = 512;
        long lastID = Long.MAX_VALUE;
        ArrayList<Status> tweets = new ArrayList<Status>();
        while (tweets.size () < numberOfTweets) {
            if (numberOfTweets - tweets.size() > 100)
                query.setCount(100);
            else
                query.setCount(numberOfTweets - tweets.size());
            try {
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Gathered " + tweets.size() + " tweets");
                for (Status t: tweets)
                    if(t.getId() < lastID) lastID = t.getId();

            }

            catch (TwitterException te) {
                System.out.println("Couldn't connect: " + te);
            };
            query.setMaxId(lastID-1);
        }

        for (int i = 0; i < tweets.size(); i++) {
            Status t = (Status) tweets.get(i);

            GeoLocation loc = t.getGeoLocation();

            String user = t.getUser().getScreenName();
            String msg = t.getText();
            String time = "";
            if (loc!=null) {
                Double lat = t.getGeoLocation().getLatitude();
                Double lon = t.getGeoLocation().getLongitude();
                System.out.println(i + " USER: " + user + " wrote: " + msg + " located at " + lat + ", " + lon);
            }
            else
                System.out.println(i + " USER: " + user + " wrote: " + msg);
        }
    }

    public static void main(String[] args) {

        setup();
    }
}
