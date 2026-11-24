const paletas = {
  Eletrônicos: ['#1e3a8a', '#60a5fa'],
  Informática: ['#312e81', '#a5b4fc'],
  Casa: ['#7c2d12', '#fdba74'],
  Livros: ['#14532d', '#86efac'],
  Esportes: ['#701a75', '#f0abfc'],
};

/** Ilustração gerada em SVG quando o produto não tem foto cadastrada. */
function ilustracao(nome, categoria) {
  const [fundo, detalhe] = paletas[categoria] ?? ['#334155', '#cbd5e1'];
  const iniciais = nome
    .split(' ')
    .filter((p) => p.length > 2)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join('');
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 300">
    <rect width="400" height="300" fill="${fundo}"/>
    <circle cx="330" cy="60" r="90" fill="${detalhe}" opacity=".25"/>
    <circle cx="60" cy="260" r="70" fill="${detalhe}" opacity=".2"/>
    <text x="200" y="175" font-family="Arial, sans-serif" font-size="96" font-weight="700"
      fill="#fff" text-anchor="middle">${iniciais}</text></svg>`;
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`;
}

export function ProductImage({ produto, className = '' }) {
  return (
    <img
      src={produto.imagem || ilustracao(produto.nome, produto.categoria)}
      alt={`Imagem ilustrativa do produto ${produto.nome}`}
      className={`aspect-[4/3] w-full object-cover ${className}`}
      loading="lazy"
      width="400"
      height="300"
    />
  );
}
