package com.shane.powersaver.ui.dialog;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07 17:40:10
 *
 */

import android.app.ProgressDialog;

public interface DialogControl {

	public abstract void hideWaitDialog();

	public abstract ProgressDialog showWaitDialog();

	public abstract ProgressDialog showWaitDialog(int resid);

	public abstract ProgressDialog showWaitDialog(String text);
}
