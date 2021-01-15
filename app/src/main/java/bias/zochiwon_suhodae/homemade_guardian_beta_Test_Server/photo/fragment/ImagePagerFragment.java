package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.fragment;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.adapter.PhotoPagerAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// PhotoPickerFragment,PhotoGridAdapter로 나열한 이미지들 중에 이미지 하나를 클릭하였을 때 확대 보기를 하기위한 Fragment이다.
// ImagePagerFragment 역시 PhotoPickerActivity 와 연결되어 있지만, 사진이 클릭하는 이벤트가 발생했을 때 Intne 된다.
//    (PhotoPickerActivity) -> (PhotoPickerFragment) -> (PhotoGridAdapter)
//                        ↘ ImagePagerFragment  -> (PhotoPagerAdapter)

public class ImagePagerFragment extends Fragment {

  private ArrayList<String> PathList;
  private ViewPager Viewpager;
  private PhotoPagerAdapter Pageradapter;

  public final static String ARG_PATH = "PATHS";
  public final static String ARG_CURRENT_ITEM = "ARG_CURRENT_ITEM";
  public final static long ANIM_DURATION = 200L;
  public final static String ARG_THUMBNAIL_TOP    = "THUMBNAIL_TOP";
  public final static String ARG_THUMBNAIL_LEFT   = "THUMBNAIL_LEFT";
  public final static String ARG_THUMBNAIL_WIDTH  = "THUMBNAIL_WIDTH";
  public final static String ARG_THUMBNAIL_HEIGHT = "THUMBNAIL_HEIGHT";
  public final static String ARG_HAS_ANIM = "HAS_ANIM";
  private int thumbnailTop    = 0;
  private int thumbnailLeft   = 0;
  private int thumbnailWidth  = 0;
  private int thumbnailHeight = 0;
  private boolean hasAnim = false;
  private final ColorMatrix ColorizerMatrix = new ColorMatrix();
  private int CurrentItem = 0;

  public static ImagePagerFragment newInstance(List<String> PathList, int Currentitem) {
    ImagePagerFragment F = new ImagePagerFragment();
    Bundle Args = new Bundle();
    Args.putStringArray(ARG_PATH, PathList.toArray(new String[PathList.size()]));
    Args.putInt(ARG_CURRENT_ITEM, Currentitem);
    Args.putBoolean(ARG_HAS_ANIM, false);
    F.setArguments(Args);
    return F;
  }

  public static ImagePagerFragment newInstance(List<String> PathList, int CurrentItem, int[] ScreenLocation, int ThumbnailWidth, int ThumbnailHeight) {
    ImagePagerFragment F = newInstance(PathList, CurrentItem);
    F.getArguments().putInt(ARG_THUMBNAIL_LEFT, ScreenLocation[0]);
    F.getArguments().putInt(ARG_THUMBNAIL_TOP, ScreenLocation[1]);
    F.getArguments().putInt(ARG_THUMBNAIL_WIDTH, ThumbnailWidth);
    F.getArguments().putInt(ARG_THUMBNAIL_HEIGHT, ThumbnailHeight);
    F.getArguments().putBoolean(ARG_HAS_ANIM, true);
    return F;
  }

