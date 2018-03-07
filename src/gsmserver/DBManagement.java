package gsmserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DBManagement {
	private Connection connection;
	private String updateString = "insert into sensordata (date, temperature, humidity) values (?, ?, ?)";
	
	public DBManagement() {
		try {
			dBInitialization();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void dBInitialization() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://localhost/sajat?useSSL=false",
				"csabi", "ae293147");
	}
	
	public void insertValuesToDB(Float temperature, Float humidity) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(updateString);
			preparedStatement.setTimestamp(1, new Timestamp(new Date().getTime()));
			preparedStatement.setDouble(2, temperature);
			preparedStatement.setDouble(3, humidity);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
