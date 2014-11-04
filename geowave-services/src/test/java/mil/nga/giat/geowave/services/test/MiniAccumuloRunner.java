package mil.nga.giat.geowave.services.test;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.accumulo.minicluster.MiniAccumuloConfig;

import com.google.common.io.Files;

public class MiniAccumuloRunner extends
		JFrame implements
		ActionListener
{
	private JPanel buttonPanel;
	private JButton startButton;
	private JButton stopButton;

	private JPanel textPanel;
	private JTextArea textArea;

	private String username = "root";
	private String password = "pass";
	private String zookeeper = "";
	private String instancename = "";
	private String tempDirString = "";

	private File tempDir;
	private MiniAccumuloCluster miniAccumulo;

	private boolean initialized = false;

	public MiniAccumuloRunner() {

		setLayout(new BorderLayout());

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(
				1,
				2));

		startButton = new JButton(
				"Start MiniAccumulo");
		startButton.addActionListener(this);

		stopButton = new JButton(
				"Stop MiniAccumulo");
		stopButton.addActionListener(this);

		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);

		textPanel = new JPanel();
		
		textArea = new JTextArea(9, 45);
		textArea.setEditable(false);
		textArea.setText("MiniAccumulo: Stopped\nUsername: " + username + "\nPassword: " + password + "\nZookeepers: " + zookeeper + "\nInstance Name: " + instancename + "\nTemp Directory: " + tempDirString + "\nHDFS: file:///\nHDFS Base Path: " + tempDirString + "\\hdfs\nJob Tracker: local");
		textPanel.add(textArea);

		setTitle("MiniAccumulo Runner");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setSize(
				500,
				200);

		add(buttonPanel, BorderLayout.PAGE_START);
		add(textPanel, BorderLayout.CENTER);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(WindowEvent winEvt) {
	        	stopMiniAccumulo();
	            System.exit(0);
	        }
	    });
	}

	private synchronized void startMiniAccumulo() {
		if (!initialized) {
			tempDir = Files.createTempDir();
			tempDir.deleteOnExit();

			final MiniAccumuloConfig config = new MiniAccumuloConfig(
					tempDir,
					password);
			config.setNumTservers(4);
			try {
				miniAccumulo = new MiniAccumuloCluster(
						config);
				miniAccumulo.start();
			}
			catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			zookeeper = miniAccumulo.getZooKeepers();
			instancename = miniAccumulo.getInstanceName();
			tempDirString = tempDir.getAbsolutePath();

			textArea.setText("MiniAccumulo: Running\nUsername: " + username + "\nPassword: " + password + "\nZookeepers: " + zookeeper + "\nInstance Name: " + instancename + "\nTemp Directory: " + tempDirString + "\nHDFS: file:///\nHDFS Base Path: " + tempDirString + "\\hdfs\nJob Tracker: local");

			initialized = true;
		}
	}

	private synchronized void stopMiniAccumulo() {
		if (initialized) {
			try {
				if (miniAccumulo != null) miniAccumulo.stop();
			}
			catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			zookeeper = "";
			instancename = "";
			tempDirString = "";

			textArea.setText("MiniAccumulo: Stopped\nUsername: " + username + "\nPassword: " + password + "\nZookeepers: " + zookeeper + "\nInstance Name: " + instancename + "\nTemp Directory: " + tempDirString + "\nHDFS: file:///\nHDFS Base Path: " + tempDirString + "\\hdfs\nJob Tracker: local");

			initialized = false;
		}
	}

	@Override
	public void actionPerformed(
			ActionEvent event ) {
		if (event.getSource() == startButton && !initialized) {
			startMiniAccumulo();
		}
		else if (event.getSource() == stopButton && initialized) {
			stopMiniAccumulo();
		}
	}

	public static void main(
			String[] args ) {

		MiniAccumuloRunner runner = new MiniAccumuloRunner();
	}
}
