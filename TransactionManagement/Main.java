import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class Main {
    public static void main(String args[]) throws Exception {

        Connection conn =DbConnection.getLocalConnection();

        List<String> locksToAcquire=new ArrayList<String>();
        locksToAcquire.add("courseRegistration");
        locksToAcquire.add("course");
        locksToAcquire.add("student");

        Transaction t1=new Transaction(conn,locksToAcquire,"deregister","george",13429);
        Transaction t2= new Transaction(conn,locksToAcquire,"register","brad",13429);
        t1.start();
        t2.start();

    }
}
