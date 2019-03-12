package service;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
  private static DatabaseConnection instance = new DatabaseConnection();
  private Connection connection;

  private DatabaseConnection() {
    String connectionString;
    File connectionsFile = new File("connection.txt");
    try {
      BufferedReader br = new BufferedReader(new FileReader(connectionsFile));
      connectionString = br.readLine();

      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(connectionString);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static DatabaseConnection getInstance() {
    return instance;
  }

  public Connection getConnection() {
    return connection;
  }
}
