package com.lamnn.wego.screen.profile.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lamnn.wego.BuildConfig;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.UserService;
import com.lamnn.wego.utils.APIUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.screen.profile.update.ProfileUpdateActivity.GALLERY_REQUEST_CODE;
import static com.lamnn.wego.screen.profile.update.ProfileUpdateActivity.TAKE_PHOTO_REQUEST_CODE;

public class UpdateProfilePresenter implements UpdateProfileContract.Presenter {

    private UpdateProfileContract.View mView;
    private User mUser;
    private UserService mUserService;
    private Context mContext;

    public UpdateProfilePresenter(Context context, UpdateProfileContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void choosePhoto(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        activity.startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void takePhoto(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(activity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activity.startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE);
        }
    }

    @Override
    public void updateProfile(final User user) {
        mView.showLoading();
        mUser = user;
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUri() != null && user.getPhotoUri().startsWith("content")) {
            Calendar calendar = Calendar.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            final StorageReference imageReference = storageReference.child(calendar.getTimeInMillis() + ".png");
            UploadTask uploadTask = imageReference.putFile(Uri.parse(user.getPhotoUri()));
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getName())
                                .setPhotoUri(downloadUri)
                                .build();

                        fUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mView.hideSaveButton();
                                            mUser.setPhotoUri(downloadUri.toString());
                                            update(mUser);
                                        }
                                    }
                                });
                    } else {

                    }
                }
            });
        } else {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())
                    .build();
            fUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mView.hideSaveButton();
                                mView.hideLoading();
                                update(mUser);
                            }
                        }
                    });
        }

    }

    private File createImageFile() {
        String fileName = mContext.getString(R.string.file_name_image);
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir, fileName);
        return file;
    }

    private void update(User user) {
        mUserService = APIUtils.getUserService();
        mUserService.updateUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mView.showUpdatedProfile(response.body());
                mView.hideLoading();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
