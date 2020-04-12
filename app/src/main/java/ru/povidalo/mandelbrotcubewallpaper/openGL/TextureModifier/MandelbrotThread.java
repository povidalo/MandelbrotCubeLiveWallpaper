package ru.povidalo.mandelbrotcubewallpaper.openGL.TextureModifier;

import android.os.SystemClock;

/**
 * Created by user on 20.08.15.
 */
public class MandelbrotThread extends ModifierThread {
    private static final long FRAME_TIME = 40;
    private static final int BACKGROUND_COLOR_R = 0x40;
    private static final int BACKGROUND_COLOR_G = 0x40;
    private static final int BACKGROUND_COLOR_B = 0x40;
    private static final int MAX_COLOR = 0xff;
    private double[] xPre, yPre;
    private boolean firstPass = true;


    //params
    private final double epsilon = 4.0;

    private double rotationAngle = 0;
    private double rotationSin   = Math.sin(rotationAngle);
    private double rotationCos   = Math.cos(rotationAngle);

    private int lastMin = 0, lastMax = MAX_COLOR;

    private double xShift = -0.35, yShift = 0, scale = 0.85;
    //private double xShift = -0.7, yShift = 0, scale = 0.8;
    //private double xShift = -1.42, yShift = 0, scale = 2;
    //private double xShift = -1.194, yShift = -0.307, scale = 0.8;
    private double centralPointX = 0, centralPointY = 0;
    private int maxIterations = 10;
    private double centralPointX2 = centralPointX*centralPointX, centralPointY2 = centralPointY*centralPointY;
    int min=-1, max=-1;

    public MandelbrotThread(int[] pixels, int w, int h, OnFrameReady onFrameReady) {
        super(pixels, w, h, onFrameReady);
        xPre = new double[pixels.length];
        yPre = new double[pixels.length];
    }

    private double checkValue(double val, double goal, double step) {
        if (Math.abs(val-goal) < step) {
            val = goal;
        } else if (goal < val) {
            val -= step;
        }else {
            val += step;
        }
        return val;
    }

