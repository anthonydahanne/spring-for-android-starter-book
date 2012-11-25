/*
 * Copyright (C) 2007 The Android Open Source Project
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

package net.dahanne.spring.android.ch3.restful.example.recipeapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipesList extends ListActivity  {

    // For logging and debugging
    private static final String TAG = "RecipesList";


	private static final int ACTION_EDIT = 10;


	private static final int ACTION_ADD = 20;

    
    private ProgressDialog progressDialog;

	private boolean destroyed = false;


	private List<Recipe> recipes;

	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.destroyed = true;
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setOnCreateContextMenuListener(this);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	// we go and get the recipes, that will get added to the ListAdapter
    	new GetRecipesTask().execute();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);

        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, RecipesList.class), null, intent, 0, null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
           startActivityForResult(new Intent(this,RecipeEditor.class).putExtra(RecipeEditor.NEW_RECIPE, true), ACTION_ADD);
           return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        // Tries to get the position of the item in the ListView that was long-pressed.
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // If the menu object can't be cast, logs an error.
            Log.e(TAG, "bad menuInfo", e);
            return;
        }
        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(), 
                                        Integer.toString((int) info.id) ));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, RecipesList.class), null, intent, 0, null);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        try {
            // Casts the data object in the item into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {

            // If the object can't be cast, logs an error
            Log.e(TAG, "bad menuInfo", e);

            // Triggers default processing of the menu item.
            return false;
        }
        // Appends the selected recipe's ID to the URI sent with the incoming Intent.
        Uri recipeUri = ContentUris.withAppendedId(getIntent().getData(), info.id);

        /*
         * Gets the menu item's ID and compares it to known actions.
         */
        switch (item.getItemId()) {
        case R.id.context_open:
            // Launch activity to view/edit the currently selected item
            startActivity(new Intent(Intent.ACTION_EDIT, recipeUri));
            return true;


        case R.id.context_delete:
            getContentResolver().delete(
                recipeUri,  // The URI of the provider
                null,     // No where clause is needed, since only a single recipe ID is being
                          // passed in.
                null      // No where clause is used, so no where arguments are needed.
            );
  
            // Returns to the caller and skips further processing.
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	//we are back in the list activity, and we know the user has successfully updated the list, let's get it back from the server
    	if(resultCode==RESULT_OK || requestCode == ACTION_ADD) {
    		new GetRecipesTask().execute();
    	}
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the recipe ID URI. The effect is to call RecipeEdit.
            startActivityForResult(new Intent(this,RecipeEditor.class).putExtra(RecipeEditor.RECIPE_ID, getListAdapter().getItemId(position)).putExtra(RecipeEditor.NEW_RECIPE, false), ACTION_EDIT);
    }
    
    
    
    /**
     * AsyncTask to fetch all recipes from the recipe web service 
     */
	private class GetRecipesTask extends RecipeAbstractAsyncTask <Void, Void, List<Recipe>> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("Loading recipes. Please wait...");
		}

		@Override
		protected List<Recipe> doInBackground(Void... params) {
				HttpHeaders requestHeaders = prepareHeadersWithMediaTypeAndBasicAuthentication();

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

				try {
					// Perform the HTTP GET request
					ResponseEntity<Recipe[]> responseEntity = restTemplate.exchange(getString(R.string.recipe_resource_url), HttpMethod.GET, requestEntity,	Recipe[].class);
					return Arrays.asList(responseEntity.getBody());
				}
				catch (RestClientException e) {
					Log.e(TAG, e.getMessage(), e);
					exception = e;
					return null;
				}
		}
		

		@Override
		protected void onPostExecute(List<Recipe> result) {
			dismissProgressDialog();
			if(result != null) {
				recipes = result;
			} else {
				String message = exception != null ? exception.getMessage() : "unknown reason";
				Toast.makeText(RecipesList.this, "A problem occurred during the reception of all recipes : " +message , Toast.LENGTH_LONG).show();
				recipes = new ArrayList<Recipe>();
			}
			ListAdapter adapter = new RecipeAdapter(RecipesList.this, R.layout.recipeslist_item, recipes ) ;
			setListAdapter(adapter );
		}
	}
    
	
	private class RecipeAdapter extends ArrayAdapter<Recipe>  {

		public RecipeAdapter(RecipesList recipesList, int recipeslistItem,
				List<Recipe> recipes) {
			super(recipesList,recipeslistItem,recipes);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}
		
		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			
			View rowView;
			LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (null == convertView) {
				rowView = mInflater.inflate(R.layout.recipeslist_item, null);
			} else {
				rowView = convertView;
			}
			
			Recipe item = getItem(position);
			TextView titleView = (TextView) rowView.findViewById(android.R.id.text1);
			titleView.setText(item.getTitle());
			return titleView;
		}
	}
	
	
	private void showProgressDialog(CharSequence message) {
		if (this.progressDialog == null) {
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setIndeterminate(true);
		}

		this.progressDialog.setMessage(message);
		this.progressDialog.show();
	}

	private void dismissProgressDialog() {
		if (this.progressDialog != null && !this.destroyed) {
			this.progressDialog.dismiss();
		}
	}
	
}
