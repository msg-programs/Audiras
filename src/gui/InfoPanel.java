package gui;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import settings.Lang;
import streamlogic.RadioStation;

public class InfoPanel extends JPanel {

	private JLabel name, rate, genre, format, status;

	public InfoPanel() {
		JLabel nam = new JLabel("Name");
		nam.setBounds(10, 20, 150, 15);
		this.add(nam);

		name = new JLabel();
		name.setBounds(15, 35, 150, 15);
		this.add(name);
		name.setFont(new Font(name.getFont().getName(), Font.PLAIN, name.getFont().getSize()));

		JLabel gen = new JLabel("Genre");
		gen.setBounds(10, 55, 150, 15);
		this.add(gen);

		genre = new JLabel();
		genre.setBounds(15, 70, 150, 15);
		this.add(genre);
		genre.setFont(new Font(genre.getFont().getName(), Font.PLAIN, genre.getFont().getSize()));

		JLabel rat = new JLabel("Bitrate");
		rat.setBounds(10, 90, 150, 15);
		this.add(rat);

		rate = new JLabel();
		rate.setBounds(15, 105, 150, 15);
		this.add(rate);
		rate.setFont(new Font(rate.getFont().getName(), Font.PLAIN, rate.getFont().getSize()));

		JLabel form = new JLabel("Format");
		form.setBounds(90, 90, 150, 15);
		this.add(form);

		format = new JLabel();
		format.setBounds(100, 105, 150, 15);
		this.add(format);
		format.setFont(new Font(format.getFont().getName(), Font.PLAIN, format.getFont().getSize()));

		JLabel stat = new JLabel("Status");
		stat.setBounds(10, 125, 150, 15);
		this.add(stat);

		status = new JLabel();
		status.setBounds(15, 140, 150, 15);
		this.add(status);
		status.setFont(new Font(status.getFont().getName(), Font.PLAIN, status.getFont().getSize()));
	}

	
	public void updateText(RadioStation rs) {
		if (rs != null) {
			format.setText(rs.format);
			name.setText(rs.name);
			rate.setText(rs.bitrate);
			genre.setText(rs.genre);
			status.setText(rs.getStatus());
		} else {
			format.setText("");
			name.setText("");
			rate.setText("");
			genre.setText("");
			status.setText("");
		}
	}
}
