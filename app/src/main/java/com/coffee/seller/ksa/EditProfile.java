package com.coffee.seller.ksa;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tami.seller.ksa.R;

import org.json.JSONObject;


public class EditProfile extends BaseActivity {
	private EditText name_en , name_ar , phone , email;
	private EditText pwd , oldpassword;

	private String nameStren,nameStrar;
	private String newPwdStr;
	private String oldpasswordStr,phoneStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_editprofile);

		name_en 			= ((EditText)findViewById(R.id.username));
		email 			= ((EditText)findViewById(R.id.email));
		name_ar 			= ((EditText)findViewById(R.id.username_ar));
		pwd  				= ((EditText)findViewById(R.id.password));
		oldpassword  		= ((EditText)findViewById(R.id.oldpasword));
        phone               = (EditText)findViewById(R.id.mobile);


   	 	this.pwd.setInputType(129);
   	 	this.oldpassword.setInputType(129);
   	 	Typeface font = Typeface.createFromAsset(getAssets(), "Exo2-Regular.otf");
		this.pwd.setTypeface(font);
		this.oldpassword.setTypeface(font);



        name_en.setText(getSharePrefs().getNameEn());
        name_ar.setText(getSharePrefs().getNameAr());
        phone.setText(getSharePrefs().getPhone());
		email.setText(getSharePrefs().getEmail());


	}


	public void submitvalues(View v)
	{

		if (verify())
       {

			try
            {

					JSONObject jobj         = new JSONObject();
					jobj.put("name_en",nameStren);
					jobj.put("name_ar",nameStrar);
					jobj.put("phone",phoneStr);
					jobj.put("password",newPwdStr);
					jobj.put("currentpwd",oldpasswordStr);
					jobj.put("userthumb","");
					jobj.put("uid",getSharePrefs().getUid());
					jobj.put("caller","updateProfile");

					String encrypted        = cc.getEncryptedString(jobj.toString());
	            	AsyncHttpFunction http 	= new AsyncHttpFunction();
	                http.execute(encrypted);
            }
            catch (Exception localException)
            {
          	  localException.printStackTrace();
          	  Toast.makeText(EditProfile.this, localException.toString(), Toast.LENGTH_SHORT).show();
            }
       }
	}

	boolean verify()
	{
		nameStren 		= this.name_en.getText().toString();
		nameStrar 		= this.name_ar.getText().toString();
	    newPwdStr 		= this.pwd.getText().toString();
		oldpasswordStr	= this.oldpassword.getText().toString();
        phoneStr        = phone.getText().toString();

	    if (nameStren.trim().equals(""))
	    {
			  name_en.requestFocus();
		      Toast.makeText(getApplicationContext(), getString(R.string.name_blank), Toast.LENGTH_SHORT).show();
		      return false;
	    }
	    else if (this.nameStrar.equals(""))
	    {

			  name_ar.requestFocus();
			  Toast.makeText(getBaseActivity(), getString(R.string.invalid_name_ar), Toast.LENGTH_SHORT).show();
		      return false;
	    }
		else if(!PhoneNumberUtils.isGlobalPhoneNumber(phoneStr) || (phoneStr.length() < 10))
		{
			phone.requestFocus();
			Toast.makeText(getBaseActivity(), getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
            return false;
		}
		else if(oldpasswordStr.length() < 6)
		{
			oldpassword.requestFocus();
			Toast.makeText(getBaseActivity(), getString(R.string.old_password_cannot_b_blank), Toast.LENGTH_SHORT).show();
			return false;
		}
	    else if (!newPwdStr.equals("") && this.newPwdStr.length() < 6)
	    {
	    	  pwd.requestFocus();
		      Toast.makeText(getApplicationContext(), getString(R.string.password_min_char), Toast.LENGTH_SHORT).show();
		      return false;
	    }



	    return true;
	}


	  class AsyncHttpFunction extends AsyncTask<String, Void, String>
 	  {

			@Override
			protected String doInBackground(String... paramVarArgs) {
				return getConnection().sendPostData(paramVarArgs[0],null);
			}

	 	    @Override
	 	    protected void onPostExecute(String paramString)
	 	    {
	 	      super.onPostExecute(paramString);

		 	      try
		 	      {
                      appLog("response :: "+paramString);
		 	    	  if (paramString != null)
					  {
						  JSONObject result = new JSONObject(paramString);
                          try
                          {
                          	  if(!result.getString("message").equals(""))
                          	  		Toast.makeText(EditProfile.this.getApplicationContext(),result.getString("message"), Toast.LENGTH_SHORT).show();
                              if(result.getString("success").equals("1"))
                                  	onFinish(paramString);

                          }catch (Exception e)
                          {
                              Toast.makeText(EditProfile.this.getApplicationContext(),getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();

                          }


					  }

		 	    	  else
		 	    		  Toast.makeText(EditProfile.this.getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
		 	      }
		 	      catch (Exception localException){
					  Toast.makeText(EditProfile.this.getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
				  }
		 	      finally
		 	      {
		 	    	 getFunction().dismissDialog();
		 	      }
	 	    }
	 	    @Override
	 	    protected void onPreExecute() {
	 	    	super.onPreExecute();
				getFunction().showDialog();
	 	    }


     }


 	 private void onFinish(String paramString)
 	 {
 	    String str = paramString.trim();
 	    try
 	    {
	 	    JSONObject jobj	=new JSONObject(str);
	 	    String status	= jobj.getString("status");
	 	    if(status.equals("1"))
	 	    {
	 	    	String success	= jobj.getString("success");
	 	    	String msg		= jobj.getString("data");
	 	    	if(success.equals("1"))
	 	    	{
	 	    		  boolean b = getSharePrefs().onFinish(new JSONObject(msg));
	 	    		  if(b)
	 	    		  {
	 			 	      Intent localIntent = new Intent(this, EditProfile.class);
	 			 	      localIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 			 	      startActivity(localIntent);
	 	    		  }

	 	    	}

	 	    }
	 	    else
	 	    	Toast.makeText(getApplicationContext(), getString(R.string.error_updating_profile), Toast.LENGTH_SHORT).show();
 	    }
 	    catch(Exception e)
 	    {
 	    		e.printStackTrace();
 	    		Toast.makeText(getApplicationContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
 	    }

 	 }

}
