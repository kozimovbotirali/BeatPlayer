package com.crrl.beatplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme
import com.crrl.beatplayer.alertdialog.stylers.AlertType
import com.crrl.beatplayer.alertdialog.stylers.InputStyle
import com.crrl.beatplayer.databinding.PlaylistFragmentBinding
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.repository.PlaylistRepository
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.modelview.PlaylistAdapter
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlaylistFragment : BaseFragment<Playlist>() {

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var alert: AlertDialog
    private lateinit var binding: PlaylistFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.playlist_fragment, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        playlistAdapter = PlaylistAdapter().apply {
            itemClickListener = this@PlaylistFragment
        }

        binding.playList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0 || dy < 0 && binding.createPlayList.isShown)
                        binding.createPlayList.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        binding.createPlayList.show()
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }

        binding.createPlayList.setOnClickListener { createPlayList() }

        reloadAdapter()

        alert = createDialog()

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
    }

    override fun onItemClick(view: View, position: Int, item: Playlist) {
        val extras = Bundle()
        extras.putString(PlayerConstants.PLAY_LIST_DETAIL, item.toString())
        activity!!.addFragment(
            R.id.nav_host_fragment,
            PlaylistDetailFragment(),
            PlayerConstants.PLAY_LIST_DETAIL,
            extras = extras
        )
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Playlist) {
        PlaylistRepository.getInstance(context)!!.deletePlaylist(item.id)
        Toast.makeText(context, "${item.name} Deleted ", Toast.LENGTH_SHORT).show()
    }

    private fun createPlayList() {
        alert.show(activity as AppCompatActivity)
    }

    private fun createDialog(): AlertDialog {
        val style = InputStyle(
            activity?.getColorByTheme(R.attr.colorPrimarySecondary, "colorPrimarySecondary")!!,
            activity!!.getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2"),
            activity!!.getColorByTheme(R.attr.titleTextColor, "titleTextColor"),
            activity!!.getColorByTheme(R.attr.colorPrimaryOpacity, "colorPrimaryOpacity"),
            activity!!.getColorByTheme(R.attr.colorAccent, "colorAccent")
        )
        return AlertDialog(
            getString(R.string.new_playlist),
            getString(R.string.create_playlist),
            style,
            AlertType.INPUT,
            getString(R.string.input_hint)
        ).apply {
            addItem(AlertItemAction("Cancel", false, AlertItemTheme.CANCEL) {
            })
            addItem(AlertItemAction("OK", false, AlertItemTheme.ACCEPT) {
                PlaylistRepository.getInstance(context)!!.createPlaylist(it.input)
            })
        }
    }

    private fun reloadAdapter() {
        viewModel.playLists().observe(this) {
            playlistAdapter.updateDataSet(it)
        }
    }
}
