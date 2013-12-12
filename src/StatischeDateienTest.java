import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;


public class StatischeDateienTest {

	@Test
	public void testRun() {
		StatischeDateien tester = new StatischeDateien();
		assertEquals("Leere Optionen deshalb null", null, tester.run(null));
		
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNoFile() {
		StatischeDateien tester = new StatischeDateien();
		File dir = new File("/Files");
		File file = new File("nichtvorhanden.txt");
		assertEquals("Leeres File deshalb null", null, tester.checkMimeType(dir, file));
	}

}
