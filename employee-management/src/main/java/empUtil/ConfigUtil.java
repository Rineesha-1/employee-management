package empUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigUtil {
	private ConfigUtil() {
	}

	private static final Properties appProperties = new Properties();
	static {
		try (InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("app.properties")) {
			appProperties.load(in);
		} catch (IOException e) {
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