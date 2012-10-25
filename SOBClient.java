package sobs;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SOBClient {
	private String retMsg;
	private String username;
	private boolean isRegistered;
	private InetAddress ipAddress;
	private int mainPort;
	private int listenPort;
	private List <String>  msgQ;
	
	
	public boolean isRegistered() {
		return isRegistered;
	}
	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	public SOBClient(){
		this.setRegistered(false);
	}
	public int getMainPort() {
		return mainPort;
	}
	public void setMainPort(int mainPort) {
		this.mainPort = mainPort;
	}
	public int getListenPort() {
		return listenPort;
	}
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	public String printClient() {
		return this.username + " " + this.ipAddress.getHostAddress() + " " + this.mainPort + " " + this.listenPort;		
		
	}
	public List <String> getMsgQ() {
		return msgQ;
	}
	public void initMsgQ() {
		this.msgQ =new ArrayList<String>();
	}
	
	
}
