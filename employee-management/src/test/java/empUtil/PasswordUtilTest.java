package empUtil;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PasswordUtilTest {
	@Test
	void validInput_shouldReturnHash() {
		String hash = PasswordUtil.sha256("Password@123");
		assertNotNull(hash);
		assertEquals(64, hash.length());
	}

	@Test
	void sameInput_shouldReturnSameHash() {
		assertEquals(PasswordUtil.sha256("pass"), PasswordUtil.sha256("pass"));
	}

	@Test
	void differentInput_shouldReturnDifferentHash() {
		assertNotEquals(PasswordUtil.sha256("pass"), PasswordUtil.sha256("pass1"));
	}
}
