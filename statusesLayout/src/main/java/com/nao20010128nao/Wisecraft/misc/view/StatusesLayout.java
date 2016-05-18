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


public class StatusesLayout extends LinearLayout
{
	int[] colors;
	int[] statuses;
	ExtendedImageView[] views;
	Context ctx;
	LayoutInflater li;
	float size;
	
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

	@Override
	public void addView(View child, int index) {
		
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		
	}

	@Override
	public void addView(View child) {
		
	}

	@Override
	public void addView(View child, int width, int height) {
		
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		
	}

	@Override
	protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
		return false;
	}

	@Override
	protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
		return false;
	}

	@Override
	public void removeAllViews() {
		
	}

	@Override
	public void removeAllViewsInLayout() {
		
	}

	@Override
	public void removeViewInLayout(View view) {
		
	}

	@Override
	public void removeViewsInLayout(int start, int count) {
		
	}

	@Override
	public void removeViewAt(int index) {
		
	}

	@Override
	public void removeViews(int start, int count) {
		
	}

	@Override
	public void removeView(View view) {
		
	}

	@Override
	public void setOrientation(int orientation) {
		// TODO: Implement this method
		super.setOrientation(HORIZONTAL);
	}
	
	private void addViewInternal(View v){
		super.addView(v);
	}
	private void removeAllViewsInternal(){
		super.removeAllViews();
	}
	
	private void setup(Context ctx){
		this.ctx=ctx;
		li=LayoutInflater.from(ctx);
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

		size=ta.getDimension(R.styleable.StatusesLayout_componentSize,0);
		

		
		relayout();
	}
	
	private void relayout(){
		removeAllViewsInternal();
		int[] status=this.statuses==null?new int[0]:this.statuses;
		ExtendedImageView[] exi=new ExtendedImageView[status.length];
		if(status.length==0)return;
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams((int)(size==0?ViewGroup.LayoutParams.MATCH_PARENT:size),ViewGroup.LayoutParams.MATCH_PARENT,1);
		LinearLayout ll;
		if(size==0){
			ll=this;
		}else{
			View v=li.inflate(R.layout.scrolling_mode_layout,null);
			addViewInternal(v);
			ll=(LinearLayout)v.findViewById(R.id.statusesContent);
		}
		for(int i=0;i<status.length;i++){
			exi[i]=new ExtendedImageView(ctx);
			exi[i].setLayoutParams(lp);
			exi[i].setColor(colors[status[i]]);
			ll.addView(exi[i]);
		}
	}
	
	
	
	public void setColors(int... color){
		colors=Arrays.copyOf(color,color.length);
		relayout();
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
		statuses=Arrays.copyOf(stat,stat.length);
		relayout();
	}
	public void setStatusAt(int ofs,int val){
		statuses[ofs]=val;
	}
	public void setComponentSize(float siz){
		size=siz;
		relayout();
	}
	
	
	
	private class ExtendedImageView extends AppCompatImageView
	{
		public ExtendedImageView(Context context) {
			super(context);
		}

		public ExtendedImageView(Context context, AttributeSet attrs) {
			super(context,attrs);
		}

		public ExtendedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context,attrs,defStyleAttr);
		}

		public void setColor(int color){
			setImageDrawable(new ColorDrawable(color));
		}

		public void setColorRes(int les){
			setColor(getResources().getColor(les));
		}
	}
}
