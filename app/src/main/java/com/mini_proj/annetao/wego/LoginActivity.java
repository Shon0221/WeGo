package com.mini_proj.annetao.wego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mini_proj.annetao.wego.util.Utils;
import com.mini_proj.annetao.wego.util.login.QQLoginListener;
import com.mini_proj.annetao.wego.util.login.QQLoginSupporter;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity
        implements View.OnClickListener {

    private EditText name;
    private QQLoginSupporter qs;
    private BaseUiListener loginListener;
    public static String QQ_LOGIN_APP_ID = "1105456541";
    public static String QQ_LOGIN_RESULT_COMPLETE = "util.login.qqloginlistener.qqloginresult.complete";
    public static String QQ_LOGIN_RESULT_CANCEL = "util.login.qqloginlistener.qqloginresult.cancel";
    public static String QQ_LOGIN_RESULT_ERROR = "util.login.qqloginlistener.qqloginresult.error";
    Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        qs = new QQLoginSupporter(this);
        findView(R.id.qq).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.name_layout:
                name.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showKeyboard(name, LoginActivity.this);
                    }
                });
                break;
            case R.id.clear_name:
                name.setText("");
                onClick(findView(R.id.name_layout));
                break;
            case R.id.qq:
                qqLogin();
                break;
        }
    }

    private void qqLogin() {

        mTencent = Tencent.createInstance(QQ_LOGIN_APP_ID, getApplicationContext());
        loginListener = new BaseUiListener();
        mTencent.login(this,"all",loginListener);
    }

    @Override
    public void onBackPressed() {

    }

    public void onQQLoginResult(String result, Object response) {
        if(result.equals(QQLoginSupporter.QQ_LOGIN_RESULT_COMPLETE)) {
            //TODO 存储登录数据
            JSONObject jsonResponse = (JSONObject) response;
            User.getInstance().setLogin(true);
            try {
                User.getInstance().setOpenId(jsonResponse.getString(Constants.PARAM_OPEN_ID));
                User.getInstance().setToken(jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT);
        }

    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            onQQLoginResult(QQ_LOGIN_RESULT_COMPLETE,response);

            doComplete(response);

        }

        protected void doComplete(Object values) {

        }

        @Override

        public void onError(UiError e) {

            onQQLoginResult(QQ_LOGIN_RESULT_ERROR,e);

        }

        @Override

        public void onCancel() {

            onQQLoginResult(QQ_LOGIN_RESULT_CANCEL,null);

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 官方文档没没没没没没没没没没没这句代码, 但是很很很很很很重要, 不然不会回调!
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);

        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, loginListener);
            }
        }
    }
}
