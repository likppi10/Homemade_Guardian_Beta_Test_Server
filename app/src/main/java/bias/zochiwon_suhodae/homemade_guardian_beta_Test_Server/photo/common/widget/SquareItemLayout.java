package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

// PhotoGridAdapter를 이용하여 이미지들을 나열 할 때에 이미지의 규격을 설정한 것이다.

public class SquareItemLayout extends RelativeLayout {
  public SquareItemLayout(Context Context, AttributeSet Attrs, int DefStyle) {
    super(Context, Attrs, DefStyle);
  }

  public SquareItemLayout(Context Context, AttributeSet Attrs) {
    super(Context, Attrs);
  }

  public SquareItemLayout(Context Context) {
    super(Context);
  }

  @Override protected void onMeasure(int WidthMeasureSpec, int HeightMeasureSpec) {
    setMeasuredDimension(getDefaultSize(0, WidthMeasureSpec), getDefaultSize(0, HeightMeasureSpec));
    int ChildWidthSize = getMeasuredWidth();
    HeightMeasureSpec = WidthMeasureSpec = MeasureSpec.makeMeasureSpec(ChildWidthSize, MeasureSpec.EXACTLY);
    super.onMeasure(WidthMeasureSpec, HeightMeasureSpec);
  }
}
