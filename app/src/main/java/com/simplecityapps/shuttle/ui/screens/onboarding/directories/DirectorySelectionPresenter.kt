package com.simplecityapps.shuttle.ui.screens.onboarding.directories

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.simplecityappds.saf.SafDirectoryHelper
import com.simplecityapps.shuttle.ui.common.mvp.BaseContract
import com.simplecityapps.shuttle.ui.common.mvp.BasePresenter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

interface MusicDirectoriesContract {

    data class Directory(val tree: SafDirectoryHelper.DocumentNodeTree, val traversalComplete: Boolean, val hasWritePermission: Boolean) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Directory

            if (tree != other.tree) return false
            if (hasWritePermission != other.hasWritePermission) return false

            return true
        }

        override fun hashCode(): Int {
            var result = tree.hashCode()
            result = 31 * result + hasWritePermission.hashCode()
            return result
        }
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun loadData(contentResolver: ContentResolver)
        fun removeItem(directory: Directory)
        fun handleSafResult(contentResolver: ContentResolver, intent: Intent)
        fun presentDocumentProvider()
    }

    interface View {
        fun setData(data: List<Directory>)
        fun startActivity(intent: Intent, requestCode: Int)
        fun showDocumentProviderNotAvailable()
    }
}

class MusicDirectoriesPresenter @Inject constructor(
    private val context: Context
) : MusicDirectoriesContract.Presenter, BasePresenter<MusicDirectoriesContract.View>() {

    private var data: MutableList<MusicDirectoriesContract.Directory> = mutableListOf()

    override fun loadData(contentResolver: ContentResolver) {
        contentResolver.persistedUriPermissions
            .filter { uriPermission -> uriPermission.isWritePermission || uriPermission.isReadPermission }
            .forEach { uriPermission ->
                parseUri(contentResolver, uriPermission.uri, uriPermission.isWritePermission)
            }
        view?.setData(emptyList())
    }

    override fun removeItem(directory: MusicDirectoriesContract.Directory) {
        try {
            context.contentResolver?.releasePersistableUriPermission(directory.tree.rootUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } catch (e: SecurityException) {
            Timber.e("Failed to release persistable uri permission: ${directory.tree.rootUri}")
        }
        data.remove(directory)
        view?.setData(data)
    }

    override fun handleSafResult(contentResolver: ContentResolver, intent: Intent) {
        intent.data?.let { uri ->
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            parseUri(contentResolver, uri, true)
        }
    }

    override fun presentDocumentProvider() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        if (intent.resolveActivity(context.packageManager) != null) {
            view?.startActivity(intent, DirectorySelectionFragment.REQUEST_CODE_OPEN_DOCUMENT)
        } else {
            view?.showDocumentProviderNotAvailable()
        }
    }

    private fun parseUri(contentResolver: ContentResolver, uri: Uri, hasWritePermission: Boolean) {
        launch {
            SafDirectoryHelper.buildFolderNodeTree(contentResolver, uri)
                .collect { tree ->
                    val directory = MusicDirectoriesContract.Directory(tree, false, hasWritePermission)
                    val index = data.indexOf(directory)
                    if (index != -1) {
                        data[index] = directory
                    } else {
                        data.add(directory)
                    }
                    view?.setData(data)
                }
            view?.setData(data.map { MusicDirectoriesContract.Directory(it.tree, true, hasWritePermission) })
        }
    }
}