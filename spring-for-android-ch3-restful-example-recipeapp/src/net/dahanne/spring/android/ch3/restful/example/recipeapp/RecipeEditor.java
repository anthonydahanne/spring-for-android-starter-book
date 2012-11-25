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


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * Adaption of the NoteEditor from the Android samples to interact with the recipe web service
 * 
 * @author Anthony Dahanne
 *  
 *
 */
public class RecipeEditor extends Activity {
    // For logging and debugging purposes
    private static final String TAG = "RecipeEditor";

	public static final String RECIPE_ID = "recipeId";
	public static final String NEW_RECIPE = "newRecipe";

    // A label for the saved state of the activity
    private static final String ORIGINAL_CONTENT = "origContent";

    // This Activity can be started by more than one action. Each action is represented
    // as a "state" constant
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;


    private ProgressDialog progressDialog;

	private boolean destroyed = false;

    // Global mutable variables
    private int mState;
    private Long id;
    private EditText mText;
    private EditText title;
    private String mOriginalContent;


	private Recipe recipe;

	private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Creates an Intent to use when the Activity object's result is sent back to the
         * caller.
         */
        final Intent intent = getIntent();

		if(intent.getBooleanExtra(RecipeEditor.NEW_RECIPE, true) ){
			id = null;
			mState =  STATE_INSERT;
		} else {
			
			mState = STATE_EDIT;
			id = intent.getLongExtra(RecipeEditor.RECIPE_ID, 0L);
		}
        
        // Sets the layout for this Activity. See res/layout/recipe_editor.xml
        setContentView(R.layout.recipe_editor);

        // Gets a handle to the EditText in the the layout.
        mText = (EditText) findViewById(R.id.recipe);
        
        title = (EditText) findViewById(R.id.title);
        
        
        typeSpinner = (Spinner)this.findViewById(R.id.spinner1);

        // Step 2: Create and fill an ArrayAdapter with a bunch of "State" objects
        ArrayAdapter<DishType> spinnerArrayAdapter = new ArrayAdapter<DishType>(this,
              android.R.layout.simple_spinner_item, new DishType[] {   
                    DishType.ENTREE, 
                    DishType.MAIN_DISH, 
                    DishType.DESSERT
                    });

        // Step 3: Tell the spinner about our adapter
        typeSpinner.setAdapter(spinnerArrayAdapter); 
        

