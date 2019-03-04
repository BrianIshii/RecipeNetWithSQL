import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionSingleton {
    private static DatabaseConnectionSingleton instance = new DatabaseConnectionSingleton();
    private Connection connection;

    private DatabaseConnectionSingleton() {
        String connectionString;
        File connectionsFile = new File("connection.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(connectionsFile));
            connectionString = br.readLine();

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnectionSingleton getInstance() {
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}