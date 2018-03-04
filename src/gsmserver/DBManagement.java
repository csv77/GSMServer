package gsmserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DBManagement {
	private Connection connection;
	private String queryString = "insert into sensordata (date, temperature, humidity) values (?, ?, ?)";
	
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
			PreparedStatement preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, new Date().toString());
			preparedStatement.setString(2, temperature.toString());
			preparedStatement.setString(3, humidity.toString());
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
