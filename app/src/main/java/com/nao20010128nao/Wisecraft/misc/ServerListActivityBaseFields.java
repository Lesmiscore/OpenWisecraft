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
    //impl
    protected static final File mcpeServerList=new File(Environment.getExternalStorageDirectory(), "/games/com.mojang/minecraftpe/external_servers.txt");

    protected final List<Map.Entry<Integer,Integer>> appMenu=new ArrayList<>();
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

    protected boolean isEditing=false;
    protected ItemTouchHelper itemDecor;
    protected SimpleCallback ddManager;
    protected ActionMode.Callback am;
    
    //base2,3
    protected SecureRandom sr=new SecureRandom();
	//base4
    protected int newVersionAnnounce=0;
    protected Drawer drawer;
    protected MiniDrawer sideMenu;
    protected RecyclerView rv;
    protected Snackbar networkState;
    protected StatusesLayout statLayout;
    protected Uri userImage;
    protected ImageLoader imageLoader=new ImageLoader();
}
