package no.sample.isc.core.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Ankit on 14-01-2017.
 */
public class ServerInfo {

    public static final String port ;

    static{
        ServerSocket s = null;
        try {
            s = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        port = String.valueOf(s.getLocalPort());
        System.out.println("port selected:::"+ port);
    }
}
