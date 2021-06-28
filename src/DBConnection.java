
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



public class DBConnection {

    public DBConnection() {
        Statement st;
        Connection con;
        ResultSet rs;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sobiad", "root", "asdf1234");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error  : " + e);
        }
    }

}
