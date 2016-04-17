package me.game.networking;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable
{
	private ServerSocket ss;

	public static Hashtable<Socket, DataOutputStream> outputStreams = new Hashtable<Socket, DataOutputStream>();
	public static DataOutputStream dout;
	
	public void run() {
		
		try {
			ss = new ServerSocket(1521);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Listening on " + ss);
		System.out.println("Finished [1]: Server");

		while (true) {
			Socket s = null;
			try {
				s = ss.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}

			dout = null;
			try {
				dout = new DataOutputStream( s.getOutputStream() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			Server.reply(s, CheckMessage.LobbysToSendString());

			outputStreams.put( s, dout );

	        Thread n = new Thread(new ServerThread(this, s));
	        n.start();
		}
	}


	static Enumeration<DataOutputStream> getOutputStreams() {
		return outputStreams.elements();
	}

	public static void sendToAll( String message ) {
		synchronized( outputStreams ) {
			for (Enumeration<DataOutputStream> e = getOutputStreams(); e.hasMoreElements(); ) {
				DataOutputStream dout = (DataOutputStream)e.nextElement();
				try {
					dout.writeUTF(message);
				} catch( IOException ie ) { ie.printStackTrace(); }
			}
		}
	}
	
	public static void reply(Socket socket, String message){
		DataOutputStream dout;
		try {
			dout = new DataOutputStream( socket.getOutputStream() );
			dout.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendToAllUsersInLobby(String lobby, String message){
		for(Socket socket : CheckMessage.getUserFromLobby(lobby)){
			DataOutputStream dout;
			try {
				dout = new DataOutputStream( socket.getOutputStream() );
				dout.writeUTF(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void removeConnection( Socket s ) {
		synchronized( outputStreams ) {
			outputStreams.remove(s);
			try {
				s.close();
				if(outputStreams.contains(s.toString())){
					outputStreams.remove(s);
				}
			} catch( IOException ie ) {
				ie.printStackTrace();
				System.out.println("Error closing connection");
			}
			try{
				CheckMessage.removeUserFromLobbys(s);
			}catch(Exception e){};
			if(CheckMessage.Usernames.containsKey(s)){
				CheckMessage.Usernames.remove(s);
			}
		}
	}
}
