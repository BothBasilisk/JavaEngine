package engine.graph;

import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;

public class Mesh {
    private int vaoId;
    private int vboIdPosition;
    private int vboIdColors;
    private int vboIdIndices;
    private int vertexCount;

    public Mesh(float[] positions, float[] colors, int[] indices){
        vertexCount = indices.length;
        init(positions, colors, indices);
    }

    private void init(float[] positions, float[] colors, int[] indices){
        //VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        //VBO
        //Position
        vboIdPosition = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIdPosition);
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        //Color
        vboIdColors = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIdColors);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        //Indices
        vboIdIndices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIdIndices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Cleaning
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render(){
        //Binding and attributes
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Drawing
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        //Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public void cleanup(){
        glDeleteBuffers(vboIdPosition);
        glDeleteBuffers(vboIdColors);
        glDeleteBuffers(vboIdIndices);
        glDeleteVertexArrays(vaoId);
    }
}
