package com.dtakac.aux_remote.main.queue.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.main.view_model.SongsPagerViewModel
import kotlinx.android.synthetic.main.fragment_dialog_nickname.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class NicknameDialogFragment: DialogFragment() {
    private val viewModel by sharedViewModel<SongsPagerViewModel>(from = { requireParentFragment() })
    private val args by navArgs<NicknameDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dialog_nickname, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews(){
        etNickname.setText(args.currentNickname ?: args.ownerId)
        btnReset.setOnClickListener {
            viewModel.onNicknameSubmitted(args.ownerId, null)
            dismiss()
        }
        btnCancel.setOnClickListener { dismiss() }
        btnApply.setOnClickListener {
            viewModel.onNicknameSubmitted(args.ownerId, etNickname.text.toString())
            dismiss()
        }
    }
}