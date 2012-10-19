package sobs;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class Server {
	public int port = 1337;
	ServerSocket listenSocket;
	public int currItemNumber;
	public Hashtable<String, SOBClient> clientTable;
	public Hashtable<String, SOBItem> itemTable;
	
	 public Server() {
	        try {
	            this.listenSocket = new ServerSocket(port);
	            this.currItemNumber = 0;
	            this.clientTable = new Hashtable<String, SOBClient>();
	            this.itemTable = new Hashtable<String, SOBItem>();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	 }
	
	 public static void main(String args[]) {
		 try {
			 Server server = new Server();
			 System.out.println("Starting Server");

			 while(true) {
				 Socket connectionSocket = server.listenSocket.accept();
				 new ConnectionHandler(connectionSocket, server);
			 }
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 System.exit(1);
		 }
	 }
	
	public static void startServer(){
		String cmd;
		Scanner in = new Scanner(System.in);
		int port;


		while(true){
			cmd = in.nextLine();
			String cmdLine[] = cmd.split(" ");



			if (cmdLine.length == 3 && cmdLine[0].equals("sobs") && cmdLine[1].equals("-s")){
				try{
					port = Integer.parseInt(cmdLine[2]);
					if (port > 1024 && port < 65535){
						//startServer(port);
					}else{
						System.out.println("Error: argumentsgument" + " must be an integer between 1024-65535");
					}
				} catch (NumberFormatException e) {
					System.out.println("Error: argumentsgument" + " must be an integer between 1024-65535");
				}
			}else{
				System.out.println("Error invalid command");
				System.out.println("sobs -s <server-port> to start server");
			}
		}
	}

	public int getNextItemNumber() {
		this.currItemNumber++;
		return this.currItemNumber;
	}
}


class ConnectionHandler implements Runnable {
	private Socket socket;
	private Server server;
	private String retMsg;
	private SOBClient client;


	public ConnectionHandler(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		try{
			
//			String clientSentence, capitalizedSentence;
//			 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			 DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
//			 System.out.println("In Thread");
//			 clientSentence = inFromClient.readLine();
//			 System.out.println("Received: " + clientSentence);
//			 capitalizedSentence = clientSentence.toUpperCase() + '\n';
//			 outToClient.writeBytes(capitalizedSentence);
			this.client = new SOBClient();
			String msg;
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			
			while(true){
				msg = inFromClient.readLine();
				System.out.println("Received: " + msg);
				checkInput(client, msg);
				System.out.println("Sending " + this.retMsg);
				outToClient.writeBytes(this.retMsg + "\n");
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void checkInput(SOBClient client, String msg){
		String desc[] = msg.split("\"");
		String cmd[] = msg.split("[ ]+");
		System.out.println("length = " + cmd.length);

		for (int i = 0; i < desc.length; i++)
			System.out.println(i + " " + desc[i]);
		
		System.out.println("");
		
		for (int i = 0; i < cmd.length; i++)
			System.out.println(i + " " + cmd[i]);

		switch (cmd[0]) {
		case "register":
			if (cmd.length == 4) {
				registerUser(cmd);
			}else{
				this.retMsg = "Error: arguments";
			}
			break;
		case "sell":
			if (client.isRegistered() == false){
				this.retMsg = "Error: Not Registered";
			}else{
				sellItem(cmd, desc);
			}
			break;
		case "info":
			if (client.isRegistered() == false){
				this.retMsg = "Error: Not Registered";
			}else{
				infoCommand(cmd);
			}
			break;
		case "bid":
			if (client.isRegistered() == false){
				this.retMsg = "Error: Not Registered";
			}else{
				bidCommand(cmd);
			}
			break;
		case "buy":
			if (client.isRegistered() == false){
				this.retMsg = "Error: Not Registered";
			}else{
				bidCommand(cmd);
			}
			break;
		case "direct":
			if (client.isRegistered() == false){
				this.retMsg = "Error: Not Registered";
			}else{
				retMsg = "direct";
			}
			break;
		case "deregister":
			if (client.isRegistered() == false){
				this.retMsg = "Error: Not Registered";
			}else{
				this.client.setRegistered(false);
				this.retMsg = "You have successfully signed out. Bye!";
			}
			break;
		default: 
			this.retMsg = "Error: arguments";
			break;
		}
	}



	private void bidCommand(String[] cmd) {
		int item_number;
		int bid_increment;
		SOBItem currItem;
		
		if(cmd.length != 3){
			this.retMsg = "Error: arguments";
			return;
		}
		
		try{
			item_number = Integer.parseInt(cmd[1]);
			bid_increment = Integer.parseInt(cmd[2]);
		} catch (NumberFormatException e) {
			this.retMsg = "Error: arguments";
			return;
		}
		
		if (this.server.itemTable.containsKey("" + item_number)){
			//Gets the associated item from the itemTable 
			currItem = this.server.itemTable.get("" + item_number);
		}
		else{
			this.retMsg = "Error: " + cmd[1] + " not found";
			return;
		}
		
		
		
		if (currItem.getSeller().getUsername().equals(this.client.getUsername())){
			this.retMsg = "Error: owner";
			return;
		}
		
		if (currItem.getCurrBidder() != null){
			if (currItem.getCurrBidder().getUsername().equals(this.client.getUsername())){
				this.retMsg = "Error: duplicate bid";
				return;
			}
		}
		
		if (bid_increment <= 0 ){
			this.retMsg = "Error: negative bid";
			return;
		}
				
		currItem.incrementBidNumber();
		currItem.setCurrBid(currItem.getCurrBid() + bid_increment);
		
		if (currItem.getBidNumber() >= currItem.getTransactionLimit()){
			//Item is Sold
			
			this.server.itemTable.remove("" + item_number);
			this.retMsg = "purchased " + currItem.getItemNumber() + " " + currItem.getName() + " " + currItem.getCurrBid();
			return;
			
		}else{
			//Bidding can still take place
			currItem.setCurrBidder(this.client);
			this.retMsg = currItem.getItemNumber() + " " + currItem.getName() + " " + currItem.getCurrBid();
			return;
			
		}
					
			
	}

	private void registerUser(String cmd[]) {
		int port;
		try{
			port = Integer.parseInt(cmd[3]);
		} catch (NumberFormatException e) {
			this.retMsg = "Error: arguments";
			return;
		}
		
		System.out.println("cmd[1] = " + cmd[1]);
		
		if(this.server.clientTable.containsKey(cmd[1])){
			System.out.println(cmd[1] + " exists");
			this.client = this.server.clientTable.get(cmd[1]);
			this.client.setPort(port);
			this.client.setIpAddress(cmd[2]);
			this.client.setRegistered(true);
			System.out.println("this.client =  " + this.client.toString());
			System.out.println("Hashtable =  " + this.server.clientTable.toString());
			this.retMsg =  "Welcome " + this.client.getUsername() + ", you have successfully signed in.";
			return;
		}
		
		System.out.println(cmd[1] + " does not exists");
		this.client.setPort(port);
		this.client.setUsername(cmd[1]);
		this.client.setIpAddress(cmd[2]);
		this.client.setRegistered(true);
		this.server.clientTable.put(this.client.getUsername(), this.client);
		System.out.println("this client =  " + this.client.toString());
		System.out.println("Hashtable =  " + this.server.clientTable.toString());
		this.retMsg =  "Welcome " + this.client.getUsername() + ", you have successfully signed in.";
	}

	private void sellItem(String cmd[], String desc[]) {
		if(cmd.length < 6){
			this.retMsg = "Error: arguments";
			return;
		}
		
		if(desc.length  != 2){
			this.retMsg = "Error: arguments";
			return;
		}

		int im, tl, cb, bn;

		try{
			tl = Integer.parseInt(cmd[2]);
			cb = Integer.parseInt(cmd[3]);
			bn = Integer.parseInt(cmd[4]);
		} catch (NumberFormatException e) {
			this.retMsg = "Error: arguments";
			return;
		}
		
		if((tl <= 0) || (cb <= 0) || (bn <= 0)){
			this.retMsg = "Error: arguments";
			return;
		}

		im = this.server.getNextItemNumber();

		//this.server.getCurrentIM();
		SOBItem item = new SOBItem();
		item.setName(cmd[1]);
		item.setItemNumber(im);
		item.setTransactionLimit(tl);
		item.setCurrBid(cb);
		item.setBuyNowPrice(bn);
		item.setDescription("\"" + desc[1] + "\"");
		item.setSeller(this.client);
		item.setSold(false);
		item.setBidNumber(0);
		item.setCurrBidder(null);

		this.server.itemTable.put("" + im , item);
		System.out.println(this.server.itemTable.toString());

		this.retMsg = item.getName() + " added with number " + item.getItemNumber();

	}


	public void infoCommand(String cmd[]){
		
		int item_number;
				
		if(cmd.length == 1){
			if( this.server.itemTable.isEmpty()){
				this.retMsg = "Error: empty";
				return;
			}
			else{
				this.retMsg = printItemTable();
				return;
			}
		}
			
		else if (cmd.length >= 2){
			try{
				item_number = Integer.parseInt(cmd[1]);
			} catch (NumberFormatException e) {
				this.retMsg = "Error: " + cmd[1] + " not found";
				return;
			}
			
			if (this.server.itemTable.containsKey("" + item_number)){
				//Gets the associated item from the itemTable and prints out information 
				this.retMsg = this.server.itemTable.get("" + item_number).printItem();
				return;
			}
			else{
				this.retMsg = "Error: " + cmd[1] + " not found";
			}
			
		}
		
		else{
			this.retMsg = "Error: arguments";
			return;
		}


	}

	private String printItemTable() {
		String str = "";
		for (int i = 1; i <= server.currItemNumber; i++){
	    	  SOBItem item_l = this.server.itemTable.get("" + i);
	    	  if (item_l != null){
	    		  str +=  "(*)" + item_l.printItem();
	    	  }
		}
		return str;
		
//		Enumeration<String> e = this.server.itemTable.keys();
//		String str = "";
//		
//	    //iterate through Hashtable keys Enumeration
//	    while(e.hasMoreElements()){
//	    	  System.out.println(e);
//	    	  String key = e.nextElement();
//	    	  SOBItem item_l = this.server.itemTable.get(key);
//	    	  str +=  "(*)" + item_l.printItem();
//	    	}
//		
//	    return str;
	}
}
