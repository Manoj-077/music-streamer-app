cmake_minimum_required(VERSION 3.22.1)

project("raop")

# Add the native RAOP library stub
add_library(raop SHARED
        raop_bridge.cpp
)

# Find and link the Android log library
find_library(log-lib log)

target_link_libraries(raop
        ${log-lib}
)
