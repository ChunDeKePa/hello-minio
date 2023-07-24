import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  // devServer:{
  //   host: '127.0.0.1',
  //   port: 8080,
  //   proxy: {
  //     '/minio': {
  //       target: 'http://127.0.0.1:8080/', // target host
  //       ws: true, // proxy websockets
  //       changeOrigin: true, // needed for virtual hosted sites
  //       pathRewrite: {
  //         '^/minio': '' // rewrite path
  //       }
  //     },
  //   }
  // },
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
