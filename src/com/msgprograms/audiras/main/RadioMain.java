package com.msgprograms.audiras.main;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.msgprograms.audiras.gui.Window;
import com.msgprograms.audiras.settings.FileConst;
import com.msgprograms.audiras.settings.Lang;
import com.msgprograms.audiras.settings.Settings;
import com.msgprograms.audiras.streamlogic.RecordingMaster;
import com.msgprograms.audiras.streamlogic.StationList;

public class RadioMain {

	public static Window win = null;

	public static void main(String[] args) {
		FileConst.init();
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
				Settings.save();
			}
		});
	}

	private static void initTrayIcon() {
		TrayIcon ti = null;

		if (SystemTray.isSupported()) {
			SystemTray t = SystemTray.getSystemTray();

			Image i = null;
			try {
				i = ImageIO.read(FileConst.ICO_FILE);
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
}
