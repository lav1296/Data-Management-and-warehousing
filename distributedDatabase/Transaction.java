import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Transaction {
    Connection remoteConnection;
    Connection localConnection;

    Map<String,String> gdd=new HashMap<>();

    Set<Connection> connectionsUsed;

    public Transaction(Connection remoteConn,Connection localConn) {
        this.localConnection=localConn;
        this.remoteConnection=remoteConn;
        connectionsUsed=new HashSet<Connection>();



    }

    public void executeTransaction() throws Exception{
        // for insert a new candidate
        PreparedStatement ptstmt = null;

        // for assign skills to candidate
        PreparedStatement pstmtAssignment = null;

        // for getting candidate id
        ResultSet rs = null;
        Connection conn=null;



        try{
            localConnection.setAutoCommit(false);
            remoteConnection.setAutoCommit(true);

            //retrieving available bookings
            String profile="show profiles";
            Instant start = Instant.now();
            String bookingsAvailableQuery = "select `bookings_available` from reservationavailibility where park_id=?";
            conn=getDistributedDatabaseFromGDD(localConnection,remoteConnection,bookingsAvailableQuery);
            connectionsUsed.add(conn);
            conn.setAutoCommit(false);
            ptstmt=conn.prepareStatement(bookingsAvailableQuery);
            ptstmt.setInt(1,1);

            rs= ptstmt.executeQuery();
            int bookingsAvailable=0;
            while (rs.next()){
                bookingsAvailable=rs.getInt("bookings_available");
            }
            System.out.println("present available booking seat for the park "+bookingsAvailable);
            bookingsAvailable=bookingsAvailable+10;

            //updating available bookings
            String insertBookingQuery="update reservationavailibility set bookings_available=? where park_id=?;";
            conn=getDistributedDatabaseFromGDD(localConnection,remoteConnection,bookingsAvailableQuery);
            connectionsUsed.add(conn);
            conn.setAutoCommit(false);
            ptstmt=conn.prepareStatement(insertBookingQuery);
            ptstmt.setInt(1,bookingsAvailable);
            ptstmt.setInt(2,1);
            int result=ptstmt.executeUpdate();
            if (result==0){
                throw new Exception("no rows affected");
            }

            //retriveing status  of booking
            String parkReservationStatus = "SELECT status FROM parkreservation WHERE reservation_id = ?";
            conn=getDistributedDatabaseFromGDD(localConnection,remoteConnection,parkReservationStatus);
            conn.setAutoCommit(false);
            ptstmt=conn.prepareStatement(parkReservationStatus);
            ptstmt.setInt(1,1);

            rs= ptstmt.executeQuery();
            String bookingStatus="";
            while (rs.next()){
                bookingStatus=rs.getString("status");
            }
            System.out.println("present booking status for the park "+bookingStatus);
            bookingStatus="confirmed";

            //updating the confirmed booking status to the reservation
            String updateBookingStatus="UPDATE `parkreservation` SET `status` = ? WHERE `reservation_id` = ?;";
            conn=getDistributedDatabaseFromGDD(localConnection,remoteConnection,updateBookingStatus);
            conn.setAutoCommit(false);
            ptstmt=conn.prepareStatement(updateBookingStatus);
            ptstmt.setString(1,bookingStatus);
            ptstmt.setInt(2,1);
            int resultbookingStatus=ptstmt.executeUpdate();
            if (resultbookingStatus==0){
                throw new Exception("no rows affected");
            }



            localConnection.commit();
            remoteConnection.commit();
            System.out.println("transaction has been commited");
            Instant end = Instant.now();
            Duration transactiontime=Duration.between(start,end);
            System.out.println("Time taken: "+transactiontime.toMillis() +"ms ");


        }
        catch (Exception e ){
            localConnection.rollback();
            remoteConnection.rollback();
            System.out.println("transaction rolled back");
        }

    }
    public Connection getDistributedDatabaseFromGDD(Connection local,Connection remote,String query) throws IOException {
        String line="";
        int lineCounter=0;
        BufferedReader br = new BufferedReader(new FileReader("resource/gdd.csv"));
        if (gdd.size()==0){
            while ((line = br.readLine()) != null){
                if (lineCounter==0){
                    lineCounter++;
                    continue;
                }
                String[] csvData=line.split(",");
                gdd.put(csvData[0],csvData[1]);
                lineCounter++;
            }
        }
        for (String key:gdd.keySet()) {
            if (query.contains(key)){
                String databaseType=gdd.get(key);
                if (databaseType=="local"){
                    return local;
                }
                else{
                    return remote;
                }

            }
        }
        return null;
        
        
    }
}
