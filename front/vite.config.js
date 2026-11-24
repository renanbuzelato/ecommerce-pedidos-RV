/// <reference types="vitest/config" />
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

// VITE_BASE permite publicar em subpasta (ex.: GitHub Pages -> /ecommerce-pedidos-RV/)
export default defineConfig({
  base: process.env.VITE_BASE || '/',
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    // Em desenvolvimento, VITE_API_URL=/api faz o Vite repassar as chamadas ao
    // Spring Boot (porta 8080) — assim não é preciso configurar CORS no back.
    proxy: { '/api': 'http://localhost:8080' },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/test/setup.js',
    include: ['src/**/*.test.{js,jsx}'],
    css: false,
  },
});
