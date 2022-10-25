package me.malik.app.killer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public final class KillProcess {
	
	private static final String PIDS_CMD = "ps aux";
	private static final String TARGET_APP = "spotify";
	private static final String TERMINATE_APP = "kill ";
	
	private KillProcess() { }
	
	public final static class Executor {
		
		private String command;
		
		public Executor(String command) {
			this.command = command;
		}
		
		public String getCommand() {
			return this.command;
		}
		
		public void setCommand(String cmd) {
			this.command = cmd;
		}
		
		public Process exec() throws IOException {
			return Runtime.getRuntime().exec(this.command);
		}
	}
	
	public final static class StdInput {
		
		private InputStream inStream;
		private InputStream errStream;
		
		private final Executor exec;
		
		public StdInput(Process proc, Executor exec) {
			this.inStream = proc.getInputStream();
			this.errStream = proc.getErrorStream();
			this.exec = exec;
		}
		
		public InputStream getInputStream() {
			return this.inStream;
		}
		
		public InputStream getErrorStream() {
			return this.errStream;
		}
		
		public void setInputStream(Process in) {
			this.inStream = in.getInputStream();
			this.errStream = in.getErrorStream();
		}
		
		public void terminate() throws IOException {
			BufferedReader bufIn = new BufferedReader(new InputStreamReader(this.inStream));
			BufferedReader bufErr = new BufferedReader(new InputStreamReader(this.errStream));
			
			System.out.println("Generating Output...");
			
			String res = null;
			String content = null;
			
			while ((res = bufIn.readLine()) != null) {
				if (res.toLowerCase().contains(TARGET_APP)) {
					content = res;
				}
				System.out.println(res);
			}
			
			System.out.println("Generating Error Output...");
			
			while ((res = bufErr.readLine()) != null) {
				System.out.println(res);
			}
			
			int pid = 0;
			
			if (content == null) {
				JOptionPane.showMessageDialog(null, "Could not find Process for: " + TARGET_APP.toUpperCase(), null, JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			
			if (content.matches(".*\\d.*")) {
				pid = Integer.parseInt(KillProcess.extractNumber(content));
			}
			
			if (pid != 0) {
				System.out.println(pid);
				this.exec.setCommand(TERMINATE_APP + String.valueOf(pid));
				this.exec.exec();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Executor controller = new Executor(PIDS_CMD);
		StdInput inputController = new StdInput(controller.exec(), controller);
		inputController.terminate();
	}
	
	public static String extractNumber(final String str) {                
	    
	    if(str == null || str.isEmpty()) return "";
	    
	    StringBuilder sb = new StringBuilder();
	    boolean found = false;
	    
	    for (char c : str.toCharArray()) {
	        if (Character.isDigit(c)) {
	            sb.append(c);
	            found = true;
	        } else if (found) {
	            break;                
	        }
	    }
	    
	    return sb.toString();
	}
}
