package badda3mon.lockscreen.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.annotation.Nullable;

public class RobotoTextView extends androidx.appcompat.widget.AppCompatTextView {
	public RobotoTextView(Context context) {
		this(context,null);
	}

	public RobotoTextView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
		setTypeface(typeface);
	}


}
