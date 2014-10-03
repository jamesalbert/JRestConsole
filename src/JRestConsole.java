import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import net.miginfocom.swing.MigLayout;

class JRestConsole extends JFrame {
	private static final long serialVersionUID = 1L;
	private JLabel urlLabel, responseLabel, timeoutLabel, acceptLabel,
			payloadLabel;
	private JTextArea response, payload;
	private JScrollPane responsePane, payloadPane;
	private JTextField urlField, timeoutField, acceptField;
	private JButton getButton, postButton, putButton, deleteButton,
			optionsButton, headButton, traceButton, copyButton;
	private JPanel urlPanel, responsePanel, confPanel;
	private final int WINDOW_WIDTH = 600;
	private final int WINDOW_HEIGHT = 480;
	private final int RESPONSE_WIDTH = 50;
	private final int RESPONSE_HEIGHT = 20;
	private final int PAYLOAD_WIDTH = 20;
	private final int PAYLOAD_HEIGHT = 15;

	public JRestConsole() {
		setTitle("JRest Console");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new MigLayout());
		getContentPane().setBackground(Color.white);
		build();
		setVisible(true);
	}

	private void build() {
		// labels
		this.urlLabel = new JLabel("url:");
		this.responseLabel = new JLabel("response:");
		this.timeoutLabel = new JLabel("timeout:");
		this.acceptLabel = new JLabel("accept:");
		this.payloadLabel = new JLabel("request payload:");

		// response textarea
		this.response = new JTextArea(this.RESPONSE_HEIGHT, this.RESPONSE_WIDTH);
		this.responsePane = new JScrollPane(this.response);
		this.response.setEditable(false);

		// request payload textarea
		this.payload = new JTextArea(this.PAYLOAD_HEIGHT, this.PAYLOAD_WIDTH);
		this.payloadPane = new JScrollPane(this.payload);
		this.payload.setEditable(true);
		this.payload.setTabSize(2);

		// text fields
		this.urlField = new JTextField("http://localhost:5000/", 23);
		this.timeoutField = new JTextField("60", 4);
		this.acceptField = new JTextField("application/json", 20);

		// buttons
		this.getButton = new JButton("get");
		this.postButton = new JButton("post");
		this.putButton = new JButton("put");
		this.deleteButton = new JButton("delete");
		this.optionsButton = new JButton("options");
		this.headButton = new JButton("head");
		this.traceButton = new JButton("trace");
		this.copyButton = new JButton("copy");

		// button backgrounds
		this.getButton.setBackground(new Color(87, 169, 87));
		this.postButton.setBackground(new Color(87, 169, 87));
		this.putButton.setBackground(new Color(87, 169, 87));
		this.deleteButton.setBackground(new Color(196, 60, 53));
		this.optionsButton.setBackground(new Color(87, 169, 87));
		this.headButton.setBackground(new Color(87, 169, 87));
		this.traceButton.setBackground(new Color(87, 169, 87));
		this.copyButton.setBackground(Color.gray);

		// button action listeners
		this.getButton.addActionListener(new ButtonSubmit());
		this.postButton.addActionListener(new ButtonSubmit());
		this.putButton.addActionListener(new ButtonSubmit());
		this.deleteButton.addActionListener(new ButtonSubmit());
		this.optionsButton.addActionListener(new ButtonSubmit());
		this.headButton.addActionListener(new ButtonSubmit());
		this.traceButton.addActionListener(new ButtonSubmit());
		this.copyButton.addActionListener(new CopyRequest());

		// panels
		this.urlPanel = new JPanel(new MigLayout());
		this.responsePanel = new JPanel(new MigLayout());
		this.confPanel = new JPanel(new MigLayout());

		// panel backgrounds
		this.urlPanel.setBackground(Color.white);
		this.responsePanel.setBackground(Color.white);
		this.confPanel.setBackground(Color.white);

		// panel construction
		this.urlPanel.add(this.urlLabel);
		this.urlPanel.add(this.urlField, "cell 0 0,wrap");
		this.urlPanel.add(this.getButton);
		this.urlPanel.add(this.postButton, "cell 0 1");
		this.urlPanel.add(this.putButton, "cell 0 1");
		this.urlPanel.add(this.deleteButton, "cell 0 1,wrap");
		this.urlPanel.add(this.optionsButton);
		this.urlPanel.add(this.headButton, "cell 0 2");
		this.urlPanel.add(this.traceButton, "cell 0 2,wrap");
		this.responsePanel.add(this.responseLabel);
		this.responsePanel.add(this.copyButton, "cell 0 0,wrap");
		this.responsePanel.add(this.responsePane);
		this.urlPanel.add(this.timeoutLabel, "wrap");
		this.urlPanel.add(this.timeoutField, "wrap");
		this.urlPanel.add(this.acceptLabel, "wrap");
		this.urlPanel.add(this.acceptField, "wrap");
		this.urlPanel.add(this.payloadLabel, "wrap");
		this.urlPanel.add(this.payloadPane);

		// frame construction
		add(this.urlPanel);
		add(this.confPanel);
		add(this.responsePanel);
		pack();

		// set focus to url
		this.urlField.requestFocus();
		this.urlField.setCaretPosition(this.urlField.getText().length());
	}

	private class CopyRequest implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String contents = response.getText();
			Utils.toClipboard(contents);
		}
	}
	
	private class ButtonSubmit implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Event once button is pressed
			Map<String, String> opts = new HashMap<String, String>();
			String reqtype = e.getActionCommand().toUpperCase();
			String urlstr = urlField.getText();
			String reqPayload = payload.getText();
			String reqTimeout = timeoutField.getText();
			String reqAccept = acceptField.getText();
			opts.put("timeout", reqTimeout);
			opts.put("accept", reqAccept);
			RequestManager reqman = new RequestManager();
			try {
				String[] res = reqman.send(urlstr, reqtype, reqPayload, opts);
				if (reqman.isJson(res[0])) {
					response.setText(reqman.jsonFormatter(res[0]));
				} else if (reqman.isHtml(res[0])) {
					Document doc = Jsoup.parse(res[0]);
					response.setText(doc.toString());
				} else if (res[0].isEmpty()) {
					response.setText(String.format("empty response, status code: %s", res[1]));
				} else {
					response.setText(res[0]);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		new JRestConsole();
	}
}