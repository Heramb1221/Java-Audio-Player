package player;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import java.io.File;
import javax.swing.*;

public class Player extends JFrame {
	Clip clip;
	JButton play, stop, reset;
	JLabel status, fileLabel, position;
	JPanel progressBarPanel;
	JProgressBar progressBar;
	Timer timer;
	File File;

	Player() {
		
		ImageIcon logo = new ImageIcon("AudioPlayer.png");
		this.setIconImage(logo.getImage());
		
		this.setTitle("Heramb's Audio Player");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 250);
		this.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		fileLabel = new JLabel("File Name: ");
		topPanel.add(fileLabel);

		progressBarPanel = new JPanel();
		progressBarPanel.setLayout(new BorderLayout());
		progressBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		position = new JLabel("Position: 0:00 / 0:00");
		progressBarPanel.add(position, BorderLayout.NORTH);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(false);
		progressBarPanel.add(progressBar, BorderLayout.CENTER);

		topPanel.add(progressBarPanel);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		play = new JButton("Play");
		stop = new JButton("Stop");
		reset = new JButton("Reset");
		buttonPanel.add(play);
		buttonPanel.add(stop);
		buttonPanel.add(reset);

		status = new JLabel(" ");
		status.setHorizontalAlignment(SwingConstants.CENTER);

		this.add(topPanel, BorderLayout.NORTH);
		this.add(buttonPanel, BorderLayout.CENTER);
		this.add(status, BorderLayout.SOUTH);

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseAndPlayAudio();
			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopAudio();
			}
		});

		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetAudio();
			}
		});

    
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (clip != null && clip.isRunning()) {
					long pos = clip.getMicrosecondPosition();
					long duration = clip.getMicrosecondLength();
					int posInSeconds = (int) (pos / 1_000_000);
					int durationInSeconds = (int) (duration / 1_000_000);

					String formattedPosition = formatTime(posInSeconds);
					String formattedDuration = formatTime(durationInSeconds);
					position.setText(formattedPosition + " / " + formattedDuration);

					int progress = (int) (100.0 * posInSeconds / durationInSeconds);
					progressBar.setValue(progress);
				}
			}
		});
	}

	private String formatTime(int seconds) {
		int minutes = seconds / 60;
		seconds = seconds % 60;
		return String.format("%d:%02d", minutes, seconds);
	}

	private void chooseAndPlayAudio() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File = fileChooser.getSelectedFile();
			playAudio(File);
		}
	}

	private void playAudio(File file) {
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

			if (clip != null) {
				clip.close();
			}

			clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.start();

			fileLabel.setText("File Name: " + file.getName());
			status.setText("Playing...");
			timer.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			status.setText("Error: " + ex.getMessage());
			}
	}

	private void stopAudio() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
			status.setText("Stopped.");
			timer.stop();
		}
	}

	private void resetAudio() {
		if (clip != null) {
			clip.setMicrosecondPosition(0);
			status.setText("Reset.");
			progressBar.setValue(0);
		}
	}
}