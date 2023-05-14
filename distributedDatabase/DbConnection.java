import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static Connection remoteConnection;
    private static Connection localConnection;

    static  {
        if (remoteConnection == null) {
            getRemoteConnection();
        }
        if (localConnection == null) {
            getLocalConnection();
        }
    }
    public static Connection getLocalConnection(){
        if (localConnection!=null){
            return localConnection;
        }
            String localUrl="jdbc:mysql://localhost:3306/a2_distb00910579?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false";

            String localPassword="root";
            String localUserName="rootroot";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                localConnection = DriverManager.getConnection(localUrl, localPassword, localUserName);
                System.out.println("Connection acquired to local mysql server");
            }
            catch(ClassNotFoundException|SQLException e){
                System.out.println(e);
            }

            return localConnection;

        }

    public static Connection getRemoteConnection(){
        if (remoteConnection!=null){
            return remoteConnection;
        }
        String remoteUrl="jdbc:mysql://130.211.233.226:3306/a2_distb00910579?useSSL=false";
        String remotePassword="root";
        String remoteUserName="rootroot";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            remoteConnection = DriverManager.getConnection(remoteUrl, remotePassword, remoteUserName);
            System.out.println("Connection acquired to remote mysql server");
        }
        catch(ClassNotFoundException|SQLException e){
            System.out.println(e);
        }

        return remoteConnection;

        }

}
