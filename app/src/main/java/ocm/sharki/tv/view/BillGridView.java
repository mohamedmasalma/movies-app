package ocm.sharki.tv.view;

import android.content.Context;
import android.icu.util.Measure;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;

public class BillGridView extends GridView {
    public BillGridView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, MeasureSpec.makeMeasureSpec(536870911, MeasureSpec.EXACTLY));
    }
}
