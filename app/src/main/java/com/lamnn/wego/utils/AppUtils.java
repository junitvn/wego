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
    public static String CAR_ICON_URI = "https://firebasestorage.googleapis.com/v0/b/wego-af401.appspot.com/o/event_photo%2Fbreakdown.png?alt=media&token=338e6f81-5f4e-4a02-8263-1bca031cdf81";
    public static String GAS_ICON_URI = "https://firebasestorage.googleapis.com/v0/b/wego-af401.appspot.com/o/event_photo%2Frefuel.png?alt=media&token=da1ebd4c-01bd-4db2-909a-ec926841f2e2";
}
