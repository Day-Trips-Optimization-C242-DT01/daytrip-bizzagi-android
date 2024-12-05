package com.bizzagi.daytrip.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.Context
import com.bizzagi.daytrip.databinding.ValidationPopupBinding

@SuppressLint("PrivateResource")
fun Context.showValidationDialog(title: String, message: String) {
    val dialogBinding = ValidationPopupBinding.inflate(LayoutInflater.from(this))

    dialogBinding.dialogTitle.text = title
    dialogBinding.dialogMessage.text = message

    val builder = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_Material3_MaterialAlertDialog)
        .setView(dialogBinding.root)
        .setCancelable(false)

    val dialog = builder.create().apply {
        window?.setBackgroundDrawableResource(R.drawable.mtrl_dialog_background)
        show()

        dialogBinding.dialogOkButton.setOnClickListener {
            dismiss()
        }
    }
}
