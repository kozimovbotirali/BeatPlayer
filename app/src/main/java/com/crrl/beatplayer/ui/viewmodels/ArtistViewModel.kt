package com.crrl.beatplayer.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.repository.ArtistRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ArtistViewModel : ViewModel() {

    private val artists: MutableLiveData<List<Artist>>? = MutableLiveData()


    @SuppressLint("CheckResult")
    fun getArtists(context: Context): LiveData<List<Artist>>? {
        Observable.fromCallable { ArtistRepository.getInstance(context)!!.currentArtistList }
            .observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .subscribe { artists!!.postValue(it) }
        return artists
    }
}
