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
                //Twitter API Key Alanı +4 satır
                .setOAuthConsumerKey("NQTxc2Wl059r3W14zUkAPIUZV")
                .setOAuthConsumerSecret("OVpKwXYKKlCA2bCGbA5xWbdSMjXGtcHxqR8U9J2WpWueFszsfO")
                .setOAuthAccessToken("970960034959101952-bwr7bg2rkGMMZjf9uzted0x8GSt80L6")
                .setOAuthAccessTokenSecret("xK8DgldHUOEhhztHVnCxKc2y7cUa84uonijb3Gf0jW2Wq");

        TwitterFactory tf = new TwitterFactory(configurationBuider.build());
        twitter4j.Twitter twitter = tf.getInstance();
        int count = 0; // program çalışmayı bitirdiğinde kaç adet tweet çektiğini belirtmesi için ürettiğim sayaç.
        try {
            Query query = new Query("galatasaray");//bu kısıma search edilecek kelime girilir.
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
                    if (tweet.isRetweet()) {//bu if else in amacı rt edilen tweetleri çekmemek. Aynı tweetten iki adet bulunması gereksiz bir işlem olacaktır.
                        continue;
                    } else {

                        try {

                            String sql = "INSERT INTO tweets (TweetID, KullaniciAdi, TweetMetni, TweetTarihi) "
                                    + "VALUES  (" + "'" + tweet.getId() + "' " + " , " + "'" + tweet.getUser().getScreenName() +
                                    "'" + "," + "'" + tweet.getText() + "'" + "," + "'" + tweet.getCreatedAt().toString() + "'" + ")"; // veritabanına gönderilen sorgu
                            mySt.executeUpdate(sql);
                            // Get system properties
                            Properties prop = new Properties();
                            //Bu alan smtp ayar alanı şuan gmail e ayarlı kullanılacak alana göre değiştirilebilir. +5 satır.
                            prop.put("mail.smtp.auth", true);
                            prop.put("mail.smtp.starttls.enable", "true");
                            prop.put("mail.smtp.host", "smtp.gmail.com");
                            prop.put("mail.smtp.port", "587");
                            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                            Session session = Session.getInstance(prop, new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("denemesender@gmail.com", "komo1125"); //mail gönderecek olan mail hesabı kullanıcı adı ve şifresi bu alana giriliyor.
                                }
                            });
                            Message message = new MimeMessage(session);
                            message.setFrom(new InternetAddress("from@gmail.com"));
                            message.setRecipients(
                                    Message.RecipientType.TO, InternetAddress.parse("tekyusuferen23@gmail.com")); // mailin gönderileceği hesap ise buraya.
                            message.setSubject("Mail Subject"); // mail konusu
                            String id="";
                            id=Long.toString(tweet.getId()); // burada id yi stringe çevirmek zorunda kaldım çünkü sql sorgusunda hata alıyordum.
                            String msg = tweet.getUser().getScreenName() + " --- " + tweet.getText() + " --- " + id ;
                            MimeBodyPart mimeBodyPart = new MimeBodyPart();
                            mimeBodyPart.setContent(msg,"text/html; charset=UTF-8");
                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(mimeBodyPart);
                            message.setContent(multipart);
                            Transport.send(message);
                        } catch (SQLIntegrityConstraintViolationException e){ // veritabanında tweetid yi foreign key tanımladım. Her search ettiğimizde eski tweetleri defalarca veri tabanına kaydedip defalarca aynı tweeti mail almamak için. Catch blogunda eğer duplicate hatası alır isek o döngüyü geçiyoruz.
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





