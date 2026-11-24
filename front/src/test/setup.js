import '@testing-library/jest-dom/vitest';
import { afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';
import { resetarMock } from '../services/api/mockServer';

// Cada teste começa com o banco simulado e o carrinho "zerados"
afterEach(() => {
  cleanup();
  localStorage.clear();
  resetarMock();
});

window.scrollTo = () => {};
