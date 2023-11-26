package com.simplecityapps.shuttle.ui.screens.trial

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.simplecityapps.shuttle.R
import com.simplecityapps.shuttle.persistence.GeneralPreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class ThankYouDialogFragment : DialogFragment() {

    @Inject
    lateinit var preferenceManager: GeneralPreferenceManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        preferenceManager.hasSeenThankYouDialog = true

        val view = layoutInflater.inflate(R.layout.dialog_thanks, null)

        val closeButton: Button = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        lifecycleScope.launchWhenResumed {
            delay(8000)
            if (isResumed) {
                dismiss()
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .show()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    companion object {
        private const val TAG = "ThankYouDialogFragment"

        fun newInstance(): ThankYouDialogFragment = ThankYouDialogFragment()
    }
}
