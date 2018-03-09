package dev.codenmore.tilegame.input;

/**
 * Created by Freddy on 09.03.2018.
 */

import dev.codenmore.tilegame.ui.UIManager;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

    private static long window;
    private static final int KEYBOARD_SIZE = 512;
    private static final int MOUSE_SIZE = 16;

    public static double posX;
    public static double posY;
    private static UIManager uiManager;

    private static int[] keyStates = new int[KEYBOARD_SIZE];
    private static boolean[] activeKeys = new boolean[KEYBOARD_SIZE];

    private static int[] mouseButtonStates = new int[MOUSE_SIZE];
    private static boolean[] activeMouseButtons = new boolean[MOUSE_SIZE];
    private static long lastMouseNS = 0;
    private static long mouseDoubleClickPeriodNS = 1000000000 / 5; //5th of a second for double click.

    private static int NO_STATE = -1;


    protected static GLFWKeyCallback keyboard = new GLFWKeyCallback()
    {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods)
        {
            activeKeys[key] = action != GLFW_RELEASE;
            keyStates[key] = action;
        }
    };

    protected static GLFWMouseButtonCallback mouse = new GLFWMouseButtonCallback()
    {
        @Override
        public void invoke(long window, int button, int action, int mods)
        {
            activeMouseButtons[button] = action != GLFW_RELEASE;
            mouseButtonStates[button] = action;
            boolean flag = mouseButtonStates[button] == GLFW_RELEASE;
            if(flag) {
                onMouseReleased();
            }
        }
    };

    protected static GLFWCursorPosCallback cursor = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            posX = xpos;
            posY = ypos;
            onMouseMove();
        }
    };

    protected static void onMouseReleased()
    {
        if(uiManager != null)
            uiManager.onMouseReleaseGL();
    }

    protected static void onMouseMove()
    {
        if(uiManager != null)
            uiManager.onMouseMoveGL();
    }

    protected static void init(long window, UIManager uiman)
    {
        InputHandler.window = window;
        uiManager = uiman;

        resetKeyboard();
        resetMouse();
    }

    protected static void tick()
    {
        resetKeyboard();
        resetMouse();

        glfwPollEvents();
    }

    private static void resetKeyboard()
    {
        for (int i = 0; i < keyStates.length; i++)
        {
            keyStates[i] = NO_STATE;
        }
    }

    private static void resetMouse()
    {
        for (int i = 0; i < mouseButtonStates.length; i++)
        {
            mouseButtonStates[i] = NO_STATE;
        }

        long now = System.nanoTime();

        if (now - lastMouseNS > mouseDoubleClickPeriodNS)
            lastMouseNS = 0;
    }

    public static boolean keyDown(int key)
    {
        return activeKeys[key];
    }

    public static boolean keyPressed(int key)
    {
        return keyStates[key] == GLFW_PRESS;
    }

    public static boolean keyReleased(int key)
    {
        return keyStates[key] == GLFW_RELEASE;
    }

    public static boolean mouseButtonDown(int button)
    {
        return activeMouseButtons[button];
    }

    public static boolean mouseButtonPressed(int button)
    {
        return mouseButtonStates[button] == GLFW_RELEASE;
    }

    public static boolean mouseButtonReleased(int button)
    {
        boolean flag = mouseButtonStates[button] == GLFW_RELEASE;

        if (flag)
            lastMouseNS = System.nanoTime();

        return flag;
    }

    public static boolean mouseButtonDoubleClicked(int button)
    {
        long last = lastMouseNS;
        boolean flag = mouseButtonReleased(button);

        long now = System.nanoTime();

        if (flag && now - last < mouseDoubleClickPeriodNS)
        {
            lastMouseNS = 0;
            return true;
        }

        return false;
    }
}
