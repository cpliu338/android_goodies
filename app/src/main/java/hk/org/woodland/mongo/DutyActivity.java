package hk.org.woodland.mongo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import hk.org.woodland.mytestbed.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.mongodb.stitch.android.StitchClient;
import com.mongodb.stitch.android.auth.Auth;
import com.mongodb.stitch.android.auth.AvailableAuthProviders;
import com.mongodb.stitch.android.auth.anonymous.AnonymousAuthProvider;
import com.mongodb.stitch.android.auth.emailpass.EmailPasswordAuthProvider;
import com.mongodb.stitch.android.services.mongodb.MongoClient;

import org.bson.Document;

import java.util.List;

public class DutyActivity extends Activity {

    // Remember to replace the APP_ID with your Stitch Application ID

    private static final String APP_ID = "3f9-crgdf"; //The Stitch Application ID
    private static final String TAG = "STITCH-SDK";
    private static final String MONGODB_SERVICE_NAME = "mongodb-atlas";
    private static final String REGISTER = "register";
    private static final String CONFIRM = "confirm";
    private static final String LOGIN = "login";

    private StitchClient _client;
    private MongoClient _mongoClient;

    private String actionName;
    private EditText action;
    private static final String dbName = "woodland";
    private static final String collectionName = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty);
        _client = new StitchClient(this.getBaseContext(), APP_ID);
        _mongoClient = new MongoClient(_client, MONGODB_SERVICE_NAME);
        action = (EditText) findViewById(R.id.searchName);
        actionName = "";
        //doAnonymousAuthentication();
        Button btn = (Button)findViewById(R.id.action0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (REGISTER.equalsIgnoreCase(action.getText().toString())) {
                register("cpliu338@hotmail.com", "Pa55w0rd");
            }
            else if (CONFIRM.equalsIgnoreCase(action.getText().toString())) {
                confirm();
            }
            else if (LOGIN.equalsIgnoreCase(action.getText().toString())) {
                if (_client.isAuthenticated()) {
                    Log.i(TAG,"Logging out");
                }
                _client.logInWithProvider(new EmailPasswordAuthProvider("cpliu338@yahoo.com", "Pa55w0rd")).continueWith(new Continuation<Auth, Object>() {
                    @Override
                    public Object then(@NonNull final Task<Auth> task) throws Exception {
                        if (task.isSuccessful()) {
                            Log.i(TAG,"User Authenticated as " + _client.getAuth().getUserId());
                        } else {
                            Log.e(TAG, "Error logging in anonymously", task.getException());
                        }
                        return null;
                    }
                });

            }
            else {
                doAnonymousAuthentication();
            }
            }
        });
    }

    private void confirm() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm email");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            String s = input.getText().toString();
            String[] ar = s.split("&");
            Log.i(TAG,"token:"+ar[0]);
            Log.i(TAG,"token id:"+ar[1]);
            _client.emailConfirm(ar[0], ar[1]).continueWith(new Continuation<Boolean, Object>() {
                @Override
                public Object then(@NonNull Task<Boolean> task) throws Exception {
                    if (task.isSuccessful()) {
                        Log.i(TAG,"User confirmed");
                    } else {
                        Log.e(TAG, "Error confirming", task.getException());
                    }
                    return null;
                }
            });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void register(String email, String password) {
        // throws Exception if user already registered
        // "cpliu338@gmail.com", "Pa55w0rd"
        _client.register(email, password).continueWith(new Continuation<Boolean, Object>() {
            @Override
            public Object then(@NonNull final Task<Boolean> task) throws Exception {
                if (task.isSuccessful()) {
                    Log.i(TAG,"User Registered for CP");
                } else {
                    Log.e(TAG, "Error registering", task.getException());
                }
                return null;
            }
        });
    }
    
    /*
    Click on this link to confirm your email: https://cpliu.myqnapcloud.com:8443/welcome.php?
    token=d43e10af1730f42c7fef59fda19d69c74b10ef7d23323c211d3d0a2dbe486182db7ce2918bb7b9ca586a3e1164c93a6591ec2f703f95b20dbae5a11f8247fd52
    &tokenId=5a0e3d6705842952945f9aed
    */
    private void doAnonymousAuthentication() {

        _client.getAuthProviders().addOnCompleteListener(new OnCompleteListener<AvailableAuthProviders>() {
            @Override
            public void onComplete(@NonNull final Task<AvailableAuthProviders> task) {
                if (!task.isSuccessful()){
                    Log.e(TAG, "Could not retrieve authentication providers");
                } else {
                    Log.i(TAG, "Retrieved authentication providers");

                    if (task.getResult().hasAnonymous()){
                        _client.logInWithProvider(new AnonymousAuthProvider()).continueWith(new Continuation<Auth, Object>() {
                            @Override
                            public Object then(@NonNull final Task<Auth> task) throws Exception {
                                if (task.isSuccessful()) {
                                    Log.i(TAG,"User Authenticated as " + _client.getAuth().getUserId());
                                } else {
                                    Log.e(TAG, "Error logging in anonymously", task.getException());
                                }
                                return null;
                            }
                        });
                    }
                }
            }
        });
    }

    public void searchRestaurant(View view){

        if (!_client.isAuthenticated()){
            warnAuth();
        } else {

            actionName = action.getText().toString();

            if (actionName.matches("")){
                new AlertDialog.Builder(this)
                        .setTitle("Invalid action")
                        .setMessage("Please specify a action name. Try searching again.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return;
            }

            final Document query = new Document( "name", actionName);
            Log.i(TAG, "Restaurant search query:" + query);

            // This code block is a simple find() command on the "users" collection in the "woodland" database.
            // The application only cares about the first returned result, even if there are multiple matches.

            _mongoClient.getDatabase(dbName).getCollection(collectionName).find(query).continueWith(new Continuation<List<Document>, Object>() {

                @Override
                public Object then(@NonNull final Task<List<Document>> task) throws Exception {
                    if (!task.isSuccessful()){
                        Log.e(TAG,"Failed to execute query");
                    } else {
                        TextView res = (TextView) findViewById(R.id.resultFound);

                        if (task.getResult().size() == 0) {
                            res.setText("No results found");
                            Log.i(TAG, "Query failed to return any results");
                            clearComments();
                            return null;
                        }
                        res.setText("Restaurant found");

                        final Document doc = task.getResult().get(0);

                        final TextView cuisine = (TextView) findViewById(R.id.cuisine);
                        cuisine.setText(doc.get("user_id").toString());

                        final TextView location = (TextView) findViewById(R.id.location);
                        location.setText(doc.get("name").toString());

                        final List<Document> comments = (List<Document>) doc.get("comments");
                        if (comments.size() > 0) {

                            // showComments() passes the list of documents to a custom list adapter.
                            // It then passes the list adapter to a list view, where the comments are displayed.
                            showComments(comments);
                        } else {
                            clearComments();
                        }
                    }
                    return null;
                }
            });
        }
    }

    private void clearComments() {

        final ListView lv = (ListView) findViewById(R.id.commentList);
        lv.setAdapter(null);

    }

    public void writeComment(final View view) {

        // Its important to check for authentication, as the authenticated user ID is used as
        // part of the document.
        if (!_client.isAuthenticated()){
            warnAuth();
            return;
        }

        if (actionName.matches("")){
            warnSearch();
            return;
        }

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Write Comment");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // The query document uses the name of the currently displayed action
                final Document query = new Document("name", actionName);

                // This is specific to anonymous authentication.
                // For facebook or google, you can check for a username using _client.getAuth().getUser.getData().get("name")
                final Document newComment = new Document("user_id", _client.getAuth().getUserId());
                newComment.put("comment" , input.getText().toString());

                // The $push update operator adds the "newComment" document to the "comment" array.
                // If "comment" does not exist, $push creates the array and adds "newComment" to it.
                final Document update = new Document( "$push" , new Document("comments", newComment));

                // This code block performs an "updateOne" operation, updating the document associated to the currently selected action and adding a new comment to the "comment" array.
                // On success, it calls "refreshComments()", which refreshes the List View displaying the comments associated to the action.
                _mongoClient.getDatabase(dbName).getCollection(collectionName).updateOne(query,update).continueWith(new Continuation<Void, Object>() {
                    @Override
                    public Object then(@NonNull final Task<Void> task) {
                        if (task.isSuccessful()) {
                            refreshComments(view);
                        } else {
                            Log.e(TAG,"Error writing comment");
                        }
                        return null;
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void refreshComments(View view) {

        if (actionName.matches("")){
            warnSearch();
            return;
        }

        final Document query = new Document("name", actionName);

        _mongoClient.getDatabase(dbName).getCollection(collectionName).find(query).continueWith(new Continuation<List<Document>, Object>() {
            @Override
            public Object then(@NonNull final Task<List<Document>> task) {
                if (!task.isSuccessful()){
                    Log.e(TAG, "Error refreshing comments");
                } else {

                    final Document result = task.getResult().get(0);
                    final List<Document> comments = (List<Document>) result.get("comments");

                    if (comments.size() > 0 ){
                        showComments(comments);
                    } else {
                        clearComments();
                    }
                }
                return null;
            }
        });
    }

    private void showComments(List<Document> comments) {

        final CustomListAdapter cla = new CustomListAdapter(this,comments);

        final ListView lv = (ListView) findViewById(R.id.commentList);
        lv.setAdapter(cla);
    }

    private void warnAuth() {
        new AlertDialog.Builder(this)
                .setTitle("Not Authenticated")
                .setMessage("The application automatically performs anonymous authentication. If you continue to see this message, check for network connectivity")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return;
    }

    private void warnSearch() {
        new AlertDialog.Builder(this)
                .setTitle("Invalid action")
                .setMessage("You can only read or write comments for a valid action. Try searching again.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return;
    }
}
