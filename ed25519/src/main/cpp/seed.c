#include "ed25519.h"

#include <android/log.h>

#define LOGD(...) \
  ((void)__android_log_print(ANDROID_LOG_DEBUG, "wrapper::", __VA_ARGS__))

#ifndef ED25519_NO_SEED

int ed25519_create_seed(unsigned char *seed) {
    FILE *f = fopen("/dev/urandom", "rb");

    if (f == NULL) {
        return 1;
    }

    fread(seed, 1, 32, f);
    fclose(f);

    return 0;
}

#endif