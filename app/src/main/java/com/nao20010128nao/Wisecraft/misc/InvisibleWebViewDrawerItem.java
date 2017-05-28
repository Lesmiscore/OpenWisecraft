package com.nao20010128nao.Wisecraft.misc;

import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.*;
import java.util.*;

public class InvisibleWebViewDrawerItem extends AbstractDrawerItem<InvisibleWebViewDrawerItem,InvisibleWebViewDrawerItem.ViewHolder>
{
    String url;
    
    @Override
    public int getLayoutRes() {
        return R.layout.drawer_item_invisible_webview;
    }
	
	@Override
	public InvisibleWebViewDrawerItem.ViewHolder getViewHolder(View parent) {
		View v=LayoutInflater.from(parent.getContext()).inflate(getLayoutRes(),(ViewGroup)parent,false);
		return new ViewHolder(v);
	}

    @Override
    public int getType() {
        return getLayoutRes();
    }

    public void bindView(InvisibleWebViewDrawerItem.ViewHolder p1,List payload) {
        p1.webView.loadUrl(url);
        p1.itemView.setId(hashCode());
        p1.itemView.setClickable(false);
        p1.itemView.setEnabled(true);
        onPostBindView(this,p1.itemView);
    }
    
    public InvisibleWebViewDrawerItem withUrl(String url){
        this.url=url;
        return this;
    }
    
    public String getUrl(){
        return url;
    }



    
    public static class ViewHolder extends FindableViewHolder{
        public FrameLayout container;
        public WebView webView;
        public ViewHolder(View v){
            super(v);
            container=(FrameLayout)v.findViewById(R.id.container);
            webView=(WebView)container.findViewById(R.id.webView);
        }
    }
}