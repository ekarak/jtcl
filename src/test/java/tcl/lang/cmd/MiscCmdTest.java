package tcl.lang.cmd;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import tcl.lang.TclCmdTest;

public class MiscCmdTest extends TclCmdTest {
	public void testBasic() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList( new String[] {
				"basic-24.3" // 'info commands' result in a different order; this is not a bug
			}));
		
		String resName = "/tcl/lang/cmd/basic.test";
		tclTestResource(TCLTEST_NAMEOFEXECUTABLE, resName, expectedFailureList);
	}
	
	public void testIo() throws Exception {
		String resName = "/tcl/lang/cmd/io.test";
		tclTestResource(TCLTEST_NAMEOFEXECUTABLE,resName, Collections.EMPTY_LIST);
	}
	
	public void testUtil() throws Exception {
		String resName = "/tcl/lang/cmd/util.test";
		tclTestResource(resName);
	}
	
	public void testVar() throws Exception {
		String resName = "/tcl/lang/cmd/var.test";
		tclTestResource(resName);
	}
}