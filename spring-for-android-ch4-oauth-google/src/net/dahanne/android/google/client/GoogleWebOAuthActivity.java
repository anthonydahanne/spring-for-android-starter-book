/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dahanne.android.google.client;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 *  This class is based on the FacebookWebOAuthActivity by Roy Clarkson
 */
public class GoogleWebOAuthActivity extends AbstractWebViewActivity {

	private static final String TAG = GoogleWebOAuthActivity.class.getSimpleName();

	private ConnectionRepository connectionRepository;

	private GoogleConnectionFactory connectionFactory;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//javascript is mandatory
		getWebView().getSettings().setJavaScriptEnabled(true);

		// Using a custom web view client to capture the access token
		getWebView().setWebViewClient(new GoogleOAuthWebViewClient());

		this.connectionRepository = getApplicationContext().getConnectionRepository();
		this.connectionFactory = getApplicationContext().getGoogleConnectionFactory();
	}

	@Override
	public void onStart() {
		super.onStart();

		// display the Google authorization page
		getWebView().loadUrl(getAuthorizeUrl());
	}

	// ***************************************
	// Private methods
	// ***************************************
	private String getAuthorizeUrl() {
		String redirectUri = getString(R.string.google_oauth_callback_url);
		String scope = getString(R.string.google_scope);

		// Generate the Google authorization url to be used in the browser or web view
		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri(redirectUri);
		params.setScope(scope);
		return this.connectionFactory.getOAuthOperations().buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
	}

	private void displayGoogleOptions() {
		Intent intent = new Intent();
		intent.setClass(this, GoogleActivity.class);
		startActivity(intent);
		finish();
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class GoogleOAuthWebViewClient extends WebViewClient {

		private static final String LOCALHOST = "localhost";
		private static final String CODE = "code";

		/*
		 * The WebViewClient has another method called shouldOverridUrlLoading which does not capture the javascript 
		 * redirect to the success page. So we're using onPageStarted to capture the url.
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// parse the captured url
			Uri uri = Uri.parse(url);
			// log the url : very interesting for debugging the OAuth workflow
			Log.d(TAG, url);
			
			/*
			 * The WebViewClient is launched to load an URL from the provider that will ask the user whether or not he accepts our app to access his data.
			 * Once the provider successfully gets the approval from the user, it will redirect this WebViewClient to the callback_uri, with a query parameter named "code" : this is the authorization code
			 */
			String host = uri.getHost();
			String code = uri.getQueryParameter(CODE);

			
			// The WebViewClient is redirected to the callback_uri, let's trade the authorization code for the access token
			if (LOCALHOST.equals(host)) {
				if(!exchangeAuthorizationCodeForAccessToken.getStatus().equals(AsyncTask.Status.RUNNING)) {
					exchangeAuthorizationCodeForAccessToken.execute(code);
					Toast.makeText(getApplicationContext(), "Redirecting you to the app main activity", Toast.LENGTH_LONG).show();
					//preparing to quit this activity for the main activity
					getWebView().setVisibility(View.INVISIBLE);
				}
			}
		}
	}
	
	
	private AsyncTask<String, Void, Void> exchangeAuthorizationCodeForAccessToken =  new AsyncTask<String, Void, Void>() {
		
		private Exception exception;
		
		@Override
		protected Void doInBackground(String... params) {
			// executed by a background thread
			//params[0] should contain the authorization code
			try {
				AccessGrant exchangeForAccess = connectionFactory.getOAuthOperations().exchangeForAccess(params[0], getString(R.string.google_oauth_callback_url), null);
				Log.d(TAG,"AccessToken  : "+exchangeForAccess.getAccessToken());
				Log.d(TAG,"RefreshToken  : "+exchangeForAccess.getRefreshToken());
				Log.d(TAG,"ExpireTime  : "+exchangeForAccess.getExpireTime());
				
				Connection<Google> connection = connectionFactory.createConnection(exchangeForAccess);
				connectionRepository.addConnection(connection);
			} catch (DuplicateConnectionException e) {
				Log.e(TAG,"something went wrong when adding the accessToken to the connection repository",e);
				exception = e;
			} catch (Exception e) {
				Log.e(TAG,"something went wrong when adding the accessToken to the connection repository",e);
				exception = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// executed by the UI thread once the background thread is done getting the result
			if(exception != null) {
				Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
			}
			// we go back to the main activity to display the options
			displayGoogleOptions();
		}
		
	};
	
}
