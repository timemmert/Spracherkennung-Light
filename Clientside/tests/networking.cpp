#include <jni.h>
#include <stdio.h>
#include "GUI.h"
#include "usefull.h"
 
JNIEXPORT jstring JNICALL Java_GUI_c_1send
  (JNIEnv *envi, jobject o, jstring s)
  {
	  client c("127.0.0.1",500);
	  printf("Verbinden...");
	  c.connectTo();
	  
	  const char *nativeString = (envi)->GetStringUTFChars( s, 0);
	  
	  c.sendTCP(nativeString);
	  char* newstr=c.receiveTCP();
	  (envi)->ReleaseStringUTFChars( s, nativeString);
	  
	  jstring jstrBuf = (envi)->NewStringUTF( newstr);
	  printf("Fertig");
	  
	  return jstrBuf;
  }
