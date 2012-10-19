package sobs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class Client {
	public List <SOBClient> clientTable;
	
	
	
	 public static void main(String args[]) throws IOException {
		 initConnection("localhost", 1337);
	
		 
	 }
		 	
		 

	 public void checkInput(String msg){
		 System.out.print("sobs> ");
		 Scanner in = new Scanner(System.in);
		 String str;
		 String cmd[];
		 int retValue;

		 while(true){
			 str = in.nextLine();
			 cmd = str.split(" ");
			 System.out.println("length = " + cmd.length);

			 if (cmd[0] == null) {
				 System.out.println("Error: Invalid command");
			 }	 
		 }
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

	 public static void initConnection(String ipAddress, int port){
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
				 //clientSocket.close();
				
			 } catch (IOException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				 System.exit(1);
			 }
		 }
	 
}
