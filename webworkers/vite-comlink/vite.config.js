import {defineConfig} from 'vite';

export default defineConfig({
  root: 'src',
  publicDir: false,
  build: {
    outDir: '../dist',
    emptyOutDir: true
  }
});