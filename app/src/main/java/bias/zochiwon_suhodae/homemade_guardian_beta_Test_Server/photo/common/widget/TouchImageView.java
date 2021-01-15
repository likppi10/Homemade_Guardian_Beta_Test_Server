/*
 * TouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * Updated By: Babay88
 * Updated By: @ipsilondev
 * Updated By: hank-cp
 * Updated By: singpolyma
 * -------------------
 * Extends Android ImageView to include pinch zooming, panning, fling and double tap zoom.
 */

package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import androidx.appcompat.widget.AppCompatImageView;

// PhotoPagerAdapter에서 나타낸 이미지 한장을 나타내는 화면에서 여러가지 기능들을 구현 할 수 있도록 만들어 놓은 것이다.
// 더블탭으로 이미지 확대 축소헤서 보기, 이미지 화면의 폭에 맞추기, 이미지의 높이가 더 크면 스크롤로 볼 수 있게 하기,
public class TouchImageView extends AppCompatImageView {
	
	private static final String DEBUG = "DEBUG";
	
	//
	// SuperMin and SuperMax multipliers. Determine how much the image can be
	// zoomed below or above the zoom boundaries, before animating back to the
	// min/max zoom boundary.
	//
	private static final float SUPER_MIN_MULTIPLIER = .75f;
	private static final float SUPER_MAX_MULTIPLIER = 1.25f;

    //
    // Scale of image ranges from minScale to maxScale, where minScale == 1
    // when the image is stretched to fit view.
    //
    private float NormalizedScale;
    
    //
    // Matrix applied to image. MSCALE_X and MSCALE_Y should always be equal.
    // MTRANS_X and MTRANS_Y are the other values used. prevMatrix is the matrix
    // saved prior to the screen rotating.
    //
	private Matrix Matrix, PrevMatrix;

    private static enum State { NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM };
    private State State;

    private float MinScale;
    private float MaxScale;
    private float SuperMinScale;
    private float SuperMaxScale;
    private float[] M;
    
    private Context MContext;
    private Fling Fling;
    
    private ScaleType ScaleType;
    
    private boolean ImageRenderedAtLeastOnce;
    private boolean OnDrawReady;
    
    private ZoomVariables DelayedZoomVariables;

    //
    // Size of view and previous view size (ie before rotation)
    //
    private int ViewWidth, ViewHeight, PrevViewWidth, PrevViewHeight;
    
    //
    // Size of image when it is stretched to fit view. Before and After rotation.
    //
    private float MatchViewWidth, MatchViewHeight, PrevMatchViewWidth, PrevMatchViewHeight;
    
    private ScaleGestureDetector MScaleDetector;
    private GestureDetector MGestureDetector;
    private GestureDetector.OnDoubleTapListener DoubleTapListener = null;
    private OnTouchListener UserTouchListener = null;
    private OnTouchImageViewListener TouchImageViewListener = null;

    public TouchImageView(Context Context) {
        super(Context);
        sharedConstructing(Context);
    }

    public TouchImageView(Context MContext, AttributeSet attrs) {
        super(MContext, attrs);
        sharedConstructing(MContext);
    }
    
