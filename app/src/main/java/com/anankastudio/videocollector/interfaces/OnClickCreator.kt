package com.anankastudio.videocollector.interfaces

import com.anankastudio.videocollector.models.item.ContentDetailProfile

interface OnClickCreator {

    fun onClickShareCreator(creator: ContentDetailProfile?)
    fun onClickViewCreator(creator: ContentDetailProfile?)
}