        /*
         * If this Activity had stopped previously, its state was written the ORIGINAL_CONTENT
         * location in the saved Instance state. This gets the state.
         */
        if (savedInstanceState != null) {
            mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isNewRecipe =  getIntent().getBooleanExtra(RecipeEditor.NEW_RECIPE, false);
        id = getIntent().getLongExtra(RecipeEditor.RECIPE_ID, 0L);
        if(!isNewRecipe) {
        	new GetRecipeTask().execute(id);
        	
        } else {
        	recipe = new Recipe();
        	setTitle(getText(R.string.title_create));
        	recipe.setDescription("");
        	mText.setTextKeepState(recipe.getDescription());
        	title.setText(recipe.getTitle());
        }
            // Modifies the window title for the Activity according to the current Activity state.
            if (mState == STATE_EDIT) {
            // Sets the title to "create" for inserts
            } else if (mState == STATE_INSERT) {
                setTitle(getText(R.string.title_create));
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ORIGINAL_CONTENT, mOriginalContent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_options_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
        case R.id.menu_save:
            String text = mText.getText().toString();
            String titleText = title.getText().toString();
            updateRecipe(text, titleText);
            
            break;
        case R.id.menu_delete:
            deleteRecipe();
            break;
        case R.id.menu_revert:
            cancelRecipe();
            break;
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * Replaces the current recipe contents with the text and title provided as arguments.
     * @param text The new recipe contents to use.
     * @param title The new recipe title to use
     */
    private final void updateRecipe(String text, String title) {
    	recipe.setTitle(title);
    	recipe.setDescription(text);
    	recipe.setType(typeSpinner.getSelectedItem().toString());
    	if(recipe.getId()==null) {
    		new CreateRecipeTask().execute();
    	} else {
    		new UpdateRecipeTask().execute();
    	}
    }

    /**
     * This helper method cancels the work done on a recipe.  It deletes the recipe if it was
     * newly created, or reverts to the original text of the recipe i
     */
    private final void cancelRecipe() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Take care of deleting a recipe.  Simply deletes the entry.
     */
    private final void deleteRecipe() {
    	new DeleteRecipeTask().execute();
    }
    
    
    /**
     * AsyncTask to read a recipe from the recipe web service 
     */
    private class GetRecipeTask extends RecipeAbstractAsyncTask<Long, Void, Recipe> {
    	
		@Override
		protected void onPreExecute() {
			showProgressDialog("Loading. Please wait...");
		}

		@Override
		protected Recipe doInBackground(Long... params) {
				final String url = getString(R.string.recipe_resource_url);
				// Set the Accept header for "application/json"
				HttpHeaders requestHeaders = prepareHeadersWithMediaTypeAndBasicAuthentication();

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

				try {
					// Perform the HTTP GET request
					Log.i(TAG,"Getting the recipe with id : "+params[0] + " : " +url + params[0]);
					ResponseEntity<Recipe> responseEntity = restTemplate.exchange(url + params[0], HttpMethod.GET, requestEntity,	Recipe.class);
					return responseEntity.getBody();
				}
				catch (RestClientException e) {
					Log.d(TAG, e.getMessage(), e);
					exception = e;
					return null;
				}
		}

		@Override
		protected void onPostExecute(Recipe result) {
			
			dismissProgressDialog();
			
			if(result!=null) {
				recipe = result;
	        	setTitle(recipe.getTitle());
	        	 mText.setTextKeepState(recipe.getDescription());
	             title.setText(recipe.getTitle());
	             ArrayAdapter<DishType> typeSpinnerAdapter =  (ArrayAdapter<DishType>) typeSpinner.getAdapter();
	             int position = typeSpinnerAdapter.getPosition(DishType.fromString(recipe.getType()));
	             typeSpinner.setSelection(position);
			} else {
				String message = "unknown reason";
				if(exception != null) {
					message = exception.getMessage();
				}
				Toast.makeText(RecipeEditor.this, "A problem occurred during the reception of all recipes : " +message , Toast.LENGTH_LONG).show();
				recipe = new Recipe();
			}
			
		}

	}
    
    /**
     * AsyncTask to update a recipe from the recipe web service 
     */
    private class UpdateRecipeTask extends RecipeAbstractAsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("Updating new recipe on server. Please wait...");
		}

		@Override
		protected Void doInBackground(Void... params) {
				// The URL for making the GET request
				final String url = getString(R.string.recipe_resource_url);
				// Set the Accept header for "application/json"
				HttpHeaders requestHeaders = prepareHeadersWithMediaTypeAndBasicAuthentication();


				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
				
				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<Recipe> requestEntity = new HttpEntity<Recipe>(recipe,requestHeaders);
				try {
					// Perform the HTTP PUT request
					Log.i(TAG,"Updating the recipe with id : "+recipe.getId() + " : " + url);
					ResponseEntity<Void> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,	Void.class);
					if(responseEntity.getStatusCode() != HttpStatus.OK) {
						throw new HttpServerErrorException(responseEntity.getStatusCode());
					}
				}
				catch (RestClientException e) {
					Log.d(TAG, e.getMessage(), e);
					exception = e;
				}
				return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			dismissProgressDialog();
			
			if(exception==null) {
				finish();
			} else {
				Toast.makeText(RecipeEditor.this, "A problem occurred during the update of the recipe : " +exception.getMessage() , Toast.LENGTH_LONG).show();
			}
			
		}

	}
    
    /**
     * AsyncTask to create a recipe from the recipe web service 
     */
    private class CreateRecipeTask extends RecipeAbstractAsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("Posting new recipe to server. Please wait...");
		}

		@Override
		protected Void doInBackground(Void... params) {
				final String url = getString(R.string.recipe_resource_url);

				HttpHeaders requestHeaders = prepareHeadersWithMediaTypeAndBasicAuthentication();


				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
				
				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<Recipe> requestEntity = new HttpEntity<Recipe>(recipe,requestHeaders);
				try {
					// Perform the HTTP POST request
					Log.i(TAG,"Posting the recipe with id : "+recipe.getId() + " : to " +url);
					ResponseEntity<Void> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,	Void.class);
					if(responseEntity.getStatusCode() != HttpStatus.CREATED) {
						throw new HttpServerErrorException(responseEntity.getStatusCode());
					}
				}
				catch (RestClientException e) {
					Log.d(TAG, e.getMessage(), e);
					exception = e;
				}
				return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			dismissProgressDialog();
			
			if(exception==null) {
				finish();
			} else {
				Toast.makeText(RecipeEditor.this, "A problem occurred during the creation of the recipe : " +exception.getMessage() , Toast.LENGTH_LONG).show();
			}
			
		}

	}
    
    /**
     * AsyncTask to delete a recipe from the recipe web service 
     */
    private class DeleteRecipeTask extends RecipeAbstractAsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("Deleting recipe from server. Please wait...");
		}

		@Override
		protected Void doInBackground(Void... params) {
				final String url = getString(R.string.recipe_resource_url);
				// Set the Accept header for "application/json"
				HttpHeaders requestHeaders = prepareHeadersWithMediaTypeAndBasicAuthentication();

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
				
				try {
					// Perform the HTTP DELETE request
					Log.i(TAG,"Deleting the recipe with id : "+recipe.getId() + " : from " +url +recipe.getId() );
					ResponseEntity<Void> responseEntity = restTemplate.exchange(url+recipe.getId(), HttpMethod.DELETE, requestEntity,	Void.class);
					if(responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
						throw new HttpServerErrorException(responseEntity.getStatusCode());
					}
				}
				catch (RestClientException e) {
					Log.d(TAG, e.getMessage(), e);
					exception = e;
				}
				return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			dismissProgressDialog();
			
			if(exception==null) {
				finish();
			} else {
				Toast.makeText(RecipeEditor.this, "A problem occurred during the deletion of the recipe : " +exception.getMessage() , Toast.LENGTH_LONG).show();
			}
			
		}

	}
    
    public void showProgressDialog(CharSequence message) {
		if (this.progressDialog == null) {
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setIndeterminate(true);
		}

		this.progressDialog.setMessage(message);
		this.progressDialog.show();
	}

	public void dismissProgressDialog() {
		if (this.progressDialog != null && !this.destroyed) {
			this.progressDialog.dismiss();
		}
	}
    
}
