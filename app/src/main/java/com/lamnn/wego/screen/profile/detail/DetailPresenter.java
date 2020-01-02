package com.lamnn.wego.screen.profile.detail;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.utils.Utils;

import static com.lamnn.wego.utils.AppUtils.KEY_FRIENDS;
import static com.lamnn.wego.utils.AppUtils.KEY_USERS;

public class DetailPresenter implements DetailContract.Presenter {
    private Context mContext;
    private DetailContract.View mView;
    private FirebaseFirestore mFirestore;

    public DetailPresenter(Context context, DetailContract.View view) {
        mContext = context;
        mView = view;
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void getUserData(final String friendId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DocumentReference docRef = mFirestore.collection(KEY_USERS).document(auth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(snapshot.getData());
                    User user = gson.fromJson(jsonElement, User.class);
                    if (user.getFriends() != null && user.getFriends().size() != 0)
                        if (Utils.checkExistUid(friendId, user.getFriends())) {
                            mView.updateRelationship(true);
                        } else {
                            mView.updateRelationship(false);
                        }
                    else
                        mView.updateRelationship(false);
                } else {
                    mView.updateRelationship(false);
                }
            }
        });
    }

    @Override
    public void addFriend(String uid) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mFirestore.collection(KEY_USERS)
                .document(auth.getUid())
                .update(KEY_FRIENDS, FieldValue.arrayUnion(uid));
    }

    @Override
    public void removeFriend(String uid) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mFirestore.collection(KEY_USERS)
                .document(auth.getUid())
                .update(KEY_FRIENDS, FieldValue.arrayRemove(uid));
    }
}
