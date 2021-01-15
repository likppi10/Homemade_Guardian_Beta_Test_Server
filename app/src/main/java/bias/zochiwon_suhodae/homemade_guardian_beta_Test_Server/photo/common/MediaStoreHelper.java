package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.DirectoryModel;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.activity.PhotoPickerActivity.EXTRA_SHOW_GIF;

//?

public class MediaStoreHelper {
    public final static int INDEX_ALL_PHOTOS = 0;

    public static void getPhotoDirs(FragmentActivity activity, Bundle args, PhotosResultCallback resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(0, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    }

    static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private Context Context;
        private PhotosResultCallback ResultCallback;

        public PhotoDirLoaderCallbacks(Context Context, PhotosResultCallback ResultCallback) {
            this.Context = Context;
            this.ResultCallback = ResultCallback;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int Id, Bundle Args) { return new PhotoDirectoryLoader(Context, Args.getBoolean(EXTRA_SHOW_GIF, false)); }

        @Override
        public void onLoadFinished(Loader<Cursor> Loader, Cursor Data) {

            if (Data == null) return;
            List<DirectoryModel> Directory_List = new ArrayList<>();
            DirectoryModel directoryModelAll = new DirectoryModel();
            directoryModelAll.setPhotoDirectory_Name(Context.getString(R.string.y_photopicker_all_image));
            directoryModelAll.setPhotoDirectory_Id("ALL");

            while (Data.moveToNext()) {

                int ImageId = Data.getInt(Data.getColumnIndexOrThrow(_ID));
                String BucketId = Data.getString(Data.getColumnIndexOrThrow(BUCKET_ID));
                String Name = Data.getString(Data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                String Path = Data.getString(Data.getColumnIndexOrThrow(DATA));

                DirectoryModel photodirectory = new DirectoryModel();
                photodirectory.setPhotoDirectory_Id(BucketId);
                photodirectory.setPhotoDirectory_Name(Name);

                if (!Directory_List.contains(photodirectory)) {
                    photodirectory.setPhotoDirectory_CoverPath(Path);
                    photodirectory.addPhoto(ImageId, Path);
                    photodirectory.setPhotoDirectory_Dateadded(Data.getLong(Data.getColumnIndexOrThrow(DATE_ADDED)));
                    Directory_List.add(photodirectory);
                } else {
                    Directory_List.get(Directory_List.indexOf(photodirectory)).addPhoto(ImageId, Path);
                }
                directoryModelAll.addPhoto(ImageId, Path);
            }
            if (directoryModelAll.getPhotoPaths().size() > 0) {
                directoryModelAll.setPhotoDirectory_CoverPath(directoryModelAll.getPhotoPaths().get(0));
            }
            Directory_List.add(INDEX_ALL_PHOTOS, directoryModelAll);
            if (ResultCallback != null) {
                ResultCallback.onResultCallback(Directory_List);
            }
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    }

    public interface PhotosResultCallback { void onResultCallback(List<DirectoryModel> directories);}
}
