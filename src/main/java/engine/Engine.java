package engine;

import engine.graph.Mesh;
import engine.shader.ShaderProgram;
import engine.utils.Utils;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Engine {
    private long window;

    float[] positions = new float[]{
            -0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f
    };
    float[] colors = new float[]{
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 1.0f, 1.0f
    };
    int[] indices = new int[]{
            0, 1, 3,
            3, 1, 2
    };

    ShaderProgram shaderProgram;
    Mesh mesh;

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

        try{
            String vertexShader = Utils.loadResource("/shaders/vertex.vert");
            String fragmentShader = Utils.loadResource("/shaders/fragment.frag");

            //Loading shaders
            shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

            shaderProgram.createUniform("uColor");
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error while loading shaders");
        }

        mesh = new Mesh(positions, colors, indices);
    }

    private void loop(){
        while(!glfwWindowShouldClose(window)){
            double time = glfwGetTime();
            float greenValue = (float) (Math.sin(time) / 2.0f + 0.5f);

            //cleaning the screen and setting buffers and listeners for events
            glClear(GL_COLOR_BUFFER_BIT);

            shaderProgram.bind();
            shaderProgram.setUniforms("uColor", 0.0f, greenValue, 0.0f);

            mesh.render();

            shaderProgram.unbind();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup(){
        //Destroying the window
        glfwDestroyWindow(window);
        //Cleaning
        shaderProgram.cleanup();
        mesh.cleanup();
        //Terminating
        glfwTerminate();
    }
}
