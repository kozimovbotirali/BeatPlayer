package com.crrl.beatplayer.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.repository.AlbumRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class AlbumViewModel(private val context: Context) : ViewModel() {

    private val albums: MutableLiveData<List<Album>>? = MutableLiveData()

    private fun getAlbumData(context: Context) =
        Callable { AlbumRepository.getInstance(context)!!.currentAlbumList }

    @SuppressLint("CheckResult")
    fun getAlbums(): LiveData<List<Album>>? {
        Observable.fromCallable(getAlbumData(context)).observeOn(Schedulers.newThread())
            .subscribeOn(Schedulers.newThread()).subscribe { albums!!.postValue(it) }
        return albums
    }
}
