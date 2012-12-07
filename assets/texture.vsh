uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;

attribute vec4 aVertices;
attribute vec4 aColor;

varying vec2 vTextureCoord;
varying vec4 vColor;

void main() {
	gl_Position = (uProjectionMatrix * uViewMatrix) * aVertices;
    vColor = aColor;
}
