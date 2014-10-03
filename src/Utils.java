import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Utils {
	public static void toClipboard(String text) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Clipboard cb = tk.getSystemClipboard();
		StringSelection ss = new StringSelection(text);
		cb.setContents(ss, null);
	}
}