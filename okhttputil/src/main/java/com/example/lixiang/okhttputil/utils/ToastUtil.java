package com.example.lixiang.okhttputil.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	private static Toast toast;

	/**
	 * 显示吐司
	 * @param context
	 * @param letter
	 */
	public static void showToast(Context context, String letter) {
		if(toast == null){
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		toast.setText(letter);
		toast.show();
	}

}
