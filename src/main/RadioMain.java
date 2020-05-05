package main;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;

import gui.Window;

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

		try {
			TrayIcon ti = null;

			if (SystemTray.isSupported()) {
				SystemTray t = SystemTray.getSystemTray();

				Image i = ImageIO.read(Settings.ICO);

				MenuItem open = new MenuItem(Lang.get("opn_win"));
				MenuItem close = new MenuItem(Lang.get("ext_fin"));

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

				t.add(ti);

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

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

}
