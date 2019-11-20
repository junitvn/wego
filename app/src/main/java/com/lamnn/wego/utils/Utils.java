package com.lamnn.wego.utils;

import android.app.Activity;
import android.app.ProgressDialog;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class Utils {
    public static Boolean checkExistUid(String userId, List<String> users) {
        Boolean result = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equals(userId)) {
                result = true;
            }
        }
        return result;
    }
}
