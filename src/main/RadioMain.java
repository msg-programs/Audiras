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
import java.io.InputStream;
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
import streamlogic.StreamList;

public class RadioMain {

	public static Window win = null;

	public static void main(String[] args) {

		
		Settings.init();
		System.out.println("s init");
		Lang.init();
		System.out.println("l init");
		RecordingMaster.init();
		System.out.println("rm init");
		StreamList.init();
		System.out.println("sl init");
		
		initTrayIcon();


		if (Settings.getInstRec()) {
			RecordingMaster.setAllOn();
		}
		
		if (Settings.getShowWin()) {
			win = new Window();
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Settings.save();
				System.out.println("Settings saved!");
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
				} catch (IOException e1) {
					System.err.println("Couldn't load the tray icon image!");
					e1.printStackTrace();
				}

				MenuItem open = new MenuItem("Open window");
				MenuItem close = new MenuItem("Exit Audiras");

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

				try {
					t.add(ti);
				} catch (AWTException e) {
					System.err.println("Couldn't create the tray icon:");
					e.printStackTrace();
				}
			}
	}
	
	public static void test() {
		String url = "http://208.113.165.40:8000/stream.mp3";
		
		try {
			URLConnection conn;
			conn = new URL(url).openConnection();
			conn.setRequestProperty("Icy-MetaData", "1");
			conn.setRequestProperty("Connection", "close");
			conn.setRequestProperty("Accept", null);
			conn.connect();
			
			Map<String, List<String>> hmap=  conn.getHeaderFields();
			for (String s : hmap.keySet()) {
				System.out.print(s+": ");
				String.join("\n", hmap.get(s));
				System.out.println();
			}
			
			System.out.println("######");
			
			InputStream is = conn.getInputStream();
			byte[] buf = new byte[16255];
			is.readNBytes(buf, 0, 16255);
			System.out.println("######");
			System.out.println(buf[16000]*16);
			System.out.println("######");

			
			System.out.println(new String(buf));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
