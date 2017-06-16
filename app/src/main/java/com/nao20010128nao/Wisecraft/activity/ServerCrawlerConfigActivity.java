package com.nao20010128nao.Wisecraft.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class ServerCrawlerConfigActivity extends AppCompatActivity {
    RecyclerView rv;
    ServerCrawlerManager scm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_crawler_config);
        scm=new ServerCrawlerManager(this);
        rv= (RecyclerView) findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rv.setAdapter(new LocalAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,R.string.addSingle);
        menu.add(0,1,1,R.string.reschedule);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:/*Add function*/break;
            case 1:scm.reschedule();return true;
        }
        return false;
    }

    class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.VH>{

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(getLayoutInflater().inflate(R.layout.server_crawler_entry,parent,false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            Protobufs.ServerCrawlerEntry entry=scm.getEntries().get(position);
            holder.itemView.setTag(R.id.id,entry.getId());
            holder.name.setText(entry.getName());
            holder.start.setText(Utils.formatDate(entry.getStart()));
            holder.interval.setText(Utils.formatTimeSpan(entry.getInterval()));
            holder.enabled.setText(entry.getEnabled()?R.string.yesWord:R.string.noWord);
        }

        @Override
        public int getItemCount() {
            return scm.getEntries().size();
        }

        class VH extends FindableViewHolder{
            TextView name,interval,start,enabled;

            public VH(View v) {
                super(v);
                name= (TextView) findViewById(R.id.name);
                interval= (TextView) findViewById(R.id.intervalText);
                start= (TextView) findViewById(R.id.startTimeText);
                enabled= (TextView) findViewById(R.id.enabledText);
            }
        }
    }
}
