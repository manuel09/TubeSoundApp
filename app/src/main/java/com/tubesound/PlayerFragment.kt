package com.tubesound

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.io.File
import java.net.URL

class PlayerFragment : Fragment() {

    private var streamUrl: String? = null
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var coverImageView: ImageView
    private lateinit var titleView: TextView
    private lateinit var artistView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var downloadButton: Button
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val ARG_URL = "stream_url"

        fun newInstance(url: String): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putString(ARG_URL, url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        streamUrl = arguments?.getString(ARG_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        coverImageView = view.findViewById(R.id.coverImageView)
        titleView = view.findViewById(R.id.titleView)
        artistView = view.findViewById(R.id.artistView)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        downloadButton = view.findViewById(R.id.downloadButton)
        progressBar = view.findViewById(R.id.progressBar)

        playButton.setOnClickListener {
            mediaPlayer?.start() ?: startPlayback()
        }

        pauseButton.setOnClickListener {
            mediaPlayer?.pause()
        }

        downloadButton.setOnClickListener {
            downloadAudio()
        }

        loadStreamInfo()

        return view
    }

    private fun loadStreamInfo() {
        streamUrl?.let { url ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val service = ServiceList.YouTubeMusic
                    val streamInfo = NewPipe.getService(service.serviceId).getStreamExtractor(url).getStreamInfo()

                    withContext(Dispatchers.Main) {
                        titleView.text = streamInfo.name
                        artistView.text = streamInfo.uploaderName
                        Glide.with(this@PlayerFragment)
                            .load(streamInfo.thumbnailUrl)
                            .into(coverImageView)

                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(streamInfo.audioStreams.firstOrNull()?.url)
                            prepare()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun startPlayback() {
        mediaPlayer?.start()
    }

    private fun downloadAudio() {
        streamUrl?.let { url ->
            progressBar.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val service = ServiceList.YouTubeMusic
                    val streamInfo = NewPipe.getService(service.serviceId).getStreamExtractor(url).getStreamInfo()
                    val audioUrl = streamInfo.audioStreams.firstOrNull()?.url

                    if (audioUrl != null) {
                        val fileName = sanitizeFileName(streamInfo.name) + ".mp3"
                        val file = File(requireContext().filesDir, fileName)

                        URL(audioUrl).openStream().use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
    }
}