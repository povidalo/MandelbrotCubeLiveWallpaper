package ru.povidalo.mandelbrotcubewallpaper.openGL.TextureModifier;

import java.util.Random;

public abstract class ModifierThread extends Thread {
    protected int[] pixels;
    protected int w, h;

    protected int[] pixelsTmp;
    protected int wTmp, hTmp;

    private boolean needReconfiguration = false;

    protected final    Random  rnd     = new Random(System.currentTimeMillis());
    protected volatile boolean running = true;
    protected final OnFrameReady onFrameReady;

    public ModifierThread(int[] pixels, int w, int h, OnFrameReady onFrameReady) {
        this.pixels = pixels;
        this.w = w;
        this.h = h;
        this.onFrameReady = onFrameReady;
        setPriority(Thread.MAX_PRIORITY);
    }

    public void reconfigure(int[] pixels, int w, int h) {
        this.pixelsTmp = pixels;
        this.wTmp = w;
        this.hTmp = h;
        needReconfiguration = true;
    }

    protected boolean performReconfiguration() {
        if (needReconfiguration) {
            needReconfiguration = false;
            pixels = pixelsTmp;
            w = wTmp;
            h = hTmp;
            return true;
        }
        return false;
    }

    @Override
    public void interrupt() {
        running = false;
        super.interrupt();
    }
}