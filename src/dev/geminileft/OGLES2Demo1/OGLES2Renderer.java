package dev.geminileft.OGLES2Demo1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class OGLES2Renderer implements GLSurfaceView.Renderer {

	//size in bytes of float
    private final int FLOAT_SIZE = 4;
    private int maVertices;
    private int mWidth;
    private int mHeight;
    
	public OGLES2Renderer(GLSurfaceView view) {
		super();
		
		//set OGLES version
        view.setEGLContextClientVersion(2);
	}
	
	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		String vSource;
		String fSource;
		vSource = GraphicsUtils.readShaderFile("texture.vsh");
		fSource = GraphicsUtils.readShaderFile("texture.fsh");
		int progId = GraphicsUtils.createProgram(vSource, fSource);
		GraphicsUtils.activateProgram(progId);
		GLES20.glClearColor(0, 0, 0, 1);
		
		//read attribute locations and enable
		int aColor = GLES20.glGetAttribLocation(GraphicsUtils.currentProgramId(), "aColor");
		GLES20.glEnableVertexAttribArray(aColor);
		maVertices = GLES20.glGetAttribLocation(GraphicsUtils.currentProgramId(), "aVertices");
		GLES20.glEnableVertexAttribArray(maVertices);

		//set color buffer and pass to shader
	    float colors[] = {
	        1.0f, 0.0f, 0.0f, 0.0f
	        , 1.0f, 0.0f, 0.0f, 0.0f
	        , 1.0f, 0.0f, 0.0f, 0.0f
	        , 1.0f, 0.0f, 0.0f, 0.0f
	        , 1.0f, 0.0f, 0.0f, 0.0f
	        , 1.0f, 0.0f, 0.0f, 0.0f
	    };
        FloatBuffer colorBuffer = ByteBuffer.allocateDirect(colors.length
                * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(colors).position(0);
	    GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		mWidth = width;
		mHeight = height;
		
	    GLES20.glViewport(0, 0, width, height);
	    
	    //set matrices and pass to shader
	    float projMatrix[] = new float[16];
	    Matrix.orthoM(projMatrix, 0, 0, width, 0, height, -1, 1);
	    float viewMatrix[] = new float[16];
	    Matrix.setIdentityM(viewMatrix, 0);
	    float texMatrix[] = new float[16];
	    Matrix.setIdentityM(texMatrix, 0);
	    int uProjectionMatrix = GLES20.glGetUniformLocation(GraphicsUtils.currentProgramId(), "uProjectionMatrix");
	    int uViewMatrix = GLES20.glGetUniformLocation(GraphicsUtils.currentProgramId(), "uViewMatrix");
	    int uTextureMatrix = GLES20.glGetUniformLocation(GraphicsUtils.currentProgramId(), "uTextureMatrix");
	    int uColorOnly = GLES20.glGetUniformLocation(GraphicsUtils.currentProgramId(), "uColorOnly");
	    GLES20.glUniformMatrix4fv(uProjectionMatrix, 1, false, projMatrix, 0);
	    GLES20.glUniformMatrix4fv(uViewMatrix, 1, false, viewMatrix, 0);
	    GLES20.glUniformMatrix4fv(uTextureMatrix, 1, false, texMatrix, 0);
	    GLES20.glUniform1f(uColorOnly, 1);
	    
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		//clear screen
	    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

	    //to move quad around screen for drawing modes
	    final float offset = 150;

	    //measure out vertices and set for quad
	    final float SIZE = 50;
	    final float HALF_WIDTH = mWidth / 2.0f;
	    final float HALF_HEIGHT = mHeight / 2.0f;
	    
	    final float leftX = HALF_WIDTH - SIZE;
	    final float rightX = HALF_WIDTH + SIZE;
	    final float topY = HALF_HEIGHT + SIZE;
	    final float bottomY = HALF_HEIGHT - SIZE;
	    
        FloatBuffer verticesBuffer;
        
        //triangle strip
	    final float verticesTriangleStrip[] = {
	    	leftX - offset, bottomY + offset
	        , leftX - offset, topY + offset
	        , rightX - offset, bottomY + offset
	        , rightX - offset, topY + offset
	    };
        verticesBuffer = ByteBuffer.allocateDirect(verticesTriangleStrip.length
                * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(verticesTriangleStrip).position(0);
		GLES20.glVertexAttribPointer(maVertices, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

	    //triangle fan
	    final float verticesTriangleFan[] = {
	    	leftX + offset, bottomY + offset
	        , leftX + offset, topY + offset
	        , rightX + offset, topY + offset
	        , rightX + offset, bottomY + offset
	    };
        verticesBuffer = ByteBuffer.allocateDirect(verticesTriangleFan.length
                * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(verticesTriangleFan).position(0);
		GLES20.glVertexAttribPointer(maVertices, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
	    
	    //triangles
	    final float verticesTriangle[] = {
	    	leftX, bottomY - offset
	        , rightX, bottomY - offset
	        , leftX, topY - offset
	        , rightX, bottomY - offset
	        , leftX, topY - offset
	        , rightX, topY - offset
	    };	    
        verticesBuffer = ByteBuffer.allocateDirect(verticesTriangle.length
                * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(verticesTriangle).position(0);
		GLES20.glVertexAttribPointer(maVertices, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
}
