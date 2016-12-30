#include <jni.h>
#include <stdio.h>
#include "GUI.h"
#include "usefull.h"
 
JNIEXPORT void JNICALL Java_GUI_c_1send
  (JNIEnv *envi, jobject o, jstring s)
  {
  	struct hostent* h;
  	h=	gethostbyname ("timemmert.ddns.net");
  	
	  client c(inet_ntoa (*((struct in_addr *) h->h_addr_list[0])),500);
	  c.connectTo();
	  const char *nativeString = (envi)->GetStringUTFChars( s, 0);
	  
	  c.sendTCP(nativeString);
	  c.close();
	  (envi)->ReleaseStringUTFChars( s, nativeString);
	  return;
  }

JNIEXPORT jstring JNICALL Java_GUI_c_1receive
  (JNIEnv *envi, jobject o)
  {
  	
  	
  	struct hostent* h;
  	h=	gethostbyname ("timemmert.ddns.net");
  	
	  client c(inet_ntoa (*((struct in_addr *) h->h_addr_list[0])),501);
	c.connectTo();
  	char* newstr=c.receiveTCP();
	c.close();
	  
	jstring jstrBuf = (envi)->NewStringUTF( newstr);
	return jstrBuf;
	
  }
