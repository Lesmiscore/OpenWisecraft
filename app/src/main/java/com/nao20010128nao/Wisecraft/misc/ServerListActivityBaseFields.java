package com.nao20010128nao.Wisecraft.misc;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.view.*;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.*;
import android.support.v7.widget.helper.ItemTouchHelper.*;
import android.view.*;
import com.google.gson.*;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import com.nao20010128nao.Wisecraft.misc.view.*;
import java.io.*;
import java.security.*;
import java.util.*;

import android.support.v7.view.ActionMode;

//Fields
public abstract class ServerListActivityBaseFields extends ServerListActivityBaseGrand
{
	public static final int EDIT_MODE_NULL=0;
	public static final int EDIT_MODE_EDIT=1;
	public static final int EDIT_MODE_SELECT_UPDATE=2;
	
    //impl
    protected static final File mcpeServerList=new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/external_servers.txt");

    protected final SextetWalker<Integer,Integer,Treatment<ServerListActivity>,Treatment<ServerListActivity>,IDrawerItem,UUID> appMenu=new SextetWalker<>();
    protected ServerPingProvider spp,updater;
    protected Gson gson=new Gson();
    protected int clicked=-1;
    protected WorkingDialog wd;
    protected SwipeRefreshLayout srl;
    protected List<MenuItem> items=new ArrayList<>();
    protected DrawerLayout dl;
    protected boolean drawerOpened;
    protected boolean skipSave=false;
	protected Map<Server,Boolean> pinging=new NonNullableMap<Server>();

    protected int editMode=EDIT_MODE_NULL;
    protected ItemTouchHelper itemDecor;
    protected SimpleCallback ddManager;
    protected ActionMode.Callback editAm,selectUpdateAm;
	protected CoordinatorLayout coordinator;
    
    //base2,3,5
    protected SecureRandom sr=new SecureRandom();
	//base4
    protected int newVersionAnnounce=0;
    protected Drawer drawer;
    protected MiniDrawer sideMenu;
    protected RecyclerView rv;
    protected Snackbar indicator;
    protected StatusesLayout statLayout;
    protected Uri userImage;
    protected ImageLoader imageLoader=new ImageLoader();
}
