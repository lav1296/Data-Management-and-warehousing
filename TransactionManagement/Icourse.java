import java.sql.SQLException;

public interface Icourse {
    public void register(String studentName,int courseId) throws Exception;
    public void deregisterCourse(String studentName, int courseId) throws Exception;
}
