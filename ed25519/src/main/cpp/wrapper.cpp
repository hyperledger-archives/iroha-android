#ifdef __cplusplus
extern "C" {
#endif

#include <android/log.h>
#include "wrapper.h"


#define LOGD(...) \
  ((void)__android_log_print(ANDROID_LOG_DEBUG, "wrapper::", __VA_ARGS__))

#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, "wrapper::", __VA_ARGS__))


void Java_com_kobaken0029_ed25519_Ed25519_CreateSeed(
        JNIEnv *env, jclass type, jintArray seed_) {

    jint *seed = env->GetIntArrayElements(seed_, NULL);

    ed25519_create_seed(reinterpret_cast<unsigned char *>(seed));

    env->ReleaseIntArrayElements(seed_, seed, 0);
}

void Java_com_kobaken0029_ed25519_Ed25519_Ed25519CreateKeyPair(
        JNIEnv *env, jclass type, jintArray publicKey_, jintArray privateKey_, jintArray seed_) {
    jint *publicKey = env->GetIntArrayElements(publicKey_, NULL);
    jint *privateKey = env->GetIntArrayElements(privateKey_, NULL);
    jint *seed__ = env->GetIntArrayElements(seed_, NULL);

    unsigned char *public_key = reinterpret_cast<unsigned char *>(publicKey);
    unsigned char *private_key = reinterpret_cast<unsigned char *>(privateKey);
    const unsigned char *seed = reinterpret_cast<const unsigned char *>(seed__);

    ed25519_create_keypair(public_key, private_key, seed);

    jclass ed25519Class = env->FindClass("com/kobaken0029/ed25519/Ed25519");
    jmethodID initKeyPair = env->GetStaticMethodID(ed25519Class, "initKeyPair", "()V");
    jmethodID setPublicKey = env->GetStaticMethodID(ed25519Class, "setPublicKey", "(Ljava/lang/String;)V");
    jmethodID setPrivateKey = env->GetStaticMethodID(ed25519Class, "setPrivateKey", "(Ljava/lang/String;)V");

    env->CallStaticVoidMethod(ed25519Class, initKeyPair);
    env->CallStaticVoidMethod(ed25519Class, setPublicKey, JNU_NewString32Native(env, public_key));
    env->CallStaticVoidMethod(ed25519Class, setPrivateKey, JNU_NewString64Native(env, private_key));

    env->DeleteLocalRef(ed25519Class);

    env->ReleaseIntArrayElements(publicKey_, publicKey, 0);
    env->ReleaseIntArrayElements(privateKey_, privateKey, 0);
    env->ReleaseIntArrayElements(seed_, seed__, 0);
}

void Java_com_kobaken0029_ed25519_Ed25519_Ed25519Sign(
        JNIEnv *env, jclass type, unsigned char *signature, const unsigned char *message,
        unsigned char *public_key, unsigned char *private_key) {

    size_t len = sizeof(message);
    ed25519_sign(signature, message, len, public_key, private_key);

    jclass ed25519Class = env->FindClass("com/kobaken0029/ed25519/Ed25519");
    jmethodID setSignature = env->GetStaticMethodID(
            ed25519Class, "setSignature", "([I)V");

    env->CallStaticVoidMethod(ed25519Class, setSignature, reinterpret_cast<int *>(signature));

    env->DeleteLocalRef(ed25519Class);
}

jint Java_com_kobaken0029_ed25519_Ed25519_Ed25519Verify(
        JNIEnv *env, jclass type, const unsigned char *signature, const unsigned char *message,
        size_t message_len, unsigned char *public_key) {

    return ed25519_verify(signature, message, message_len, public_key);
}

void Java_com_kobaken0029_ed25519_Ed25519_Sha3(
        JNIEnv *env, jobject thiz, const unsigned char *message, size_t message_len,
        unsigned char *out) {

    sha3_256(message, message_len, out);

    jclass ed25519Class = env->FindClass("com/kobaken0029/ed25519/Ed25519");
    jmethodID setSha3Result = env->GetStaticMethodID(
            ed25519Class, "setSha3Result", "([I)V");

    env->CallStaticVoidMethod(ed25519Class, setSha3Result, reinterpret_cast<int *>(out));

    env->DeleteLocalRef(ed25519Class);
}

jintArray Java_com_kobaken0029_ed25519_Ed25519_Base64Encode(
        JNIEnv *env, jclass type, jintArray bytes_to_encode_, jint in_len) {

    const jsize length = env->GetArrayLength(bytes_to_encode_);
    jintArray newArray = env->NewIntArray(length);
    jint *oarr = env->GetIntArrayElements(bytes_to_encode_, NULL);
    jint *bytes_to_encode = env->GetIntArrayElements(newArray, NULL);

    for (int i = 0; i < length; i++) {
        bytes_to_encode[i] = oarr[i];
    }

    char *encoded = base64_encode(reinterpret_cast<const unsigned char*>(bytes_to_encode), in_len);

    env->ReleaseIntArrayElements(newArray, reinterpret_cast<jint *>(encoded), NULL);

    return newArray;
}

jintArray Java_com_kobaken0029_ed25519_Ed25519_Base64Decode(
        JNIEnv *env, jclass type, jintArray encoded_string_) {

    const jsize length = env->GetArrayLength(encoded_string_);
    jintArray newArray = env->NewIntArray(length);
    jint *encoded_string = env->GetIntArrayElements(newArray, NULL);

    unsigned char *decoded = base64_decode(reinterpret_cast<const char*>(encoded_string));

    env->ReleaseIntArrayElements(newArray, reinterpret_cast<jint *>(decoded), NULL);

    return newArray;
}

jstring JNU_NewString32Native(JNIEnv *env, const unsigned char *str) {
    jstring result;
    jbyteArray bytes = 0;
    if (env->EnsureLocalCapacity(2) < 0) {
        return NULL; /* out of memory error */
    }
    bytes = env->NewByteArray(32);

    if (bytes != NULL) {
        env->SetByteArrayRegion(bytes, 0, 32, (jbyte *)str);
        jchar *buff = reinterpret_cast<jchar *>(bytes);
        result = env->NewString(buff, sizeof(buff));
        env->DeleteLocalRef(bytes);
        return result;
    } /* else fall through */
    return NULL;
}

jstring JNU_NewString64Native(JNIEnv *env, const unsigned char *str) {
    jstring result;
    jbyteArray bytes = 0;
    if (env->EnsureLocalCapacity(2) < 0) {
        return NULL; /* out of memory error */
    }
    bytes = env->NewByteArray(64);

    if (bytes != NULL) {
        env->SetByteArrayRegion(bytes, 0, 64, (jbyte *)str);
        jchar *buff = reinterpret_cast<jchar *>(bytes);
        result = env->NewString(buff, sizeof(buff));
        env->DeleteLocalRef(bytes);
        return result;
    } /* else fall through */
    return NULL;
}

//jbyteArray as_byte_array(JNIEnv *env, unsigned char *buf, int len) {
//    jbyteArray array = env->NewByteArray(len);
//    env->SetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
//    return array;
//}
//
//unsigned char *as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
//    int len = env->GetArrayLength(array);
//    unsigned char *buf = new unsigned char[len];
//    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
//    return buf;
//}

#ifdef __cplusplus
}
#endif