    public TouchImageView(Context MContext, AttributeSet attrs, int defStyle) {
    	super(MContext, attrs, defStyle);
    	sharedConstructing(MContext);
    }
    
    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.MContext = context;
        MScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        MGestureDetector = new GestureDetector(context, new GestureListener());
        Matrix = new Matrix();
        PrevMatrix = new Matrix();
        M = new float[9];
        NormalizedScale = 1;
        if (ScaleType == null) {
        	ScaleType = ScaleType.FIT_CENTER;
        }
        MinScale = 1;
        MaxScale = 3;
        SuperMinScale = SUPER_MIN_MULTIPLIER * MinScale;
        SuperMaxScale = SUPER_MAX_MULTIPLIER * MaxScale;
        setImageMatrix(Matrix);
        setScaleType(ScaleType.MATRIX);
        setState(State.NONE);
        OnDrawReady = false;
        super.setOnTouchListener(new PrivateOnTouchListener());
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        UserTouchListener = l;
    }
    
    public void setOnTouchImageViewListener(OnTouchImageViewListener l) {
    	TouchImageViewListener = l;
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener l) {
        DoubleTapListener = l;
    }

    @Override
    public void setImageResource(int resId) {
    	super.setImageResource(resId);
    	savePreviousImageValues();
    	fitImageToView();
    }
    
    @Override
    public void setImageBitmap(Bitmap bm) {
    	super.setImageBitmap(bm);
    	savePreviousImageValues();
    	fitImageToView();
    }
    
    @Override
    public void setImageDrawable(Drawable drawable) {
    	super.setImageDrawable(drawable);
    	savePreviousImageValues();
    	fitImageToView();
    }
    
    @Override
    public void setImageURI(Uri uri) {
    	super.setImageURI(uri);
    	savePreviousImageValues();
    	fitImageToView();
    }
    
    @Override
    public void setScaleType(ScaleType type) {
    	if (type == ScaleType.FIT_START || type == ScaleType.FIT_END) {
    		throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
    	}
    	if (type == ScaleType.MATRIX) {
    		super.setScaleType(ScaleType.MATRIX);
    		
    	} else {
    		ScaleType = type;
    		if (OnDrawReady) {
    			//
    			// If the image is already rendered, scaleType has been called programmatically
    			// and the TouchImageView should be updated with the new scaleType.
    			//
    			setZoom(this);
    		}
    	}
    }
    
    @Override
    public ScaleType getScaleType() {
    	return ScaleType;
    }
    
    /**
     * Returns false if image is in initial, unzoomed state. False, otherwise.
     * @return true if image is zoomed
     */
    public boolean isZoomed() {
    	return NormalizedScale != 1;
    }
    
    /**
     * Return a Rect representing the zoomed image.
     * @return rect representing zoomed image
     */
    public RectF getZoomedRect() {
    	if (ScaleType == ScaleType.FIT_XY) {
    		throw new UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
    	}
    	PointF topLeft = transformCoordTouchToBitmap(0, 0, true);
    	PointF bottomRight = transformCoordTouchToBitmap(ViewWidth, ViewHeight, true);
    	
    	float w = getDrawable().getIntrinsicWidth();
    	float h = getDrawable().getIntrinsicHeight();
    	return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
    }
    
    /**
     * Save the current matrix and view dimensions
     * in the prevMatrix and prevView variables.
     */
    private void savePreviousImageValues() {
    	if (Matrix != null && ViewHeight != 0 && ViewWidth != 0) {
	    	Matrix.getValues(M);
	    	PrevMatrix.setValues(M);
	    	PrevMatchViewHeight = MatchViewHeight;
	        PrevMatchViewWidth = MatchViewWidth;
	        PrevViewHeight = ViewHeight;
	        PrevViewWidth = ViewWidth;
    	}
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
    	Bundle bundle = new Bundle();
    	bundle.putParcelable("instanceState", super.onSaveInstanceState());
    	bundle.putFloat("saveScale", NormalizedScale);
    	bundle.putFloat("matchViewHeight", MatchViewHeight);
    	bundle.putFloat("matchViewWidth", MatchViewWidth);
    	bundle.putInt("viewWidth", ViewWidth);
    	bundle.putInt("viewHeight", ViewHeight);
    	Matrix.getValues(M);
    	bundle.putFloatArray("matrix", M);
    	bundle.putBoolean("imageRendered", ImageRenderedAtLeastOnce);
    	return bundle;
    }
    
    @Override
    public void onRestoreInstanceState(Parcelable state) {
      	if (state instanceof Bundle) {
	        Bundle bundle = (Bundle) state;
	        NormalizedScale = bundle.getFloat("saveScale");
	        M = bundle.getFloatArray("matrix");
	        PrevMatrix.setValues(M);
	        PrevMatchViewHeight = bundle.getFloat("matchViewHeight");
	        PrevMatchViewWidth = bundle.getFloat("matchViewWidth");
	        PrevViewHeight = bundle.getInt("viewHeight");
	        PrevViewWidth = bundle.getInt("viewWidth");
	        ImageRenderedAtLeastOnce = bundle.getBoolean("imageRendered");
	        super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
	        return;
      	}

      	super.onRestoreInstanceState(state);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	OnDrawReady = true;
    	ImageRenderedAtLeastOnce = true;
    	if (DelayedZoomVariables != null) {
    		setZoom(DelayedZoomVariables.Scale, DelayedZoomVariables.FocusX, DelayedZoomVariables.FocusY, DelayedZoomVariables.ScaleType);
    		DelayedZoomVariables = null;
    	}
    	super.onDraw(canvas);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	savePreviousImageValues();
    }
    
    /**
     * Get the max zoom multiplier.
     * @return max zoom multiplier.
     */
    public float getMaxZoom() {
    	return MaxScale;
    }

    /**
     * Set the max zoom multiplier. Default value: 3.
     * @param max max zoom multiplier.
     */
    public void setMaxZoom(float max) {
        MaxScale = max;
        SuperMaxScale = SUPER_MAX_MULTIPLIER * MaxScale;
    }
    
    /**
     * Get the min zoom multiplier.
     * @return min zoom multiplier.
     */
    public float getMinZoom() {
    	return MinScale;
    }
    
    /**
     * Get the current zoom. This is the zoom relative to the initial
     * scale, not the original resource.
     * @return current zoom multiplier.
     */
    public float getCurrentZoom() {
    	return NormalizedScale;
    }
    
    /**
     * Set the min zoom multiplier. Default value: 1.
     * @param min min zoom multiplier.
     */
    public void setMinZoom(float min) {
    	MinScale = min;
    	SuperMinScale = SUPER_MIN_MULTIPLIER * MinScale;
    }
    
    /**
     * Reset zoom and translation to initial state.
     */
    public void resetZoom() {
    	NormalizedScale = 1;
    	fitImageToView();
    }
    
    /**
     * Set zoom to the specified scale. Image will be centered by default.
     * @param scale
     */
    public void setZoom(float scale) {
    	setZoom(scale, 0.5f, 0.5f);
    }
    
    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left 
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     * @param scale
     * @param focusX
     * @param focusY
     */
    public void setZoom(float scale, float focusX, float focusY) {
    	setZoom(scale, focusX, focusY, ScaleType);
    }
    
    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left 
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     * @param scale
     * @param focusX
     * @param focusY
     * @param scaleType
     */
    public void setZoom(float scale, float focusX, float focusY, ScaleType scaleType) {
    	//
    	// setZoom can be called before the image is on the screen, but at this point, 
    	// image and view sizes have not yet been calculated in onMeasure. Thus, we should
    	// delay calling setZoom until the view has been measured.
    	//
    	if (!OnDrawReady) {
    		DelayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
    		return;
    	}
    	
    	if (scaleType != ScaleType) {
    		setScaleType(scaleType);
    	}
    	resetZoom();
    	scaleImage(scale, ViewWidth / 2, ViewHeight / 2, true);
    	Matrix.getValues(M);
    	M[Matrix.MTRANS_X] = -((focusX * getImageWidth()) - (ViewWidth * 0.5f));
    	M[Matrix.MTRANS_Y] = -((focusY * getImageHeight()) - (ViewHeight * 0.5f));
    	Matrix.setValues(M);
    	fixTrans();
    	setImageMatrix(Matrix);
    }
    
    /**
     * Set zoom parameters equal to another TouchImageView. Including scale, position,
     * and ScaleType.
     * @param TouchImageView
     */
    public void setZoom(TouchImageView img) {
    	PointF center = img.getScrollPosition();
    	setZoom(img.getCurrentZoom(), center.x, center.y, img.getScaleType());
    }
    
    /**
     * Return the point at the center of the zoomed image. The PointF coordinates range
     * in value between 0 and 1 and the focus point is denoted as a fraction from the left 
     * and top of the view. For example, the top left corner of the image would be (0, 0). 
     * And the bottom right corner would be (1, 1).
     * @return PointF representing the scroll position of the zoomed image.
     */
    public PointF getScrollPosition() {
    	Drawable drawable = getDrawable();
    	if (drawable == null) {
    		return null;
    	}
    	int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        
        PointF point = transformCoordTouchToBitmap(ViewWidth / 2, ViewHeight / 2, true);
        point.x /= drawableWidth;
        point.y /= drawableHeight;
        return point;
    }
    
    /**
     * Set the focus point of the zoomed image. The focus points are denoted as a fraction from the
     * left and top of the view. The focus points can range in value between 0 and 1. 
     * @param focusX
     * @param focusY
     */
    public void setScrollPosition(float focusX, float focusY) {
    	setZoom(NormalizedScale, focusX, focusY);
    }
    
    /**
     * Performs boundary checking and fixes the image matrix if it 
     * is out of bounds.
     */
    private void fixTrans() {
        Matrix.getValues(M);
        float transX = M[Matrix.MTRANS_X];
        float transY = M[Matrix.MTRANS_Y];
        
        float fixTransX = getFixTrans(transX, ViewWidth, getImageWidth());
        float fixTransY = getFixTrans(transY, ViewHeight, getImageHeight());
        
        if (fixTransX != 0 || fixTransY != 0) {
            Matrix.postTranslate(fixTransX, fixTransY);
        }
    }
    
    /**
     * When transitioning from zooming from focus to zoom from center (or vice versa)
     * the image can become unaligned within the view. This is apparent when zooming
     * quickly. When the content size is less than the view size, the content will often
     * be centered incorrectly within the view. fixScaleTrans first calls fixTrans() and 
     * then makes sure the image is centered correctly within the view.
     */
    private void fixScaleTrans() {
    	fixTrans();
    	Matrix.getValues(M);
    	if (getImageWidth() < ViewWidth) {
    		M[Matrix.MTRANS_X] = (ViewWidth - getImageWidth()) / 2;
    	}
    	
    	if (getImageHeight() < ViewHeight) {
    		M[Matrix.MTRANS_Y] = (ViewHeight - getImageHeight()) / 2;
    	}
    	Matrix.setValues(M);
    }

    private float getFixTrans(float Trans, float ViewSize, float ContentSize) {
        float MinTrans, MaxTrans;

        if (ContentSize <= ViewSize) {
            MinTrans = 0;
            MaxTrans = ViewSize - ContentSize;
            
        } else {
            MinTrans = ViewSize - ContentSize;
            MaxTrans = 0;
        }

        if (Trans < MinTrans)
            return -Trans + MinTrans;
        if (Trans > MaxTrans)
            return -Trans + MaxTrans;
        return 0;
    }
    
    private float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }
    
    private float getImageWidth() {
    	return MatchViewWidth * NormalizedScale;
    }
    
    private float getImageHeight() {
    	return MatchViewHeight * NormalizedScale;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable Drawable = getDrawable();
        if (Drawable == null || Drawable.getIntrinsicWidth() == 0 || Drawable.getIntrinsicHeight() == 0) {
        	setMeasuredDimension(0, 0);
        	return;
        }
        
        int DrawableWidth = Drawable.getIntrinsicWidth();
        int DrawableHeight = Drawable.getIntrinsicHeight();
        int WidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int WidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int HeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int HeightMode = MeasureSpec.getMode(heightMeasureSpec);
        ViewWidth = setViewSize(WidthMode, WidthSize, DrawableWidth);
        ViewHeight = setViewSize(HeightMode, HeightSize, DrawableHeight);
        
        //
        // Set view dimensions
        //
        setMeasuredDimension(ViewWidth, ViewHeight);
        
        //
        // Fit content within view
        //
        fitImageToView();
    }
    
    /**
     * If the normalizedScale is equal to 1, then the image is made to fit the screen. Otherwise,
     * it is made to fit the screen according to the dimensions of the previous image matrix. This
     * allows the image to maintain its zoom after rotation.
     */
    private void fitImageToView() {
    	Drawable Drawable = getDrawable();
        if (Drawable == null || Drawable.getIntrinsicWidth() == 0 || Drawable.getIntrinsicHeight() == 0) {
        	return;
        }
        if (Matrix == null || PrevMatrix == null) {
        	return;
        }
        
        int DrawableWidth = Drawable.getIntrinsicWidth();
        int DrawableHeight = Drawable.getIntrinsicHeight();
    	
    	//
    	// Scale image for view
    	//
        float ScaleX = (float) ViewWidth / DrawableWidth;
        float ScaleY = (float) ViewHeight / DrawableHeight;
        
        switch (ScaleType) {
        case CENTER:
        	ScaleX = ScaleY = 1;
        	break;
        	
        case CENTER_CROP:
        	ScaleX = ScaleY = Math.max(ScaleX, ScaleY);
        	break;
        	
        case CENTER_INSIDE:
        	ScaleX = ScaleY = Math.min(1, Math.min(ScaleX, ScaleY));
        	
        case FIT_CENTER:
        	ScaleX = ScaleY = Math.min(ScaleX, ScaleY);
        	break;
        	
        case FIT_XY:
        	break;
        	
    	default:
    		//
    		// FIT_START and FIT_END not supported
    		//
    		throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
        	
        }

        //
        // Center the image
        //
        float RedundantXSpace = ViewWidth - (ScaleX * DrawableWidth);
        float RedundantYSpace = ViewHeight - (ScaleY * DrawableHeight);
        MatchViewWidth = ViewWidth - RedundantXSpace;
        MatchViewHeight = ViewHeight - RedundantYSpace;
        if (!isZoomed() && !ImageRenderedAtLeastOnce) {
        	//
        	// Stretch and center image to fit view
        	//
        	Matrix.setScale(ScaleX, ScaleY);
        	Matrix.postTranslate(RedundantXSpace / 2, RedundantYSpace / 2);
        	NormalizedScale = 1;
        	
        } else {
        	//
        	// These values should never be 0 or we will set viewWidth and viewHeight
        	// to NaN in translateMatrixAfterRotate. To avoid this, call savePreviousImageValues
        	// to set them equal to the current values.
        	//
        	if (PrevMatchViewWidth == 0 || PrevMatchViewHeight == 0) {
        		savePreviousImageValues();
        	}
        	
        	PrevMatrix.getValues(M);
        	
        	//
        	// Rescale Matrix after rotation
        	//
        	M[Matrix.MSCALE_X] = MatchViewWidth / DrawableWidth * NormalizedScale;
        	M[Matrix.MSCALE_Y] = MatchViewHeight / DrawableHeight * NormalizedScale;
        	
        	//
        	// TransX and TransY from previous matrix
        	//
            float TransX = M[Matrix.MTRANS_X];
            float TransY = M[Matrix.MTRANS_Y];
            
            //
            // Width
            //
            float PrevActualWidth = PrevMatchViewWidth * NormalizedScale;
            float ActualWidth = getImageWidth();
            translateMatrixAfterRotate(Matrix.MTRANS_X, TransX, PrevActualWidth, ActualWidth, PrevViewWidth, ViewWidth, DrawableWidth);
            
            //
            // Height
            //
            float PrevActualHeight = PrevMatchViewHeight * NormalizedScale;
            float ActualHeight = getImageHeight();
            translateMatrixAfterRotate(Matrix.MTRANS_Y, TransY, PrevActualHeight, ActualHeight, PrevViewHeight, ViewHeight, DrawableHeight);
            
            //
            // Set the matrix to the adjusted scale and translate values.
            //
            Matrix.setValues(M);
        }
        fixTrans();
        setImageMatrix(Matrix);
    }
    
    /**
     * Set view dimensions based on layout params
     * 
     * @param mode 
     * @param size
     * @param drawableWidth
     * @return
     */
    private int setViewSize(int mode, int size, int drawableWidth) {
    	int ViewSize;
    	switch (mode) {
		case MeasureSpec.EXACTLY:
			ViewSize = size;
			break;
			
		case MeasureSpec.AT_MOST:
			ViewSize = Math.min(drawableWidth, size);
			break;
			
		case MeasureSpec.UNSPECIFIED:
			ViewSize = drawableWidth;
			break;
			
		default:
			ViewSize = size;
		 	break;
		}
    	return ViewSize;
    }
    
    /**
     * After rotating, the matrix needs to be translated. This function finds the area of image 
     * which was previously centered and adjusts translations so that is again the center, post-rotation.
     * 
     * @param axis Matrix.MTRANS_X or Matrix.MTRANS_Y
     * @param trans the value of trans in that axis before the rotation
     * @param prevImageSize the width/height of the image before the rotation
     * @param imageSize width/height of the image after rotation
     * @param prevViewSize width/height of view before rotation
     * @param viewSize width/height of view after rotation
     * @param drawableSize width/height of drawable
     */
    private void translateMatrixAfterRotate(int axis, float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize) {
    	if (imageSize < viewSize) {
        	//
        	// The width/height of image is less than the view's width/height. Center it.
        	//
        	M[axis] = (viewSize - (drawableSize * M[Matrix.MSCALE_X])) * 0.5f;
        	
        } else if (trans > 0) {
        	//
        	// The image is larger than the view, but was not before rotation. Center it.
        	//
        	M[axis] = -((imageSize - viewSize) * 0.5f);
        	
        } else {
        	//
        	// Find the area of the image which was previously centered in the view. Determine its distance
        	// from the left/top side of the view as a fraction of the entire image's width/height. Use that percentage
        	// to calculate the trans in the new view width/height.
        	//
        	float percentage = (Math.abs(trans) + (0.5f * prevViewSize)) / prevImageSize;
        	M[axis] = -((percentage * imageSize) - (viewSize * 0.5f));
        }
    }
    
    private void setState(State state) {
    	this.State = state;
    }
    
    public boolean canScrollHorizontallyFroyo(int direction) {
        return canScrollHorizontally(direction);
    }
    
    @Override
    public boolean canScrollHorizontally(int direction) {
    	Matrix.getValues(M);
    	float x = M[Matrix.MTRANS_X];
    	
    	if (getImageWidth() < ViewWidth) {
    		return false;
    		
    	} else if (x >= -1 && direction < 0) {
    		return false;
    		
    	} else if (Math.abs(x) + ViewWidth + 1 >= getImageWidth() && direction > 0) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Gesture Listener detects a single click or long click and passes that on
     * to the view's listener.
     * @author Ortiz
     *
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    	
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if(DoubleTapListener != null) {
            	return DoubleTapListener.onSingleTapConfirmed(e);
            }
        	return performClick();
        }
        
        @Override
        public void onLongPress(MotionEvent e)
        {
        	performLongClick();
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
        	if (Fling != null) {
        		//
        		// If a previous fling is still active, it should be cancelled so that two flings
        		// are not run simultaenously.
        		//
        		Fling.cancelFling();
        	}
        	Fling = new Fling((int) velocityX, (int) velocityY);
        	compatPostOnAnimation(Fling);
        	return super.onFling(e1, e2, velocityX, velocityY);
        }
        
        @Override
        public boolean onDoubleTap(MotionEvent e) {
        	boolean consumed = false;
            if(DoubleTapListener != null) {
            	consumed = DoubleTapListener.onDoubleTap(e);
            }
        	if (State == State.NONE) {
	        	float targetZoom = (NormalizedScale == MinScale) ? MaxScale : MinScale;
	        	DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, e.getX(), e.getY(), false);
	        	compatPostOnAnimation(doubleTap);
	        	consumed = true;
        	}
        	return consumed;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if(DoubleTapListener != null) {
            	return DoubleTapListener.onDoubleTapEvent(e);
            }
            return false;
        }
    }
    
    public interface OnTouchImageViewListener {
    	public void onMove();
    }
    
    /**
     * Responsible for all touch events. Handles the heavy lifting of drag and also sends
     * touch events to Scale Detector and Gesture Detector.
     * @author Ortiz
     *
     */
    private class PrivateOnTouchListener implements OnTouchListener {
    	
    	//
        // Remember last point position for dragging
        //
        private PointF Last = new PointF();
    	
    	@Override
        public boolean onTouch(View v, MotionEvent event) {
            MScaleDetector.onTouchEvent(event);
            MGestureDetector.onTouchEvent(event);
            PointF Curr = new PointF(event.getX(), event.getY());
            
            if (State == State.NONE || State == State.DRAG || State == State.FLING) {
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                	Last.set(Curr);
	                    if (Fling != null)
	                    	Fling.cancelFling();
	                    setState(State.DRAG);
	                    break;
	                    
	                case MotionEvent.ACTION_MOVE:
	                    if (State == State.DRAG) {
	                        float deltaX = Curr.x - Last.x;
	                        float deltaY = Curr.y - Last.y;
	                        float fixTransX = getFixDragTrans(deltaX, ViewWidth, getImageWidth());
	                        float fixTransY = getFixDragTrans(deltaY, ViewHeight, getImageHeight());
	                        Matrix.postTranslate(fixTransX, fixTransY);
	                        fixTrans();
	                        Last.set(Curr.x, Curr.y);
	                    }
	                    break;
	
	                case MotionEvent.ACTION_UP:
	                case MotionEvent.ACTION_POINTER_UP:
	                    setState(State.NONE);
	                    break;
	            }
            }
            
            setImageMatrix(Matrix);
            
            //
    		// User-defined OnTouchListener
    		//
    		if(UserTouchListener != null) {
    			UserTouchListener.onTouch(v, event);
    		}
            
    		//
    		// OnTouchImageViewListener is set: TouchImageView dragged by user.
    		//
    		if (TouchImageViewListener != null) {
    			TouchImageViewListener.onMove();
    		}
    		
            //
            // indicate event was handled
            //
            return true;
        }
    }

    /**
     * ScaleListener detects user two finger scaling and scales image.
     * @author Ortiz
     *
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            setState(State.ZOOM);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
        	scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);
        	
        	//
        	// OnTouchImageViewListener is set: TouchImageView pinch zoomed by user.
        	//
        	if (TouchImageViewListener != null) {
        		TouchImageViewListener.onMove();
        	}
            return true;
        }
        
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        	super.onScaleEnd(detector);
        	setState(State.NONE);
        	boolean animateToZoomBoundary = false;
        	float targetZoom = NormalizedScale;
        	if (NormalizedScale > MaxScale) {
        		targetZoom = MaxScale;
        		animateToZoomBoundary = true;
        		
        	} else if (NormalizedScale < MinScale) {
        		targetZoom = MinScale;
        		animateToZoomBoundary = true;
        	}
        	
        	if (animateToZoomBoundary) {
	        	DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, ViewWidth / 2, ViewHeight / 2, true);
	        	compatPostOnAnimation(doubleTap);
        	}
        }
    }
    
    private void scaleImage(double DeltaScale, float FocusX, float FocusY, boolean StretchImageToSuper) {
    	
    	float LowerScale, UpperScale;
    	if (StretchImageToSuper) {
    		LowerScale = SuperMinScale;
    		UpperScale = SuperMaxScale;
    		
    	} else {
    		LowerScale = MinScale;
    		UpperScale = MaxScale;
    	}
    	
    	float origScale = NormalizedScale;
        NormalizedScale *= DeltaScale;
        if (NormalizedScale > UpperScale) {
            NormalizedScale = UpperScale;
            DeltaScale = UpperScale / origScale;
        } else if (NormalizedScale < LowerScale) {
            NormalizedScale = LowerScale;
            DeltaScale = LowerScale / origScale;
        }
        
        Matrix.postScale((float) DeltaScale, (float) DeltaScale, FocusX, FocusY);
        fixScaleTrans();
    }
    
    /**
     * DoubleTapZoom calls a series of runnables which apply
     * an animated zoom in/out graphic to the image.
     * @author Ortiz
     *
     */
    private class DoubleTapZoom implements Runnable {
    	
    	private long StartTime;
    	private static final float ZOOM_TIME = 500;
    	private float StartZoom, TargetZoom;
    	private float BitmapX, BitmapY;
    	private boolean StretchImageToSuper;
    	private AccelerateDecelerateInterpolator Interpolator = new AccelerateDecelerateInterpolator();
    	private PointF StartTouch;
    	private PointF EndTouch;

    	DoubleTapZoom(float TargetZoom, float FocusX, float FocusY, boolean StretchImageToSuper) {
    		setState(State.ANIMATE_ZOOM);
    		StartTime = System.currentTimeMillis();
    		this.StartZoom = NormalizedScale;
    		this.TargetZoom = TargetZoom;
    		this.StretchImageToSuper = StretchImageToSuper;
    		PointF bitmapPoint = transformCoordTouchToBitmap(FocusX, FocusY, false);
    		this.BitmapX = bitmapPoint.x;
    		this.BitmapY = bitmapPoint.y;
    		
    		//
    		// Used for translating image during scaling
    		//
    		StartTouch = transformCoordBitmapToTouch(BitmapX, BitmapY);
    		EndTouch = new PointF(ViewWidth / 2, ViewHeight / 2);
    	}

		@Override
		public void run() {
			float T = interpolate();
			double DeltaScale = calculateDeltaScale(T);
			scaleImage(DeltaScale, BitmapX, BitmapY, StretchImageToSuper);
			translateImageToCenterTouchPosition(T);
			fixScaleTrans();
			setImageMatrix(Matrix);
			
			//
			// OnTouchImageViewListener is set: double tap runnable updates listener
			// with every frame.
			//
			if (TouchImageViewListener != null) {
				TouchImageViewListener.onMove();
			}
			
			if (T < 1f) {
				//
				// We haven'T finished zooming
				//
				compatPostOnAnimation(this);
				
			} else {
				//
				// Finished zooming
				//
				setState(State.NONE);
			}
		}
		
		/**
		 * Interpolate between where the image should start and end in order to translate
		 * the image so that the point that is touched is what ends up centered at the end
		 * of the zoom.
		 * @param t
		 */
		private void translateImageToCenterTouchPosition(float t) {
			float TargetX = StartTouch.x + t * (EndTouch.x - StartTouch.x);
			float TargetY = StartTouch.y + t * (EndTouch.y - StartTouch.y);
			PointF Curr = transformCoordBitmapToTouch(BitmapX, BitmapY);
			Matrix.postTranslate(TargetX - Curr.x, TargetY - Curr.y);
		}
		
		/**
		 * Use interpolator to get t
		 * @return
		 */
		private float interpolate() {
			long CurrTime = System.currentTimeMillis();
			float Elapsed = (CurrTime - StartTime) / ZOOM_TIME;
			Elapsed = Math.min(1f, Elapsed);
			return Interpolator.getInterpolation(Elapsed);
		}
		
		/**
		 * Interpolate the current targeted zoom and get the delta
		 * from the current zoom.
		 * @param t
		 * @return
		 */
		private double calculateDeltaScale(float t) {
			double Zoom = StartZoom + t * (TargetZoom - StartZoom);
			return Zoom / NormalizedScale;
		}
    }
    
    /**
     * This function will transform the coordinates in the touch event to the coordinate 
     * system of the drawable that the imageview contain
     * @param X X-coordinate of touch event
     * @param Y Y-coordinate of touch event
     * @param ClipToBitmap Touch event may occur within view, but outside image content. True, to clip return value
     * 			to the bounds of the bitmap size.
     * @return Coordinates of the point touched, in the coordinate system of the original drawable.
     */
    private PointF transformCoordTouchToBitmap(float X, float Y, boolean ClipToBitmap) {
         Matrix.getValues(M);
         float OrigW = getDrawable().getIntrinsicWidth();
         float OrigH = getDrawable().getIntrinsicHeight();
         float TransX = M[Matrix.MTRANS_X];
         float TransY = M[Matrix.MTRANS_Y];
         float FinalX = ((X - TransX) * OrigW) / getImageWidth();
         float FinalY = ((Y - TransY) * OrigH) / getImageHeight();
         
         if (ClipToBitmap) {
        	 FinalX = Math.min(Math.max(FinalX, 0), OrigW);
        	 FinalY = Math.min(Math.max(FinalY, 0), OrigH);
         }
         
         return new PointF(FinalX , FinalY);
    }
    
    /**
     * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
     * drawable's coordinate system to the view's coordinate system.
     * @param bx x-coordinate in original bitmap coordinate system
     * @param by y-coordinate in original bitmap coordinate system
     * @return Coordinates of the point in the view's coordinate system.
     */
    private PointF transformCoordBitmapToTouch(float bx, float by) {
        Matrix.getValues(M);
        float origW = getDrawable().getIntrinsicWidth();
        float origH = getDrawable().getIntrinsicHeight();
        float px = bx / origW;
        float py = by / origH;
        float finalX = M[Matrix.MTRANS_X] + getImageWidth() * px;
        float finalY = M[Matrix.MTRANS_Y] + getImageHeight() * py;
        return new PointF(finalX , finalY);
    }
    
    /**
     * Fling launches sequential runnables which apply
     * the fling graphic to the image. The values for the translation
     * are interpolated by the Scroller.
     * @author Ortiz
     *
     */
    private class Fling implements Runnable {
    	
        CompatScroller Scroller;
    	int CurrX, CurrY;
    	
    	Fling(int VelocityX, int VelocityY) {
    		setState(State.FLING);
    		Scroller = new CompatScroller(MContext);
    		Matrix.getValues(M);
    		
    		int StartX = (int) M[Matrix.MTRANS_X];
    		int StartY = (int) M[Matrix.MTRANS_Y];
    		int MinX, MaxX, MinY, MaxY;
    		
    		if (getImageWidth() > ViewWidth) {
    			MinX = ViewWidth - (int) getImageWidth();
    			MaxX = 0;
    			
    		} else {
    			MinX = MaxX = StartX;
    		}
    		
    		if (getImageHeight() > ViewHeight) {
    			MinY = ViewHeight - (int) getImageHeight();
    			MaxY = 0;
    			
    		} else {
    			MinY = MaxY = StartY;
    		}
    		
    		Scroller.fling(StartX, StartY, (int) VelocityX, (int) VelocityY, MinX,
                    MaxX, MinY, MaxY);
    		CurrX = StartX;
    		CurrY = StartY;
    	}
    	
    	public void cancelFling() {
    		if (Scroller != null) {
    			setState(State.NONE);
    			Scroller.forceFinished(true);
    		}
    	}
    	
		@Override
		public void run() {
			
			//
			// OnTouchImageViewListener is set: TouchImageView listener has been flung by user.
			// Listener runnable updated with each frame of fling animation.
			//
			if (TouchImageViewListener != null) {
				TouchImageViewListener.onMove();
			}
			
			if (Scroller.isFinished()) {
        		Scroller = null;
        		return;
        	}
			
			if (Scroller.computeScrollOffset()) {
	        	int NewX = Scroller.getCurrX();
	            int NewY = Scroller.getCurrY();
	            int TransX = NewX - CurrX;
	            int TransY = NewY - CurrY;
	            CurrX = NewX;
	            CurrY = NewY;
	            Matrix.postTranslate(TransX, TransY);
	            fixTrans();
	            setImageMatrix(Matrix);
	            compatPostOnAnimation(this);
        	}
		}
    }
    
    @TargetApi(VERSION_CODES.GINGERBREAD)
	private class CompatScroller {
    	Scroller Scroller;
    	OverScroller OverScroller;
    	boolean IsPreGingerbread;
    	
    	public CompatScroller(Context context) {
    		if (VERSION.SDK_INT < VERSION_CODES.GINGERBREAD) {
    			IsPreGingerbread = true;
    			Scroller = new Scroller(context);
    			
    		} else {
    			IsPreGingerbread = false;
    			OverScroller = new OverScroller(context);
    		}
    	}
    	
    	public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
    		if (IsPreGingerbread) {
    			Scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    		} else {
    			OverScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    		}
    	}
    	
    	public void forceFinished(boolean finished) {
    		if (IsPreGingerbread) {
    			Scroller.forceFinished(finished);
    		} else {
    			OverScroller.forceFinished(finished);
    		}
    	}
    	
    	public boolean isFinished() {
    		if (IsPreGingerbread) {
    			return Scroller.isFinished();
    		} else {
    			return OverScroller.isFinished();
    		}
    	}
    	
    	public boolean computeScrollOffset() {
    		if (IsPreGingerbread) {
    			return Scroller.computeScrollOffset();
    		} else {
    			OverScroller.computeScrollOffset();
    			return OverScroller.computeScrollOffset();
    		}
    	}
    	
    	public int getCurrX() {
    		if (IsPreGingerbread) {
    			return Scroller.getCurrX();
    		} else {
    			return OverScroller.getCurrX();
    		}
    	}
    	
    	public int getCurrY() {
    		if (IsPreGingerbread) {
    			return Scroller.getCurrY();
    		} else {
    			return OverScroller.getCurrY();
    		}
    	}
    }
    
    @TargetApi(VERSION_CODES.JELLY_BEAN)
	private void compatPostOnAnimation(Runnable runnable) {
    	if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            postOnAnimation(runnable);
            
        } else {
            postDelayed(runnable, 1000/60);
        }
    }
    
    private class ZoomVariables {
    	public float Scale;
    	public float FocusX;
    	public float FocusY;
    	public ScaleType ScaleType;
    	
    	public ZoomVariables(float Scale, float FocusX, float FocusY, ScaleType ScaleType) {
    		this.Scale = Scale;
    		this.FocusX = FocusX;
    		this.FocusY = FocusY;
    		this.ScaleType = ScaleType;
    	}
    }
    
    private void printMatrixInfo() {
    	float[] n = new float[9];
    	Matrix.getValues(n);
    	Log.d(DEBUG, "Scale: " + n[Matrix.MSCALE_X] + " TransX: " + n[Matrix.MTRANS_X] + " TransY: " + n[Matrix.MTRANS_Y]);
    }
}