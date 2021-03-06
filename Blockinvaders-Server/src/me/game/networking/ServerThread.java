package me.game.networking;

import java.io.*;
import java.net.*;


public class ServerThread implements Runnable
{
	
	private Server server;
	private Socket socket;

	public ServerThread( Server server, Socket socket ) {
		this.server = server;
		this.socket = socket;
	}

	public void run(){
		try {
			DataInputStream din = new DataInputStream( socket.getInputStream() );
			while (true) {
				String message = din.readUTF();
				String Args[] = message.split(" ");
				CheckMessage.ParseMessage(socket, Args, message);
			}
		} catch( SocketException ie){
			System.out.println(socket + " disconnected");
		} catch( EOFException ie ) {
		} catch( IOException ie ) {
			ie.printStackTrace();
		} finally {
			try{
				CheckMessage.removeUserFromLobbys(socket);
			}catch(Exception e){};
			if(CheckMessage.Usernames.containsKey(socket)){
				CheckMessage.Usernames.remove(socket);
			}
			server.removeConnection( socket );
		}
	}
}