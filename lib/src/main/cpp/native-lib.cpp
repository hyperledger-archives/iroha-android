/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

#include <jni.h>
#include <string>

#include <memory>
#include <vector>

#include <ed25519.h>
#include <base64.hpp>

class KeyPair {
public:
    std::vector<unsigned char> publicKey;
    std::vector<unsigned char> privateKey;

    KeyPair(
            std::vector<unsigned char> &&pub,
            std::vector<unsigned char> &&pri
    ) :
            publicKey(std::move(pub)),
            privateKey(std::move(pri)) { }
};

template<typename T>
std::unique_ptr<T> vector2UnsignedCharPointer(
        std::vector<T> vec
) {
    std::unique_ptr<T> res(new T[sizeof(T) * vec.size() + 1]);
    size_t pos = 0;
    for (auto c : vec) {
        res.get()[pos] = c;
        pos++;
    }
    res.get()[pos] = '\0';
    return res;
}

template<typename T>
std::vector<T> pointer2Vector(
        std::unique_ptr<T> &&array,
        size_t length
) {
    std::vector<T> res(length);
    res.assign(array.get(), array.get() + length);
    return res;
}

bool verify(
        const std::string signature,
        const std::string message,
        const std::string publicKey) {
    return ed25519_verify(
            vector2UnsignedCharPointer(base64::decode(signature)).get(),
            reinterpret_cast<const unsigned char *>(message.c_str()),
            message.size(),
            vector2UnsignedCharPointer(base64::decode(publicKey)).get());
}

std::string sign(
        std::string message,
        KeyPair keyPair
) {
    std::unique_ptr<unsigned char> signature(new unsigned char[sizeof(unsigned char) * 64]);
    ed25519_sign(
            signature.get(),
            reinterpret_cast<const unsigned char *>(message.c_str()),
            message.size(),
            vector2UnsignedCharPointer(keyPair.publicKey).get(),
            vector2UnsignedCharPointer(keyPair.privateKey).get()
    );
    return base64::encode(pointer2Vector(std::move(signature), 64));
}

std::string sign(
        std::string message,
        std::string publicKey_b64,
        std::string privateKey_b64
) {
    std::unique_ptr<unsigned char> signatureRaw(new unsigned char[sizeof(unsigned char) * 64]);
    ed25519_sign(
            signatureRaw.get(),
            reinterpret_cast<const unsigned char *>(message.c_str()),
            message.size(),
            vector2UnsignedCharPointer<unsigned char>(
                    base64::decode(publicKey_b64)
            ).get(),
            vector2UnsignedCharPointer<unsigned char>(
                    base64::decode(privateKey_b64)
            ).get()
    );
    return base64::encode(
            pointer2Vector(std::move(signatureRaw), 64)
    );
}

KeyPair generateKeyPair() {
    std::unique_ptr<unsigned char> publicKeyRaw(new unsigned char[sizeof(unsigned char) * 32]);
    std::unique_ptr<unsigned char> privateKeyRaw(new unsigned char[sizeof(unsigned char) * 64]);
    std::unique_ptr<unsigned char> seed(new unsigned char[sizeof(unsigned char) * 32]);

    ed25519_create_seed(seed.get());
    ed25519_create_keypair(
            publicKeyRaw.get(),
            privateKeyRaw.get(),
            seed.get()
    );

    return KeyPair(
            pointer2Vector(std::move(publicKeyRaw), 32),
            pointer2Vector(std::move(privateKeyRaw), 64)
    );
}


extern "C"
jobject
Java_click_kobaken_rxirohaandroid_Ed25519_GenerateKeyPair(
        JNIEnv *env,
        jobject /* this*/) {

    KeyPair pair = generateKeyPair();

    jclass clazz = (*env).FindClass("java/util/ArrayList");
    jobject obj = (*env).NewObject(clazz, (*env).GetMethodID(clazz, "<init>", "()V"));

    std::string privateKeyb64 = base64::encode(pair.privateKey);
    std::string publicKeyb64 = base64::encode(pair.publicKey);

    (*env).CallBooleanMethod(obj, (*env).GetMethodID(clazz, "add", "(Ljava/lang/Object;)Z"),
                             (*env).NewStringUTF(privateKeyb64.c_str()));
    (*env).CallBooleanMethod(obj, (*env).GetMethodID(clazz, "add", "(Ljava/lang/Object;)Z"),
                             (*env).NewStringUTF(publicKeyb64.c_str()));

    return obj;
}

extern "C"
jstring
Java_click_kobaken_rxirohaandroid_Ed25519_Signature(
        JNIEnv *env,
        jobject /* this*/,
        jstring aMessage,
        jstring aPriKeyb64,
        jstring aPubKeyb64
) {

    std::string message = std::string(env->GetStringUTFChars(aMessage, 0));
    std::string priKeyb64 = std::string(env->GetStringUTFChars(aPriKeyb64, 0));
    std::string pubKeyb64 = std::string(env->GetStringUTFChars(aPubKeyb64, 0));

    return (*env).NewStringUTF(sign(message, pubKeyb64, priKeyb64).c_str());
}

extern "C"
jboolean
Java_click_kobaken_rxirohaandroid_Ed25519_Verify(
        JNIEnv *env,
        jobject /* this*/,
        jstring aSignatureb64,
        jstring aMessage,
        jstring aPubKeyb64
) {

    std::string signatureb64 = std::string(env->GetStringUTFChars(aSignatureb64, 0));
    std::string message = std::string(env->GetStringUTFChars(aMessage, 0));
    std::string pubKeyb64 = std::string(env->GetStringUTFChars(aPubKeyb64, 0));

    return (jboolean) verify(signatureb64, message, pubKeyb64);
}