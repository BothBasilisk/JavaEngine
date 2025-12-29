package engine.shader;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.*;

public class ShaderProgram {
    private int vertexShaderId;
    private int fragmentShaderId;
    private int programId;

    private Map<String, Integer> uniforms;

    public ShaderProgram(String vertexShader, String fragmentShader){
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        createShader(vertexShaderId, vertexShader);
        createShader(fragmentShaderId, fragmentShader);

        createProgram();

        uniforms = new HashMap<>();
    }

    public void bind(){
        glUseProgram(programId);
    }

    public void unbind(){
        glUseProgram(0);
    }

    public void cleanup(){
        glDeleteProgram(programId);
    }

    private void createShader(int shaderId, String shaderString){
        glShaderSource(shaderId, shaderString);
        glCompileShader(shaderId);

        int[] resultCompilation = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, resultCompilation);

        if(resultCompilation[0] == GL_FALSE){
            System.out.println(glGetShaderInfoLog(shaderId));
        }
    }

    private void createProgram(){
        programId = glCreateProgram();

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);

        int[] resultProgram = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, resultProgram);
        if (resultProgram[0] == GL_FALSE) {
            System.out.println("Program linking error: " + glGetProgramInfoLog(programId));
        }
    }

    public void createUniform(String uniformName){
        int result = glGetUniformLocation(programId, uniformName);

        if(result < 0){
            throw new RuntimeException("Error while creating uniform");
        }

        uniforms.put(uniformName, result);
    }

    public void setUniforms(String uniformName, float x, float y, float z){
        int location = uniforms.get(uniformName);

        glUniform3f(location, x, y, z);
    }
}
