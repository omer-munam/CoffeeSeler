package com.coffee.seller.ksa;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
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

import org.json.JSONObject;


import static com.utils.ConnectionClass.WEB_AJAX;
import static com.utils.ConnectionClass.WEB_RESET;
import static com.utils.Constants.ANDROID_DEVICE;



public class SignupSignin extends BaseActivity implements View.OnClickListener {

    long last , diff ;
    int allTry;


    FotEditText username_ar , email , password , confirmpwd , mobile , usernameEtx;
    FotButton commit;
    FotTextView signup,signin,reset_pwd;
    TextView tab1,tab2;
    boolean singinEnabled;


    LinearLayout name_lr , userid_lr , email_lr , password_lr , cpassword_lr , phone_lr;
    TextView email_error , mobile_error;

    String emailTxt , passwordTxt , cpasswordTxt , namearTxt , mobileTxt , nameEtx;
    String functionName;
    boolean workingThread = false;
    Dialog resetDialog;
    FotEditText emailVal;
    String locationData = "";
    TelephonyManager tMgr;
    String mPhoneNumber;
    TextView goto_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_social);
        username_ar     = (FotEditText)findViewById(R.id.username_ar);
        password        = (FotEditText)findViewById(R.id.password);
        confirmpwd      = (FotEditText)findViewById(R.id.confirmpwd);
        mobile          = (FotEditText)findViewById(R.id.mobile);
        email           = (FotEditText)findViewById(R.id.email);
        usernameEtx     = (FotEditText)findViewById(R.id.username);
        commit          = (FotButton)findViewById(R.id.commit);
        goto_signup     = (TextView) findViewById(R.id.goto_signup);



        name_lr         = (LinearLayout)findViewById(R.id.fname_lr);
        userid_lr       = (LinearLayout)findViewById(R.id.userid_lr);
        email_lr        = (LinearLayout)findViewById(R.id.email_lr);
        password_lr     = (LinearLayout)findViewById(R.id.password_lr);
        cpassword_lr    = (LinearLayout)findViewById(R.id.confirmpwd_lr);
        phone_lr        = (LinearLayout)findViewById(R.id.mobile_lr);

        mobile_error    = (FotTextView)findViewById(R.id.mobile_error);
        email_error     = (FotTextView)findViewById(R.id.email_error);
        signup          = (FotTextView)findViewById(R.id.signup);
        signin          = (FotTextView)findViewById(R.id.signin);
        tab1            = (TextView)findViewById(R.id.tab1);
        tab2            = (TextView)findViewById(R.id.tab2);
        reset_pwd       = (FotTextView)findViewById(R.id.reset_pwd);

        singinEnabled   = true;


        signup.setOnClickListener(this);
        signin.setOnClickListener(this);
        commit.setOnClickListener(this);
        reset_pwd.setOnClickListener(this);
        goto_signup.setOnClickListener(this);
        signin.performClick();


    }

    @Override
    protected void onResume() {
        super.onResume();
        try{mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);}catch (Exception e){}

    }




    @Override
    public void onClick(View view) {
        int id= view.getId();

        if(workingThread)
            return;

        resetErrorWarning(3);

        if(id == R.id.goto_signup)
        {
                if(tab1.getVisibility() == View.VISIBLE)
                    signup.performClick();
                else
                    signin.performClick();
        }
        else if(id == R.id.signup)
        {
            functionName    = "register";
            singinEnabled   = false;
            signin.setTextColor(Color.parseColor("#ffffff"));
            signup.setTextColor(Color.parseColor("#cc0000"));
            goto_signup.setTextColor(getResources().getColor(R.color.green));
            goto_signup.setText(getResources().getString(R.string.already_have_account));
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
        }
        else if(id == R.id.signin)
        {
            goto_signup.setTextColor(getResources().getColor(R.color.red));
            goto_signup.setText(getResources().getString(R.string.signup_for_new));
            email.setTextColor(getResources().getColor(R.color.profile_txt));
            functionName    = "login";
            email.setOnFocusChangeListener(null);
            singinEnabled   = true;
            signin.setTextColor(Color.parseColor("#cc0000"));
            signup.setTextColor(Color.parseColor("#ffffff"));
            email.setHint(getString(R.string.email_username));
            password.setHint(getString(R.string.password));
            tab2.setVisibility(View.INVISIBLE);
            tab1.setVisibility(View.VISIBLE);
            password.setImeOptions(EditorInfo.IME_ACTION_DONE);
            commit.setText(getResources().getString(R.string.signin));

            phone_lr.setVisibility(View.GONE);
            cpassword_lr.setVisibility(View.GONE);
            userid_lr.setVisibility(View.GONE);
            name_lr.setVisibility(View.GONE);

            reset_pwd.setVisibility(View.VISIBLE);
        }
        else if(id == R.id.commit)
        {
            emailTxt        = email.getText().toString();
            namearTxt       = username_ar.getText().toString();
            passwordTxt     = password.getText().toString();
            cpasswordTxt    = confirmpwd.getText().toString();
            mobileTxt       = mobile.getText().toString();
            nameEtx         = usernameEtx.getText().toString();

            if(!singinEnabled)
            {
                if(nameEtx.equals(""))
                {
                    usernameEtx.requestFocus();
                    Toast.makeText(getBaseActivity(), getString(R.string.invalid_name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(namearTxt.equals(""))
                {
                    usernameEtx.requestFocus();
                    Toast.makeText(getBaseActivity(), getString(R.string.invalid_name_ar), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
                {
                    email.requestFocus();
                    Toast.makeText(getBaseActivity(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!PhoneNumberUtils.isGlobalPhoneNumber(mobileTxt) || (mobileTxt.length() < 10))
                {
                    mobile.requestFocus();
                    Toast.makeText(getBaseActivity(), getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else
            {
                if(emailTxt.equals(""))
                {
                    email.requestFocus();
                    Toast.makeText(getBaseActivity(), getString(R.string.id_cannot_be_blank) , Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            if(passwordTxt.length() < 6)
            {
                password.requestFocus();
                Toast.makeText(getBaseActivity(), getString(R.string.invalid_pwd), Toast.LENGTH_SHORT).show();
                return;
            }


            if(!singinEnabled)
            {
                if (!cpasswordTxt.equals(passwordTxt))
                {
                    confirmpwd.requestFocus();
                    Toast.makeText(getBaseActivity(), getString(R.string.invalid_cpwd), Toast.LENGTH_SHORT).show();
                    return;
                }


            }

            new AsyncTask<Void,Void,String>()
            {
                @Override
                protected String doInBackground(Void... voids) {
                    nameEtx     = Uri.encode(nameEtx,"UTF-8");
                    namearTxt   = Uri.encode(namearTxt,"UTF-8");
                    passwordTxt = Uri.encode(passwordTxt,"UTF-8");
                    String res  = null;

                    try
                    {
                        JSONObject jobj         = new JSONObject();
                        jobj.put("email",emailTxt);
                        jobj.put("name",nameEtx);
                        jobj.put("mobile",mobileTxt);
                        jobj.put("password",passwordTxt);
                        jobj.put("device_type",ANDROID_DEVICE);
                        jobj.put("name_ar",namearTxt);
                        jobj.put("device_type",ANDROID_DEVICE);
                        jobj.put("push_noti",getSharePrefs().getPushID()+"");
                        jobj.put("deviceid",getSharePrefs().getDeviceId()+"");
                        jobj.put("caller",functionName);

                        String encrypted        = cc.getEncryptedString(jobj.toString());
                        res                     = cc.sendPostData(encrypted,null);

                    }catch (Exception e){
                        e.printStackTrace();
                    }



                    return res;
                }


                @Override
                protected void onPostExecute(String paramString) {
                    super.onPostExecute(paramString);
                    try
                    {
                        if (paramString != null)
                        {

                            try
                            {
                                JSONObject jsonObject           = new JSONObject(paramString);
                                if(!jsonObject.getString("message").equals(""))
                                    Toast.makeText(SignupSignin.this.getApplicationContext(),jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if(jsonObject.getString("success").equals("1") && jsonObject.getString("status").equals("1"))
                                {
                                    if(!singinEnabled)
                                        onFinishSignup(paramString);
                                    else
                                        onFinishSignin(paramString);
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();

                            }
                        }
                        else
                        {
                            Toast.makeText(getBaseActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                        }

                    }
                    catch (Exception localException){}
                    finally
                    {
                        workingThread   = false;
                        getFunction().dismissDialog();
                    }
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    getFunction().showDialog();
                    workingThread   = true;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else if(id == R.id.reset_pwd)
        {
            resetDialog             = new Dialog(this, android.R.style.Theme_Translucent);
            resetDialog.setContentView(R.layout.dialog_reset_pwd);

            FotButton  cancel       = (FotButton)resetDialog.findViewById(R.id.cancel);
            FotButton done          = (FotButton)resetDialog.findViewById(R.id.done);
            emailVal                = (FotEditText)resetDialog.findViewById(R.id.email_rst);

            cancel.setOnClickListener(this);
            done.setOnClickListener(this);

            resetDialog.show();

        }
        else if(id == R.id.cancel)
        {
            resetDialog.dismiss();
        }
        else if(id == R.id.done)
        {

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

    void onFinishSignin(String result)
    {
        try
        {
            JSONObject jobj	=new JSONObject(result);
            String status	= jobj.getString("status");
            if(status.equals("1"))
            {
                String success	= jobj.getString("success");
                if(success.equals("1"))
                {
                    String data		    = jobj.getString("data");
                    boolean b			= getSharePrefs().onFinish(new JSONObject(data));
                    if(b)
                    {
                        Intent localIntent = new Intent(getBaseActivity(), Locations.class);
                        localIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(localIntent);
                        getBaseActivity().finish();
                        try{Splash.splash.finish();}catch(Exception e){}

                    }
                }
                else
                {
                    String msg		    = jobj.getString("message");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

            }
            else
                Toast.makeText(getApplicationContext(), jobj.getString("message"), Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.error_network),Toast.LENGTH_SHORT).show();
        }
    }


    private void onFinishSignup(String paramString)
    {
        String str = paramString.trim();

        try
        {
            JSONObject jobj	=new JSONObject(str);
            String status	= jobj.getString("status");
            if(status.equals("1"))
            {
                String success	= jobj.getString("success");
                String msg		= jobj.getString("message");
                String data		= jobj.getString("data");
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                if(success.equals("1"))
                {
                    boolean b   = getSharePrefs().onFinish(new JSONObject(data));
                    if(b)
                    {
                        Intent localIntent = new Intent(getBaseActivity(), MainActivity.class);
                        localIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(localIntent);
                        getBaseActivity().finish();
                        try{Splash.splash.finish();}catch(Exception e){}
                    }
                }

            }
            else
                Toast.makeText(getApplicationContext(), getString(R.string.error_registration), Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }

    }








    class AsyncHttpFunctionAjax extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String[] paramArrayOfString)
        {
            return  getConnection().sendPostData(paramArrayOfString[0],null);
        }

        protected void onPostExecute(String paramString)
        {
            super.onPostExecute(paramString);
            if (paramString != null)
            {
                try
                {
                    JSONObject jobj	    =new JSONObject(paramString);
                    String status	    = jobj.getString("status");
                    if(status.equals("1"))
                    {
                        String success	= jobj.getString("success");
                        if(success.equals("0"))
                        {
                            int err                     = jobj.getInt("err");
                            if(err == 1)
                            {
                                email.setTextColor(Color.parseColor("#ff5555"));
                                email_error.setTextColor(Color.parseColor("#ff5555"));
                                email_error.setVisibility(View.VISIBLE);
                                email_error.setText(jobj.getString("message"));
                            }
                        }
                        else if(success.equals("1"))
                        {
                            int type                     = jobj.getInt("type");
                            if(type == 1)
                            {
                                email.setTextColor(Color.parseColor("#00cc00"));
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(getBaseActivity().getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }
    }

    void emailBox()
    {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
            {
                if ((!email.hasFocus())  && android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                {

                    try
                    {
                        JSONObject jobj         = new JSONObject();
                        jobj.put("email",email.getText().toString());
                        jobj.put("caller",WEB_AJAX);

                        appLog("jobj :: "+jobj);
                        String encrypted        = cc.getEncryptedString(jobj.toString());
                        AsyncHttpFunctionAjax localAsyncHttpFunctionAjax = new AsyncHttpFunctionAjax();
                        localAsyncHttpFunctionAjax.execute(encrypted);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });
        email.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable paramAnonymousEditable)
            {
            }

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
            {
            }

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
            {
                resetErrorWarning(1);
                email.setTextColor(getResources().getColor(R.color.profile_txt));
            }
        });

        mobile.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable paramAnonymousEditable)
            {
            }

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
            {
            }

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
            {
                resetErrorWarning(2);
            }
        });


    }

    void resetErrorWarning(int forType)
    {
        if(forType == 1)
        {
            email.setTextColor(getResources().getColor(R.color.profile_txt));
            email_error.setVisibility(View.GONE);
            email_error.setText("");
        }
        else if(forType == 2)
        {
            mobile.setTextColor(getResources().getColor(R.color.profile_txt));
            mobile_error.setVisibility(View.GONE);
            mobile_error.setText("");
        }
        else
        {
            email.setTextColor(getResources().getColor(R.color.profile_txt));
            email_error.setVisibility(View.GONE);
            email_error.setText("");
            mobile.setTextColor(getResources().getColor(R.color.profile_txt));
            mobile_error.setVisibility(View.GONE);
            mobile_error.setText("");
        }


    }


}
