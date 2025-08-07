/* Example01.java - Create a window. */


import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;

import static java.lang.System.*;

import java.util.Scanner;


public class Example01 {
	public static void main( String[] args ) {
		long my_window;
		
		if ( !glfwInit() ) {
			return;
		}
		
		out.println( "glfwInit ok." );
		
		my_window = glfwCreateWindow( 640, 640, "Hello World", NULL, NULL );
		if ( my_window == NULL ) {
			glfwTerminate();
			return;
		}
		
		glfwSetWindowPos( my_window, 640, 0 );
		
		glfwMakeContextCurrent( my_window );
		GL.createCapabilities();
		
		
		out.printf( "GL_VENDOR: %s\n", glGetString( GL_VENDOR ) );
		out.printf( "GL_RENDERER: %s\n", glGetString( GL_RENDERER ) );
		out.printf( "GL_VERSION: %s\n", glGetString( GL_VERSION ) );
		
		
		
		while ( !glfwWindowShouldClose( my_window ) ) {
			glClear( GL_COLOR_BUFFER_BIT );
			
			glfwSwapBuffers( my_window );
			glfwPollEvents();
		}
		
		glfwTerminate();
		out.println( "glfwTerminate ok." );
		new Scanner( System.in ).nextLine();		
	}
}

