package com.anankastudio.videocollector.viewholders

import android.text.Html
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ItemDetailProfileBinding
import com.anankastudio.videocollector.interfaces.OnClickCreator
import com.anankastudio.videocollector.models.item.ContentDetailProfile
import com.anankastudio.videocollector.utilities.HolderDetail

class ItemDetailProfile(
    override val binding: ItemDetailProfileBinding,
    private val onClickCreator: OnClickCreator
) : HolderDetail<ContentDetailProfile>(binding) {

    override fun onBind() {
        binding.name.text = data.userName
        binding.viewCreator.text = Html.fromHtml(
            itemView.context.getString(R.string.view_creator_underline),
            Html.FROM_HTML_MODE_LEGACY
        )

        binding.viewCreator.setOnClickListener {
            onClickCreator.onClickViewCreator(data)
        }

        binding.shareProfile.setOnClickListener {
            onClickCreator.onClickShareCreator(data)
        }
    }
}