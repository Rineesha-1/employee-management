package empUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatabaseUtil {
	//db properties
	private static final Properties property = new Properties();
	private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
	static {
	    try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
	        if (in == null) {
	            logger.error("db.properties file not found in classpath");
	            throw new RuntimeException("Database configuration file missing");
	        }
	        property.load(in);
	    } catch (IOException e) {
	        logger.error("Failed to load db.properties", e);
	        throw new RuntimeException("Cannot load db.properties", e);
	    }
	}
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(property.getProperty("url"), property.getProperty("username"),
				property.getProperty("password"));
	}
}
