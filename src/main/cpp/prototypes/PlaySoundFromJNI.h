/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class PlaySoundFromJNI */

#ifndef _Included_PlaySoundFromJNI
#define _Included_PlaySoundFromJNI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     PlaySoundFromJNI
 * Method:    getAudioBuffer
 * Signature: (I)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_PlaySoundFromJNI_getAudioBuffer
  (JNIEnv *, jobject, jint);

/*
 * Class:     PlaySoundFromJNI
 * Method:    loadNextFrame
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_PlaySoundFromJNI_loadNextFrame
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    loadAudio
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_PlaySoundFromJNI_loadAudio
  (JNIEnv *, jobject, jstring);

/*
 * Class:     PlaySoundFromJNI
 * Method:    getSampleFormat
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_PlaySoundFromJNI_getSampleFormat
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    getCodecName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_PlaySoundFromJNI_getCodecName
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    getSampleRate
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_PlaySoundFromJNI_getSampleRate
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    getSampleSizeInBits
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getSampleSizeInBits
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    getNumberOfChannels
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getNumberOfChannels
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    getFrameSizeInBy
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_PlaySoundFromJNI_getFrameSizeInBy
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    getFramesPerSecond
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_PlaySoundFromJNI_getFramesPerSecond
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    bigEndian
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_PlaySoundFromJNI_bigEndian
  (JNIEnv *, jobject);

/*
 * Class:     PlaySoundFromJNI
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_PlaySoundFromJNI_release
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif