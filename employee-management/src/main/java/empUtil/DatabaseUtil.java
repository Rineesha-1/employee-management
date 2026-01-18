package empUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseUtil {
	private static final Properties property = new Properties();
	static {
		try {
			InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties");
			property.load(in);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load db.properties");
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(property.getProperty("url"), property.getProperty("username"),
				property.getProperty("password"));
	}
}
