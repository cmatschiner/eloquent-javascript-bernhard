#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include "whisper.h"

#define LOG_TAG "MeetMinutesWhisper"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// Lädt das ggml-Modell und liefert einen Kontext-Zeiger (0 = Fehler).
JNIEXPORT jlong JNICALL
Java_at_matschiner_meetminutes_transcription_WhisperNative_nativeInit(
        JNIEnv *env, jobject /* this */, jstring modelPath) {
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    whisper_context_params cparams = whisper_context_default_params();
    cparams.use_gpu = false;
    whisper_context *ctx = whisper_init_from_file_with_params(path, cparams);
    env->ReleaseStringUTFChars(modelPath, path);
    if (ctx == nullptr) {
        LOGE("whisper_init_from_file fehlgeschlagen");
    }
    return reinterpret_cast<jlong>(ctx);
}

// Transkribiert PCM-Float-Audio (16 kHz, Mono, [-1,1]).
// Rückgabe: String[] mit Zeilen "startMs|endMs|text" (null bei Fehler).
JNIEXPORT jobjectArray JNICALL
Java_at_matschiner_meetminutes_transcription_WhisperNative_nativeTranscribe(
        JNIEnv *env, jobject /* this */, jlong ctxPtr, jfloatArray audio,
        jstring language, jint threads) {
    auto *ctx = reinterpret_cast<whisper_context *>(ctxPtr);
    if (ctx == nullptr) return nullptr;

    jsize n = env->GetArrayLength(audio);
    std::vector<float> pcm(static_cast<size_t>(n));
    env->GetFloatArrayRegion(audio, 0, n, pcm.data());

    const char *lang = env->GetStringUTFChars(language, nullptr);

    whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.language = lang;
    params.n_threads = threads > 0 ? threads : 4;
    params.print_progress = false;
    params.print_realtime = false;
    params.print_timestamps = false;
    params.translate = false;
    params.no_context = true;

    int rc = whisper_full(ctx, params, pcm.data(), n);
    env->ReleaseStringUTFChars(language, lang);

    if (rc != 0) {
        LOGE("whisper_full fehlgeschlagen: %d", rc);
        return nullptr;
    }

    int n_segments = whisper_full_n_segments(ctx);
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray result = env->NewObjectArray(n_segments, stringClass, nullptr);

    for (int i = 0; i < n_segments; ++i) {
        const char *text = whisper_full_get_segment_text(ctx, i);
        // t0/t1 sind in Hundertstelsekunden (10-ms-Einheiten).
        int64_t t0 = whisper_full_get_segment_t0(ctx, i) * 10;
        int64_t t1 = whisper_full_get_segment_t1(ctx, i) * 10;
        std::string line = std::to_string(t0) + "|" + std::to_string(t1) + "|" +
                           (text != nullptr ? text : "");
        jstring jline = env->NewStringUTF(line.c_str());
        env->SetObjectArrayElement(result, i, jline);
        env->DeleteLocalRef(jline);
    }
    return result;
}

// Gibt den Kontext frei.
JNIEXPORT void JNICALL
Java_at_matschiner_meetminutes_transcription_WhisperNative_nativeFree(
        JNIEnv *env, jobject /* this */, jlong ctxPtr) {
    auto *ctx = reinterpret_cast<whisper_context *>(ctxPtr);
    if (ctx != nullptr) {
        whisper_free(ctx);
    }
}

} // extern "C"
