package com.crrl.beatplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.ui.adapters.ArtistAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.ArtistViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import kotlinx.android.synthetic.main.fragment_artist.view.*

class ArtistFragment : BaseFragment<Artist>() {

    companion object {
        fun newInstance() = ArtistFragment()
    }

    private lateinit var viewModel: ArtistViewModel
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        val sc = if (GeneralUtils.getRotation(safeActivity) == GeneralUtils.VERTICAL) 2 else 5

        viewModel = ViewModelProviders.of(this).get(ArtistViewModel::class.java)

        artistAdapter = ArtistAdapter(context).apply {
            showHeader = true
            itemClickListener = this@ArtistFragment
            spanCount = sc
        }

        view.apply {
            artist_list.apply {
                layoutManager = GridLayoutManager(context!!, sc).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 0) sc else 1
                        }
                    }
                }
                adapter = artistAdapter
            }
        }

        reloadAdapter()
    }

    private fun reloadAdapter() {
        viewModel.getArtists(context!!)!!.observe(this) { list ->
            artistAdapter.updateDataSet(list)
        }
    }

    override fun onItemClick(view: View, position: Int, item: Artist) {
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
