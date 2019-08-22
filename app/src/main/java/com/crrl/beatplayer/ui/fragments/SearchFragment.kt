package com.crrl.beatplayer.ui.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentSearchBinding
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.modelview.AlbumAdapter
import com.crrl.beatplayer.ui.modelview.ArtistAdapter
import com.crrl.beatplayer.ui.modelview.SongAdapter
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.GeneralUtils.toggleShowKeyBoard
import com.crrl.beatplayer.utils.PlayerConstants
import kotlinx.android.synthetic.main.fragment_search.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("UNCHECKED_CAST")
class SearchFragment : BaseFragment<MediaItem>() {

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var binding: FragmentSearchBinding

    private lateinit var songAdapter: SongAdapter
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_search, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        songAdapter = SongAdapter(activity).apply {
            itemClickListener = this@SearchFragment as ItemClickListener<Song>
        }

        albumAdapter = AlbumAdapter(context).apply {
            itemClickListener = this@SearchFragment as ItemClickListener<Album>
            spanCount = 3
        }

        artistAdapter = ArtistAdapter(context).apply {
            itemClickListener = this@SearchFragment as ItemClickListener<Artist>
            spanCount = 3
        }

        binding.apply {
            searchSrcText.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(
                        src: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        viewModel!!.search(src.toString())
                        status = src.isNotEmpty()
                    }

                    override fun afterTextChanged(s: Editable?) = Unit

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) = Unit
                })
                toggleShowKeyBoard(context, this, true)
            }

            songList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = songAdapter
            }

            albumList.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = albumAdapter
            }

            artistList.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = artistAdapter
            }

            back.setOnClickListener {
                toggleShowKeyBoard(context, view.search_src_text, false)
                activity!!.onBackPressed()
            }

            close.setOnClickListener {
                searchSrcText.setText("")
            }
        }

        viewModel.searchLiveData.observe(this) {
            songAdapter.updateDataSet(it.songList)
            albumAdapter.updateDataSet(it.albumList)
            artistAdapter.updateDataSet(it.artistList)
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.status = false
        }
    }

    override fun addToList(playListId: Long, song: Song) {
        viewModel.addToPlaylist(playListId, arrayOf(song.id).toLongArray())
    }

    override fun onItemClick(view: View, position: Int, item: MediaItem) {
        super.onItemClick(view, position, item)
        toggleShowKeyBoard(context, this@SearchFragment.view!!.search_src_text, false)
        when (item) {
            is Song -> Toast.makeText(context, "Song: ${item.title}", Toast.LENGTH_SHORT).show()
            is Album -> albumClicked(item)
            is Artist -> artistClicked(item)
        }
    }

    override fun onPopupMenuClick(view: View, position: Int, item: MediaItem) {
        super.onPopupMenuClick(view, position, item)
        when (item) {
            is Song -> songPopup(item, view)
            is Album -> Toast.makeText(
                context,
                "Album Menu of ${item.title}",
                Toast.LENGTH_SHORT
            ).show()
            is Artist -> Toast.makeText(
                context,
                "Artist Menu of ${item.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun songPopup(song: Song, view: View) {
        powerMenu!!.showAsAnchorRightTop(view)
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, song)
        }
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

    private fun artistClicked(item: Artist) {
        val extras = Bundle()
        extras.putString(PlayerConstants.ARTIST_KEY, item.toString())
        activity!!.addFragment(
            R.id.nav_host_fragment,
            ArtistDetailFragment(),
            PlayerConstants.ARTIST_DETAIL,
            true,
            extras
        )
    }
}
