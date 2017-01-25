package com.nekrosius.asgardascension.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nekrosius.asgardascension.Main;

public class Logger {
	
	File file;
	String prefix;
	
	Main plugin;
	
	public Logger(Main plugin) {
		this.plugin = plugin;
		
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("Y-MM-d");
		String fileName = df.format(date) + ".log";
		(new File(plugin.getDataFolder() + File.separator + "logs" + File.separator)).mkdir();
		file = new File(plugin.getDataFolder() + File.separator + "logs" + File.separator, fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void log(String message) {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		prefix = "[" + df.format(date) + "] ";
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
			pw.write(prefix + message + "\n");
			pw.close();
		} catch(IOException e) {
			System.out.println("AsgardAscension : LOGGING ERROR");
			return;
		}
	}

}
