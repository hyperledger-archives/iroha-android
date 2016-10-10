#ifdef __cplusplus
extern "C" {
#endif

#include <android/log.h>
#include "wrapper.h"


#define LOGD(...) \
  ((void)__android_log_print(ANDROID_LOG_DEBUG, "wrapper::", __VA_ARGS__))

#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, "wrapper::", __VA_ARGS__))


jbyteArray Java_com_kobaken0029_ed25519_Ed25519_CreateSeed(
        JNIEnv *env, jobject thiz, unsigned char *seed) {

    ed25519_create_seed(seed);
    LOGD("seed is %s\n", seed);
    return as_byte_array(env, seed, sizeof(seed));
}

jobject Java_com_kobaken0029_ed25519_Ed25519_Ed25519CreateKeyPair(
        JNIEnv *env, jobject thiz, unsigned char *public_key, unsigned char *private_key, const unsigned char *seed) {

    jclass paramClass = env->FindClass("com/kobaken0029/ed25519/Ed25519$KeyPair");
    jmethodID paramConstructor = env->GetMethodID(paramClass, "<init>", "([B[B)V");

    ed25519_create_keypair(public_key, private_key, seed);
    LOGD("publicKey[%s], privateKey[%s], seed[%s]\n", public_key, private_key, seed);

    jobject result = env->NewObject(
            paramClass,
            paramConstructor,
            as_byte_array(env, public_key, sizeof(public_key)),
            as_byte_array(env, private_key, sizeof(private_key))
    );
    return result;
}

jbyteArray Java_com_kobaken0029_ed25519_Ed25519_Ed25519Sign(
        JNIEnv *env, jobject thiz, unsigned char *signature, const unsigned char *message,
        unsigned char *public_key, unsigned char *private_key) {

    size_t len = sizeof(message);
    ed25519_sign(signature, message, len, public_key, private_key);
    return as_byte_array(env, signature, sizeof(signature));
}

jint Java_com_kobaken0029_ed25519_Ed25519_Ed25519Verify(
    JNIEnv *env, jobject thiz, const unsigned char *signature, const unsigned char *message, unsigned char *public_key) {

    size_t message_len = sizeof(message);
    return ed25519_verify(signature, message, message_len, public_key);
}

jbyteArray Java_com_kobaken0029_ed25519_Ed25519_sha3(
        JNIEnv *env, jobject thiz, const unsigned char *message, size_t message_len) {

    unsigned char *out;
    sha3_256(message, message_len, out);
    return as_byte_array(env, out, sizeof(out));
}

jbyteArray as_byte_array(JNIEnv *env, unsigned char *buf, int len) {
    jbyteArray array = env->NewByteArray(len);
    env->SetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return array;
}

unsigned char *as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength(array);
    unsigned char *buf = new unsigned char[len];
    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return buf;
}

#ifdef __cplusplus
}
#endif
