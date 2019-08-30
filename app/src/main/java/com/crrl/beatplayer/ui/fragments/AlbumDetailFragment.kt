package com.crrl.beatplayer.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.extensions.toAlbum
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.AlbumSongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration
import kotlinx.android.synthetic.main.fragment_album_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AlbumDetailFragment : BaseFragment<Song>() {

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var album: Album
    private lateinit var albumSongAdapter: AlbumSongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        album = arguments!!.getString(PlayerConstants.ALBUM_KEY)!!.toAlbum()

        albumSongAdapter = AlbumSongAdapter(context).apply {
            showHeader = true
            itemClickListener = this@AlbumDetailFragment
            this.album = this@AlbumDetailFragment.album
        }

        view.apply {
            album_song_list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = albumSongAdapter
                addItemDecoration(EndOffsetItemDecoration(resources.getDimensionPixelOffset(R.dimen.song_item_size)))
            }
        }

        viewModel.getSongsByAlbum(album.id)!!.observe(this) { list ->
            albumSongAdapter.updateDataSet(list)
        }

    }

    override fun addToList(playListId: Long, song: Song) {
        viewModel.addToPlaylist(playListId, arrayOf(song.id).toLongArray())
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        (safeActivity as MainActivity).viewModel.update(item)
    }

    override fun onPlayAllClick(view: View) {
        Toast.makeText(context, "Play All", Toast.LENGTH_LONG).show()
    }

    override fun onShuffleClick(view: View) {
        Toast.makeText(context, "Shuffle", Toast.LENGTH_LONG).show()
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song) {
        powerMenu!!.showAsAnchorRightTop(view)
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
