package empUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigUtil {
	private ConfigUtil() {
	}
	//default ADMIN details
	private static final Properties appProperties = new Properties();
	private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
	static {
		try (InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("app.properties")) {
			if (in == null) {
				logger.error("app.properties file not found in classpath");
				throw new RuntimeException("Configuration file missing");
			}
			appProperties.load(in);
		} catch (IOException e) {
			logger.error("Failed to load app.properties", e);
			throw new RuntimeException("Failed to load", e);
		}
	}
	public static String getProperty(String key) {
		return appProperties.getProperty(key);
	}
	public static String getDefaultAdminId() {
		return getProperty("default.admin.id");
	}
	public static String getDefaultAdminName() {
		return getProperty("default.admin.name");
	}
	public static String getDefaultAdminEmail() {
		return getProperty("default.admin.email");
	}
	public static String getDefaultAdminPassword() {
		return getProperty("default.admin.password");
	}
	public static String getDefaultAdminDepartment() {
		return getProperty("default.admin.department");
	}
	public static String getDefaultAdminAddress() {
		return getProperty("default.admin.address");
	}
}