package ru.povidalo.mandelbrotcubewallpaper;

import android.app.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MandelbrotCube extends Application {

    public static long readCPUMaxFreq() {
        ProcessBuilder cmd;
        String result="";
        long MHz = -1;

        try{
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (true) {
                int bytes = in.read(re);
                if (bytes < 0) {
                    break;
                }
                result = result + new String(Arrays.copyOf(re, bytes));
            }
            in.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }

        result = result.replaceAll("\n", "");

        try {
            MHz = Long.valueOf(result);
        } catch (Exception e) {

        }

        return MHz;
    }
}
