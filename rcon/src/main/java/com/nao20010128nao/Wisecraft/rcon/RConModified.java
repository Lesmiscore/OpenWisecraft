package com.nao20010128nao.Wisecraft.rcon;

import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.rcon.*;

import java.io.*;

import static com.nao20010128nao.Wisecraft.misc.RconModule_Utils.*;

public class RConModified extends RCon {
    public RConModified(String ip, int port, char[] password) throws IOException, AuthenticationException {
        super(ip, port, password);
    }

    @Override
    public String[] list() throws IOException, AuthenticationException {
        String[] data = lines(send("list"));
        if (data.length >= 2) {
            String[] players = data[1].split("\\, ");
            for (int i = 0; i < players.length; i++) {
                players[i] = RconModule_Utils.deleteDecorations(players[i]);
            }
        }
        return RconModule_Constant.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] banList() throws IOException, AuthenticationException {
        String[] data = lines(send("banlist players"));
        if (data.length >= 2) {
            return data[1].split("\\, ");
        }
        return RconModule_Constant.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] banIPList() throws IOException, AuthenticationException {
        String[] data = lines(send("banlist ips"));
        if (data.length >= 2) {
            return data[1].split("\\, ");
        }
        return RconModule_Constant.EMPTY_STRING_ARRAY;
    }
}
