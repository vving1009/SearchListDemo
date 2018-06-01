package com.example.administrator.searchlistdemo;

/**
 * BaseViewContract
 *
 * @author 贾博瑄
 */

public interface BaseViewContract {

    void showToast(String string);

    void showToastById(int resId);

    void showProgressDialog();

    void dismissProgressDialog();

}
