package com.example.igorcouto.augumentedreallity;

import android.opengl.GLES20;
import android.util.Log;

public class Shaders {

    public enum ShaderType{
        PINK_TEST,
        SCREEN,
        AXIS,
        MODEL;
    }

    private static Shaders instance;
    private int[] shaderList;
    private String[] vertexShaderSources;
    private String[] fragmentShaderSources;

    private Shaders(){
        shaderList = new int[ShaderType.values().length];
        vertexShaderSources = new String[ShaderType.values().length];
        fragmentShaderSources = new String[ShaderType.values().length];
        setShaderSources();
    }

    public static Shaders getInstance(){
        if(instance == null){
            instance = new Shaders();
        }
        return instance;
    }

    public void loadShaders(){
        for(int i = 0; i < shaderList.length; i++){
            int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
            GLES20.glShaderSource(vertexShader,vertexShaderSources[i]);
            GLES20.glCompileShader(vertexShader);

            int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
            GLES20.glShaderSource(fragmentShader,fragmentShaderSources[i]);
            GLES20.glCompileShader(fragmentShader);

            shaderList[i] = GLES20.glCreateProgram();
            GLES20.glAttachShader(shaderList[i], vertexShader);
            GLES20.glAttachShader(shaderList[i], fragmentShader);

            GLES20.glBindAttribLocation(shaderList[i], 0, "aPosition");

            GLES20.glLinkProgram(shaderList[i]);
        }
    }


    public int getShader(ShaderType shader){
        return shaderList[shader.ordinal()];
    }


    void setShaderSources(){

        vertexShaderSources[0] =
                "precision mediump float;\n" +
                        "attribute vec4 aPosition;\n" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "void main() {\n" +
                        "\tgl_Position = uMVPMatrix * aPosition;\n" +
                        "}";
        fragmentShaderSources[0] =
                "void main(){\n" +
                        "\tgl_FragColor = vec4(1.0, 0.0, 1.0, 1.0); \n" +
                        "}";

        vertexShaderSources[1] =
                "precision mediump float;\n" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "uniform float uCRatio;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "varying vec2 vTextureNormCoord;\n" +
                        "\n" +
                        "void main() {\n" +
                        "\tvec4 scaledPos = aPosition;\n" +
                        "\tscaledPos.x = scaledPos.x * uCRatio;\n" +
                        "\tscaledPos.y = scaledPos.y * uCRatio;\n" +
                        "\tgl_Position = uMVPMatrix * scaledPos;\n" +
                        "\tvTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "\tvTextureNormCoord = aTextureCoord.xy;\n" +
                        "}";
        fragmentShaderSources[1] =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "varying vec2 vTextureNormCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n";

        vertexShaderSources[2] =
                "precision mediump float;\n" +
                        "attribute vec3 inputColor"+
                        "varying vec4 color;\n" +
                        "attribute vec4 aPosition;\n" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "void main() {\n" +
                        "\tgl_Position = uMVPMatrix * aPosition;\n" +
                        "\tcolor = vec4(color, 0.1);" +
                        "}";

        fragmentShaderSources[2] =
                "varying vec4 color;\n" +
                        "void main(){\n" +
                        "\tgl_FragColor = color ; \n" +
                        "}";


        vertexShaderSources[3] =
                "precision mediump float;\n" +
                        "attribute vec3 aPosition;\n" +
                        "attribute vec2 a_TexCoordinate;\n" +
                        "uniform mat4 uMVPMatrix;\n" +
                        "varying vec2 v_TexCoordinate;\n" +
                        "\n" +
                        "void main() {\n" +
                        "\tgl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y, aPosition.z, 1.0);\n" +
                        "\tv_TexCoordinate = a_TexCoordinate;\n" +
                        "}";

        fragmentShaderSources[3] =
                        "uniform sampler2D u_Texture;\n" +
                                "varying vec2 v_TexCoordinate;\n" +
                                "\n" +
                                "void main(){\n" +
                                "\tgl_FragColor = texture2D(u_Texture, v_TexCoordinate);\n" +
                                "}";
    }

}
