package com.nao20010128nao.Wisecraft.misc.collector;

import java.util.*;

public interface CollectorMainUploaderProvider {
	public boolean isAvailable() throws Throwable;
	public Interface forInterface();
	public static interface Interface{
		public void init() throws Throwable;
		public boolean doUpload(String uuid,String filename,String content) throws Throwable;
	}
}
