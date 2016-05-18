package com.nao20010128nao.Wisecraft.misc.view;
import android.widget.LinearLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import java.util.Arrays;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import com.nao20010128nao.StatusesLayout.R;
import android.content.res.TypedArray;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.FrameLayout;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class StatusesLayout extends View
{
	int[] colors;
	int[] statuses;
	Context ctx;
	Paint paint;
	
	public StatusesLayout(Context context) {
		super(context);
		setup(context);
	}

    public StatusesLayout(Context context, AttributeSet attrs) {
		super(context,attrs);
		setup(context,attrs);
	}

    public StatusesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
		setup(context,attrs);
	}

	
	private void setup(Context ctx){
		this.ctx=ctx;
		paint=new Paint();
		paint.setStyle(Paint.Style.FILL);
	}
	
	private void setup(Context ctx,AttributeSet as){
		setup(ctx);
		TypedArray ta = ctx.obtainStyledAttributes(as, R.styleable.StatusesLayout);
		int colDat=ta.getResourceId(R.styleable.StatusesLayout_colors,0);
		if(colDat!=0){
			TypedArray array = ctx.getResources().obtainTypedArray(colDat);
			try {
				int[] colors = new int[array.length()];
				for (int i = 0; i < colors.length; ++i) {
					colors[i] = ContextCompat.getColor(ctx, array.getResourceId(i, 0));
				}
				this.colors=colors;
			} finally {
				array.recycle();
			}
		}
		
		int statRes=ta.getResourceId(R.styleable.StatusesLayout_statuses,0);
		if(statRes!=0){
			this.statuses=ctx.getResources().getIntArray(statRes);
		}

		

		
		relayout();
	}
	
	private void relayout(){
		invalidate();
	}
	private void redye(){
		relayout();
	}
	
	
	public void setColors(int... color){
		colors=Arrays.copyOf(color,color.length);
		redye();
	}
	public void setColorRes(int... res){
		int[] c=new int[res.length];
		for(int i=0;i<c.length;i++){
			c[i]=ContextCompat.getColor(ctx,res[i]);
		}
		setColors(c);
	}
	public void initStatuses(int len,int def){
		int[] statuses=new int[len];
		Arrays.fill(statuses,def);
		setStatuses(statuses);
	}
	public void setStatuses(int... stat){
		boolean doRedye=statuses==null?false:stat.length==statuses.length;
		statuses=Arrays.copyOf(stat,stat.length);
		if(doRedye) redye();
		else relayout();
	}
	public void setStatusAt(int ofs,int val){
		statuses[ofs]=val;
		redye();
	}
	
	private boolean isInvalid(){
		return statuses==null|colors==null;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO: Implement this method
		if(isInvalid())return;
		if(statuses.length==0){
			if(colors.length==0)return;//nothing to do
			paint.setColor(colors[0]);
			canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),paint);
			return;
		}
		float oneComp=BigDecimal.valueOf(getWidth()).divide(BigDecimal.valueOf(statuses.length),10,RoundingMode.DOWN).floatValue();
		for(int i=0;i<statuses.length;i++){
			paint.setColor(colors[statuses[i]]);
			canvas.drawRect(oneComp*i,0,oneComp*(i+1),canvas.getHeight(),paint);
		}
	}
}
