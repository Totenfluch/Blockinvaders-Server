package me.game.networking;


import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class CheckMessage {

	public static Hashtable<Socket, String> Usernames = new Hashtable<Socket, String>();
	public static Hashtable<String, ArrayList<Socket>> Lobbys = new Hashtable<String, ArrayList<Socket>>();

	public static void ParseMessage(Socket socket, String[] args, String FullMsg){
	}

	public static String GetUserLobbyFromSocket(Socket s){
		String channelname = null;
		
		return channelname;
	}


	public static String GetUsernameFromSocket(Socket s){
		String usernameZ = null;

		return usernameZ;
	}

	public static void CreateLobby(String channelname){
		// Create a Lobby
	}

	public static Socket GetSocketFromUsernameAndLobby(String s, String lobby){
		Socket key= null;

		return key;
	}
}

