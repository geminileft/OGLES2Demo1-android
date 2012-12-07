precision mediump float;

varying vec2 vTextureCoord;
varying vec4 vColor;

uniform sampler2D sTexture;
uniform bool uColorOnly;

void main() {
    vec4 color;
    color = vColor;
	gl_FragColor = color;
}