    @Override
    public void run() {
        int pixelPointer = 0, count = 0, color;

        double xBr = 0, yBr = 0, x = 0, y = 0, valx = 0, valy = 0, valx2 = 0, valy2 = 0;
        double k1 = 1.0, k2 = 1.0, k3 = 1.0;
        double rotationGoal = rotationAngle, scaleGoal = scale, cxGoal = centralPointX, cyGoal = centralPointY, xsGoal = xShift, ysGoal = yShift;
        double k1Goal = k1, k2Goal = k2, k3Goal = k3;
        int colorR, colorG, colorB;

        long time = SystemClock.uptimeMillis();
        long timePassed = 0;

        while (running) {
            if (pixelPointer >= pixels.length) {
                pixelPointer = 0;

                timePassed = SystemClock.uptimeMillis() - time;
                if (timePassed < FRAME_TIME) {
                    try {
                        synchronized (this) {
                            wait(FRAME_TIME - timePassed);
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                time = SystemClock.uptimeMillis();

                if (performReconfiguration()) {
                    pixelPointer = 0;
                    firstPass = true;
                    xPre = new double[pixels.length];
                    yPre = new double[pixels.length];
                    continue;
                }
                pixels = onFrameReady.onFrameReady(pixels);
                if (performReconfiguration()) {
                    pixelPointer = 0;
                    firstPass = true;
                    xPre = new double[pixels.length];
                    yPre = new double[pixels.length];
                    continue;
                }
                firstPass = false;

                if (rotationGoal == rotationAngle) {
                    rotationGoal = rnd.nextDouble() * 360;
                }
                rotationAngle = checkValue(rotationAngle, rotationGoal, 0.7);
                rotationSin   = Math.sin(rotationAngle * Math.PI / 180.0);
                rotationCos   = Math.cos(rotationAngle * Math.PI / 180.0);

                /*if (scaleGoal == scale) {
                    scaleGoal = rnd.nextDouble() * 0.3 + 0.7;
                }
                scale = checkValue(scale, scaleGoal, step);*/

                if (cxGoal == centralPointX) {
                    cxGoal = rnd.nextDouble() * 3.0 - 1.5;
                }
                centralPointX = checkValue(centralPointX, cxGoal, 0.01);

                if (cyGoal == centralPointY) {
                    cyGoal = rnd.nextDouble() * 3.0 - 1.5;
                }
                centralPointY = checkValue(centralPointY, cyGoal, 0.01);

                if (xsGoal == xShift) {
                    xsGoal = rnd.nextDouble() * 0.6 - 0.3;
                }
                xShift = checkValue(xShift, xsGoal, 0.002);

                if (ysGoal == yShift) {
                    ysGoal = rnd.nextDouble() * 0.6 - 0.3;
                }
                yShift = checkValue(yShift, ysGoal, 0.002);

                if (k1Goal == k1) {
                    k1Goal = rnd.nextDouble() * 0.5 + 0.75;
                }
                k1 = checkValue(k1, k1Goal, 0.005);

                if (k2Goal == k2) {
                    k2Goal = rnd.nextDouble() * 0.5 + 0.75;
                }
                k2 = checkValue(k2, k2Goal, 0.005);

                if (k3Goal == k3) {
                    k3Goal = rnd.nextDouble() * 0.5 + 0.75;
                }
                k3 = checkValue(k3, k3Goal, 0.005);

                lastMin = min;
                lastMax = max;

                if (lastMax == lastMin) {
                    lastMin = 0;
                    lastMax = MAX_COLOR;
                    maxIterations += 30;
                } else if (lastMax - lastMin < 10) {
                    maxIterations += 5;
                } else if (lastMax - lastMin > 20) {
                    maxIterations -= 5;
                    if (maxIterations < 10) {
                        maxIterations = 10;
                    }
                }

                min = -1;
                max = -1;
            }

            if (firstPass) {
                if (w < h) {
                    xPre[pixelPointer] = ((pixelPointer % w - w / 2) * 4.0 / w);
                    yPre[pixelPointer] = ((pixelPointer / w - h / 2) * 4.0 / w);
                } else {
                    xPre[pixelPointer] = ((pixelPointer % w - w / 2) * 4.0 / h);
                    yPre[pixelPointer] = ((pixelPointer / w - h / 2) * 4.0 / h);
                }
            }
            yBr = yPre[pixelPointer] * scale + yShift;
            xBr = xPre[pixelPointer] * scale + xShift;

            x = xBr * rotationCos - yBr * rotationSin;
            y = xBr * rotationSin + yBr * rotationCos;

            valx = centralPointX;
            valy = centralPointY;
            valx2 = centralPointX2;
            valy2 = centralPointY2;

            count = 0;
            do {
                valy = k1 * 2.0 * valx * valy + y;
                valx = k2 * valx2 - k3 * valy2 + x;

                //valx = valx2 - valy2 + x;
                //valy = 2.0 * valx * valy + y;

                //valy = -2.0 * valx * valy + y;
                //valx = valx2 - valy2 + x;

                //valx = valx2 - valy2 + x;
                //valy = -2.0 * valx * valy + y;

                valx2 = valx * valx;
                valy2 = valy * valy;

                count++;
            } while (count < maxIterations && valx2 + valy2 < epsilon);


            if (min < 0 || count < min) {
                min = count;
            }

            if (max < 0 || count > max) {
                max = count;
            }

            if (count >= maxIterations) {
                color = 0;
            } else {
                color = MAX_COLOR * (count - lastMin) / (lastMax - lastMin);
                if (color < 0 || color > MAX_COLOR) {
                    color = MAX_COLOR;
                }
            }

            colorR = BACKGROUND_COLOR_R;
            colorG = BACKGROUND_COLOR_G + color;
            colorB = BACKGROUND_COLOR_B;
            if (colorG > MAX_COLOR) {
                colorR = colorR - (colorG - MAX_COLOR);
                if (colorR < 0) {
                    colorR = 0;
                }
                colorB = colorB - (colorG - MAX_COLOR);
                if (colorB < 0) {
                    colorB = 0;
                }
                colorG = MAX_COLOR;
            }

            pixels[pixelPointer] = (colorR << 16) | (colorG << 8) | (colorB) | 0xff000000;
            pixelPointer++;
        }
    }
}