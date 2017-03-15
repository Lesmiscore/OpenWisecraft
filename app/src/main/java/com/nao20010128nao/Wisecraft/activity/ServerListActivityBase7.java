package com.nao20010128nao.Wisecraft.activity;

import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.widget.*;
import android.support.v7.view.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.google.common.collect.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.util.*;

import android.support.v7.view.ActionMode;
import android.support.v4.view.*;
//Remove servers on unused domains
public abstract class ServerListActivityBase7 extends ServerListActivityBaseFields 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		removeUnusedDomainsAm=new ActionMode.Callback(){
			RecyclerView.Adapter slaDefaultAdapter;
			RecyclerView.LayoutManager slaDefaultLayoutManager;
			Drawable slaViewBackground;
			
            public boolean onCreateActionMode(ActionMode p1, Menu p2) {
                srl.setEnabled(false);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				slaViewBackground=rv.getBackground();
				slaDefaultAdapter=rv.getAdapter();
				slaDefaultLayoutManager=rv.getLayoutManager();
				Multimap<String,Server> domains=listDomains();
				rv.setAdapter(new DomainListAdapter(domains));
				rv.setLayoutManager(new LinearLayoutManager(ServerListActivityBase7.this));
				ViewCompat.setBackground(rv,ThemePatcher.getWindowBackground(ServerListActivityBase7.this));
				isInSelectMode=false;
				editMode = EDIT_MODE_REMOVE_UNUSED_DOMAINS;
                return true;
            }

            public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
                editMode = EDIT_MODE_EDIT;
                return true;
            }

            public boolean onActionItemClicked(ActionMode p1, MenuItem p2) {
                return true;
            }

            public void onDestroyActionMode(ActionMode p1) {
                editMode = EDIT_MODE_NULL;
                itemDecor.attachToRecyclerView(null);
                srl.setEnabled(true);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				rv.setAdapter(slaDefaultAdapter);
				rv.setLayoutManager(slaDefaultLayoutManager);
				ViewCompat.setBackground(rv,slaViewBackground);
                saveServers();
				isInSelectMode=false;
            }
        };
	}
	
	/* Following methods are needed for this class */
	public abstract void saveServers();
	public abstract List<Server> getServers();
	
	public void startRemoveDomainsActionMode(){
		startSupportActionMode(removeUnusedDomainsAm);
	}
	
	Multimap<String,Server> listDomains(){
		HashMultimap<String,Server> hmm=HashMultimap.<String,Server>create();
		for(Server s:getServers()){
			if(!(s.ip.matches(Constant.IPV4_PATTERN)|s.ip.matches(Constant.IPV6_PATTERN))){
				hmm.put(s.ip.toLowerCase(),s);
			}
		}
		return hmm;
	}

	class DomainListAdapter extends RecyclerView.Adapter<Vh> {
		Multimap<String,Server> domains;
		List<String> listedDomains;
		List<Boolean> expandStates;
		
		public DomainListAdapter(Multimap<String,Server> domains){
			this.domains=domains;
			listedDomains=new ArrayList<>(domains.keySet());
			expandStates=new ArrayList<>(Collections.nCopies(listedDomains.size(),false));
		}
		
		@Override
		public void onBindViewHolder(Vh p1, final int p2) {
			p1.domain.setText(listedDomains.get(p2));
			p1.servers.removeAllViews();
			if(expandStates.get(p2)){
				p1.servers.setVisibility(View.VISIBLE);
				for(Server serv:domains.get(listedDomains.get(p2))){
					View entryView=getLayoutInflater().inflate(R.layout.simple_list_item_1,p1.servers,false);
					TextView tv=(TextView)entryView.findViewById(android.R.id.text1);
					tv.setText(serv.resolveVisibleTitle());
					p1.servers.addView(entryView);
				}
				p1.expand.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_expand_less_black_48dp,p1.domain.getTextColor()));
			}else{
				p1.servers.setVisibility(View.GONE);
				p1.expand.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_expand_more_black_48dp,p1.domain.getTextColor()));
			}
			p1.expand.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						expandStates.set(p2,!expandStates.get(p2));
						notifyItemChanged(p2);
					}
				});
		}

		@Override
		public Vh onCreateViewHolder(ViewGroup p1, int p2) {
			return new Vh(getLayoutInflater().inflate(R.layout.remove_unused_domains_entry,p1,false));
		}

		@Override
		public int getItemCount() {
			return listedDomains.size();
		}
	}

	class Vh extends FindableViewHolder{
		final ImageButton expand;
		final TextView domain;
		final LinearLayout servers;

		public Vh(View v){
			super(v);
			expand=findTypedViewById(R.id.expand);
			domain=findTypedViewById(R.id.domain);
			servers=findTypedViewById(R.id.servers);
		}
	}
}
