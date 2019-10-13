package com.lamnn.wego.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.User;

public class AppUtils {
    private static User sUser = null;
    private static String TAG = "APP_UTILS_TAG";

//    public static User getCurrentUser() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        DocumentReference docRef = db.collection("users").document(auth.getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//
//                        Gson gson = new Gson();
//                        JsonElement jsonElement = gson.toJsonTree(document.getData());
//                        sUser = gson.fromJson(jsonElement, User.class);
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//        return sUser;
//    }

}
