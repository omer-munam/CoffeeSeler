package com.coffee.seller.ksa;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balram.library.FotButton;
import com.balram.library.FotEditText;
import com.balram.library.FotTextView;
import com.tami.seller.ksa.R;
import com.utils.SharePrefsEntry;

import org.json.JSONObject;

import java.util.regex.Pattern;

import static com.utils.ConnectionClass.WEB_AJAX;
import static com.utils.ConnectionClass.WEB_RESET;
import static com.utils.Constants.ANDROID_DEVICE;
import static com.utils.Constants.USER_PATTERN;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    long last, diff;
    int allTry;


    FotEditText userid, email, password, confirmpwd, mobile, usernameEtx, usernameAr;
    LinearLayout address;
    FotButton commit;
    FotTextView signup, signin, reset_pwd;
    TextView tab1, tab2;
    boolean singinEnabled;


    LinearLayout name_lr, userid_lr, email_lr, password_lr, cpassword_lr, phone_lr;
    TextView email_error;

    String emailTxt, passwordTxt, cpasswordTxt, usernameTxt, mobileTxt, nameEtx, namearTxt;
    String functionName;
    boolean workingThread = false;
    Dialog resetDialog;
    FotEditText emailVal;
    String locationData = "";
    TextView account_function;

    LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        userid = (FotEditText) findViewById(R.id.userid);
        password = (FotEditText) findViewById(R.id.password);
        confirmpwd = (FotEditText) findViewById(R.id.confirmpwd);
        mobile = (FotEditText) findViewById(R.id.mobile);
        email = (FotEditText) findViewById(R.id.email);
        usernameEtx = (FotEditText) findViewById(R.id.username);
        usernameAr = (FotEditText) findViewById(R.id.username_ar);
        commit = (FotButton) findViewById(R.id.commit);


        account_function = (FotTextView) findViewById(R.id.account_function);
        address = (LinearLayout) findViewById(R.id.map_address);
        name_lr = (LinearLayout) findViewById(R.id.fname_lr);
        userid_lr = (LinearLayout) findViewById(R.id.userid_lr);
        email_lr = (LinearLayout) findViewById(R.id.email_lr);
        password_lr = (LinearLayout) findViewById(R.id.password_lr);
        cpassword_lr = (LinearLayout) findViewById(R.id.confirmpwd_lr);
        phone_lr = (LinearLayout) findViewById(R.id.mobile_lr);

        email_error = (FotTextView) findViewById(R.id.email_error);
        signup = (FotTextView) findViewById(R.id.signup);
        signin = (FotTextView) findViewById(R.id.signin);
        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
        reset_pwd = (FotTextView) findViewById(R.id.reset_pwd);


        singinEnabled = false;

         commit.setOnClickListener(this);
        reset_pwd.setOnClickListener(this);
        account_function.setOnClickListener(this);
        functionName = "register";

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } catch (Exception e) {
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (workingThread)
            return;

        resetErrorWarning(3);

        if (id == R.id.account_function) {
            if (tab1.getVisibility() == View.VISIBLE) {
                signup.performClick();
                account_function.setText(getResources().getString(R.string.already_have_account));
            } else {
                signin.performClick();
                account_function.setText(getResources().getString(R.string.create_new_acccount));
            }
        } else if (id == R.id.signup) {
            functionName = "register";
            singinEnabled = false;
            signin.setTextColor(Color.parseColor("#ffffff"));
            signup.setTextColor(Color.parseColor("#cc0000"));
//            goto_signup.setTextColor(getResources().getColor(R.color.green));
//            goto_signup.setText(getResources().getString(R.string.already_have_account));
            tab1.setVisibility(View.INVISIBLE);
            tab2.setVisibility(View.VISIBLE);
            email.setHint(getString(R.string.email));
            password.setHint(getString(R.string.password));
            password.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            commit.setText(getResources().getString(R.string.signup));

            phone_lr.setVisibility(View.VISIBLE);
            cpassword_lr.setVisibility(View.VISIBLE);
            name_lr.setVisibility(View.VISIBLE);
            userid_lr.setVisibility(View.VISIBLE);
            reset_pwd.setVisibility(View.GONE);
            usernameEtx.requestFocus();

            emailBox();
        } else if (id == R.id.signin) {
        } else if (id == R.id.commit) {
            emailTxt = email.getText().toString();
            namearTxt = usernameAr.getText().toString();
            passwordTxt = password.getText().toString();
            cpasswordTxt = confirmpwd.getText().toString();
            mobileTxt = mobile.getText().toString();
            nameEtx = usernameEtx.getText().toString();


            if (nameEtx.equals("")) {
                usernameEtx.requestFocus();
                Toast.makeText(getBaseActivity(), getString(R.string.invalid_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (namearTxt.equals("")) {
                usernameEtx.requestFocus();
                Toast.makeText(getBaseActivity(), getString(R.string.invalid_name_ar), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                email.requestFocus();
                Toast.makeText(getBaseActivity(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!PhoneNumberUtils.isGlobalPhoneNumber(mobileTxt) || (mobileTxt.length() < 10)) {
                mobile.requestFocus();
                Toast.makeText(getBaseActivity(), getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordTxt.length() < 6) {
                password.requestFocus();
                Toast.makeText(getBaseActivity(), getString(R.string.invalid_pwd), Toast.LENGTH_SHORT).show();
                return;
            }


            if (!singinEnabled) {
                if (!cpasswordTxt.equals(passwordTxt)) {
                    confirmpwd.requestFocus();
                    Toast.makeText(getBaseActivity(), getString(R.string.invalid_cpwd), Toast.LENGTH_SHORT).show();
                    return;
                }


            }


            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    nameEtx = Uri.encode(nameEtx, "UTF-8");
                    namearTxt = Uri.encode(namearTxt, "UTF-8");
                    passwordTxt = Uri.encode(passwordTxt, "UTF-8");
                    String res = null;

                    try {
                        JSONObject jobj = new JSONObject();
                        jobj.put("email", emailTxt);
                        jobj.put("name", nameEtx);
                        jobj.put("mobile", mobileTxt);
                        jobj.put("password", passwordTxt);
                        jobj.put("device_type", ANDROID_DEVICE);
                        jobj.put("name_ar", namearTxt);
                        jobj.put("device_type", ANDROID_DEVICE);
                        jobj.put("push_noti", getSharePrefs().getPushID() + "");
                        jobj.put("deviceid", getSharePrefs().getDeviceId() + "");
                        jobj.put("caller", functionName);

                        String encrypted = cc.getEncryptedString(jobj.toString());
                        res = cc.sendPostData(encrypted, null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return res;
                }


                @Override
                protected void onPostExecute(String paramString) {
                    super.onPostExecute(paramString);
                    try {
                        if (paramString != null) {

                            try {
                                JSONObject jsonObject = new JSONObject(paramString);
                                if (!jsonObject.getString("message").equals(""))
                                    Toast.makeText(SignUpActivity.this.getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (jsonObject.getString("success").equals("1") && jsonObject.getString("status").equals("1")) {
                                    if (!singinEnabled)
                                        onFinishSignup(paramString);
                                    else
                                        onFinishSignin(paramString);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception localException) {
                    } finally {
                        workingThread = false;
                        getFunction().dismissDialog();
                    }
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    getFunction().showDialog();
                    workingThread = true;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (id == R.id.reset_pwd) {

        } else if (id == R.id.cancel) {
            resetDialog.dismiss();
        } else if (id == R.id.done) {
            emailTxt        = emailVal.getText().toString();
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
            {
                emailVal.requestFocus();
                Toast.makeText(getBaseActivity(), getString(R.string.invalid_email)+" -- "+singinEnabled, Toast.LENGTH_SHORT).show();
                return;
            }


            new AsyncTask<Void,Void,String>()
            {
                @Override
                protected String doInBackground(Void... voids) {
                    String res = null;
                    try
                    {
                        JSONObject jobj          = new JSONObject();
                        jobj.put("email",emailTxt);
                        jobj.put("caller",WEB_RESET);
                        String encrypted        = getConnection().getEncryptedString(jobj.toString());
                        res                     = getConnection().sendPostData(encrypted,null);
                    }catch (Exception e){}
                    return res;
                }


                @Override
                protected void onPostExecute(String paramString) {
                    super.onPostExecute(paramString);
                    try
                    {

                        if (paramString != null)
                        {
                            JSONObject jobj = new JSONObject(paramString);
                            Toast.makeText(getBaseActivity(), jobj.getString("message"), Toast.LENGTH_SHORT).show();
                            if(jobj.getString("success").equals("0"))
                                resetDialog.show();
                        }

                        else
                            Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception localException){}
                    finally
                    {
                        getFunction().dismissDialog();
                    }
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    getFunction().showDialog();
                    resetDialog.dismiss();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    void onFinishSignin(String result) {

    }


    private void onFinishSignup(String paramString) {
        String str = paramString.trim();

        try {
            JSONObject jobj = new JSONObject(str);
            String status = jobj.getString("status");
            if (status.equals("1")) {
                String success = jobj.getString("success");
                String msg = jobj.getString("message");
                String data = jobj.getString("data");
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                if (success.equals("1")) {

                    boolean b = getSharePrefs().onFinish(new JSONObject(data));
                    if (b) {
                        Intent localIntent = new Intent(getBaseActivity(), SignInActivity.class);
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callActivity(getBaseActivity(), localIntent);
                        getBaseActivity().finish();
                        try{Splash.splash.finish();}catch(Exception e){}
                    }
                }

            } else
                Toast.makeText(getApplicationContext(), getString(R.string.error_registration), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }

    }


    class AsyncHttpFunctionAjax extends AsyncTask<String, Void, String> {
        protected String doInBackground(String[] paramArrayOfString) {
            return getConnection().sendPostData(paramArrayOfString[0], null);
        }

        protected void onPostExecute(String paramString) {
            super.onPostExecute(paramString);
            if (paramString != null) {
                try {
                    JSONObject jobj = new JSONObject(paramString);
                    String status = jobj.getString("status");
                    if (status.equals("1")) {
                        String success = jobj.getString("success");
                        if (success.equals("0")) {
                            int err = jobj.getInt("err");
                            if (err == 1) {
                                email.setTextColor(Color.parseColor("#ff5555"));
                                email_error.setTextColor(Color.parseColor("#ff5555"));
                                email_error.setVisibility(View.VISIBLE);
                                email_error.setText(jobj.getString("message"));
                            }
                        } else if (success.equals("1")) {
                            int type = jobj.getInt("type");
                            if (type == 1) {
                                email.setTextColor(Color.parseColor("#00cc00"));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                Toast.makeText(getBaseActivity().getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }
    }

    void emailBox() {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean) {
                if ((!email.hasFocus()) && android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {

                    try {
                        JSONObject jobj = new JSONObject();
                        jobj.put("email", email.getText().toString());
                        jobj.put("caller", WEB_AJAX);

                        String encrypted = cc.getEncryptedString(jobj.toString());
                        AsyncHttpFunctionAjax localAsyncHttpFunctionAjax = new AsyncHttpFunctionAjax();
                        localAsyncHttpFunctionAjax.execute(encrypted);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable paramAnonymousEditable) {
            }

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            }

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
                resetErrorWarning(1);
                email.setTextColor(getResources().getColor(R.color.profile_txt));
            }
        });


    }

    void resetErrorWarning(int forType) {
        if (forType == 1) {
            email.setTextColor(getResources().getColor(R.color.profile_txt));
            email_error.setVisibility(View.GONE);
            email_error.setText("");
        } else {
            email.setTextColor(getResources().getColor(R.color.profile_txt));
            email_error.setVisibility(View.GONE);
            email_error.setText("");
        }

    }

}
