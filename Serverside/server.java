

import java.sql.*;
public class server{
	private Multinetwork n;
	static {
	    System.loadLibrary("ccode");
	  }
	
	public server()
	{
		try{n=new Multinetwork();}
		
		catch(SQLException e)
		{
			System.out.println("error");
		}
	}
	public static void main(String[]args)
	{
		server s=new server();
		
		s.run();
		
	}
	public void run()
	{
		
		while(true)
		{
			System.out.println("Warte auf Verbindung");
			String word=getString();
			word=serverNetz(word);
			System.out.println("Verbindung!");
			sendString(word);
		}
	}
	public native String getString();
	public native void sendString(String s);
	
	public String serverNetz(String s)
	{
		
			return n.correct(s);
		
		
		
	}
}
