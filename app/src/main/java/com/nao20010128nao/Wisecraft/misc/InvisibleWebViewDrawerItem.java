package com.nao20010128nao.Wisecraft.misc;

import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.mikepenz.fastadapter.utils.*;
import com.mikepenz.materialdrawer.model.*;
import com.nao20010128nao.Wisecraft.*;

public class InvisibleWebViewDrawerItem extends AbstractDrawerItem<InvisibleWebViewDrawerItem,InvisibleWebViewDrawerItem.ViewHolder>
{
    String url;
    
    @Override
    public int getLayoutRes() {
        // TODO: Implement this method
        return R.layout.drawer_item_invisible_webview;
    }

    @Override
    public ViewHolderFactory<InvisibleWebViewDrawerItem.ViewHolder> getFactory() {
        // TODO: Implement this method
        return new ItemFactory();//v->new ViewHolder(v)
    }

    @Override
    public int getType() {
        // TODO: Implement this method
        return getLayoutRes();
    }

    @Override
    public void bindView(InvisibleWebViewDrawerItem.ViewHolder p1) {
        // TODO: Implement this method
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



    public static class ItemFactory implements ViewHolderFactory<ViewHolder>{
        public ViewHolder create(View v){
            return new ViewHolder(v);
        }
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
