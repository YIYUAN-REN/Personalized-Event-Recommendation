package rpc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StringTest {

	@Test
	public void test() {
		String str = new String("This is a unit test.");
		assertEquals("unit", str.substring(10, 14));
	}
}
