package com.prpr894.cplayer.base;

public interface BaseInterface {

	/**
	 * 关闭activity
	 */
	public void finishUI();

	/**
	 * 显示加载进度
	 */
	public void showProgress(String msg);

	public void showProgress(String msg, boolean flag);

	public void hideProgress();

	//	public void viewClick(int id);

}
