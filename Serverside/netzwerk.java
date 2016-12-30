import java.awt.List;
import java.util.Vector;
import java.util.LinkedList;
import java.sql.*;
import static jcuda.driver.JCudaDriver.*;
import jcuda.*;
import jcuda.driver.*;
import jcuda.runtime.*;
public class netzwerk
{
	private final int vectorcount;
	private final int questionlength=676;
	private final int targetlength=260;
	private final int wordstohandle=3;
	private boolean[][]matrix;
	private Vector<Boolean>[] t;
	private Vector<Boolean>[] q;
	private Datenbank db;
	public netzwerk(LinkedList<String> list, Datenbank db)
	{
		this.db=db;
		System.out.println("Hallo vom Endnetzwerk ");
		LinkedList<String> wordlist=list;

		String[]test=new String[wordlist.size()];
		for(int i=0;i<wordlist.size();i++)
		{
			test[i]=wordlist.get(i);
			System.out.println(test[i]);
		}
		//String[]test=wordlist.toArray(new String[wordlist.size()]);
		//String[]test={"tests","daten","java"};
		vectorcount=test.length;
		t = (Vector<Boolean>[]) new Vector[vectorcount];
		for(int i = 0; i < t.length; i++)
			t[i] = new Vector<Boolean>();
		
		q = (Vector<Boolean>[]) new Vector[vectorcount];
		for(int i = 0; i < q.length; i++)
			q[i] = new Vector<Boolean>();
		
		matrix=new boolean[questionlength][targetlength];
		
		for(int i=0;i<test.length;i++) //Für jedes Wort das zu lernen ist
		{
			for(int k=0;k<676;k++)
			{
				q[i].addElement(false); //Erste Initialisierung des zum Wort gehörigen Anfragevektors
			}
			for(int k=0;k<test[i].length()-1;k++) //Füllen des Anfragevektoren
			{
				q[i].set(26*(test[i].charAt(k)-97)+(test[i].charAt(k+1)-97), true); //Setzt für jeden Buchststaben des Wortes die benötigten Felder auf True
			}
		}
		for(int i=0;i<test.length;i++)  //Für jedes Wort das zu lernen ist
		{
			for(int k=0;k<260;k++)
			{
				t[i].addElement(false); //Füllen des Zielvektors
			}
			for(int k=0;k<test[i].length();k++)
			{
				t[i].set(k*26+test[i].charAt(k)-97, true);  //Setzt für jeden Buchststaben des Wortes die benötigten Felder auf True
				
			}
		}
		for(int i=0;i<vectorcount;i++) //Erstellen der Matrix
		{
			for(int k=0;k<questionlength;k++)
			{
				for(int n=0;n<targetlength;n++)
				{
					matrix[k][n]|=lernen(k,n,i); //OR des Lernwertes mit den vorherigen Werten in dem MAtrixfeld
				}
			}
		}
	}
	public netzwerk(int id,Datenbank dat)
	{
		System.out.println("Hallo von Netzwerk "+id);
		this.db=dat;
		
		ResultSet result=db.getResultSet();
		LinkedList<String> wordlist=new LinkedList<String>();
		try{
			for(int i=0;i<id*wordstohandle;i++)
			{
				result.next();
			}
			int counter=0;
			while(result.next()&&counter<wordstohandle) {
				wordlist.add(result.getString("wort"));
				counter++;
			}
		}
		catch(SQLException e)
		{}
		String[]test=new String[wordlist.size()];
		for(int i=0;i<wordlist.size();i++)
		{
			test[i]=wordlist.get(i);
			System.out.println(test[i]);
		}
		//String[]test=wordlist.toArray(new String[wordlist.size()]);
		//String[]test={"tests","daten","java"};
		vectorcount=test.length;
		t = (Vector<Boolean>[]) new Vector[vectorcount];
		for(int i = 0; i < t.length; i++)
			t[i] = new Vector<Boolean>();
		
		q = (Vector<Boolean>[]) new Vector[vectorcount];
		for(int i = 0; i < q.length; i++)
			q[i] = new Vector<Boolean>();
		
		matrix=new boolean[questionlength][targetlength];
		
		for(int i=0;i<test.length;i++) //Für jedes Wort das zu lernen ist
		{
			for(int k=0;k<676;k++)
			{
				q[i].addElement(false); //Erste Initialisierung des zum Wort gehörigen Anfragevektors
			}
			for(int k=0;k<test[i].length()-1;k++) //Füllen des Anfragevektoren
			{
				q[i].set(26*(test[i].charAt(k)-97)+(test[i].charAt(k+1)-97), true); //Setzt für jeden Buchststaben des Wortes die benötigten Felder auf True
			}
		}
		for(int i=0;i<test.length;i++)  //Für jedes Wort das zu lernen ist
		{
			for(int k=0;k<260;k++)
			{
				t[i].addElement(false); //Füllen des Zielvektors
			}
			for(int k=0;k<test[i].length();k++)
			{
				t[i].set(k*26+test[i].charAt(k)-97, true);  //Setzt für jeden Buchststaben des Wortes die benötigten Felder auf True
				
			}
		}
		for(int i=0;i<vectorcount;i++) //Erstellen der Matrix
		{
			for(int k=0;k<questionlength;k++)
			{
				for(int n=0;n<targetlength;n++)
				{
					matrix[k][n]|=lernen(k,n,i); //OR des Lernwertes mit den vorherigen Werten in dem MAtrixfeld
				}
			}
		}
	}
	public boolean lernen(int k,int n,int index)  //Einzelner Lernschritt Auslagerung
	{
		if( q[index].get(k)==false||t[index].get(n)==false)  //Wenn beide Werte der zueinandergehörigen Vektoren 0 sind -> lernwert 0
			return false;
		else
			return true;
	}
	
