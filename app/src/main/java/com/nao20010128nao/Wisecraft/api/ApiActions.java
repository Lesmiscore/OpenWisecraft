package com.nao20010128nao.Wisecraft.api;

public class ApiActions {
	private final static String ACTION_BASE="com.nao20010128nao.Wisecraft.";
	public  final static String SERVER_INFO=ACTION_BASE + "SERVER_INFO";

	private final static String SERVER_INFO_HIDE=ACTION_BASE + "SERVER_INFO_HIDE_";

	public  final static String SERVER_INFO_IP=ACTION_BASE + "SERVER_IP";
	public  final static String SERVER_INFO_PORT=ACTION_BASE + "SERVER_PORT";
	public  final static String SERVER_INFO_ISPC=ACTION_BASE + "SERVER_ISPC";

	public  final static String SERVER_INFO_HIDE_DETAILS=SERVER_INFO_HIDE + "DETAILS";
	public  final static String SERVER_INFO_HIDE_PLAYERS=SERVER_INFO_HIDE + "PLAYERS";
	public  final static String SERVER_INFO_HIDE_PLUGINS=SERVER_INFO_HIDE + "PLUGINS";
	public  final static String SERVER_INFO_DISABLE_UPDATE=SERVER_INFO_HIDE + "UPDATE";


	private final static String RCON=ACTION_BASE + "RCON";

	public  final static String RCON_ACCESS=RCON + "_ACCESS";

	public  final static String RCON_PASSWORD=RCON + "_PASSWORD";
}
