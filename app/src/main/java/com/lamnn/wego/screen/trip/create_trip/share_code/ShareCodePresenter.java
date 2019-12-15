package com.lamnn.wego.screen.trip.create_trip.share_code;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.Invitation;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.utils.APIUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareCodePresenter implements ShareCodeContract.Presenter {
    private Context mContext;
    private ShareCodeContract.View mView;
    private FirebaseFirestore mFirestore;
    private TripService mTripService;

    public ShareCodePresenter(Context context, ShareCodeContract.View view) {
        mContext = context;
        mView = view;
        mFirestore = FirebaseFirestore.getInstance();
        mTripService = APIUtils.getTripService();
    }

    @Override
    public void getUserFriends() {
        final ArrayList<User> users = new ArrayList<>();
        mFirestore.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(snapshot.getData());
                            final User user = gson.fromJson(jsonElement, User.class);
                            if (user.getFriends() != null && user.getFriends().size() != 0) {
                                for (String uid : user.getFriends()) {
                                    mFirestore.collection("users")
                                            .document(uid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Gson gson = new Gson();
                                                        JsonElement jsonElement = gson.toJsonTree(task.getResult().getData());
                                                        User user = gson.fromJson(jsonElement, User.class);
                                                        users.add(user);
                                                    }
                                                    if (users.size() == user.getFriends().size()) {
                                                        mView.showUserFriend(users);
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                        }
                    }
                });
    }

    @Override
    public void copyIdTripToClipboard(String code) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("TRIP_CODE", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, "Copied " + code + " to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInviteStatusClick(User user, Trip trip) {
        Invitation invitation = new Invitation();
        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());
        currentUser.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        invitation.setCreator(currentUser);
        invitation.setReceiverId(user.getUid());
        invitation.setStatus("invited");
        invitation.setTrip(trip);
        if (user.getInvitation() != null) {
            if (user.getInvitation().getStatus().equals("cancel")) {
                updateInvitationStatus(user, "invited");
            } else {
                updateInvitationStatus(user, "cancel");
            }
        } else {
            mTripService.inviteFriend(invitation).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });
        }
    }

    private void updateInvitationStatus(User user, String status) {
        mFirestore.collection("invitations")
                .document(user.getInvitation().getId())
                .update("status", status);
    }
}
