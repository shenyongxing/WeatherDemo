precision mediump float;

varying vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据

void main() {
	 vec4 tempColor = texture2D(sTexture, vTextureCoord);
	 if (tempColor.a < 0.1f) {
	 	discard ;
	 }
	 gl_FragColor = tempColor ;
}