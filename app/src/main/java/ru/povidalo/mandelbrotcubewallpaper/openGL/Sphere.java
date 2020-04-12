package ru.povidalo.mandelbrotcubewallpaper.openGL;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by user on 26.08.15.
 */
public class Sphere
{
    private int   stacks;
    private int   slices;
    private float radius;

    //Buffers
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ShortBuffer indexBuffer;

    //Buffer sizes in aantal bytes
    private int vertexBufferSize;
    private int colorBufferSize;
    private int indexBufferSize;

    private int vertexCount;

    private int program;

    static final int FLOATS_PER_VERTEX = 3; // Het aantal floats in een vertex (x, y, z)
    static final int FLOATS_PER_COLOR  = 4;  // Het aantal floats in een kleur (r, g, b, a)
    static final int SHORTS_PER_INDEX  = 2;
    static final int BYTES_PER_FLOAT   = 4;
    static final int BYTES_PER_SHORT   = 2;

    static final int BYTES_PER_VERTEX      = FLOATS_PER_VERTEX * BYTES_PER_FLOAT;
    static final int BYTES_PER_COLOR       = FLOATS_PER_COLOR * BYTES_PER_FLOAT;
    static final int BYTES_PER_INDEX_ENTRY = SHORTS_PER_INDEX * BYTES_PER_SHORT;

    // Set color with red, green, blue and alpha (opacity) values
    private float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public Sphere(float radius, int stacks, int slices) {
        this.stacks = stacks;
        this.slices = slices;
        this.radius = radius;

        vertexCount = stacks * slices * 3;
        vertexBufferSize    = vertexCount * BYTES_PER_VERTEX;
        colorBufferSize     = vertexCount * BYTES_PER_COLOR;
        indexBufferSize     = vertexCount * BYTES_PER_INDEX_ENTRY;

        program = GLHelpers.createProgram();
        if (program == 0) {
            return;
        }
        GLHelpers.checkGlError("program");

        // Setup vertex-array buffer. Vertices in float. A float has 4 bytes.
        vertexBuffer = ByteBuffer.allocateDirect(vertexBufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer = ByteBuffer.allocateDirect(colorBufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        indexBuffer = ByteBuffer.allocateDirect(indexBufferSize).order(ByteOrder.nativeOrder()).asShortBuffer();

        generateSphereCoords(radius, stacks, slices);

        vertexBuffer.position(0);
        colorBuffer.position(0);
        indexBuffer.position(0);
    }


    public void draw(float[] modelViewProjectionMatrix)
    {
        GLES20.glUseProgram(program);

        GLHelpers.checkGlError("useprogram");

        int positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, BYTES_PER_VERTEX, vertexBuffer);
        GLHelpers.checkGlError("pos");

        //int colorHandle = GLES20.glGetAttribLocation(program, "a_Color");
        //GLES20.glEnableVertexAttribArray(colorHandle);
        //GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, BYTES_PER_COLOR, colorBuffer);
        //GLHelpers.checkGlError("color");

        int matrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix");
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelViewProjectionMatrix, 0);

        /*
         * When using glDrawArrays rendering works but the results are not correct, when using glDrawElements I get an GL_INVALID_OPERATION error.
         */
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indexBuffer.capacity(), GLES20.GL_SHORT, indexBuffer);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

        GLHelpers.checkGlError("draw");

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        //GLES20.glDisableVertexAttribArray(colorHandle);
    }

    private void generateSphereCoords(float radius, int rings, int sectors) {
        int v = 0, n = 0, t = 0;
        final float R = 1.0f/(float)(rings-1);
        final float S = 1.0f/(float)(sectors-1);
        for(int r = 0; r < rings; r++) for(int s = 0; s < sectors; s++) {
            final float y = (float) (Math.sin(-Math.PI + Math.PI * r * R));
            final float x = (float) (Math.cos(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));
            final float z = (float) (Math.sin(2 * Math.PI * s * S) * Math.sin(Math.PI * r * R));

            /*
            cubeTextureCoordinateData[t++] = s*S;
            cubeTextureCoordinateData[t++] = r*R;
            */

            vertexBuffer.put(x * radius);
            vertexBuffer.put(y * radius);
            vertexBuffer.put(z * radius);
/*
            cubeNormalData[n++] = x;
            cubeNormalData[n++] = y;
            cubeNormalData[n++] = z;*/
        }

        float[] indices = new float[rings * sectors * 4];
        int i = 0;
        for(int r = 0; r < rings-1; r++) for(int s = 0; s < sectors-1; s++) {
            indexBuffer.put((short) (r * sectors + s));
            indexBuffer.put((short) (r * sectors + (s+1)));
            indexBuffer.put((short) ((r+1) * sectors + (s+1)));
            indexBuffer.put((short) ((r+1) * sectors + s));
        }
    }
}
