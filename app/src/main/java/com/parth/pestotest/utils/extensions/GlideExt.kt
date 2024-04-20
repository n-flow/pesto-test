package com.parth.pestotest.utils.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.parth.pestotest.R
import com.parth.pestotest.network.model.UserModel
import com.parth.pestotest.network.model.unassigned
import com.parth.pestotest.utils.views.imageViews.textdrawable.TextDrawable
import com.parth.pestotest.utils.logger

fun makeTextDrawable(context: Context, user: UserModel?, dr: (Any) -> Unit = {}) {
    val userModel = user ?: unassigned

    if (!userModel.personPhoto.isNullOrEmpty()) {
        dr.invoke(userModel.personPhoto)
    } else {
        val fName =
            if (userModel.personGivenName.isEmpty()) "F" else userModel.personGivenName.first()
        val lName =
            if (userModel.personFamilyName.isEmpty()) "L" else userModel.personFamilyName.first()
        val name = "${fName}${lName}"

        logger("makeTextDrawable:  ", "name:  $name")

        val drawable = TextDrawable.builder().beginConfig()
            .textColor(ContextCompat.getColor(context, R.color.profile_name_initial))
            .bold().toUpperCase().endConfig()
            .buildRound(name, ContextCompat.getColor(context, R.color.profile_pic_background))
        dr.invoke(drawable)
    }
}

@Synchronized
fun getColor(context: Context, resourceId: Int): Int {
    return ContextCompat.getColor(context, resourceId)
}

fun ImageView.loadImage(drawable: Any?) {
    val glide = when (drawable) {
        is String -> Glide.with(context).load(drawable)
        is Drawable -> Glide.with(context).load(drawable)
        else -> Glide.with(context).load(drawable)
    }

    glide.transition(DrawableTransitionOptions.withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(RequestOptions.circleCropTransform())
        .circleCrop()
        .placeholder(context.getDrawableRes(R.drawable.default_profile_new))
        .error(context.getDrawableRes(R.drawable.default_profile_new))
        .into(this)
}