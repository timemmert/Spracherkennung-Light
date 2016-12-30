import java.sql.*;
import java.awt.List;
import java.util.Vector;
import java.util.LinkedList;
import java.sql.*;
public class Multinetwork
{
	int wordcount=0;
	Datenbank db;
	netzwerk[] nw;
	public Multinetwork() throws SQLException
	{
		
		db=new Datenbank();
		db.connect();
		wordcount=db.getLength();
		
		System.out.println("Länge: "+wordcount);
		nw=new netzwerk[wordcount/3+1];//ID Vergabe...
		for(int i=0;i<nw.length;i++)
		{
			nw[i]=new netzwerk(i,db);
		}
		nw[1].testFor("jva");
	}
	public String correct(String s)
	{
		LinkedList<String> list=new LinkedList<String>();
		for(int i=0;i<nw.length;i++)
		{
			String str=nw[i].testFor(s);
			if(s!="")
			{
				System.out.println("Es wird "+str+" gespeichert");
				list.add(str);
			}
		}
		netzwerk ende=new netzwerk(list,db);
		return ende.testFor(s);
	}
}