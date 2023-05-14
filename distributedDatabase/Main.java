import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String args[]) throws SQLException {
        Connection localConnection = null;
        Connection remoteConnection=null;
    try{
       localConnection=DbConnection.getLocalConnection();
       remoteConnection=DbConnection.getRemoteConnection();

       Transaction t1= new Transaction(remoteConnection,localConnection);
       t1.executeTransaction();
    }
    catch (Exception e){
        e.printStackTrace();
    }
    finally {
        localConnection.close();
        remoteConnection.close();
    }







    }
}
