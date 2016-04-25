package com.nao20010128nao.Wisecraft.misc;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import com.nao20010128nao.Wisecraft.Utils;

public class FileLinkedHeavilyBinaryPrefImpl extends HeavilyEncryptedBinaryPrefImpl {
	File fileDir;
	public FileLinkedHeavilyBinaryPrefImpl(File f)throws IOException {
		super(f);
		fileDir = f;
	}

	private class BpiEdt extends PreferencesEditorWrapper {
		public BpiEdt() {
			super(FileLinkedHeavilyBinaryPrefImpl.super.edit());
		}

		@Override
		public boolean commit() {
			// TODO: Implement this method
			return super.commit() & Utils.writeToFileByBytes(fileDir, toBytes());
		}

		@Override
		public void apply() {
			// TODO: Implement this method
			super.apply();
			Utils.writeToFileByBytes(fileDir, toBytes());
		}

	}
}
