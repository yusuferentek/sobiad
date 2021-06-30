import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Properties;

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
            Query query = new Query("galatasaray");//bu kısıma aranacak kelimeyi giriyoruz
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                String url = "jdbc:mysql://localhost:3306/sobiad?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=Turkey";
                String user = "root";
                String password = "asdf1234";
                Connection myConn = DriverManager.getConnection(url, user, password);
                Statement mySt = myConn.createStatement();
                ResultSet rs=mySt.executeQuery("select TweetID from tweets");
                for (Status tweet : tweets) {
                    if (tweet.isRetweet()) {
                        continue;
                    } else {

                        try {

                            String sql = "INSERT INTO tweets (TweetID, KullaniciAdi, TweetMetni, TweetTarihi) "
                                    + "VALUES  (" + "'" + tweet.getId() + "' " + " , " + "'" + tweet.getUser().getScreenName() + "'" + "," + "'" + tweet.getText() + "'" + "," + "'" + tweet.getCreatedAt().toString() + "'" + ")";
                            mySt.executeUpdate(sql);
                            // Get system properties
                            Properties prop = new Properties();
                            prop.put("mail.smtp.auth", true);
                            prop.put("mail.smtp.starttls.enable", "true");
                            prop.put("mail.smtp.host", "smtp.gmail.com");
                            prop.put("mail.smtp.port", "587");
                            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                            Session session = Session.getInstance(prop, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("denemesender@gmail.com", "komo1125");
                                }
                            });
                            Message message = new MimeMessage(session);
                            message.setFrom(new InternetAddress("from@gmail.com"));
                            message.setRecipients(
                                    Message.RecipientType.TO, InternetAddress.parse("tekyusuferen23@gmail.com"));
                            message.setSubject("Mail Subject");
                            String id="";
                            id=Long.toString(tweet.getId());
                            String msg = tweet.getUser().getScreenName() + " --- " + tweet.getText() + " --- " + id ;
                            MimeBodyPart mimeBodyPart = new MimeBodyPart();
                            mimeBodyPart.setContent(msg,"text/html");
                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(mimeBodyPart);
                            message.setContent(multipart);
                            Transport.send(message);
                        } catch (SQLIntegrityConstraintViolationException e){
                            continue;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (AddressException e) {
                            e.printStackTrace();
                        } catch (MessagingException e) {
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

        } catch (TwitterException | SQLException te) {
            te.printStackTrace();
            System.out.println("Tweetler aranırken bir sorun oluştu: " + te.getMessage());
            System.exit(-1);
        }

    }
}





