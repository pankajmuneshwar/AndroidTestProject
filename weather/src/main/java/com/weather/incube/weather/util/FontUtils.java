package com.weather.incube.weather.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.widget.TextView;


public class FontUtils {

	public static String WEATHER_REGULAR = "fonts/weather.otf";

	public static void setFont(Context context, TextView textView, String fontStyle, float size) {
		Typeface tf = Typeface.createFromAsset(((ContextWrapper) context)
				.getBaseContext().getAssets(), fontStyle);

		if (size != 0)
			textView.setTextSize(size);

		textView.setTypeface(tf);
	}
}
