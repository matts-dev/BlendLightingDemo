// From tutorial project at: http://www.alcove-games.com/opengl-es-2-tutorials/lightmap-shader-fire-effect-glsl/
// For Libgdx shader information: https://github.com/libgdx/libgdx/wiki/Shaders
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec4 vColor;
varying vec2 vTexCoord;

void main() {
	vColor = a_color;
	vTexCoord = a_texCoord0;
	gl_Position = u_projTrans * a_position;		
}
