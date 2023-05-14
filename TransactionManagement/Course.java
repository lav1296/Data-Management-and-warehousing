import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;

public class Course implements Icourse{
    Connection conn;
    Course(Connection connection){
        this.conn=connection;
    }
    @Override
    public void register(String studentName, int courseIdToRegister) throws Exception {

        String getStudentInformation="SELECT student.studentId,netId,email,firstName,lastName FROM student where student.firstName=?";
        PreparedStatement ps1=conn.prepareStatement(getStudentInformation);
        ps1.setString(1,studentName);
        ps1.executeQuery();
        ResultSet rs1= ps1.getResultSet();
        int studentId=0;
        String netId="";
        String email="";
        String firstName="";
        String lastName="";



        Formatter fmt=new Formatter();

        if (rs1.next()){
            studentId= rs1.getInt("studentId");
            netId=rs1.getString("netId");
            email=rs1.getString("email");
            lastName=rs1.getString("lastName");
            firstName=rs1.getString("firstName");
            //int courseId=rs1.getInt("courseId");
            System.out.println("user information to register for course");
            fmt.format("%10s %10s %10s %10s %n","firstName","lastName","email","netId");
            fmt.format("%10s %10s %10s %10s %n",firstName,lastName,email,netId);
            System.out.println(fmt);

        }
        fmt=new Formatter();
        String getAvailableSeatsQuery="select totalSeats,availableSeats,credits,name, courseNumber from course where courseId=?";
        PreparedStatement ps2= conn.prepareStatement(getAvailableSeatsQuery);
        ps2.setInt(1,courseIdToRegister);
        ps2.executeQuery();
        ResultSet rs2= ps2.getResultSet();

        int totalSeats=0;
        int availableSeats=0;
        int credits=0;
        String CourseName="";

        if (rs2.next()){
            totalSeats= rs2.getInt("totalSeats");
            availableSeats=rs2.getInt("availableSeats");
            credits=rs2.getInt("credits");
            CourseName=rs2.getString("name");
            System.out.println("course information to register");
            fmt.format("%10s %10s %10s %10s %n","totalSeats","availableSeats","credits","name");
            fmt.format("%10s %10s %10s %10s %n",totalSeats,availableSeats,credits,CourseName);
            System.out.println(fmt);

        }

        fmt=new Formatter();
        //checking if the student has registered
        String getCourseRegistration="select * from courseRegistration where studentId=? and courseId=?";
        PreparedStatement ps5=conn.prepareStatement(getCourseRegistration);
        ps5.setInt(1,studentId);
        ps5.setInt(2,courseIdToRegister);
        ps5.executeQuery();

        ResultSet rs3=ps5.getResultSet();
        //for insert
        if (rs3.next()){
            throw  new Exception("student already registered");
        }

        //registering Course
        int updatedAvailableSeats=availableSeats-1;
        String updateAvailableSeatsQuery="UPDATE course SET `availableSeats` = ? WHERE `courseId` = ?;";
        PreparedStatement ps3= conn.prepareStatement(updateAvailableSeatsQuery);
        ps3.setInt(1,updatedAvailableSeats);
        ps3.setInt(2,courseIdToRegister);
        int result=ps3.executeUpdate();
        if (result==0){
            throw new Exception("row not updated");
        }


        String insertCourseRegistration="INSERT INTO courseRegistration (studentId,courseId) VALUES (?,?);";
        PreparedStatement ps6=conn.prepareStatement(insertCourseRegistration);
        ps6.setInt(1,studentId);
        ps6.setInt(2,courseIdToRegister);
        int resultInsert=ps6.executeUpdate();
        if (resultInsert==0){
            throw  new Exception("course not registered to student");
        }

        //updated course information
        ps2.executeQuery();
        rs2= ps2.getResultSet();

        if (rs2.next()){
            totalSeats= rs2.getInt("totalSeats");
            availableSeats=rs2.getInt("availableSeats");
            credits=rs2.getInt("credits");
            CourseName=rs2.getString("name");
            System.out.println("updated course information after deregisteration");
            fmt.format("%10s %10s %10s %10s,%n","totalSeats","availableSeats","credits","name");
            fmt.format("%10s %10s %10s %10s,%n",totalSeats,availableSeats,credits,CourseName);
            System.out.println(fmt);

        }


    }

