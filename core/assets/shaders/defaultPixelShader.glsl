// From tutorial project at: http://www.alcove-games.com/opengl-es-2-tutorials/lightmap-shader-fire-effect-glsl/
// For Libgdx shader information: https://github.com/libgdx/libgdx/wiki/Shaders

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 vColor;
varying vec2 vTexCoord;

//our texture samplers (libgdx is expecting a u_texture)
uniform sampler2D u_texture; //diffuse map

void main() {
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);
	gl_FragColor = vColor * DiffuseColor;
}
