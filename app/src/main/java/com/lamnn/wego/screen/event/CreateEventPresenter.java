package com.lamnn.wego.screen.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lamnn.wego.BuildConfig;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.remote.EventService;
import com.lamnn.wego.utils.APIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.screen.profile.update.ProfileUpdateActivity.GALLERY_REQUEST_CODE;
import static com.lamnn.wego.screen.profile.update.ProfileUpdateActivity.TAKE_PHOTO_REQUEST_CODE;

public class CreateEventPresenter implements CreateEventContract.Presenter {
    private Context mContext;
    private CreateEventContract.View mView;

    public CreateEventPresenter(Context context, CreateEventContract.View view) {
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
    public void showAddPhotoLayout() {
        mView.showAddPhotoLayout();
    }

    @Override
    public void hideAddPhotoLayout() {
        mView.hideAddPhotoLayout();
    }

    @Override
    public void createEvent(final Event event) {
        final List<String> photos = event.getPhotos();
        event.setPhotos(new ArrayList<String>());
        final Calendar calendar = Calendar.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference().child("event_photo");
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            final StorageReference photoReference = storageReference.child(event.getUserId() + i + calendar.getTimeInMillis() + ".png");
            UploadTask uploadTask = photoReference.putFile(Uri.parse(photos.get(i)));
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return photoReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadedUri = task.getResult();
                        list.add(downloadedUri.toString());
                        if (list.size() == photos.size()) {
                            event.setPhotos(list);
                            EventService eventService = APIUtils.getEventService();
                            eventService.createEvent(event).enqueue(new Callback<Event>() {
                                @Override
                                public void onResponse(Call<Event> call, Response<Event> response) {
                                    hideLoading();
                                }

                                @Override
                                public void onFailure(Call<Event> call, Throwable t) {

                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void updateEvent(final Event event) {
        showLoading();
        final List<String> photos = event.getPhotos();
        event.setPhotos(new ArrayList<String>());
        final Calendar calendar = Calendar.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference().child("event_photo");
        final List<String> list = new ArrayList<>();
        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                if (photos.get(i).startsWith("http")) {
                    list.add(photos.get(i));
                    if (list.size() == photos.size()) {
                        event.setPhotos(list);
                        EventService eventService = APIUtils.getEventService();
                        eventService.updateEvent(event).enqueue(new Callback<Event>() {
                            @Override
                            public void onResponse(Call<Event> call, Response<Event> response) {
                                hideLoading();
                            }

                            @Override
                            public void onFailure(Call<Event> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    final StorageReference photoReference = storageReference.child(event.getUserId() + i + calendar.getTimeInMillis() + ".png");
                    UploadTask uploadTask = photoReference.putFile(Uri.parse(photos.get(i)));
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return photoReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadedUri = task.getResult();
                                list.add(downloadedUri.toString());
                                if (list.size() == photos.size()) {
                                    event.setPhotos(list);
                                    EventService eventService = APIUtils.getEventService();
                                    eventService.updateEvent(event).enqueue(new Callback<Event>() {
                                        @Override
                                        public void onResponse(Call<Event> call, Response<Event> response) {
                                            hideLoading();
                                        }

                                        @Override
                                        public void onFailure(Call<Event> call, Throwable t) {

                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        } else {
            EventService eventService = APIUtils.getEventService();
            eventService.updateEvent(event).enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    hideLoading();
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {

                }
            });
        }

    }

    @Override
    public void showLoading() {
        mView.showLoading();
    }

    @Override
    public void hideLoading() {
        mView.hideLoading();
    }


    private File createImageFile() {
        String fileName = mContext.getString(R.string.file_name_image);
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir, fileName);
        return file;
    }
}
