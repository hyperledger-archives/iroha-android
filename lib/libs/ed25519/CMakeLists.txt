
cmake_minimum_required(VERSION 3.4.1)

file(GLOB files src/*.c)

add_library(
  ed25519
  SHARED
  ${files}
)

target_link_libraries(
    ed25519
    log
    android
)
