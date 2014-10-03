import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import net.miginfocom.swing.MigLayout;

class JRestConsole extends JFrame {
	private JLabel url_label, response_label, timeoutLabel, acceptLabel, payloadLabel;
	private JTextArea response, payload;
	private JScrollPane response_pane, payloadPane;
	private JTextField url, timeoutField, acceptField;
	private JButton get_button, post_button, put_button, delete_button, option_button, head_button, trace_button;
	private JPanel pan, response_pan, conf_pan;
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
        this.url_label = new JLabel("url:");
        this.response_label = new JLabel("response:");
        this.timeoutLabel = new JLabel("timeout:");
        this.acceptLabel = new JLabel("accept:");
        this.payloadLabel = new JLabel("request payload:");
        
        // response textarea
        this.response = new JTextArea(this.RESPONSE_HEIGHT, this.RESPONSE_WIDTH);
        this.response_pane = new JScrollPane(this.response);
        this.response.setEditable(false);
        
        // request payload textarea
        this.payload = new JTextArea(this.PAYLOAD_HEIGHT, this.PAYLOAD_WIDTH);
        this.payloadPane = new JScrollPane(this.payload);
        this.payload.setEditable(true);
        this.payload.setTabSize(2);
        
        // text fields
        this.url = new JTextField("http://localhost:5000/", 23);
        this.timeoutField = new JTextField("60", 4);
        this.acceptField = new JTextField("application/json", 20);
        
        // buttons
        this.get_button = new JButton("get");
        this.post_button = new JButton("post");
        this.put_button = new JButton("put");
        this.delete_button = new JButton("delete");
        
        // button backgrounds
        this.get_button.setBackground(new Color(87, 169, 87));
        this.post_button.setBackground(new Color(87, 169, 87));
        this.put_button.setBackground(new Color(87, 169, 87));
        this.delete_button.setBackground(new Color(196, 60, 53));
        
        // button action listeners
        this.get_button.addActionListener(new ButtonSubmit());
        this.post_button.addActionListener(new ButtonSubmit());
        this.put_button.addActionListener(new ButtonSubmit());
        this.delete_button.addActionListener(new ButtonSubmit());
        
        // panels
        this.pan = new JPanel(new MigLayout());
        this.response_pan = new JPanel(new MigLayout());
        this.conf_pan = new JPanel(new MigLayout());
        
        // panel backgrounds
        this.pan.setBackground(Color.white);
        this.response_pan.setBackground(Color.white);
        this.conf_pan.setBackground(Color.white);
        
        // panel construction
        this.pan.add(this.url_label);
        this.pan.add(this.url, "cell 0 0,wrap");
        this.pan.add(this.get_button);
        this.pan.add(this.post_button, "cell 0 1");
        this.pan.add(this.put_button, "cell 0 1");
        this.pan.add(this.delete_button, "cell 0 1,wrap");
        this.response_pan.add(this.response_label, "wrap");
        this.response_pan.add(this.response_pane);
        this.pan.add(this.timeoutLabel, "wrap");
        this.pan.add(this.timeoutField, "wrap");
        this.pan.add(this.acceptLabel, "wrap");
        this.pan.add(this.acceptField, "wrap");
        this.pan.add(this.payloadLabel, "wrap");
        this.pan.add(this.payloadPane);
        
        // frame construction
        add(this.pan);
        add(this.conf_pan);
        add(this.response_pan);
        pack();
        
        // set focus to url
        this.url.requestFocus();
        this.url.setCaretPosition(this.url.getText().length());
    }

    private class ButtonSubmit implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Event once button is pressed
        	Map<String, String> opts = new HashMap<String, String>();
        	String reqtype = e.getActionCommand().toUpperCase();
            String urlstr = url.getText();
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
            	}
            	else if (reqman.isHtml(res[0])) {
            		Document doc = Jsoup.parse(res[0]);
            		response.setText(doc.toString());
            	}
            	else {
            		response.setText(res[0]);
            	}
            } catch (IOException e1) {
            	e1.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        JRestConsole jrc = new JRestConsole();
    }
}