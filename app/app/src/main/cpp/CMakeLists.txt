cmake_minimum_required(VERSION 3.22.1)
project(meetminutes_whisper)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include(FetchContent)

# whisper.cpp-Build-Optionen: keine Beispiele/Tests, kein OpenMP, kein -march=native
# (Cross-Compile für Android).
set(WHISPER_BUILD_TESTS    OFF CACHE BOOL "" FORCE)
set(WHISPER_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(WHISPER_BUILD_SERVER   OFF CACHE BOOL "" FORCE)
set(GGML_OPENMP            OFF CACHE BOOL "" FORCE)
set(GGML_NATIVE            OFF CACHE BOOL "" FORCE)

FetchContent_Declare(
    whisper
    GIT_REPOSITORY https://github.com/ggerganov/whisper.cpp.git
    GIT_TAG v1.7.4
)
FetchContent_MakeAvailable(whisper)

add_library(meetminutes_whisper SHARED whisper_jni.cpp)

find_library(log-lib log)

target_link_libraries(meetminutes_whisper whisper ${log-lib})
