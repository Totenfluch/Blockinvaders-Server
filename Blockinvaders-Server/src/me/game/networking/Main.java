package me.game.networking;

public class Main {
	private static Thread Thread_MainServer;
	public static Server server;
	public static String MyIP;
	
	public static void main(String[] args){
		try{
			server = new Server();
		}catch (Exception e){
			e.printStackTrace();
		}

		Thread_MainServer = new Thread(server);
		Thread_MainServer.start();
	}

}
