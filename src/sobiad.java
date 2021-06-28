import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.*;
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

        TwitterFactory tf = new TwitterFactory(configurationBuider.build());
        twitter4j.Twitter twitter = tf.getInstance();
        int count = 0;
        try {
            Query query = new Query("sobiad");//bu kısıma aranacak kelimeyi giriyoruz
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    if (tweet.isRetweet()) {
                        continue;
                    } else {
                        String url = "jdbc:mysql://localhost:3306/sobiad?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=Turkey";
                        String user = "root";
                        String password = "asdf1234";
                        try {
                            Connection myConn = DriverManager.getConnection(url, user, password);
                            Statement mySt = myConn.createStatement();
                            String sql = "INSERT INTO tweets (TweetID, KullaniciAdi, TweetMetni, TweetTarihi) "
                                    + "VALUES  (" + "'" + tweet.getId() + "' " + " , " + "'" + tweet.getUser().getScreenName() + "'" + "," + "'" + tweet.getText() + "'" + "," + "'" + tweet.getCreatedAt().toString() + "'" + ")";
                            mySt.executeUpdate(sql);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        System.out.println("@" + tweet.getUser().getScreenName() + " --- " + tweet.getText() + " --- " + tweet.getId());
                        count++;
                    }
                }
            }
                while ((query = result.nextQuery()) != null) ;
                System.out.println("Çekilen tweet sayısı: " + count);
                System.exit(0);

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Tweetler aranırken bir sorun oluştu: " + te.getMessage());
            System.exit(-1);
        }

    }
}