	public Vector<Integer> matrixMultiplikation(boolean[][]matrix,boolean[] vector)  //Multipliziert Matrix mit Vektor
	{
		Vector<Integer> loesung=new Vector<Integer>();
		for(int i=0;i<targetlength;i++)
		{
			int sum=0;
			for(int k=0;k<questionlength;k++)
			{
				if(matrix[k][i]&&vector[k])
				 sum++;
				
			}
			loesung.add(sum);
		}
		return loesung;
	}
	
	
	public String testFor(String s) //Korrigiert den Übergebenen String 
	{
		System.out.println("netzwerk korrigiert "+s);
		Vector<Integer> loesung=new Vector<Integer>();
		Vector<Boolean> request=new Vector<Boolean>();
		String req=s;
		
		int[] cudavector=new int[676];
		
		int[] cudaoutputvector=new int[260];
		
		int[] cudamatrix=new int[676*260];
		
		for(int i=0;i<questionlength;i++)
		{
			for(int k=0;k<targetlength;k++)
			{
				if(matrix[i][k])
					cudamatrix[i*targetlength+k]=1;
				else
					cudamatrix[i*targetlength+k]=0;
			}
		}
		
		for(int k=0;k<676;k++)
		{
			request.addElement(false);
			cudavector[k]=0;
		}
		for(int k=0;k<req.length()-1;k++)
		{
			//System.out.println(26*(test[i].charAt(k)-97)+(test[i].charAt(k+1)-97));
			request.set(26*(req.charAt(k)-97)+(req.charAt(k+1)-97), true);
			cudavector[26*(req.charAt(k)-97)+(req.charAt(k+1)-97)]=1;
		}
		//Später aufräumen!
		Boolean [] arr=request.toArray(new Boolean[request.size()]);
		boolean[] boolarr=new boolean[request.size()];
		for(int i=0;i<request.size();i++)
		{
			boolarr[i]=(boolean)arr[i];
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		
		int totalcount=676*260;
		int threadcount=676;
		
		JCudaDriver.setExceptionsEnabled(true);
		
		cuInit(0);
		CUdevice device = new CUdevice();
		cuDeviceGet(device, 0);
		CUcontext context = new CUcontext();
		cuCtxCreate(context, 0, device);
		
		CUmodule module = new CUmodule();
		cuModuleLoad(module, "kernel.ptx");
		CUfunction function = new CUfunction();
		cuModuleGetFunction(function, module, "mult");
		
		CUdeviceptr devicevector = new CUdeviceptr();
		cuMemAlloc(devicevector, Sizeof.INT*676);
		cuMemcpyHtoD(devicevector,Pointer.to(cudavector) , Sizeof.INT*676); //Zu INT
		
		CUdeviceptr devicematrix = new CUdeviceptr();
		cuMemAlloc(devicematrix, Sizeof.INT*totalcount);
		cuMemcpyHtoD(devicematrix, Pointer.to(cudamatrix), Sizeof.INT*totalcount); //Zu INT
		
		CUdeviceptr deviceoutputvector = new CUdeviceptr();
		cuMemAlloc(deviceoutputvector, Sizeof.INT*260);
		
		
		Pointer kernelParameters = Pointer.to(
		Pointer.to(devicevector), 
		Pointer.to(devicematrix), 
		Pointer.to(deviceoutputvector)
		);
		
		
		cuLaunchKernel(
		function, 
		(totalcount + threadcount-1) / threadcount,  1, 1,
		threadcount, 1, 1,
		0, null,               
		kernelParameters, null 
		); 
		cuMemcpyDtoH(Pointer.to(cudaoutputvector), deviceoutputvector, Sizeof.INT*260);
		
		for(int i=0;i<260;i++)
		{
			loesung.add(cudaoutputvector[i]);
		}
		
		//cudaMalloc der Matrix
		//boolarr zu vector, dann cudaMalloc
		//cudaMalloc Lösung
		//CUDA memcpy matrix
		//CUDA memcpy vector
		//Kernel Launch
		//CUDA memcpy devicetohost
		//loesung=matrixMultiplikation(matrix,boolarr ); //Später auf Device
		
		String sol="";
		for(int k=req.length()-1;k>0;k--)
		{
			System.out.println("Schwelle: "+k);
			for(int i=0;i<targetlength;i++)
			{
				if(loesung.get(i)==k)
					sol+=(char)(i+97-sol.length()*26);
			}
			System.out.println("\""+sol+"\"");
			if(db.inTabel(sol)&&sol!="") 
			{
				//System.out.println(sol+"Existiert");
				return sol;
			}
		}
			//System.out.println(sol+" Existiert nicht ");
		return "";
	}
	
}