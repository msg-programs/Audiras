package com.msgprograms.audiras.main;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.msgprograms.audiras.gui.Window;
import com.msgprograms.audiras.settings.Lang;
import com.msgprograms.audiras.settings.Settings;
import com.msgprograms.audiras.streamlogic.RecordingMaster;
import com.msgprograms.audiras.streamlogic.StationList;

public class RadioMain {

	public static Window win = null;
	public static final String VERSION = "1.2.2.1";

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
				Settings.save();
			}
		});
	}

//	private static void test() {
//		BufferQueue q = new BufferQueue(0,1);
//		byte[][] bytes = {{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11},{12},{13},{14},{15}};
//		byte[][] meta  = { {}, {}, {}, {}, {},{1}, {}, {}, {},  {}, {3},  {},  {},  {},  {}};
//		String res = "";
//		q.pushBuffer(bytes[0]);
//		q.pushMeta(meta[0]);
//		q.pushBuffer(bytes[1]);
//		q.pushMeta(meta[1]);
//		q.pushBuffer(bytes[2]);
//		q.pushMeta(meta[2]);
//		q.pushBuffer(bytes[3]);
//		q.pushMeta(meta[3]);
//		for (int i = 4; i< bytes.length; i++) {
//			q.pushBuffer(bytes[i]);
//			q.pushMeta(meta[i]);
//			res += q.pop()[0] + ",";
//			if (q.getMetaHi()[0]!=q.getMetaLo()[0]) {
//				res += q.getBuffer(0)[0] + ",";
//				res += q.getBuffer(1)[0]+ ",";
//				res += q.getBuffer(2)[0]+ ",";
//				res += q.getBuffer(3)[0]+ ",";
//				System.out.println(res);
//				res = "";
//			}
//		}
//		System.out.println(res);
//		
//	}

	private static void initTrayIcon() {
		TrayIcon ti = null;

		if (SystemTray.isSupported()) {
			SystemTray t = SystemTray.getSystemTray();

			Image i = null;
			try {
				i = ImageIO.read(Settings.ICO_FILE);
				if (i == null) {
					throw new IOException("ico==null!");
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, Lang.get("err_noTrayIco"), Lang.get("err"),
						JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(null, Lang.get("err_addTrayIco"), Lang.get("err"),
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	public static void update() {
		win.update();
	}
}
