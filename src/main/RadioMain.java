package main;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import gui.Window;
import settings.Lang;
import settings.Settings;
import streamlogic.RecordingMaster;
import streamlogic.StationList;

public class RadioMain {

	public static Window win = null;

	public static void main(String[] args) {
		Settings.init();
		Lang.init();
		StationList.init();
		RecordingMaster.init();

		initTrayIcon();

		if (Settings.doStartRec()) {
			RecordingMaster.setAllOn();
		}

		if (Settings.doShowWin()) {
			win = new Window();
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (win!=null) {
					win.save(); // ugly change later
				}
			}
		});
	}

	private static void initTrayIcon() {
		TrayIcon ti = null;

		if (SystemTray.isSupported()) {
			SystemTray t = SystemTray.getSystemTray();

			Image i = null;
			try {
				i = ImageIO.read(Settings.ICO);
				if (i==null) {
					throw new IOException("ico==null!");
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, Lang.get("err_noTrayIco"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}

			MenuItem open = new MenuItem(Lang.get("ti_open"));
			MenuItem close = new MenuItem(Lang.get("ti_exit"));

			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (ae.getSource().equals(open)) {
						if (win == null) {
							win = new Window();
						}
					}
					if (ae.getSource().equals(close)) {
						System.exit(0);
					}
				}
			};

			PopupMenu popup = new PopupMenu();

			open.addActionListener(al);
			popup.add(open);

			close.addActionListener(al);
			popup.add(close);

			ti = new TrayIcon(i, "Audiras", popup);
			ti.addActionListener(al);
			ti.setImageAutoSize(true);

			try {
				t.add(ti);
			} catch (AWTException e) {
				JOptionPane.showMessageDialog(null, Lang.get("err_addTrayIco"), Lang.get("err"), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	public static void update() {
		win.update();
	}
	
	public static void test() throws MalformedURLException, IOException {
		String url = "url here";

		URLConnection conn;
		conn = new URL(url).openConnection();
		conn.setRequestProperty("Icy-MetaData", "1");
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("Accept", null);
		conn.connect();

		Map<String, List<String>> hmap = conn.getHeaderFields();
		for (String s : hmap.keySet()) {
			System.out.print(s + ": ");
			hmap.get(s).forEach(val -> System.err.println(val));
			System.out.println();
		}
		System.exit(0);
	}
}
