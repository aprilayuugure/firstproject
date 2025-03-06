package Model;

import java.sql.*;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomDB implements DatabaseInfo{
    public static Connection getConnect()
    {
        try 
        {
            Class.forName(DRIVERNAME);
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Error loading driver" + e);
        }
        
        try
        {
            Connection con = DriverManager.getConnection(DBURL, USERDB, PASSDB);
            return con;
        }
        catch (SQLException e)
        {
            System.out.println("Error: " + e);
        }
        
        return null;
    }
    
    public static Room getRoomByNumber(int x)
    {
        Room r = null;
        
        try (Connection con = getConnect())
        {
            PreparedStatement stmt = con.prepareStatement("SELECT RoomID, RoomCapacity, RoomFloor, RoomURL FROM Rooms WHERE RoomNumber = ?");
            
            stmt.setInt(1, x);

            ResultSet rs = stmt.executeQuery();
            
            if (rs.next())
            {
                int id = rs.getInt(1);
                int room_capacity = rs.getInt(2);
                int room_floor = rs.getInt(3);
                String room_url = rs.getString(4);
                
                r = new Room(id, x, room_capacity, room_floor, room_url);
            }
            con.close();
            
            return r;
        }
        catch (Exception ex)
        {
            Logger.getLogger(RoomDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static int newRoom(Room r)
    {
        int id = -1;
        
        try (Connection con = getConnect())
        {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO Rooms(RoomID, RoomNumber, RoomCapacity, RoomFloor, RoomURL) VALUES(?, ?, ?, ?, ?)");
            
            stmt.setInt(1, r.getRoomID());
            stmt.setInt(2, r.getRoomNumber());
            stmt.setInt(3, r.getRoomCapacity());
            stmt.setInt(4, r.getRoomFloor());
            stmt.setString(5, r.getRoomURL());
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next())
            {
                id = rs.getInt(1);
            }
            
            con.close();
        }
        catch (Exception ex)
        {
            Logger.getLogger(RoomDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return id;
    }
    
    public static Room update(Room r)
    {
        try (Connection con = getConnect())
        {
            PreparedStatement stmt = con.prepareStatement("UPDATE Rooms SET RoomNumber = ?, RoomCapacity = ?, RoomFloor = ?, RoomURL = ? WHERE RoomID = ?");
            
            stmt.setInt(1, r.getRoomNumber());
            stmt.setInt(2, r.getRoomCapacity());
            stmt.setInt(3, r.getRoomFloor());
            stmt.setString(4, r.getRoomURL());
            stmt.setInt(5, r.getRoomID());
            
            int rc = stmt.executeUpdate();
            
            con.close(); return r;
        }
        catch (Exception ex)
        {
            Logger.getLogger(RoomDB.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Invalid data");
        }
    }
    
    public static int delete(int id)
    {
        try (Connection con = getConnect())
        {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM Rooms WHERE RoomID = ?");
            stmt.setInt(1, id);
            
            int rc = stmt.executeUpdate();
            
            con.close(); return rc;
        }
        catch (Exception ex)
        {
            Logger.getLogger(RoomDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
    
    public static ArrayList<Room> search(Predicate<Room> p)
    {
        ArrayList<Room> list_all = listAll();
        
        ArrayList<Room> a = new ArrayList<Room>();
        
        for (Room r : list_all)
        if (p.test(r)) a.add(r);
        
        return a;
    }
    
    public static ArrayList<Room> listAll() 
    {
        ArrayList<Room> list = new ArrayList<Room>();
        
        try (Connection con = getConnect())
        {
            PreparedStatement stmt = con.prepareStatement("SELECT RoomID, RoomNumber, RoomCapacity, RoomFloor, RoomURL FROM Rooms");
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next())
            {
                list.add(new Room(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5)));
            }
            
            con.close(); return list;
        }
        catch (Exception ex)
        {
            Logger.getLogger(RoomDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static void main(String[] a){     
    }
}
