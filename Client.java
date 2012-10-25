package sobs;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Client {
	public Hashtable<String, SOBClient> clientTable;
	DatagramSocket clientSocket;
	byte[] sendData;
	byte[] receiveData;
	int port;
	InetAddress IPAddress;
	InetAddress host;
	ClientServer cs;
	String localUser;
	
	
	public Client() {
		try {
			this.clientSocket =  new DatagramSocket();
			this.clientSocket.setSoTimeout(500);
			this.IPAddress = InetAddress.getByName("localhost");
			this.port = 1337;
			this.clientTable = new Hashtable<String, SOBClient>();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	 public static void main(String args[]) throws IOException {
		 //initConnection("localhost", 1337);
		 
		 Client client = new Client();
		 
			try{
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				

				while(true){
					client.sendData = new byte[1188];
					System.out.print("sobs> ");
					String msg = inFromUser.readLine();
					
					String cmd[] = msg.split("[ ]+");
					if (cmd.length > 1 && cmd[0].equals("register")){
						client.localUser= cmd[1];
						//stop old thread from running
						if (client.cs != null){
							client.cs.run = false;
							client.cs.interrupt();
						}
						//Start new thread to accept requests from other users and server
						client.cs = new ClientServer(client);
						msg = msg + " " + client.cs.listenSocket.getLocalPort();
					}
					
					if (client.localUser == null){
						System.out.println("sobs> Error not registered");
						continue;
					}
						
					
					if (cmd[0].equals("buy")){
						client.directBuy(msg);
						continue;
					}
					
					if (cmd[0].equals("deregister")){
						client.localUser= null;
						//stop old thread from running
						if (client.cs != null){
							client.cs.run = false;
							client.cs.interrupt();
						}
					}
					
					client.sendData = msg.getBytes();
					client.sendToServer();
					
					
					
				}
			}catch(IOException e){
				e.printStackTrace();
			}
	
		 
	 }
	 
	 public void sendToServer(){
			int i = 0;
			int timeoutAttempt = 5;

			this.receiveData = new byte[1188];

			System.out.print("sobs> ");
			while (i < timeoutAttempt){
				try{
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, this.port);
					this.clientSocket.send(sendPacket);
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);

					byte rawPacket[] = receivePacket.getData();
					for (int j = 0; j < rawPacket.length; j++){
						if (rawPacket[j] == 0)
							rawPacket[j] = 32;
					}

					String retMsg = new String(rawPacket);
					String cmd[] = retMsg.split("[ ]+");

					if (cmd[0].equals("&long&")){
						int n = 1;
						try{
							n = Integer.parseInt(cmd[1]);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						recieveNMessages(n);
						break;
					}

					else{
						System.out.print("[");
						for (int j = 0; j < cmd.length; j++){
							if (j > 0)
								System.out.print(" ");
							System.out.print(cmd[j]);
						}
						System.out.println("]");
						break;
					}
				}catch(SocketTimeoutException  e){
					i++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (i == timeoutAttempt){
				System.out.println("Error: Timeout Limit Reached");
			}

		}
		 	
		 

	 private void recieveNMessages(int n) {


		 System.out.print("[");
		 try{
			 for (int i = 0; i<n; i++){ 

				 this.receiveData = new byte[1188];
				 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				 clientSocket.receive(receivePacket);

				 byte rawPacket[] = receivePacket.getData();
				 for (int j = 0; j < rawPacket.length; j++){
					 if (rawPacket[j] == 0)
						 rawPacket[j] = 32;
				 }

				 String retMsg = new String(rawPacket);
				 String cmd[] = retMsg.split("[ ]+");



				 for (int j = 0; j < cmd.length; j++){
					 if (j > 0)
						 System.out.print(" ");
					 System.out.print(cmd[j]);
				 }
			 }
			 System.out.println("]");
		 	}catch (IOException e) {
				 e.printStackTrace();
			 }

	}

	public void checkInput(String msg){
		 System.out.print("sobs> ");
		 Scanner in = new Scanner(System.in);
		 String str;
		 String cmd[];

		 while(true){
			 str = in.nextLine();
			 cmd = str.split(" ");
			 System.out.println("length = " + cmd.length);

			 if (cmd[0] == null) {
				 System.out.println("Error: Invalid command");
				 break;
			 }	 
		 }
		 in.close();
	 }

//	public static void CheckForStart() throws UnknownHostException {
//		 String cmd;
//			Scanner in = new Scanner(System.in);
//			int port;
//			String ipAddress;
//			
//			System.out.println(InetAddress.getLocalHost());
//			
//			while(true){
//				cmd = in.nextLine();
//				String cmdLine[] = cmd.split(" ");
//				
//
//				if (cmdLine.length == 4 && cmdLine[0].equals("sobs") && cmdLine[1].equals("-c")){
//					if (validateIPAddress(cmdLine[2]) || cmdLine[2].equals("localhost")){
//						ipAddress = cmdLine[2];
//						try{
//							port = Integer.parseInt(cmdLine[3]);
//							if (port > 1024 && port < 65535){
//								initConnection(ipAddress, port);
//							}else{
//								System.out.println("Error: port " + " must be an integer between 1024-65535");
//							}
//						} catch (NumberFormatException e) {
//							System.out.println("Error: port" + " must be an integer between 1024-65535");
//						}
//					}else{
//						System.out.println("Error: Invalid IP Address");
//					}
//				}else{
//					System.out.println("Error: Invalid command");
//				}
//			}
//		}
		 
	 //Reproduced from http://www.wikihow.com/Validate-an-IP-Address-in-Java
	 public static boolean validateIPAddress( String ipAddress ) {
		 String[] tokens = ipAddress.split("\\.");
		 
		 if (tokens.length != 4) {
			 return false;
		 }
		 
		 try{
			 for (String str : tokens) {
				 int i = Integer.parseInt(str);
				 if ((i < 0) || (i > 255)) {
					 return false;
				 }
			 }
			 return true;
		 } catch (NumberFormatException e) {
			 System.out.println("Error: Invalid IP address");
			 return false;
		 }
	 }

	 public void initConnection(String ipAddress, int port){
		 String sentence;
		 String retMsg;
		 InetAddress host;

		
			 try {
				 // Get IP Address
				 host = InetAddress.getLocalHost();

				 Socket clientSocket = new Socket(ipAddress, port);
	
				 BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
				 DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				 BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				 
				 while(true){
				 System.out.print("sobs> ");
				 sentence = inFromUser.readLine();
				 String cmd[] = sentence.split(" ");
				 
				 System.out.println(cmd[0]);
				 if (cmd[0].equals("register")){
					 
					 sentence = sentence + " " + host.getHostAddress() + " " + clientSocket.getLocalPort();
					 System.out.println("sending " + sentence );
				 }
				
				 outToServer.writeBytes(sentence + "\n");
				 retMsg = inFromServer.readLine();
				 retMsg = retMsg.replace("(*)", "\n");
				 System.out.println("[" + retMsg + "]");
				 }
			
				
			 } catch (IOException e) {
				 e.printStackTrace();
				 clientSocket.close();
				 System.exit(1);
			 }
		 }
	 
	 private void directBuy(String msg) {

		 String cmd[] = msg.split("[ ]+");
		 
		 if (cmd.length < 3){
			 System.out.print( "Error: arguments");
			 return;
		 }

		 SOBClient user = this.clientTable.get(cmd[1]);

		 if (user != null){
			 String sellerMsg = "&buy& " + this.localUser + " wants to buy your " + cmd[2];
			 if (sendUserMsg(user, sellerMsg, cmd[1]))
				 return;
		 }
		 
		 //offline buy
		 this.directSell(this.localUser, cmd[2]);

	 }


	private boolean sendUserMsg(SOBClient user, String msg, String name) {
		//System.out.println("Sending Message to " + user.getUsername());
		
		int i = 0;
		int timeoutAttempt = 5;
		DatagramSocket sendSocket = null;

		byte [] receiveData = new byte[1188];
		byte[] sendData = new byte[1188];
		sendData = msg.getBytes();
		
		try {
			sendSocket =  new DatagramSocket();
			sendSocket.setSoTimeout(500);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		while (i < timeoutAttempt){
			try{
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, user.getIpAddress(), user.getListenPort());
				sendSocket.send(sendPacket);
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				sendSocket.receive(receivePacket);

				byte rawPacket[] = receivePacket.getData();
				for (int j = 0; j < rawPacket.length; j++){
					if (rawPacket[j] == 0)
						rawPacket[j] = 32;
				}

				
				System.out.println("sobs> [" + name + " has recieved your request.]");
				break;
			}catch(SocketTimeoutException  e){
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}			

		}
		
		try {
			sendSocket.setSoTimeout(0);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		sendSocket.close();
		
		if (i == timeoutAttempt)
			return false;
		else
			return true;

	}

	public void directSell(String customer, String item) {
		String saleMsg = "direct "+ item + " " + customer;
		System.out.println("direct " + item + " " + customer);
		//offline buy
		this.sendData = saleMsg.getBytes();
		this.sendToServer();		
	}
	 
}

class ClientServer extends Thread{
	DatagramSocket listenSocket;
	Client client;
	boolean run;


	public ClientServer(Client client) {
		try {
			listenSocket =  new DatagramSocket();
			this.client = client;
			this.run = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		start();
	}

	public void run(){		
		try{
			byte[] sendAck = new byte[1188];
			sendAck = "Ack".getBytes();
			
			while(this.run == true){
				byte[] receiveData = new byte[1188];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				this.listenSocket.receive(receivePacket);
				
				
				byte rawPacket[] = receivePacket.getData();
				for (int j = 0; j < rawPacket.length; j++){
					if (rawPacket[j] == 0)
						rawPacket[j] = 32;
				}
				
				//System.out.print("sobs> ");
				String retMsg = new String(rawPacket);
				String cmd[] = retMsg.split("[ ]+");
				
				if (cmd[0].equals("&table&")){
					int n = 1;
					try{
						n = Integer.parseInt(cmd[1]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					updateTable(n);
					continue;
				}
				
				
				InetAddress ip = receivePacket.getAddress();
				int port = receivePacket.getPort();
								
				DatagramPacket sendPacket = new DatagramPacket(sendAck, sendAck.length, ip, port);
				this.listenSocket.send(sendPacket);
				
				System.out.print("[");
				for (int j = 0; j < cmd.length; j++){
					if (j > 0)
						System.out.print(" ");
					if (cmd[j].equals("&buy&") == false)
						System.out.print(cmd[j]);
				}
				System.out.println("]");
				System.out.print("sobs> ");
				
				if (cmd[0].equals("&buy&")){
					client.directSell(cmd[1], cmd[6]);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	

	private void updateTable(int n) {
		client.clientTable.clear();
		try{
			for (int i = 1; i<n; i++){ 
				byte[] receiveData = new byte[1188];
				receiveData = new byte[1188];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				this.listenSocket.receive(receivePacket);


				byte rawPacket[] = receivePacket.getData();
				for (int j = 0; j < rawPacket.length; j++){
					if (rawPacket[j] == 0)
						rawPacket[j] = 32;
				}

				String retMsg = new String(rawPacket);
				String cmd[] = retMsg.split("[ ]+");


				int mp = 0, lp = 0;
				try{
					mp = Integer.parseInt(cmd[2]);
					lp = Integer.parseInt(cmd[3]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				for (int j = 0; j < cmd.length; j++){
					SOBClient temp = new SOBClient();
					temp.setUsername(cmd[0]);
					temp.setIpAddress(InetAddress.getByName(cmd[1]));
					temp.setMainPort(mp);
					temp.setListenPort(lp);
					client.clientTable.put(temp.getUsername(), temp);
				}
				
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[Client Table Updated.]");
		System.out.println(client.clientTable.toString());
		System.out.print("sobs> ");
		
	}

}

