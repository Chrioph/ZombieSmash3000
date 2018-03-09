package dev.codenmore.tilegame.gfx;

import dev.codenmore.tilegame.Maths.Matrix4f;
import dev.codenmore.tilegame.Maths.Vector3f;
import dev.codenmore.tilegame.utils.ShaderUtils;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

public class Shader {

    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;

    public static Shader BG, ENTITY, PIPE, FADE;

    private boolean enabled = false;

    private final int ID;
    private Map<String, Integer> locationCache = new HashMap<String, Integer>();

    public Shader(String vertex, String fragment) {
        ID = ShaderUtils.load(vertex, fragment);
    }

    public static void loadAll() {
        ENTITY = new Shader("/shaders/entity.vert", "/shaders/entity.frag");
    }

    public int getUniform(String name) {
        if (locationCache.containsKey(name))
            return locationCache.get(name);

        int result = glGetUniformLocation(ID, name);
        if (result == -1)
            System.err.println("Could not find uniform variable '" + name + "'!");
        else
            locationCache.put(name, result);
        return result;
    }

    public void setUniform1i(String name, int value) {
        if (!enabled) enable();
        glUniform1i(getUniform(name), value);
    }

    public void setUniform1f(String name, float value) {
        if (!enabled) enable();
        glUniform1f(getUniform(name), value);
    }

    public void setUniform2f(String name, float x, float y) {
        if (!enabled) enable();
        glUniform2f(getUniform(name), x, y);
    }

    public void setUniform3f(String name, Vector3f vector) {
        if (!enabled) enable();
        glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
    }

    public void setUniformMat4f(String name, Matrix4f matrix) {
        if (!enabled) enable();
        glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
    }

    public void enable() {
        glUseProgram(ID);
        enabled = true;
    }

    public void disable() {
        glUseProgram(0);
        enabled = false;
    }

}
