/* Example02.java - A rotating colorful triangle! */

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import static java.lang.System.*;

import java.nio.*;

import org.joml.*;


public class Example02 {
	public static class ErrorCallback implements GLFWErrorCallbackI {
		@Override
		public void invoke( int error, long description ) {
			err.println( "GLFW Error: " + error + ": " + GLFWErrorCallback.getDescription( description ) );
		}
	}
	
	public static class KeyCallback implements GLFWKeyCallbackI {
		@Override
		public void invoke( long window, int key, int scancode, int action, int mods ) {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_PRESS ) {
				glfwSetWindowShouldClose( window, true );
			}
		}
	}
	
	
	
	
	public static void main( String[] args ) {
		final int FLOAT_SIZE_BYTES = 4;
		final int VERTEX_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
	
	
	
		float[] vertices = { /* Keep in mind this name: vertices! */
			-0.6f, -0.4f, 1f, 0f, 0f,
			 0.6f, -0.4f, 0f, 1f, 0f,
			   0f,  0.6f, 0f, 0f, 1f,
		};
		
	
		String vertex_shader_text =
			"#version 110\n" + 
			"uniform mat4 MVP;\n" + 
			"attribute vec3 vCol;\n" +
			"attribute vec2 vPos;\n" +
			"varying vec3 color;\n" +
			"void main()\n" +
			"{\n" +
			"	gl_Position = MVP * vec4( vPos, 0.0, 1.0 );\n" +
			"	color = vCol;\n" +
			"}\n";
		
		
		String fragment_shader_text = 
			"#version 110\n" +
			"varying vec3 color;\n" +
			"void main()\n" +
			"{\n" +
			"	gl_FragColor = vec4( color, 1.0 );\n" +
			"}\n";
	
	
	
		long my_window;
	
		
		int vertex_buffer, vertex_shader, fragment_shader, program;
		int mvp_location, vpos_location, vcol_location;
	
	
		GLFWErrorCallback.create( new ErrorCallback() ).set();
		
		KeyCallback key_callback = new KeyCallback();
		
		
		if ( !glfwInit() ) {
			System.exit( -1 );
		}
		
		glfwWindowHint( GLFW_RESIZABLE, GLFW_FALSE );
		glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 2 );
		glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 0 );
		
		my_window = glfwCreateWindow( 640, 480, "Simple example", NULL, NULL );
		if ( my_window == NULL ) {
			glfwTerminate();
			System.exit( -1 );
		}
		
		glfwSetWindowPos( my_window, 300, 100 );
		
		glfwSetKeyCallback( my_window, key_callback );
		
		glfwMakeContextCurrent( my_window );
		GL.createCapabilities();
		
		glfwSwapInterval( 1 );
		
		
		vertex_buffer = glGenBuffers();
		
		glBindBuffer( GL_ARRAY_BUFFER, vertex_buffer );
		
		
		/* Use a MemoryStack to allocate a temporary FloatBuffer. */
		try( MemoryStack stack = MemoryStack.stackPush() ) {
			/* Allocate enough memory for the floats. */
			FloatBuffer vertex_buffer_of_vertices = stack.mallocFloat( vertices.length );
			
			/* Put the data from the Java array into the buffer. */
			vertex_buffer_of_vertices.put( vertices ).flip();
			
			/* Now, call glBufferData with the FloatBuffer. */
			glBufferData( GL_ARRAY_BUFFER, vertex_buffer_of_vertices, GL_STATIC_DRAW );
		}
		
		
		/* Create a new shader and store its ID. */
		vertex_shader = glCreateShader( GL_VERTEX_SHADER );
		
		/* Pass the String to glShaderSource. */
		glShaderSource( vertex_shader, vertex_shader_text );
		
		/* Compile the shader. */
	        glCompileShader( vertex_shader );
	        
	        /* vertex_shader: INFO LOG? */
	        int vertex_shader_compile_status = glGetShaderi( vertex_shader, GL_COMPILE_STATUS );
	        if ( vertex_shader_compile_status == 0 ) {
	        	/* Get the info log as a String. */
	        	String infoLog = glGetShaderInfoLog( vertex_shader );
	        	
	        	/* Print the log and throw an exception. */
            		err.print( "vertex_shader compilation failed - " );
           		err.println( infoLog );
           		
           		/* Exit */
           		System.exit( -1 );
	        }
	        out.println( "vertex_shader compiled successfully." );
	        
	        
	        /* Create a new fragment and store its ID. */
		fragment_shader = glCreateShader( GL_FRAGMENT_SHADER );
		
		/* Pass the String to glShaderSource. */
		glShaderSource( fragment_shader, fragment_shader_text );
		
		/* Compile the shader. */
	        glCompileShader( fragment_shader );
	        
	        /* fragment_shader: INFO LOG? */
	        int fragment_shader_compile_status = glGetShaderi( fragment_shader, GL_COMPILE_STATUS );
	        if ( fragment_shader_compile_status == 0 ) {
	        	/* Get the info log as a String. */
	        	String infoLog = glGetShaderInfoLog( fragment_shader );
	        	
	        	/* Print the log and throw an exception. */
            		err.print( "fragment_shader compilation failed - " );
           		err.println( infoLog );
           		
           		/* Exit */
           		System.exit( -1 );
	        }
	        out.println( "fragment_shader compiled successfully." );
	        
	        
	        
	        program = glCreateProgram();
	        glAttachShader( program, vertex_shader );
	        glAttachShader( program, fragment_shader );
	        glLinkProgram( program );
	        
	        
	        mvp_location = glGetUniformLocation( program, "MVP" );
	        vpos_location = glGetAttribLocation( program, "vPos" );
	        vcol_location = glGetAttribLocation( program, "vCol" );
	        
	        glEnableVertexAttribArray( vpos_location );
	        glVertexAttribPointer(
		    vpos_location,                  /* attribute location */
		    2,                              /* number of components per vertex ( x,y ) */
		    GL_FLOAT,                       /* data type of components */
		    false,                          /* whether to normalize (false so NO normalize!) */
		    VERTEX_STRIDE_BYTES,            /* stride (size of one vertex) in bytes */
		    0L                              /* byte offset of the position data from the start of a vertex (0) */ 
		);
		
		glEnableVertexAttribArray(vcol_location);
		glVertexAttribPointer(
		    vcol_location,                  /* attribute location */
		    3,                              /* number of components per vertex */
		    GL_FLOAT,                       /* data type of components */
		    false,                          /* whether to normalize */
		    VERTEX_STRIDE_BYTES,            /* stride (size of one vertex) in bytes */
		    2L * FLOAT_SIZE_BYTES           /* byte offset of the color data (after 2 floats) */
		);

	        
	        
	  	
		while ( !glfwWindowShouldClose( my_window ) ) {
			float ratio;
			int width, height;
			
			
			Matrix4f m, p, mvp;
			m = new Matrix4f();
			p = new Matrix4f();
			mvp = new Matrix4f();
			
			
			
			try ( MemoryStack stack = MemoryStack.stackPush() ) {
			        /* Allocate a temporary IntBuffer on the stack to hold the width. */
			        IntBuffer width_buffer = stack.mallocInt( 1 );
			        /* Allocate a temporary IntBuffer on the stack to hold the height. */
			        IntBuffer height_buffer = stack.mallocInt( 1 );
			
			        /* Call the LWJGL function with the buffers. */
			        glfwGetFramebufferSize( my_window, width_buffer, height_buffer );
			
			        /* Retrieve the values from the buffers. */
			        width = width_buffer.get(0);
			        height = height_buffer.get(0);
    			} /* The stack is automatically popped and the memory is freed here. */
    			
    			ratio = width / (float)( height );

    			
    			glViewport( 0, 0, width, height );
    			glClear( GL_COLOR_BUFFER_BIT );
    			
    			
    			m.identity();
    			m.rotateZ( (float)( glfwGetTime() ) );
    			p.ortho( -ratio, ratio, -1.0f, 1.0f, 1.0f, -1.0f );
    			/* This performs the operation: mvp = p * m. */
			p.mul( m, mvp );
    			
    			glUseProgram( program );
    			
    			try (MemoryStack stack = MemoryStack.stackPush()) {
			    /* Allocate a FloatBuffer on the stack to hold the 16 floats of the matrix. */
			    FloatBuffer mvp_buffer = stack.mallocFloat( 16 );
			
			    /* Populate the buffer with the matrix data. */
			    mvp.get( mvp_buffer );
			
			    /* Upload the matrix to the shader. */
			    /* The LWJGL function's 'transpose' parameter is a boolean. */
			    /* GL_FALSE translates to false. */
			    glUniformMatrix4fv( mvp_location, false, mvp_buffer );
			}
			
			glDrawArrays( GL_TRIANGLES, 0, 3 );
		
			glfwSwapBuffers( my_window );
			glfwPollEvents();
		}
		
		
		
		
		glfwDestroyWindow( my_window );
		glfwTerminate();
		
		System.exit( 1 );
	}
}

