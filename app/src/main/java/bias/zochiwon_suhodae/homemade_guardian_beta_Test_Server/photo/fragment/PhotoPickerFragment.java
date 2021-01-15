package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.adapter.PhotoGridAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.activity.PhotoPickerActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.adapter.PopupDirectoryListAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.PhotoModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.DirectoryModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event.OnPhotoClickListener;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.ImageCaptureManager;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.MediaStoreHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

// PhotoPickerActivity와 연결된 Fragment로 앨범의 이미지들을 나열하는 기능을한다.
//    (PhotoPickerActivity) -> PhotoPickerFragment -> (PhotoGridAdapter)
//                        ↘ (ImagePagerFragment)  -> (PhotoPagerAdapter)

public class PhotoPickerFragment extends Fragment {

    private Context Context = null;
    private Activity Activity = null;

    private ImageCaptureManager Capturemanager;
    private PhotoGridAdapter PhotogridAdapter;

    private PopupDirectoryListAdapter PopupdirectoryListAdapter;
    private List<DirectoryModel> DirectoryList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context = this.getActivity().getApplicationContext();
        Activity = this.getActivity();
        DirectoryList = new ArrayList<>();
        Capturemanager = new ImageCaptureManager(getActivity());
        Bundle mediaStoreArgs = new Bundle();
        if (getActivity() instanceof PhotoPickerActivity) {
            mediaStoreArgs.putBoolean(PhotoPickerActivity.EXTRA_SHOW_GIF, ((PhotoPickerActivity) getActivity()).isShowGif());
        }
        MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
                new MediaStoreHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<DirectoryModel> dirs) {
                        DirectoryList.clear();
                        DirectoryList.addAll(dirs);
                        PhotogridAdapter.notifyDataSetChanged();
                        PopupdirectoryListAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup Photo_Container, Bundle savedInstanceState) {
        setRetainInstance(true);
        final View Rootview = inflater.inflate(R.layout.util_fragment_photo_picker, Photo_Container, false);
        PhotogridAdapter = new PhotoGridAdapter(getActivity(), DirectoryList, ((PhotoPickerActivity)getActivity()).IsCheckBoxOnly);
        PopupdirectoryListAdapter = new PopupDirectoryListAdapter(getActivity(), DirectoryList);

        RecyclerView recyclerView = (RecyclerView) Rootview.findViewById(R.id.Arrange_Images);
        StaggeredGridLayoutManager LayoutManager = new StaggeredGridLayoutManager(((PhotoPickerActivity)getActivity()).MaxGrideItemCount, OrientationHelper.VERTICAL);
        LayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setAdapter(PhotogridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final Button SwitchDirectory_Button = (Button) Rootview.findViewById(R.id.SwitchDirectory_Button);

        final ListPopupWindow ListPopupWindow = new ListPopupWindow(getActivity());
        ListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        ListPopupWindow.setAnchorView(SwitchDirectory_Button);
        ListPopupWindow.setAdapter(PopupdirectoryListAdapter);
        ListPopupWindow.setModal(true);
        ListPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        ListPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
        ListPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_popup_menu));

        ListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListPopupWindow.dismiss();
                DirectoryModel directory = DirectoryList.get(position);
                SwitchDirectory_Button.setText(directory.getPhotoDirectory_Name());
                PhotogridAdapter.setCurrentDirectoryIndex(position);
                PhotogridAdapter.notifyDataSetChanged();
            }
        });

        PhotogridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                final int Index = showCamera ? position - 1 : position;
                List<String> PhotoList = PhotogridAdapter.getCurrentPhotoPaths();
                int[] ScreenLocation = new int[2];
                v.getLocationOnScreen(ScreenLocation);
                ImagePagerFragment ImagepagerFragment = ImagePagerFragment.newInstance(PhotoList, Index, ScreenLocation, v.getWidth(), v.getHeight());
                ((PhotoPickerActivity) getActivity()).addImagePagerFragment(ImagepagerFragment);
            }
        });
        PhotogridAdapter.setOnCameraClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent Intent_ImageCapture = Capturemanager.dispatchTakePictureIntent();
                    startActivityForResult(Intent_ImageCapture, ImageCaptureManager.REQUEST_TAKE_PHOTO);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        SwitchDirectory_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ListPopupWindow.isShowing()) {
                    ListPopupWindow.dismiss();
                } else if (!getActivity().isFinishing()) {
                    ListPopupWindow.setHeight(Math.round(Rootview.getHeight() * 0.8f));
                    ListPopupWindow.show();
                }
            }
        });
        return Rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Capturemanager.galleryAddPic();
            if (DirectoryList.size() > 0) {
                String Path = Capturemanager.getCurrentPhotoPath();
                DirectoryModel directory = DirectoryList.get(MediaStoreHelper.INDEX_ALL_PHOTOS);
                directory.getPhotoModelList().add(MediaStoreHelper.INDEX_ALL_PHOTOS, new PhotoModel(Path.hashCode(), Path));
                directory.setPhotoDirectory_CoverPath(Path);
//                String temp_patch = getLastPhotoPath();
//                temp_patch = temp_patch.replace(".jpg", "");
//                String newFileName = new File(Path).getName();
//
//                if (Path.contains(temp_patch)) {
//                }
                PhotogridAdapter.notifyDataSetChanged();
            }
        }else{
            PhotogridAdapter.notifyDataSetChanged();
        }
    }

    public PhotoGridAdapter getPhotogridAdapter() {
        return PhotogridAdapter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Capturemanager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Capturemanager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        return PhotogridAdapter.getSelectedPhotoPaths();
    }

    public String getLastPhotoPath() {
        final String[] IMAGE_PROJECTION = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Thumbnails.DATA};
        final Uri UriImage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String CameraPath = "'";
        String FileName = "";
        try {
            final Cursor CursorImages = Activity.getContentResolver().query(UriImage, IMAGE_PROJECTION, null, null, null);
            if (CursorImages != null && CursorImages.moveToLast()) {
                CameraPath = CursorImages.getString(0);
                CursorImages.close();
                FileName = new File(CameraPath).getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FileName;
    }
}
