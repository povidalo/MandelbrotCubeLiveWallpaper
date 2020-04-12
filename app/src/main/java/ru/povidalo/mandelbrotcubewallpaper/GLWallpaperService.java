package ru.povidalo.mandelbrotcubewallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import ru.povidalo.mandelbrotcubewallpaper.openGL.CubeRenderer;

public abstract class GLWallpaperService extends WallpaperService {
    public class GLEngine extends Engine {
        private WallpaperGLSurfaceView glSurfaceView;
        private GLSurfaceView.Renderer renderer = null;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (renderer != null) {
                if (visible) {
                    if (renderer instanceof CubeRenderer) {
                        ((CubeRenderer) renderer).onResume();
                    }
                    glSurfaceView.onResume();
                } else {
                    if (renderer instanceof CubeRenderer) {
                        ((CubeRenderer) renderer).onPause();
                    }
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (renderer instanceof CubeRenderer) {
                ((CubeRenderer) renderer).onPause();
            }
            renderer = null;
            glSurfaceView.onDestroy();
        }

        protected void setRenderer(GLSurfaceView.Renderer renderer) {
            glSurfaceView.setRenderer(renderer);
            this.renderer = renderer;
        }

        protected void setEGLContextClientVersion(int version) {
            glSurfaceView.setEGLContextClientVersion(version);
        }

        protected void setPreserveEGLContextOnPause(boolean preserve) {
            glSurfaceView.setPreserveEGLContextOnPause(preserve);
        }

        class WallpaperGLSurfaceView extends GLSurfaceView {
            WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }
}
