package com.nao20010128nao.Wisecraft.misc.collector;

import java.util.*;
import java.io.*;

public interface CollectorMainUploaderProvider {
	public boolean isAvailable() throws Throwable;
	public Interface forInterface();
	public static interface Interface{
		public void init() throws Throwable;
		public boolean doUpload(String uuid,String filename,String content) throws Throwable;
		public boolean streamingUpload(String uuid,InputStream data,int length,UploadKind kind) throws Throwable;
	}
	public static enum UploadKind{
		PREFERENCES,
		@Deprecated
		ATTACHES;
	}
}
