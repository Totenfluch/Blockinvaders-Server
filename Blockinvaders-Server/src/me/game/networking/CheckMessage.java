package me.game.networking;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

public class CheckMessage {

	public static Hashtable<Socket, String> Usernames = new Hashtable<Socket, String>();
	public static Hashtable<String, ArrayList<Socket>> Lobbys = new Hashtable<String, ArrayList<Socket>>();

	public static void ParseMessage(Socket socket, String[] args, String FullMsg) {
		//System.out.println(FullMsg);
		if (FullMsg.startsWith("createLobby") && args.length > 1) {
			if (CreateLobby(args[1], socket)) {
				System.out.println("Set lobby of " + socket + " to " + args[1]);
				Server.reply(socket, "setLobby " + args[1]);
			} else {
				Server.reply(socket, "setLobbyFailed");
			}
			Server.sendToAll(LobbysToSendString());
		} else if (FullMsg.startsWith("joinLobby") && args.length > 1) {
			if (addUserToLobby(args[1], socket)) {
				Server.reply(socket, "setLobby " + args[1]);
				if (getUserFromLobby(args[1]).size() == 2) {
					Server.sendToAllUsersInLobby(args[1], "StartGame");
				}
			} else {
				Server.reply(socket, "joinLobbyFailed");
			}
			Server.sendToAll(LobbysToSendString());
		} else if (FullMsg.startsWith("playerPos")) {
			Server.sendToAllUsersInLobbyBut(getLobbyFromSocket(socket), FullMsg, socket);
		} else if (FullMsg.startsWith("shoot")){
			Server.sendToAllUsersInLobbyBut(getLobbyFromSocket(socket), FullMsg, socket);
		}
	}

	public static ArrayList<Socket> getUserFromLobby(String lobby) {
		if (Lobbys.containsKey(lobby))
			return Lobbys.get(lobby);
		else
			return null;
	}

	public static String getLobbyFromSocket(Socket player) {
		for(Entry<String, ArrayList<Socket>> entry : Lobbys.entrySet()) {
		    ArrayList<Socket> value = entry.getValue();
		    if(value.contains(player))
		    	return entry.getKey();
		}

		return null;
	}

	public static String GetUsernameFromSocket(Socket s) {
		if (Usernames.containsKey(s))
			return Usernames.get(s);
		return null;
	}

	public static boolean CreateLobby(String channelname, Socket Player) {
		if (Lobbys.containsKey(channelname))
			return false;
		else {
			removeUserFromLobbys(Player);
			
			ArrayList<Socket> ppl = new ArrayList<Socket>();
			ppl.add(Player);
			Lobbys.put(channelname, ppl); 
			return true;
		}
	}

	public static boolean addUserToLobby(String lobby, Socket Player) {
		if (Lobbys.containsKey(lobby)) {
			if (Lobbys.get(lobby).size() < 2) {
				if(!Lobbys.get(lobby).contains(Player)){
					removeUserFromLobbys(Player);
					
					ArrayList<Socket> newStuff = new ArrayList<Socket>();
					newStuff.addAll(Lobbys.get(lobby));
					newStuff.add(Player);
					Lobbys.put(lobby, newStuff);
					return true;
				}
				else {
					//already in this lobby
					return false;
				}
			} else {
				// Lobby full
				return false;
			}
		} else {
			// No such lobby
			return false;
		}
	}

	public static boolean removeUserFromLobbys(Socket Player) {
		if (getLobbyFromSocket(Player) == null)
			return false;
		
		String theUsersLobby = getLobbyFromSocket(Player);
		ArrayList<Socket> UsersInLobby = getUserFromLobby(theUsersLobby);
		UsersInLobby.remove(Player);
		if (UsersInLobby.size() < 1)
			disbandLobby(theUsersLobby);
		else {
			Lobbys.remove(theUsersLobby);
			Lobbys.put(theUsersLobby, UsersInLobby);
		}
		Server.sendToAll(LobbysToSendString());
		return true;
	}

	public static boolean disbandLobby(String lobby) {
		if (Lobbys.containsKey(lobby)) {
			Lobbys.remove(lobby);
			return true;
		}
		return false;
	}

	public static Socket GetSocketFromUsernameAndLobby(String s, String lobby) {
		for (String thelobby : Lobbys.keySet()) {
			if (thelobby.equals(lobby)) {
				for (Socket players : Lobbys.get(thelobby)) {
					if (Usernames.get(players).equals(s))
						return players;
				}
			}

		}
		return null;
	}

	public static String LobbysToSendString() {
		StringBuffer sb = new StringBuffer();
		sb.append("lobbys");
		if(Lobbys.keySet().size() > 0)
			sb.append(" ");
		for (String Lobbynames : Lobbys.keySet()) {
			sb.append(Lobbynames + "," + Lobbys.get(Lobbynames).size() + " ");
		}
		
		return sb.toString();
	}
}
