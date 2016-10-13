#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include "ed25519.h"


jbyteArray Java_com_kobaken0029_ed25519_Ed25519_CreateSeed(
        JNIEnv *env,
        jobject thiz,
        unsigned char *seed
);
jobject Java_com_kobaken0029_ed25519_Ed25519_Ed25519CreateKeyPair(
        JNIEnv *env,
        jobject thiz,
        unsigned char *public_key,
        unsigned char *private_key,
        const unsigned char *seed
);
jbyteArray Java_com_kobaken0029_ed25519_Ed25519_Ed25519Sign(
        JNIEnv *env,
        jobject thiz,
        unsigned char *signature,
        const unsigned char *message,
        unsigned char *public_key,
        unsigned char *private_key
);
jint Java_com_kobaken0029_ed25519_Ed25519_Ed25519Verify(
        JNIEnv *env,
        jobject thiz,
        const unsigned char *signature,
        const unsigned char *message,
        unsigned char *public_key
);
jbyteArray Java_com_kobaken0029_ed25519_Ed25519_sha3(
        JNIEnv *env,
        jobject thiz,
        const unsigned char *message,
        size_t message_len
);
jbyteArray as_byte_array(JNIEnv *env, unsigned char* buf, int len);
unsigned char *as_unsigned_char_array(JNIEnv *env, jbyteArray array);

#ifdef __cplusplus
}
#endif
