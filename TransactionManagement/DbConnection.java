import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static Connection conn;


    static  {

        if (conn == null) {
            getLocalConnection();
        }
    }
    public static Connection getLocalConnection(){
        if (conn!=null){
            return conn;
        }
        String localUrl="jdbc:mysql://localhost:3306/dalhousieKnowledge?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false";

        String localPassword="root";
        String localUserName="rootroot";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(localUrl, localPassword, localUserName);
            System.out.println("Connection acquired to local mysql server");
        }
        catch(ClassNotFoundException|SQLException e){
            System.out.println(e);
        }

        return conn;

    }


}

