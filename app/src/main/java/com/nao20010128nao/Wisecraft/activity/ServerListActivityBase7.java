package com.nao20010128nao.Wisecraft.activity;

import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.view.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.google.common.collect.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import java.net.*;
import java.util.*;

import android.support.v7.view.ActionMode;

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
			Multimap<String,Server> domains;
			DomainListAdapter domainLstAdptr;
			DomainStatusChecker worker;
			
            public boolean onCreateActionMode(ActionMode p1, Menu p2) {
                srl.setEnabled(false);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				slaViewBackground=rv.getBackground();
				slaDefaultAdapter=rv.getAdapter();
				slaDefaultLayoutManager=rv.getLayoutManager();
				Multimap<String,Server> domains=listDomains();
				domainLstAdptr=new DomainListAdapter(domains);
				rv.setAdapter(domainLstAdptr);
				rv.setLayoutManager(new LinearLayoutManager(ServerListActivityBase7.this));
				ViewCompat.setBackground(rv,ThemePatcher.getWindowBackground(ServerListActivityBase7.this));
				(worker=new DomainStatusChecker()).execute(domainLstAdptr);
				this.domains=domains;
				isInSelectMode=false;
                return true;
            }

            public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
                editMode = EDIT_MODE_REMOVE_UNUSED_DOMAINS;
				MenuItem delete=p2.add(0,0,0,R.string.delete)
					.setIcon(TheApplication.instance.getTintedDrawable(R.drawable.ic_delete_forever_black_48dp,ThemePatcher.getMenuTintColor(ServerListActivityBase7.this)));
				MenuItemCompat.setShowAsAction(delete,MenuItem.SHOW_AS_ACTION_ALWAYS);
                return true;
            }

            public boolean onActionItemClicked(ActionMode p1, MenuItem p2) {
				switch(p2.getItemId()){
					case 0:
						Set<Server> reallyDeletingServers=new HashSet<>();
						for(int i=0;i<domainLstAdptr.getItemCount();i++){
							if(domainLstAdptr.domainChecked.get(i)&&domainLstAdptr.deletingDomain.get(i)){
								reallyDeletingServers.addAll(domains.get(domainLstAdptr.listedDomains.get(i)));
							}
						}
						for(Server deletion:reallyDeletingServers){
							removeFromList(deletion);
						}
						p1.finish();
						return true;
				}
                return false;
            }

            public void onDestroyActionMode(ActionMode p1) {
                editMode = EDIT_MODE_NULL;
                itemDecor.attachToRecyclerView(null);
                srl.setEnabled(true);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				rv.setAdapter(slaDefaultAdapter);
				rv.setLayoutManager(slaDefaultLayoutManager);
				ViewCompat.setBackground(rv,slaViewBackground);
				if(worker.getStatus()==AsyncTask.Status.RUNNING){
					worker.cancel(true);
				}
                saveServers();
				isInSelectMode=false;
            }
        };
	}
	
	/* Following methods are needed for this class */
	public abstract void saveServers();
	public abstract List<Server> getServers();
	public abstract void removeFromList(Server s);
	
	public void startRemoveDomainsActionMode(){
		startSupportActionMode(removeUnusedDomainsAm);
	}
	
	Multimap<String,Server> listDomains(){
		HashMultimap<String,Server> hmm=HashMultimap.<String,Server>create();
		for(Server s:getServers()){
			String ip=s.ip.toLowerCase();
			if(!(ip.matches(Constant.IPV4_PATTERN)|ip.matches(Constant.IPV6_PATTERN)|"localhost".equalsIgnoreCase(ip))){
				hmm.put(ip,s);
			}
		}
		return hmm;
	}

	class DomainListAdapter extends RecyclerView.Adapter<Vh> {
		Multimap<String,Server> domains;
		List<String> listedDomains;
		List<Boolean> expandStates;
		List<Boolean> domainChecked;
		List<Boolean> domainUsed;
		List<Boolean> deletingDomain;
		
		public DomainListAdapter(Multimap<String,Server> domains){
			this.domains=domains;
			listedDomains=  Collections.synchronizedList(new ArrayList<>(domains.keySet()));
			List<Boolean> nCopied=Collections.nCopies(listedDomains.size(),false);
			expandStates=   Collections.synchronizedList(new ArrayList<>(nCopied));
			domainChecked=  Collections.synchronizedList(new ArrayList<>(nCopied));
			domainUsed=     Collections.synchronizedList(new ArrayList<>(nCopied));
			deletingDomain= Collections.synchronizedList(new ArrayList<>(nCopied));
			
			Collections.sort(listedDomains);
		}
		
		@Override
		public void onBindViewHolder(Vh p1, final int p2) {
			p1.domain.setText(listedDomains.get(p2));
			p1.servers.removeAllViews();
			p1.available.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_check_black_48dp,ThemePatcher.getMainColor(ServerListActivityBase7.this)));
			if(expandStates.get(p2)){
				p1.servers.setVisibility(View.VISIBLE);
				for(Server serv:domains.get(listedDomains.get(p2))){
					View entryView=getLayoutInflater().inflate(R.layout.simple_list_item_1,p1.servers,false);
					TextView tv=(TextView)entryView.findViewById(android.R.id.text1);
					tv.setText(serv.resolveVisibleTitle());
					p1.servers.addView(entryView);
				}
				p1.expand.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_expand_less_black_48dp,p1.domain.getTextColors().getDefaultColor()));
			}else{
				p1.servers.setVisibility(View.GONE);
				p1.expand.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_expand_more_black_48dp,p1.domain.getTextColors().getDefaultColor()));
			}
			if(domainChecked.get(p2)){
				if(domainUsed.get(p2)){
					//check mark
					p1.showAvailable();
				}else{
					//checkbox to ask user to delete
					p1.showWillDelete(deletingDomain.get(p2));
				}
			}else{
				//loading
				p1.showLoading();
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
		final FrameLayout domainStatus;
		final ProgressBar loading;
		final CheckBox willDelete;
		final ImageView available;

		public Vh(View v){
			super(v);
			expand=findTypedViewById(R.id.expand);
			domain=findTypedViewById(R.id.domain);
			servers=findTypedViewById(R.id.servers);
			domainStatus=findTypedViewById(R.id.domainStatus);
			loading=findTypedViewById(R.id.loading);
			willDelete=findTypedViewById(R.id.willDelete);
			available=findTypedViewById(R.id.available);
		}
		
		public void showLoading(){
			loading.setVisibility(View.VISIBLE);
			willDelete.setVisibility(View.GONE);
			available.setVisibility(View.GONE);
		}
		
		public void showWillDelete(boolean value){
			loading.setVisibility(View.GONE);
			willDelete.setVisibility(View.VISIBLE);
			available.setVisibility(View.GONE);
			willDelete.setChecked(value);
		}
		
		public void showAvailable(){
			loading.setVisibility(View.GONE);
			willDelete.setVisibility(View.GONE);
			available.setVisibility(View.VISIBLE);
		}
	}
	
	class DomainStatusChecker extends AsyncTask<DomainListAdapter,Map.Entry<Integer,Boolean>,Void> {
		DomainListAdapter adapter;
		
		@Override
		protected Void doInBackground(DomainListAdapter[] p1) {
			adapter=p1[0];
			List<String> domains=adapter.listedDomains;
			for(String dm:domains){
				if(isCancelled())break;
				boolean ok=false;
				try {
					InetAddress.getAllByName(dm);
					ok=true;
				} catch (UnknownHostException e) {}
				publishProgress(new KVP<>(domains.indexOf(dm),ok));
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Map.Entry<Integer, Boolean>[] values) {
			for(Map.Entry<Integer, Boolean> updates:values){
				int offset=updates.getKey();
				boolean value=updates.getValue();
				adapter.domainChecked.set(offset,true);
				adapter.domainUsed.set(offset,value);
				adapter.deletingDomain.set(offset,false);
				adapter.notifyItemChanged(offset);
			}
		}
	}
}
