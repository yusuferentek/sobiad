import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class sobiad {

    public static void main(String[] args) throws TwitterException {
        String[] query = new String[5];//ben deneme amaçlı localde çalıştığım için tablo adları içinde bu arrayi kullandım. Tablo adları için 5 indisli bir arrray oluşturulmasını öneriyorum. Array kullanımı 71. satırdadır.
        query[0]="sobiad";
        query[1]="asosindex";
        query[2]="bookcitestr";
        query[3]="akademiktv";
        query[4]="pirikeşifaraci";

        String[] maillerArray= new String[5];
        maillerArray[0]="sobiad@sobiad.com";
        maillerArray[1]="asos@asosindex.com.tr";
        maillerArray[2]="info@bookcites.com";
        maillerArray[3]="akademiktv@akademiktv.com";
        maillerArray[4]="info@kesifaraci.com";

        ConfigurationBuilder configurationBuider = new ConfigurationBuilder();
        configurationBuider.setDebugEnabled(true)
                //Twitter API Key Alanı +4 satır
                .setOAuthConsumerKey("NQTxc2Wl059r3W14zUkAPIUZV")
                .setOAuthConsumerSecret("OVpKwXYKKlCA2bCGbA5xWbdSMjXGtcHxqR8U9J2WpWueFszsfO")
                .setOAuthAccessToken("970960034959101952-bwr7bg2rkGMMZjf9uzted0x8GSt80L6")
                .setOAuthAccessTokenSecret("xK8DgldHUOEhhztHVnCxKc2y7cUa84uonijb3Gf0jW2Wq");

        TwitterFactory tf = new TwitterFactory(configurationBuider.build());
        twitter4j.Twitter twitter = tf.getInstance();
        for (int j = 0; j < 5;j++) {

            int count = 0; // program çalışmayı bitirdiğinde kaç adet tweet çektiğini belirtmesi için ürettiğim sayaç.
            try {
                    String queryStr=query[j];
                    Query querys = new Query(queryStr);//bu kısıma search edilecek kelime girilir.
                    QueryResult result;

                    do {

                        result = twitter.search(querys);
                        List<Status> tweets = result.getTweets();
                        String url = "jdbc:mysql://localhost:3306/sobiad?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=Turkey";
                        String user = "root";
                        String password = "asdf1234";
                        Connection myConn = DriverManager.getConnection(url, user, password);
                        Statement mySt = myConn.createStatement();
                       // ResultSet rs = mySt.executeQuery("select TweetID from tweets");
                        for (Status tweet : tweets) {
                            if (tweet.isRetweet()) {//bu if else in amacı rt edilen tweetleri çekmemek. Aynı tweetten iki adet bulunması gereksiz bir işlem olacaktır.
                                continue;
                            } else {

                                try {
                                    //String tweetMetni="";
                                    StringBuilder tweetMetni = new StringBuilder(tweet.getText());
                                    for (int i = 0; i < tweetMetni.length(); i++) {
                                        if (tweetMetni.charAt(i) == '\'') {
                                            tweetMetni.setCharAt(i, ' ');
                                        }
                                    }
                                    String tweetDate;
                                    tweetDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(tweet.getCreatedAt());
                                    String tweetUrl = "https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + tweet.getId();
                                    String sql = "INSERT INTO "+ "`"+query[j] +"`" +" (TweetID, KullaniciAdi, TweetMetni, BegeniSayisi, RetweetSayisi, TweetTarihi, TweetURL) "
                                            + "VALUES  (" + tweet.getId() + " , " + "'" + tweet.getUser().getScreenName() + "'" +
                                            "," + "'" + tweetMetni + "'" + "," + tweet.getFavoriteCount() + "," + tweet.getRetweetCount() + "," + "'" + tweetDate + "'" + "," + "'" + tweetUrl + "'" + ")"; // veritabanına gönderilen sorgu

                                    mySt.executeUpdate(sql);
                                    String msg = "<b> Kullanıcı Adı: </b>@" + tweet.getUser().getScreenName() + "<br>" + "<b>Tweet içeriği:</b> " + tweet.getText() + "<br>" + "<b>Beğeni sayısı:</b> " + tweet.getFavoriteCount() + "     " + "<b>Retweet Sayısı:</b> " + tweet.getRetweetCount() + "     " + "<b>Tarih:</b> " + tweetDate + "<br>" + "<b>Tweet Linki: </b>" + tweetUrl;
                                    MailGun mp = new MailGun();
                                    String a = mp.sendMail_withISPESCongress(maillerArray[j], "mail konusu", msg , "mail rapor adı");
                                    System.out.println("a = " + a);
                                } catch (SQLIntegrityConstraintViolationException e) { // veritabanında tweetid yi foreign key tanımladım. Her search ettiğimizde eski tweetleri defalarca veri tabanına kaydedip defalarca aynı tweeti mail almamak için, Catch blogunda eğer duplicate hatası alır isek o döngüyü geçiyoruz.
                                    continue;
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                count++; // Buradaki sayacımız her çekilen tweette 1 artıyor.
                            }
                        }
                    }
                    while ((querys = result.nextQuery()) != null);
                    System.out.println(queryStr+" için çekilen tweet sayısı: " + count);
                    //System.exit(0);

                }catch(TwitterException | SQLException te){
                te.printStackTrace();
                System.out.println("Tweetler aranırken bir sorun oluştu: " + te.getMessage());
                System.exit(-1);
            }

        }
    }
}