  public void setPhotos(List<String> Paths, int CurrentItem) {
    this.PathList.clear();
    this.PathList.addAll(Paths);
    this.CurrentItem = CurrentItem;
    Viewpager.setCurrentItem(CurrentItem);
    Viewpager.getAdapter().notifyDataSetChanged();
    // TODO Prevent memory leaks
    Viewpager.setOffscreenPageLimit(1);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PathList = new ArrayList<>();
    Bundle bundle = getArguments();
    if (bundle != null) {
      String[] Patharr = bundle.getStringArray(ARG_PATH);
      PathList.clear();
      if (Patharr != null) {
        PathList = new ArrayList<>(Arrays.asList(Patharr));
      }
      hasAnim         = bundle.getBoolean(ARG_HAS_ANIM);
      CurrentItem     = bundle.getInt(ARG_CURRENT_ITEM);
      thumbnailTop    = bundle.getInt(ARG_THUMBNAIL_TOP);
      thumbnailLeft   = bundle.getInt(ARG_THUMBNAIL_LEFT);
      thumbnailWidth  = bundle.getInt(ARG_THUMBNAIL_WIDTH);
      thumbnailHeight = bundle.getInt(ARG_THUMBNAIL_HEIGHT);
    }
    Pageradapter = new PhotoPagerAdapter(getActivity(), PathList);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater Inflater, ViewGroup Photo_Container, Bundle savedInstanceState) {
    View Rootview = Inflater.inflate(R.layout.util_fragment_image_pager, Photo_Container, false);
    Viewpager = (ViewPager) Rootview.findViewById(R.id.Enlarge_Images);
    Viewpager.setAdapter(Pageradapter);
    Viewpager.setCurrentItem(CurrentItem);
    Viewpager.setOffscreenPageLimit(5);
    // Only run the animation if we're coming from the parent activity, not if
    // we're recreated automatically by the window manager (e.g., device rotation)
    if (savedInstanceState == null && hasAnim) {
      ViewTreeObserver Observer = Viewpager.getViewTreeObserver();
      Observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          Viewpager.getViewTreeObserver().removeOnPreDrawListener(this);
          // Figure out where the thumbnail and full size versions are, relative
          // to the screen and each other
          int[] screenLocation = new int[2];
          Viewpager.getLocationOnScreen(screenLocation);
          thumbnailLeft = thumbnailLeft - screenLocation[0];
          thumbnailTop  = thumbnailTop - screenLocation[1];
          runEnterAnimation();
          return true;
        }
      });
    }
    Viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
      @Override
      public void onPageSelected(int position) {
        hasAnim = CurrentItem == position;
      }
      @Override
      public void onPageScrollStateChanged(int state) { }
    });
    return Rootview;
  }

  /**
   * The enter animation scales the picture in from its previous thumbnail
   * size/location, colorizing it in parallel. In parallel, the background of the
   * activity is fading in. When the pictue is in place, the text description
   * drops down.
   */

  // 앨범 나열에서 이미지를 한번 눌러서 이미지의 큰 화면의 View로 볼 수 있게 하는 함수
  private void runEnterAnimation() {
    final long Duration = ANIM_DURATION;
    // Set starting values for properties we're going to animate. These
    // values scale and position the full size version down to the thumbnail
    // size/location, from which we'll animate it back up
    ViewHelper.setPivotX(Viewpager, 0);
    ViewHelper.setPivotY(Viewpager, 0);
    ViewHelper.setScaleX(Viewpager, (float) thumbnailWidth / Viewpager.getWidth());
    ViewHelper.setScaleY(Viewpager, (float) thumbnailHeight / Viewpager.getHeight());
    ViewHelper.setTranslationX(Viewpager, thumbnailLeft);
    ViewHelper.setTranslationY(Viewpager, thumbnailTop);
    // Animate scale and translation to go from thumbnail to full size
    ViewPropertyAnimator.animate(Viewpager)
        .setDuration(Duration)
        .scaleX(1)
        .scaleY(1)
        .translationX(0)
        .translationY(0)
        .setInterpolator(new DecelerateInterpolator());
    // Fade in the black background
    ObjectAnimator Anim = ObjectAnimator.ofInt(Viewpager.getBackground(), "alpha", 0, 255);
    Anim.setDuration(Duration);
    Anim.start();
    // Animate a color filter to take the image from grayscale to full color.
    // This happens in parallel with the image scaling and moving into place.
    ObjectAnimator Colorizer = ObjectAnimator.ofFloat(ImagePagerFragment.this, "saturation", 0, 1);
    Colorizer.setDuration(Duration);
    Colorizer.start();
  }

  /**
   * The exit animation is basically a reverse of the enter animation, except that if
   * the orientation has changed we simply scale the picture back into the center of
   * the screen.
   *
   * @param endAction This action gets run after the animation completes (this is
   * when we actually switch activities)
   */
  // 이미지를 한번 더 눌러서 Backpressed이벤트의 함수
  public void runExitAnimation(final Runnable endAction) {
    if (!getArguments().getBoolean(ARG_HAS_ANIM, false) || !hasAnim) {
      endAction.run();
      return;
    }
    final long Duration = ANIM_DURATION;
    // Animate image back to thumbnail size/location
    ViewPropertyAnimator.animate(Viewpager)
        .setDuration(Duration)
        .setInterpolator(new AccelerateInterpolator())
        .scaleX((float) thumbnailWidth / Viewpager.getWidth())
        .scaleY((float) thumbnailHeight / Viewpager.getHeight())
        .translationX(thumbnailLeft)
        .translationY(thumbnailTop)
        .setListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) { }
          @Override
          public void onAnimationEnd(Animator animation) { endAction.run(); }
          @Override
          public void onAnimationCancel(Animator animation) { }
          @Override
          public void onAnimationRepeat(Animator animation) { }
        });
    // Fade out background
    ObjectAnimator Anim = ObjectAnimator.ofInt(Viewpager.getBackground(), "alpha", 0);
    Anim.setDuration(Duration);
    Anim.start();
    // Animate a color filter to take the image back to grayscale,
    // in parallel with the image scaling and moving into place.
    ObjectAnimator Colorizer =
        ObjectAnimator.ofFloat(ImagePagerFragment.this, "saturation", 1, 0);
    Colorizer.setDuration(Duration);
    Colorizer.start();
  }

  /**
   * This is called by the colorizing animator. It sets a saturation factor that is then
   * passed onto a filter on the picture's drawable.
   * @param Value saturation
   */
  public void setSaturation(float Value) {
    ColorizerMatrix.setSaturation(Value);
    ColorMatrixColorFilter colorizerFilter = new ColorMatrixColorFilter(ColorizerMatrix);
    Viewpager.getBackground().setColorFilter(colorizerFilter);
  }

  public ViewPager getViewPager() {
    return Viewpager;
  }

  public ArrayList<String> getPathList() { return PathList; }

  public int getCurrentItem() {
    return Viewpager.getCurrentItem();
  }
}