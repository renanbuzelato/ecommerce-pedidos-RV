/** Contêiner visual padrão. `as` permite usar <article>, <section> etc. (semântica). */
export function Card({ as: Tag = 'div', className = '', children, ...props }) {
  return (
    <Tag
      className={`rounded-xl border border-slate-200 bg-white shadow-sm ${className}`}
      {...props}
    >
      {children}
    </Tag>
  );
}
