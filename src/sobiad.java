import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class sobiad {
    public static void main(String[] args) throws TwitterException {
        ConfigurationBuilder configurationBuider = new ConfigurationBuilder();
        configurationBuider.setDebugEnabled(true)
                .setOAuthConsumerKey("NQTxc2Wl059r3W14zUkAPIUZV")
                .setOAuthConsumerSecret("OVpKwXYKKlCA2bCGbA5xWbdSMjXGtcHxqR8U9J2WpWueFszsfO")
                .setOAuthAccessToken("970960034959101952-bwr7bg2rkGMMZjf9uzted0x8GSt80L6")
                .setOAuthAccessTokenSecret("xK8DgldHUOEhhztHVnCxKc2y7cUa84uonijb3Gf0jW2Wq");
/*
        TwitterFactory tf=new TwitterFactory(configurationBuider.build());
        twitter4j.Twitter twitter=tf.getInstance();
        String aranacakKelime = "yusuf";
        Query query = new Query(aranacakKelime);
        QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                if(status.isRetweet()){
                    continue;
                }else {
                System.out.println("@" + status.getUser().getScreenName() + " : " + status.getText() + " : " + status.getId());
            }}
*/
        TwitterFactory tf=new TwitterFactory(configurationBuider.build());
        twitter4j.Twitter twitter=tf.getInstance();

        try {
            Query query = new Query("yusuf");//bu kısıma aranacak kelimeyi giriyoruz
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }

/*
        List<Status> status=twitter.getHomeTimeline();
        for(Status s:status){
            HashtagEntity[] hashTags = s.getHashtagEntities();
            for (HashtagEntity hashtag : hashTags) {
                if (hashtag.equals(hashTagToSearchFor)) {
                    System.out.println(s.getUser().getName() + ": " + s.getText());

                    continue;
                }
            }
        }
*/

    }



