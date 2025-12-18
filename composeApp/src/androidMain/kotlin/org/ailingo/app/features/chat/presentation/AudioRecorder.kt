import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.ailingo.app.features.chat.presentation.model.AudioRecordingException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.coroutineContext

// kotlin.coroutines.coroutineContext is implicitly available in suspend functions

class AudioRecorder(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var isCurrentlyRecording: Boolean = false

    private var pcmFile: File? = null
    private var wavFile: File? = null

    companion object {
        private const val TAG = "AudioRecorder"
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BITS_PER_SAMPLE: Short = 16
        private const val NUM_CHANNELS: Short = 1
    }

    fun startRecording(outputFileName: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            throw AudioRecordingException("RECORD_AUDIO permission not granted.")
        }
        if (isCurrentlyRecording) {
            Log.w(TAG, "Already recording.")
            return
        }

        val baseName = outputFileName.substringBeforeLast('.')
        pcmFile = File(context.cacheDir, "$baseName.pcm")
        wavFile = File(context.cacheDir, "$baseName.wav")

        Log.d(TAG, "PCM file: ${pcmFile?.absolutePath}")
        Log.d(TAG, "WAV file: ${wavFile?.absolutePath}")

        pcmFile?.delete()
        wavFile?.delete()

        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        if (bufferSize == AudioRecord.ERROR_BAD_VALUE || bufferSize == AudioRecord.ERROR) {
            cleanupFiles()
            throw AudioRecordingException("AudioRecord: Invalid parameters for getMinBufferSize.")
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )
        } catch (e: IllegalArgumentException) {
            cleanupFiles()
            throw AudioRecordingException("Failed to create AudioRecord instance: ${e.message}")
        }

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            cleanupRecorder()
            cleanupFiles()
            throw AudioRecordingException("AudioRecord failed to initialize.")
        }

        isCurrentlyRecording = true
        audioRecord?.startRecording()

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            writeAudioDataToFile(pcmFile!!, bufferSize)
        }
        Log.d(TAG, "Recording started.")
    }

    private suspend fun writeAudioDataToFile(file: File, bufferSize: Int) {
        val data = ByteArray(bufferSize)
        var outputStream: FileOutputStream? = null

        try {
            outputStream = FileOutputStream(file)
            // MODIFIED HERE: Use coroutineContext.isActive
            while (coroutineContext.isActive && isCurrentlyRecording && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val readBytes = audioRecord?.read(data, 0, bufferSize) ?: 0
                if (readBytes > 0) {
                    outputStream.write(data, 0, readBytes)
                } else if (readBytes < 0) {
                    Log.e(TAG, "Error reading audio data: $readBytes")
                    break
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing audio data to file: ${e.message}", e)
        } finally {
            try {
                outputStream?.flush()
                outputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing output stream: ${e.message}", e)
            }
            Log.d(TAG, "Finished writing raw PCM data. Loop active: ${coroutineContext.isActive}, isCurrentlyRecording: $isCurrentlyRecording")
        }
    }

    // This version of stopRecording is synchronous and will block the calling thread
    // until the audio processing is complete. This can cause ANR if called on the main thread
    // for long operations. Consider making it a suspend function or using callbacks/flows
    // if asynchronous behavior is preferred.
    fun stopRecording(): String? {
        if (!isCurrentlyRecording || audioRecord == null) {
            Log.w(TAG, "Not recording or recorder not initialized.")
            return null
        }

        isCurrentlyRecording = false // Signal the recording loop to stop

        Log.d(TAG, "stopRecording called. Waiting for I/O job to complete...")
        // Block until the recordingJob finishes.
        // This is generally not recommended on the main UI thread.
        runBlocking { // You can specify a context like Dispatchers.IO if preferred for the join itself
            try {
                recordingJob?.join()
                Log.d(TAG, "Recording I/O job completed.")
            } catch (e: CancellationException) {
                Log.w(TAG, "Recording I/O job was cancelled during join.")
                // The pcmFile might be incomplete or empty.
            } catch (e: Exception) {
                Log.e(TAG, "Exception during recordingJob.join(): ${e.message}", e)
            }
        }
        Log.d(TAG, "Proceeding after I/O job join.")


        if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            try {
                audioRecord?.stop()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "IllegalStateException stopping AudioRecord: ${e.message}", e)
            }
        }
        cleanupRecorder() // Release AudioRecord resources

        if (pcmFile?.exists() == true && pcmFile!!.length() > 0) {
            try {
                addWavHeader(pcmFile!!, wavFile!!)
                pcmFile?.delete() // Clean up raw PCM file
                Log.d(TAG, "WAV file created: ${wavFile!!.absolutePath}")
                return wavFile!!.absolutePath
            } catch (e: IOException) {
                Log.e(TAG, "Error creating WAV file: ${e.message}", e)
                cleanupFiles() // Clean up WAV file if creation failed
                return null
            }
        } else {
            Log.w(TAG, "PCM file is empty or does not exist after recording (length: ${pcmFile?.length()}).")
            cleanupFiles()
            return null
        }
    }


    fun cancelRecording() {
        if (!isCurrentlyRecording && audioRecord == null && recordingJob == null) return

        isCurrentlyRecording = false
        recordingJob?.cancel(CancellationException("Recording cancelled by user"))

        // Wait for the job to acknowledge cancellation if it's still running
        // This part is tricky without making cancelRecording suspend or blocking
        // For now, just ensures AudioRecord is stopped.
        // A more robust cancel might involve runBlocking { recordingJob?.join() }
        // but that makes cancelRecording blocking.

        if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            try {
                audioRecord?.stop()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "IllegalStateException stopping AudioRecord during cancel: ${e.message}", e)
            }
        }
        cleanupRecorder()
        cleanupFiles()
        Log.d(TAG, "Recording cancelled.")
    }

    private fun cleanupRecorder() {
        audioRecord?.release()
        audioRecord = null
    }

    private fun cleanupFiles() {
        pcmFile?.delete()
        wavFile?.delete()
        pcmFile = null
        wavFile = null
    }

    fun isRecording(): Boolean = isCurrentlyRecording
    fun getAudioFilePath(): String? = wavFile?.absolutePath

    @Throws(IOException::class)
    private fun addWavHeader(pcmFile: File, wavFile: File) {
        val pcmDataSize = pcmFile.length()
        // val totalDataLen = pcmDataSize + 36 (Header size without RIFF chunk and size)
        val totalFileSize = pcmDataSize + 44 // Total size including all header fields

        val header = ByteBuffer.allocate(44)
        header.order(ByteOrder.LITTLE_ENDIAN)

        header.put('R'.code.toByte())
        header.put('I'.code.toByte())
        header.put('F'.code.toByte())
        header.put('F'.code.toByte())
        header.putInt((totalFileSize - 8).toInt()) // ChunkSize (Total file size - 8 bytes for "RIFF" and this field)
        header.put('W'.code.toByte())
        header.put('A'.code.toByte())
        header.put('V'.code.toByte())
        header.put('E'.code.toByte())

        header.put('f'.code.toByte())
        header.put('m'.code.toByte())
        header.put('t'.code.toByte())
        header.put(' '.code.toByte())
        header.putInt(16) // Subchunk1Size (16 for PCM)
        header.putShort(1) // AudioFormat (1 for PCM)
        header.putShort(NUM_CHANNELS)
        header.putInt(SAMPLE_RATE)
        header.putInt(SAMPLE_RATE * NUM_CHANNELS * BITS_PER_SAMPLE / 8) // ByteRate
        header.putShort((NUM_CHANNELS * BITS_PER_SAMPLE / 8).toShort()) // BlockAlign
        header.putShort(BITS_PER_SAMPLE)

        header.put('d'.code.toByte())
        header.put('a'.code.toByte())
        header.put('t'.code.toByte())
        header.put('a'.code.toByte())
        header.putInt(pcmDataSize.toInt()) // Subchunk2Size (PCM data size)

        FileOutputStream(wavFile).use { wavOutputStream ->
            wavOutputStream.write(header.array())
            FileInputStream(pcmFile).use { pcmInputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (pcmInputStream.read(buffer).also { bytesRead = it } != -1) {
                    wavOutputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    }
}

@Composable
fun rememberAudioRecorder(): AudioRecorder {
    val context = LocalContext.current
    return remember { AudioRecorder(context) }
}
