package com.space.mycoffee.custom;

import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.space.mycoffee.R;
import com.space.mycoffee.model.Address;
import com.space.mycoffee.utils.Extensions;

public class BindAdapter {
    @BindingAdapter("app:imageUrl")
    public static void setImageUrl(@NonNull ImageView imageView, String url) {
        if (url == null || url.isEmpty())
            imageView.setImageResource(R.drawable.logo);
        else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(imageView);
        }
    }

    @BindingAdapter("app:userAvatar")
    public static void setImageAvatarUrl(@NonNull ImageView imageView, String urlAvatar) {
        if (urlAvatar == null || urlAvatar.isEmpty())
            imageView.setImageResource(R.drawable.avatar_user_default);
        else {
            Glide.with(imageView.getContext())
                    .load(urlAvatar)
                    .placeholder(R.drawable.avatar_user_default)
                    .error(R.drawable.avatar_user_default)
                    .into(imageView);
        }
    }

    @BindingAdapter("app:userBackground")
    public static void setImageUrl(@NonNull ShapeableImageView imageView, String urlBackground) {
        if (urlBackground == null || urlBackground.isEmpty())
            imageView.setImageResource(R.color.background_discount);
        else {
            Glide.with(imageView.getContext())
                    .load(urlBackground)
                    .placeholder(R.color.background_discount)
                    .error(R.color.background_discount)
                    .into(imageView);
        }
    }

    @BindingAdapter(
            value = {"app:defaultHtml", "app:textNum", "app:textAddress"},
            requireAll = false
    )
    public static void setText(@NonNull TextView textView, String des, Integer numToString, Address address) {
        if (des != null) {
            Spanned htmlSpanned;
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N)
                htmlSpanned = Html.fromHtml(des);
            else htmlSpanned = HtmlCompat.fromHtml(des, HtmlCompat.FROM_HTML_MODE_LEGACY);
            textView.setText(htmlSpanned);
        }

        if (numToString != null) {
            textView.setText(String.format(textView.getContext().getString(R.string.book_price), Extensions.convertNumberToStringComma(numToString)));
        }

        if (address != null) {
            textView.setText(Extensions.getStringFromAddress(address));
        }
    }
}