precision lowp float;

varying vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform float mProgress;

void main(){
     vec2 v1 = normalize(vec2(0.0, 1.0));
     vec2 v2 = normalize(vec2(vTextureCoord.x - 0.5, vTextureCoord.y - 0.5));
     float tempAngle = degrees(acos(dot(v1, v2)));
     if (tempAngle > mProgress){
        discard;
     } else {
        gl_FragColor = texture2D(sTexture, vTextureCoord);
     }
}