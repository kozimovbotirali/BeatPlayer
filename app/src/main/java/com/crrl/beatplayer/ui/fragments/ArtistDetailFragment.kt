package com.crrl.beatplayer.ui.fragments

import android.content.ContentUris
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ArtistDetailFragmentBinding
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.toArtist
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.modelview.AlbumAdapter
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("UNCHECKED_CAST")
class ArtistDetailFragment : BaseFragment<MediaItem>() {

    companion object {
        fun newInstance() = ArtistDetailFragment()
    }

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var binding: ArtistDetailFragmentBinding
    private lateinit var artist: Artist

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.artist_detail_fragment, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        artist = arguments!!.getString(PlayerConstants.ARTIST_KEY)!!.toArtist()

        albumAdapter = AlbumAdapter(context).apply {
            itemClickListener = this@ArtistDetailFragment as ItemClickListener<Album>
            spanCount = 3
        }

        binding.apply {
            albumList.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = albumAdapter
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
            }
            cover.layoutParams.height = (GeneralUtils.screenHeight / 2.2).toInt()
        }

        viewModel.getArtistAlbums(artist.id).observe(this) {
            albumAdapter.updateDataSet(it)
            val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, it[0].id)
            Glide.with(context!!)
                .load(uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.song_cover_frame)
                .error(R.drawable.ic_empty_cover)
                .into(binding.cover)
        }

        binding.artist = artist
    }

    override fun addToList(playListId: Long, song: Song) {
        viewModel.addToPlaylist(playListId, arrayOf(song.id).toLongArray())
    }

    private fun albumClicked(item: Album) {
        val extras = Bundle()
        extras.putString(PlayerConstants.ALBUM_KEY, item.toString())
        activity!!.addFragment(
            R.id.nav_host_fragment,
            AlbumDetailFragment(),
            PlayerConstants.ALBUM_DETAIL,
            true,
            extras
        )
    }

    override fun onItemClick(view: View, position: Int, item: MediaItem) {
        when (item) {
            is Song -> Toast.makeText(context, "Song: ${item.title}", Toast.LENGTH_SHORT).show()
            is Album -> albumClicked(item)
        }
    }

    override fun onPopupMenuClick(view: View, position: Int, item: MediaItem) {
        item as Song
        powerMenu!!.showAsAnchorRightTop(view)
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
