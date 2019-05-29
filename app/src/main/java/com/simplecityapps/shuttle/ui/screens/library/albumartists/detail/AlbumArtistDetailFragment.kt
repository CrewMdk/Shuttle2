package com.simplecityapps.shuttle.ui.screens.library.albumartists.detail

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import au.com.simplecityapps.shuttle.imageloading.ArtworkImageLoader
import au.com.simplecityapps.shuttle.imageloading.glide.GlideImageLoader
import com.simplecityapps.adapter.RecyclerAdapter
import com.simplecityapps.mediaprovider.model.Album
import com.simplecityapps.mediaprovider.model.AlbumArtist
import com.simplecityapps.mediaprovider.model.Song
import com.simplecityapps.shuttle.R
import com.simplecityapps.shuttle.dagger.Injectable
import com.simplecityapps.shuttle.ui.common.error.userDescription
import com.simplecityapps.shuttle.ui.common.recyclerview.clearAdapterOnDetach
import com.simplecityapps.shuttle.ui.common.view.DetailImageAnimationHelper
import com.simplecityapps.shuttle.ui.screens.library.albums.detail.AlbumDetailFragmentArgs
import com.simplecityapps.shuttle.ui.screens.playlistmenu.CreatePlaylistDialogFragment
import com.simplecityapps.shuttle.ui.screens.playlistmenu.PlaylistData
import com.simplecityapps.shuttle.ui.screens.playlistmenu.PlaylistMenuPresenter
import com.simplecityapps.shuttle.ui.screens.playlistmenu.PlaylistMenuView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_album_artist_detail.*
import javax.inject.Inject

class AlbumArtistDetailFragment :
    Fragment(),
    Injectable,
    AlbumArtistDetailContract.View,
    ExpandableAlbumBinder.Listener,
    CreatePlaylistDialogFragment.Listener {

    @Inject lateinit var presenterFactory: AlbumArtistDetailPresenter.Factory

    @Inject lateinit var playlistMenuPresenter: PlaylistMenuPresenter

    private lateinit var imageLoader: ArtworkImageLoader

    private lateinit var presenter: AlbumArtistDetailPresenter

    private lateinit var adapter: RecyclerAdapter

    private lateinit var animationHelper: DetailImageAnimationHelper

    private val handler = Handler(Looper.getMainLooper())

    private var postponedTransitionCounter = 2

    private var isFirstLoad = true

    private lateinit var albumArtist: AlbumArtist

    private lateinit var playlistMenuView: PlaylistMenuView


    // Lifecycle

    override fun onAttach(context: Context) {
        super.onAttach(context)

        AndroidSupportInjection.inject(this)

        albumArtist = AlbumArtistDetailFragmentArgs.fromBundle(arguments!!).albumArtist
        presenter = presenterFactory.create(albumArtist)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)
        (sharedElementEnterTransition as Transition).duration = 200L
        (sharedElementEnterTransition as Transition).addListener(object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                animationHelper.showHeroView()
                transition.removeListener(this)
            }
        })

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element_transition)
        (sharedElementReturnTransition as Transition).duration = 200L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        postponedTransitionCounter = 2
        postponeEnterTransition()
        return inflater.inflate(R.layout.fragment_album_artist_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistMenuView = PlaylistMenuView(context!!, playlistMenuPresenter, childFragmentManager)

        adapter = RecyclerAdapter()

        imageLoader = GlideImageLoader(this)

        handler.postDelayed(1000) {
            startPostponedEnterTransition() // In case our Glide load takes too long
        }

        toolbar?.let { toolbar ->
            toolbar.setNavigationOnClickListener { NavHostFragment.findNavController(this).popBackStack() }
            MenuInflater(context).inflate(R.menu.menu_album_artist_detail, toolbar.menu)
            playlistMenuView.createPlaylistMenu(toolbar.menu)
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.shuffle -> {
                        presenter.shuffle()
                        true
                    }
                    else -> {
                        playlistMenuView.handleMenuItem(menuItem, PlaylistData.AlbumArtists(albumArtist))
                    }
                }
            }
        }

        toolbar.title = albumArtist.name
        toolbar.subtitle = "${albumArtist.albumCount} Albums • ${albumArtist.songCount} Songs"

        dummyImage.transitionName = "album_artist_${albumArtist.name}"

        imageLoader.loadArtwork(dummyImage, albumArtist, ArtworkImageLoader.Options.CircleCrop, ArtworkImageLoader.Options.Priority(ArtworkImageLoader.Options.Priority.Priority.Max)) {
            maybeStartPostponedEnterTransition()
        }

        imageLoader.loadArtwork(heroImage, albumArtist, ArtworkImageLoader.Options.Priority(ArtworkImageLoader.Options.Priority.Priority.Max), completionHandler = null)

        recyclerView.adapter = adapter

        recyclerView.doOnPreDraw { maybeStartPostponedEnterTransition() }

        animationHelper = DetailImageAnimationHelper(heroImage, dummyImage)

        if (!isFirstLoad) {
            heroImage.visibility = View.VISIBLE
        }

        presenter.bindView(this)
        playlistMenuPresenter.bindView(playlistMenuView)

        isFirstLoad = false

        presenter.loadData()
    }

    override fun onDestroyView() {
        adapter.dispose()

        presenter.unbindView()
        playlistMenuPresenter.unbindView()

        recyclerView.clearAdapterOnDetach()

        super.onDestroyView()
    }

    private fun maybeStartPostponedEnterTransition() {
        postponedTransitionCounter--
        if (postponedTransitionCounter == 0) {
            startPostponedEnterTransition()
        }
    }


    // AlbumArtistDetailContract.View Implementation

    override fun setListData(albums: Map<Album, List<Song>>) {
        adapter.setData(albums.map { entry ->
            ExpandableAlbumBinder(
                entry.key,
                entry.value,
                imageLoader,
                listener = this,
                expanded = adapter.items.filterIsInstance<ExpandableAlbumBinder>().find { binder -> binder.album == entry.key }?.expanded ?: false
            )
        })
    }

    override fun showLoadError(error: Error) {
        Toast.makeText(context, error.userDescription(), Toast.LENGTH_LONG).show()
    }


    // ExpandableAlbumArtistBinder.Listener Implementation

    override fun onArtworkClicked(album: Album, viewHolder: ExpandableAlbumBinder.ViewHolder) {
        view?.findNavController()?.navigate(
            R.id.action_albumArtistDetailFragment_to_albumDetailFragment,
            AlbumDetailFragmentArgs(album).toBundle(),
            null,
            FragmentNavigatorExtras(viewHolder.imageView to viewHolder.imageView.transitionName)
        )
    }

    override fun onItemClicked(position: Int, expanded: Boolean) {
        val items = adapter.items.toMutableList()
        items[position] = (items[position] as ExpandableAlbumBinder).clone(!expanded)
        adapter.setData(items)
    }

    override fun onSongClicked(song: Song, songs: List<Song>) {
        presenter.onSongClicked(song, songs)
    }

    override fun onOverflowClicked(view: View, song: Song) {
        playlistMenuView.createPlaylistPopupMenu(view, PlaylistData.Songs(song))
    }

    override fun onOverflowClicked(view: View, album: Album) {
        playlistMenuView.createPlaylistPopupMenu(view, PlaylistData.Albums(album))
    }


    // CreatePlaylistDialogFragment.Listener Implementation

    override fun onSave(text: String, playlistData: PlaylistData) {
        playlistMenuPresenter.createPlaylist(text, playlistData)
    }
}