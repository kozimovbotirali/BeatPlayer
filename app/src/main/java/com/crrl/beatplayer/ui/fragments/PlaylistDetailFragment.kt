package com.crrl.beatplayer.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentPlaylistDetailBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.extensions.toPlaylist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlaylistDetailFragment : BaseFragment<Song>() {

    lateinit var binding: FragmentPlaylistDetailBinding
    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_playlist_detail, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.playlist = arguments!!.getString(PlayerConstants.PLAY_LIST_DETAIL)!!.toPlaylist()

        // Set up adapter
        songAdapter = SongAdapter(activity).apply {
            showHeader = true
            isPlaylist = true
            itemClickListener = this@PlaylistDetailFragment
        }

        // Set up RecyclerView
        binding.songList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
        }

        viewModel.songsByPlayList(binding.playlist!!.id).observe(this) {
            songAdapter.updateDataSet(it)
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
    }

    override fun removeFromList(playListId: Long, item: Song?) {
        Log.println(Log.DEBUG, "Dev", "$playListId, $item")
        viewModel.removeFromPlaylist(playListId, item!!.id)
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        (safeActivity as MainActivity).viewModel.update(item)
    }

    override fun onShuffleClick(view: View) {
        Toast.makeText(context, "Shuffle", Toast.LENGTH_LONG).show()
    }

    override fun onPlayAllClick(view: View) {
        Toast.makeText(context, "Play All", Toast.LENGTH_LONG).show()
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song) {
        super.onPopupMenuClick(view, position, item)
        powerMenu!!.showAsAnchorRightTop(view)
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