    @Override
    public void deregisterCourse(String studentName, int courseIdToDeregister) throws Exception {

        conn.setAutoCommit(false);
        String getStudentInformation="SELECT student.studentId,netId,email,firstName," +
                "lastName,course.courseId FROM student,courseRegistration,course where student.firstName=? " +
                "and courseRegistration.studentId=student.studentId and courseRegistration.courseId=course.courseId ;";
        PreparedStatement ps1=conn.prepareStatement(getStudentInformation);
        ps1.setString(1,studentName);
        ps1.executeQuery();
        ResultSet rs1= ps1.getResultSet();
        int studentId=0;
        String netId="";
        String email="";
        String firstName="";
        String lastName="";



        Formatter fmt=new Formatter();

        if (rs1.next()){
            studentId= rs1.getInt("studentId");
            netId=rs1.getString("netId");
            email=rs1.getString("email");
            lastName=rs1.getString("lastName");
            firstName=rs1.getString("firstName");
            int courseId=rs1.getInt("courseId");
            System.out.println("user information to deregister course");
            fmt.format("%10s %10s %10s %10s %10s %n","firstName","lastName","email","netId","courseId");
            fmt.format("%10s %10s %10s %10s %10s %n",firstName,lastName,email,netId,courseId);
            System.out.println(fmt);

        }
        fmt=new Formatter();
        String getAvailableSeatsQuery="select totalSeats,availableSeats,credits,name, courseNumber from course where courseId=?";
        PreparedStatement ps2= conn.prepareStatement(getAvailableSeatsQuery);
        ps2.setInt(1,courseIdToDeregister);
        ps2.executeQuery();
        ResultSet rs2= ps2.getResultSet();

        int totalSeats=0;
        int availableSeats=0;
        int credits=0;
        String CourseName="";

        if (rs2.next()){
            totalSeats= rs2.getInt("totalSeats");
            availableSeats=rs2.getInt("availableSeats");
            credits=rs2.getInt("credits");
            CourseName=rs2.getString("name");

            System.out.println("course information to deregister");
            fmt.format("%10s %10s %10s %10s %n","totalSeats","availableSeats","credits","name");
            fmt.format("%10s %10s %10s %10s %n",totalSeats,availableSeats,credits,CourseName);
            System.out.println(fmt);

        }
        fmt=new Formatter();
        //checking if the student has registered
        String getCourseRegistration="select * from courseRegistration where studentId=? and courseId=?";
        PreparedStatement ps5=conn.prepareStatement(getCourseRegistration);
        ps5.setInt(1,studentId);
        ps5.setInt(2,courseIdToDeregister);
        ps5.executeQuery();

        ResultSet rs3=ps5.getResultSet();
        //for delete
        if (!rs3.next()){
            throw  new Exception("student is not registered for the course");
        }

        String deleteCourseRegistration="DELETE from courseRegistration where studentId=? and courseId=?";
        PreparedStatement ps7=conn.prepareStatement(deleteCourseRegistration);
        ps7.setInt(1,studentId);
        ps7.setInt(2,courseIdToDeregister);
        int rowAffected=ps7.executeUpdate();
        if (rowAffected==0){
            throw new Exception("course removal failed");
        }

        //registering Course
        int updatedAvailableSeats=availableSeats+1;
        String updateAvailableSeatsQuery="UPDATE course SET `availableSeats` = ? WHERE `courseId` = ?;";
        PreparedStatement ps3= conn.prepareStatement(updateAvailableSeatsQuery);
        ps3.setInt(1,updatedAvailableSeats);
        ps3.setInt(2,courseIdToDeregister);
        int result=ps3.executeUpdate();
        if (result==0){
            throw new Exception("row not updated");
        }

        //updated course information
        ps2.executeQuery();
        rs2= ps2.getResultSet();

        if (rs2.next()){
            totalSeats= rs2.getInt("totalSeats");
            availableSeats=rs2.getInt("availableSeats");
            credits=rs2.getInt("credits");
            CourseName=rs2.getString("name");
            System.out.println("updated course information after deregisteration");
            fmt.format("%10s %10s %10s %10s,%n","totalSeats","availableSeats","credits","name");
            fmt.format("%10s %10s %10s %10s,%n",totalSeats,availableSeats,credits,CourseName);
            System.out.println(fmt);

        }
    }
}
