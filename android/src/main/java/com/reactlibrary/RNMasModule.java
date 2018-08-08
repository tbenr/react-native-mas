
package com.reactlibrary;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.app.Activity;
import android.net.Uri;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import org.json.JSONObject;
import org.json.JSONException;

import com.ca.mas.core.error.TargetApiException;
import com.ca.mas.core.service.MssoIntents;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthenticationListener;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASDevice;
import com.ca.mas.foundation.MASOtpAuthenticationHandler;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.auth.MASAuthenticationProviders;
import com.ca.mas.ui.MASLoginActivity;

import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;

public class RNMasModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNMasModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @ReactMethod
  public void debug() {
     MAS.debug();
  }


  @ReactMethod
  public void init(String config, Promise promise) {
    try {
      JSONObject jsonConfiguration = new JSONObject(config);
      MAS.start(this.reactContext,jsonConfiguration);
      final Activity activity = getCurrentActivity();

      MAS.setAuthenticationListener(new MASAuthenticationListener() {
            @Override
            public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders providers) {
                Intent intent = new Intent(activity, MASLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MssoIntents.EXTRA_REQUEST_ID, requestId);
                intent.putExtra(MssoIntents.EXTRA_AUTH_PROVIDERS, providers);
                activity.startActivity(intent);
            }

            @Override
            public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler handler) {
                //Ignore for now
            }
        });


      Log.i("MAS", "initialized");
      promise.resolve(null);
      return;
    }
    catch(JSONException jex) {};
    Log.e("MAS","initialization error");
    promise.reject("","initialization error");
  }

  @ReactMethod
  public void login(final Promise promise) {

        MASUser.login(new MASCallback<MASUser>() {
            @Override
            public void onSuccess(MASUser result) {
                String id = MASDevice.getCurrentDevice().getIdentifier();
                Log.w("MAS", "Logged in as " + id);

                promise.resolve(id);

            }

            @Override
            public void onError(Throwable e) {
                Log.w("MAS", "Login failure: " + e);
                promise.reject("",e);
            }
        });
  }

  @ReactMethod
  public void logout(final Promise promise) {
    MASUser.getCurrentUser().logout(new MASCallback<Void>() {
       @Override
       public void onSuccess(Void result) {
          Log.i("MAS", "Logout success!");
          promise.resolve(null);
       }

       @Override
       public void onError(Throwable e) {
          Log.w("MAS", "Logout failure: " + e);
          promise.reject("",e);
       }
    });
  }

  @ReactMethod
  public void invoke(String uriPath, final Promise promise) {
     Uri.Builder uriBuilder = new Uri.Builder().encodedPath(uriPath);
     Uri uri = uriBuilder.build();
     MASRequest request = new MASRequest.MASRequestBuilder(uri).build();

     MAS.invoke(request, new MASCallback<MASResponse<String>>() {

      @Override
      public void onSuccess(MASResponse<String> response) {

        String b = new String(response.getBody().getRawContent());

        Log.i("MAS", "api returned: " + b);

        //Check for the response code;
        //The module considers success when receiving a response with HTTP status code range 200-299
        if (HttpURLConnection.HTTP_OK == response.getResponseCode()) {
            promise.resolve(b);
        }
        else {
            promise.reject("",b);
        }
      }

      @Override
      public void onError(Throwable e) {
        promise.reject("", e);
      }
    });
  };

  @Override
  public String getName() {
    return "RNMas";
  }
}