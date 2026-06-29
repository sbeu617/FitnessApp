package com.sbeu.fitnessapp.presentation.player

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import com.sbeu.fitnessapp.R
import com.sbeu.fitnessapp.databinding.FragmentPlayerBinding
import com.sbeu.fitnessapp.domain.model.VideoInfo
import com.sbeu.fitnessapp.domain.model.Workout
import kotlinx.coroutines.launch

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: PlayerFragmentArgs by navArgs()
    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(args.workout)
    }

    private var player: ExoPlayer? = null

    private val qualityLabels = listOf(
        R.string.quality_auto,
        R.string.quality_240p,
        R.string.quality_480p,
        R.string.quality_720p
    )
    private val qualitySizes: List<Pair<Int, Int>?> = listOf(
        null,
        426 to 240,
        854 to 480,
        1280 to 720
    )
    private var currentQualityIndex = 0

    private var isAdaptiveSource = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)

        renderWorkoutDetails(args.workout)
        observeUiState()
    }

    private fun renderWorkoutDetails(workout: Workout) {
        binding.tvWorkoutTitle.text = workout.title
        binding.tvDuration.text = workout.durationMinutes?.let {
            resources.getQuantityString(R.plurals.minutes, it, it)
        } ?: getString(R.string.duration_unknown)
        binding.tvType.text = getString(workout.type.labelRes)
        binding.tvDescription.text = workout.description ?: getString(R.string.description_placeholder)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: PlayerUiState) {
        binding.progressBar.isVisible = state is PlayerUiState.Loading
        binding.errorView.isVisible = state is PlayerUiState.Error
        binding.contentScrollView.isVisible = state is PlayerUiState.Success

        when (state) {
            is PlayerUiState.Success -> setupPlayer(state.videoInfo)
            is PlayerUiState.Error -> {
                binding.errorText.text = state.message.asString(requireContext())
                binding.retryButton.setOnClickListener { viewModel.retry() }
            }
            else -> Unit
        }
    }

    private fun setupPlayer(videoInfo: VideoInfo) {
        if (player != null) return

        val exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = exoPlayer

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                binding.contentScrollView.isVisible = false
                binding.errorView.isVisible = true
                binding.errorText.text = getString(R.string.error_playback_failed)
                binding.retryButton.setOnClickListener { viewModel.retry() }
            }

            override fun onTracksChanged(tracks: Tracks) {
                val videoGroups = tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }
                isAdaptiveSource = videoGroups.any { it.length > 1 }
            }
        })

        binding.playerView.setControllerVisibilityListener(
            PlayerView.ControllerVisibilityListener { visibility ->
                binding.qualityButton.isVisible = visibility == View.VISIBLE
            }
        )

        binding.qualityButton.setOnClickListener {
            onQualityButtonClicked(exoPlayer)
        }

        val mediaItem = MediaItem.fromUri(videoInfo.videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        player = exoPlayer
    }

    private fun onQualityButtonClicked(exoPlayer: ExoPlayer) {
        if (!isAdaptiveSource) {
            Toast.makeText(
                requireContext(),
                R.string.error_adaptive_only,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        currentQualityIndex = (currentQualityIndex + 1) % qualityLabels.size
        binding.qualityButton.text = getString(qualityLabels[currentQualityIndex])

        val params = exoPlayer.trackSelectionParameters.buildUpon()
        val size = qualitySizes[currentQualityIndex]
        if (size != null) {
            params.setMaxVideoSize(size.first, size.second)
        } else {
            params.clearVideoSizeConstraints()
        }
        exoPlayer.trackSelectionParameters = params.build()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        _binding = null
    }
}