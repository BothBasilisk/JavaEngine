package engine;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;

public class Engine {
    private long window;

    private float[] vertexArray = {
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
             0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
             0.0f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f
    };

    private int vaoId;
    private int vboId;
    int shaderProgramId;

    public void run(){
        init();
        loop();
        cleanup();
    }

    private void init(){
        //Try to initialize GLFW
        if(!glfwInit()){
            throw new RuntimeException("Unable to initialize GLFW");
        }

        //Window configurations
        glfwWindowHint(GLFW_VISIBLE, 0);
        glfwWindowHint(GLFW_RESIZABLE, 1);

        //window generation
        window = glfwCreateWindow(800, 600, "Temporary title", 0, 0);

        //Setting context
        glfwMakeContextCurrent(window);

        //Activating V-Sync
        glfwSwapInterval(1);

        //Showing the window
        glfwShowWindow(window);

        GL.createCapabilities();

        //Loading shaders
        shaderLoading();
    }

    private void loop(){
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STATIC_DRAW);

        //Position
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*4, 0);
        //Color
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*4, 3*4);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        while(!glfwWindowShouldClose(window)){
            //cleaning the screen and setting buffers and listeners for events
            glClear(GL_COLOR_BUFFER_BIT);
            glUseProgram(shaderProgramId);
            glBindVertexArray(vaoId);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup(){
        //Destroying the window
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void shaderLoading(){
        String vertexShader = """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                layout (location = 1) in vec3 aColor;
                out vec3 ourColor;
                void main(){
                    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
                    ourColor = aColor;
                }""";

        String fragmentShader = """
                #version 330 core
                in vec3 ourColor;
                out vec4 FragColor;
                void main(){
                    FragColor = vec4(ourColor, 1.0);
                }""";

        int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertexShaderId, vertexShader);
        glCompileShader(vertexShaderId);
        glShaderSource(fragmentShaderId, fragmentShader);
        glCompileShader(fragmentShaderId);

        //Checking if shaders loaded correctly
        int[] resultVertex = new int[1];
        glGetShaderiv(vertexShaderId, GL_COMPILE_STATUS, resultVertex);
        if(resultVertex[0] == GL_FALSE){
            System.out.println(glGetShaderInfoLog(vertexShaderId));
        }

        int[] resultFragment = new int[1];
        glGetShaderiv(fragmentShaderId, GL_COMPILE_STATUS, resultFragment);
        if(resultFragment[0] == GL_FALSE){
            System.out.println(glGetShaderInfoLog(fragmentShaderId));
        }

        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexShaderId);
        glAttachShader(shaderProgramId, fragmentShaderId);
        glLinkProgram(shaderProgramId);

        int[] resultProgram = new int[1];
        glGetProgramiv(shaderProgramId, GL_LINK_STATUS, resultProgram);
        if (resultProgram[0] == GL_FALSE) {
            System.out.println("Program linking error: " + glGetProgramInfoLog(shaderProgramId));
        }
    }
}
