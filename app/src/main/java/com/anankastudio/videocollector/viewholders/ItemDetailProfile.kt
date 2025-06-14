package com.anankastudio.videocollector.viewholders

import android.text.Html
import com.anankastudio.videocollector.R
import com.anankastudio.videocollector.databinding.ItemDetailProfileBinding
import com.anankastudio.videocollector.interfaces.OnClickCreator
import com.anankastudio.videocollector.models.item.ContentDetailProfile
import com.anankastudio.videocollector.utilities.HolderDetail
import com.anankastudio.videocollector.utilities.addUnderline

class ItemDetailProfile(
    override val binding: ItemDetailProfileBinding,
    private val onClickCreator: OnClickCreator
) : HolderDetail<ContentDetailProfile>(binding) {

    override fun onBind() {
        binding.creator.text = data.userName
        binding.creator.addUnderline()

        binding.pexels.text = Html.fromHtml(
            itemView.context.getString(R.string.pexels),
            Html.FROM_HTML_MODE_LEGACY
        )

        binding.creator.setOnClickListener {
            onClickCreator.onClickViewCreator(data)
        }

        binding.pexels.setOnClickListener {
            onClickCreator.onClickPexels()
        }

        binding.shareProfile.setOnClickListener {
            onClickCreator.onClickShareCreator(data)
        }
    }
}