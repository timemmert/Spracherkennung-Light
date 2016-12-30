#include <jni.h>
#include <stdio.h>
#include "server.h"
#include "usefull.h"
 

JNIEXPORT jstring JNICALL Java_server_getString
  (JNIEnv *env, jobject o)
  {
  	char* newstr;
  	server s(500);
  	newstr=s.receiveTCP();
  	s.close();
  	jstring jstrBuf = (env)->NewStringUTF( newstr);
  	return jstrBuf;
  }

 
JNIEXPORT void JNICALL Java_server_sendString
  (JNIEnv *env, jobject o, jstring st)
  {
  	  const char *nativeString = (env)->GetStringUTFChars( st, 0);
  	  server s(501);
  	  s.sendTCP(nativeString);
  	  s.close();
  	  (env)->ReleaseStringUTFChars( st, nativeString);
  	  return;
  }
