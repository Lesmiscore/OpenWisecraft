package com.nao20010128nao.Wisecraft.misc;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import com.google.gson.*;
import com.mikepenz.materialdrawer.*;
import com.nao20010128nao.Wisecraft.misc.provider.*;
import com.nao20010128nao.Wisecraft.misc.view.*;
import java.io.*;
import java.security.*;
import java.util.*;

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
    //base2,3
    protected SecureRandom sr=new SecureRandom();
	//base4
    protected int newVersionAnnounce=0;
    protected Drawer drawer;
    protected MiniDrawer sideMenu;
    protected RecyclerView rv;
    protected Snackbar networkState;
    protected StatusesLayout statLayout;
}
