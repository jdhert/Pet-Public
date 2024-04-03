const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  chainWebpack: config => {
    config.plugins.delete('prefetch'); // This deletes the prefetch plugin
  },
  devServer: {
    proxy: {
      "/api": {
        target: "http://localhost:8080", 
        changeOrigin: true,
      },
      '/oauth2.0': {
        target: 'https://nid.naver.com',
        changeOrigin: true,
      },
      "/getimage": {
        target : "https://dapi.kakao.com",
        changeOrigin: true,
        pathRewrite: { '^/getimage' : '/v2/search/image'},
      }
    }
  },
});