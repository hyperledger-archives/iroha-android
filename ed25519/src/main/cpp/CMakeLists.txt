cmake_minimum_required(VERSION 3.4.1)

add_library(
    ed25519-android
    SHARED
    add_scalar.c
    base64.c
    fe.c
    ge.c
    key_exchange.c
    keypair.c
    sc.c
    seed.c
    sha3.c
    sign.c
    verify.c
    wrapper.cpp
)

# Include libraries needed for hello-jni lib
target_link_libraries(
    ed25519-android
    log
    android
)