package com.crrl.beatplayer.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentPlaylistDetailBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.toPlaylist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.modelview.SongAdapter
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailFragment : BaseFragment<Song>() {

    private lateinit var binding: FragmentPlaylistDetailBinding
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
            addItemDecoration(EndOffsetItemDecoration(resources.getDimensionPixelOffset(R.dimen.song_item_size)))
        }

        reloadAdapter()

        binding.let {
            it.lifecycleOwner = this
        }
    }

    private fun reloadAdapter() {
        viewModel.songsByPlayList(binding.playlist!!.id).observe(this) {
            songAdapter.updateDataSet(it)
        }
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        Toast.makeText(context, "Song: ${item.title}", Toast.LENGTH_LONG).show()
    }

    override fun onShuffleClick(view: View) {
        Toast.makeText(context, "Shuffle", Toast.LENGTH_LONG).show()
    }

    override fun onPlayAllClick(view: View) {
        Toast.makeText(context, "Play All", Toast.LENGTH_LONG).show()
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song) {
        Toast.makeText(context, "Menu of ${item.title}", Toast.LENGTH_LONG).show()
    }
}
