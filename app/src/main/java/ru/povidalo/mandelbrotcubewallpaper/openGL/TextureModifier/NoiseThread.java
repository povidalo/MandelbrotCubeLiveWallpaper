package ru.povidalo.mandelbrotcubewallpaper.openGL.TextureModifier;

public class NoiseThread extends ModifierThread {
    public NoiseThread(int[] pixels, int w, int h, OnFrameReady onFrameReady) {
        super(pixels, w, h, onFrameReady);
    }

    @Override
    public void run() {
        int pixelPointer = 0;
        while (running) {
            if (pixelPointer >= pixels.length) {
                pixelPointer = 0;
                pixels = onFrameReady.onFrameReady(pixels);
            }

            pixels[pixelPointer] = rnd.nextInt() | 0xff000000;
            pixelPointer++;
        }
    }
}