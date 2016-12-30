import java.sql.*;
import java.io.*;

public class Datenbank_init
{
	
	Datenbank db;
	public Datenbank_init()
	{
		db=new Datenbank();
		db.connect();
		db.initTables();
	}
	public static void main(String[]args)
	{
		new Datenbank_init().read();
	}
	public void read  ()
	{
		BufferedReader br;
		try {
		br = new BufferedReader(new FileReader("wortliste.txt"));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			db.insert(line);
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
			
		}
		String everything = sb.toString();
		}
		catch(Exception e)
		{
			
		}
	}
}