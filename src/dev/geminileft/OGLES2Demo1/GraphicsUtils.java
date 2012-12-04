package dev.geminileft.OGLES2Demo1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GraphicsUtils {

	private static Context mContext = null;
	private static int mCurrentProgram = -1;
	private static HashMap<Integer, Integer> mTextureCache = new HashMap<Integer, Integer>();
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
    public static String readShaderFile(String filename) {
		StringBuffer resultBuffer = new StringBuffer();
		try {
			final int BUFFER_SIZE = 1024;
			char buffer[] = new char[BUFFER_SIZE];
			int charsRead;
			InputStream stream = mContext.getAssets().open(filename);
			InputStreamReader reader = new InputStreamReader(stream);
    		resultBuffer = new StringBuffer();
			while ((charsRead = reader.read(buffer, 0, BUFFER_SIZE)) != -1) {
				resultBuffer.append(buffer, 0, charsRead);
			}
			reader.close();
			stream.close();
		} catch (Exception e) {
			Log.v("info", "very bad");
		}
		return resultBuffer.toString();
    }    

    public static int createProgram(String vertexSource, String fragmentSource) {
        int program = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        GLES20.glAttachShader(program, vertexShader);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("Error", "Could not link program: ");
            Log.e("Error", GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, source);
        checkGlError("glShaderSource");
        GLES20.glCompileShader(shader);
        checkGlError("glCompileShader");
        return shader;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
        	//String errorMsg = GLES20.glGetProgramInfoLog(mCurrentProgram);
            //Log.e("info", op + ": glError " + error + errorMsg);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
    
    public static void activateProgram(int programId) {
        mCurrentProgram = programId;
        GLES20.glUseProgram(programId);
    }
    
    public static int currentProgramId() {
    	return mCurrentProgram;
    }
    
	public static int getTexture2D(int resourceId) {
		int resultTextureId = -1;
		if (mTextureCache.containsKey(resourceId)) {
			resultTextureId = mTextureCache.get(resourceId);
		} else {
			int textures[] = new int[1];
			GLES20.glGenTextures(1, textures, 0);
			int error = GLES20.glGetError();
			if (error != 0) {
				Log.v("error", "here");
			}
			InputStream is = mContext.getResources().openRawResource(resourceId);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			//gl.glTexEnvf(GLES20.GL_TEXTURE_ENV, GLES20.GL_TEXTURE_ENV_MODE, GLES20.GL_MODULATE); //GL10.GL_REPLACE);
			
			Bitmap bitmap = null;
			try {
				//BitmapFactory is an Android graphics utility for images
				bitmap = BitmapFactory.decodeStream(is);
				Log.v("GraphicsUtils", "getTexture2D after decoding");
			}
			catch (Exception e) {
				Log.v("GraphicsUtils", "getTexture2D has an exception");
			} finally {
				//Always clear and close
					try {
						is.close();
						is = null;
					} catch (IOException e) {
					}
			}
				
			final int bitmapHeight = bitmap.getHeight();
			final int bitmapWidth = bitmap.getWidth();
			final int textureHeight = closestPowerOf2(bitmapHeight);
			final int textureWidth = closestPowerOf2(bitmapWidth);
			if ((bitmapHeight == textureHeight) && (bitmapWidth == textureWidth)) {
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				/*
				int error = GLES20.glGetError();
				if (error != 0) {
					Log.v("error", "here");
				}
				*/
			} else {
				Bitmap adjustedBitmap = Bitmap.createScaledBitmap(bitmap, textureHeight, textureWidth, false);
		        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, adjustedBitmap, 0);
		        adjustedBitmap.recycle();
			}
			bitmap.recycle();
			resultTextureId = textures[0];
			mTextureCache.put(resourceId, resultTextureId);
		}
		return resultTextureId;
	}

	final static int MAX_TEXTURE_SIZE = 1024;

	public static int closestPowerOf2(int n) {
		int c = 1;
		while (c < n && c < MAX_TEXTURE_SIZE)
			c <<= 1;
		return c;
	}

}
