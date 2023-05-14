import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Transaction extends Thread {

    List<String> locksToAcquire;

    String actionToPerform ;

    Icourse course;

    String studentName;
    int courseId;

    Connection conn;
    static Integer lockTransaction=1;
    Transaction(Connection conn,List<String> locks,String action,String studentName,int courseId){
        this.locksToAcquire=locks;
        this.conn=conn;
        this.course=new Course(conn);
        this.actionToPerform=action;
        this.studentName=studentName;
        this.courseId=courseId;
    }
    @Override
    public void run() {

            synchronized (lockTransaction) {


                try {

                    acquireLock();
                    conn.setAutoCommit(false);
                    operation();
                    releaseLock();
                    conn.commit();
                    System.out.println("transaction " + actionToPerform + " committed successfully");
                } catch (Exception e) {
                    try {
                        System.out.println(actionToPerform + " " + e.getMessage());
                        releaseLock();
                        conn.rollback();
                        System.out.println("transaction " + actionToPerform + " rolledback");
                    } catch (Exception ex) {

                    }
                }
            }





    }
    public void acquireLock() throws SQLException {
        PreparedStatement ps;
        String sql="LOCK TABLES ";

        for (String table:locksToAcquire) {
            sql=sql+table+" WRITE,";



        }
        //remove trailing comma
        sql = sql.replaceAll(",$", "");
        ps=conn.prepareStatement(sql);
        boolean result =ps.execute();
        System.out.println("acquired locks for transaction "+actionToPerform);

    }
    public void operation() throws Exception {
        System.out.println("performing operation for transaction "+actionToPerform);
        if (actionToPerform=="deregister"){

            course.deregisterCourse(studentName,courseId);
        }
        else{
            course.register(studentName,courseId);
        }

    }
    public void  releaseLock() throws SQLException {
        String sql="UNLOCK TABLES";
        PreparedStatement ps;
        ps=conn.prepareStatement(sql);
        ps.execute();
        System.out.println("locks released for transaction "+actionToPerform);
    }
}
