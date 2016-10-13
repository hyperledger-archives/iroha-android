#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include "ed25519.h"
#include "base64.h"


void Java_com_kobaken0029_ed25519_Ed25519_CreateSeed(
        JNIEnv *env,
        jclass type,
        jintArray seed_
);
void Java_com_kobaken0029_ed25519_Ed25519_Ed25519CreateKeyPair(
        JNIEnv *env,
        jclass type,
        jintArray publicKey_,
        jintArray privateKey_,
        jintArray seed_
);
void Java_com_kobaken0029_ed25519_Ed25519_Ed25519Sign(
        JNIEnv *env,
        jclass type,
        unsigned char *signature,
        const unsigned char *message,
        unsigned char *public_key,
        unsigned char *private_key
);
jint Java_com_kobaken0029_ed25519_Ed25519_Ed25519Verify(
        JNIEnv *env,
        jclass type,
        const unsigned char *signature,
        const unsigned char *message,
        size_t message_len,
        unsigned char *public_key
);
void Java_com_kobaken0029_ed25519_Ed25519_Sha3(
        JNIEnv *env,
        jobject thiz,
        const unsigned char *message,
        size_t message_len,
        unsigned char *out
);
jintArray Java_com_kobaken0029_ed25519_Ed25519_Base64Encode(
        JNIEnv *env,
        jclass type,
        jintArray bytes_to_encode_,
        jint in_len
);
jintArray Java_com_kobaken0029_ed25519_Ed25519_Base64Decode(
        JNIEnv *env,
        jclass type,
        jintArray encoded_string_
);
jstring JNU_NewString32Native(JNIEnv *env, const unsigned char *str);
jstring JNU_NewString64Native(JNIEnv *env, const unsigned char *str);
//jbyteArray as_byte_array(JNIEnv *env, unsigned char* buf, int len);
//unsigned char *as_unsigned_char_array(JNIEnv *env, jbyteArray array);

#ifdef __cplusplus
}
#endif
