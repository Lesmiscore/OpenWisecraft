(function(xhr,supported){
   supported = (function () {
        try {
            var canvas = document.createElement('canvas');
            var webGLContext = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");
            return !!( window.WebGLRenderingContext && webGLContext && webGLContext.getShaderPrecisionFormat );
        } catch (e) {
            return false;
        }
    })()&&(function () {
        try {
            new THREE.WebGLRenderer();
            return true;
        } catch ( e ) {
            return false;
        }
    })();
    
    xhr.onsuccess=xhr.onerror=function(){};
    xhr.open("GET","/xhr/webgl_"+(supported?"available":"bad"),true);
    xhr.send();
})(new XMLHttpRequest());
