import { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { ResumoValores } from '../components/pedidos/ResumoValores';
import {
  Alert,
  Button,
  ButtonLink,
  Card,
  EmptyState,
  ErrorState,
  Input,
  Loading,
  PageHeader,
  Select,
} from '../components/ui';
import { useCart } from '../contexts/CartContext';
import { useAsync } from '../hooks/useAsync';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { clienteService } from '../services/clienteService';
import { pagamentoService } from '../services/pagamentoService';
import { pedidoService } from '../services/pedidoService';
import { FORMAS_PAGAMENTO, RESULTADOS_PAGAMENTO } from '../utils/constants';
import { formatarMoeda, mascararCartao, somenteDigitos } from '../utils/formatters';
import { focarPrimeiroErro } from '../utils/foco';
import { validarCartao } from '../utils/validators';

const ETAPAS = ['Entrega', 'Pagamento', 'Resultado'];

function Stepper({ atual }) {
  return (
    <ol className="mb-8 grid grid-cols-3 gap-2" aria-label="Etapas da compra">
      {ETAPAS.map((nome, i) => (
        <li
          key={nome}
          aria-current={i === atual ? 'step' : undefined}
          className={`rounded-lg border-b-4 px-2 py-2 text-center text-sm font-semibold ${
            i <= atual ? 'border-marca-700 text-marca-800' : 'border-slate-300 text-slate-500'
          }`}
        >
          <span className="sr-only">Etapa {i + 1} de 3: </span>
          {nome}
        </li>
      ))}
    </ol>
  );
}

function enderecoEmTexto(e) {
  return `${e.logradouro}, ${e.numero}${e.complemento ? ` - ${e.complemento}` : ''} — ${e.bairro}, ${e.cidade}/${e.uf} · CEP ${e.cep}`;
}

function vencimentoBoleto() {
  const d = new Date();
  d.setDate(d.getDate() + 3);
  return d.toLocaleDateString('pt-BR');
}

export default function CheckoutPage() {
  useDocumentTitle('Finalizar compra');
  const carrinho = useCart();
  const clientes = useAsync(() => clienteService.listar(), []);
  const titulo = useRef(null);

  const [etapa, setEtapa] = useState(0);
  const [clienteId, setClienteId] = useState('');
  const [enderecoId, setEnderecoId] = useState('');
  const [forma, setForma] = useState('PIX');
  const [cartao, setCartao] = useState({
    numero: '',
    titular: '',
    validade: '',
    cvv: '',
    parcelas: '1',
  });
  const [chavePix, setChavePix] = useState('');
  const [erros, setErros] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [erroEnvio, setErroEnvio] = useState(null);
  const [pedido, setPedido] = useState(null); // pedido criado (permite tentar pagar de novo)
  const [resultado, setResultado] = useState(null);

  const cliente = clientes.data?.find((c) => c.id === Number(clienteId));
  const endereco = cliente?.enderecos.find((e) => e.id === Number(enderecoId));
  const valores = pedido ?? carrinho;

  // Depois da 1ª tentativa, revalida a cada digitação: o erro some assim que o campo é corrigido
  function alterarCartao(campo, valor) {
    const novo = { ...cartao, [campo]: valor };
    setCartao(novo);
    if (Object.keys(erros).length) setErros(validarCartao(novo));
  }

  function irPara(n) {
    setEtapa(n);
    setErros({});
    setErroEnvio(null);
    setTimeout(() => titulo.current?.focus(), 0);
  }

  function escolherCliente(id) {
    setClienteId(id);
    const c = clientes.data?.find((x) => x.id === Number(id));
    setEnderecoId(c?.enderecos[0]?.id ? String(c.enderecos[0].id) : '');
    setChavePix(c?.email ?? '');
  }

  function confirmarEntrega(e) {
    e.preventDefault();
    const novos = {};
    if (!cliente) novos.cliente = 'Selecione o cliente.';
    else if (!endereco) novos.endereco = 'Selecione o endereço de entrega.';
    setErros(novos);
    if (novos.cliente) return focarPrimeiroErro(e.currentTarget);
    if (novos.endereco) return e.currentTarget.querySelector('input[name="endereco"]')?.focus();
    irPara(1);
  }

  async function confirmarPagamento(e) {
    e.preventDefault();
    const form = e.currentTarget;
    let novos = {};
    if (forma === 'CARTAO') novos = validarCartao(cartao);
    if (forma === 'PIX' && !chavePix.trim()) novos.chavePix = 'Informe a chave Pix.';
    setErros(novos);
    if (Object.keys(novos).length) return focarPrimeiroErro(form);

    setEnviando(true);
    setErroEnvio(null);
    try {
      // 1) cria o pedido (só na primeira tentativa) — o back baixa o estoque aqui
      let atual = pedido;
      if (!atual) {
        atual = await pedidoService.criar({
          clienteId: cliente.id,
          enderecoEntrega: endereco,
          itens: carrinho.itens,
        });
        setPedido(atual);
        carrinho.limpar();
      }
      // 2) envia o pagamento simulado
      const dados =
        forma === 'CARTAO'
          ? { ...cartao, numero: somenteDigitos(cartao.numero), parcelas: Number(cartao.parcelas) }
          : forma === 'PIX'
            ? { chave: chavePix }
            : { vencimento: vencimentoBoleto() };
      const r = await pagamentoService.processar({ pedidoId: atual.id, forma, dados });
      setResultado(r);
      irPara(2);
    } catch (err) {
      setErroEnvio(err);
    } finally {
      setEnviando(false);
    }
  }

  if (!pedido && carrinho.itens.length === 0) {
    return (
      <>
        <PageHeader titulo="Finalizar compra" />
        <EmptyState
          titulo="Não há itens para finalizar"
          acao={<ButtonLink to="/">Ver catálogo</ButtonLink>}
        >
          Seu carrinho está vazio.
        </EmptyState>
      </>
    );
  }

  const tituloEtapa = ['Dados da entrega', 'Forma de pagamento', 'Resultado do pagamento'][etapa];

  return (
    <>
      <PageHeader titulo="Finalizar compra" />
      <Stepper atual={etapa} />

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="p-6 lg:col-span-2">
          <h2 ref={titulo} tabIndex={-1} className="mb-4 text-xl font-bold outline-none">
            {tituloEtapa}
          </h2>

          {etapa === 0 && (
            <>
              {clientes.loading && <Loading texto="Carregando clientes..." />}
              {clientes.error && (
                <ErrorState erro={clientes.error} aoTentarNovamente={clientes.reload} />
              )}
              {clientes.data && (
                <form noValidate onSubmit={confirmarEntrega} className="space-y-5">
                  <Select
                    label="Cliente"
                    obrigatorio
                    placeholder="Selecione o cliente..."
                    opcoes={clientes.data.map((c) => ({
                      valor: String(c.id),
                      rotulo: `${c.nome} — ${c.email}`,
                    }))}
                    value={clienteId}
                    erro={erros.cliente}
                    onChange={(e) => escolherCliente(e.target.value)}
                    dica="Não encontrou? Cadastre o cliente na tela de Clientes."
                  />
                  <p className="-mt-3 text-sm">
                    <Link to="/clientes/novo" className="font-medium text-marca-700 underline">
                      Cadastrar novo cliente
                    </Link>
                  </p>

                  {cliente && (
                    <fieldset aria-describedby={erros.endereco ? 'erro-endereco' : undefined}>
                      <legend className="text-sm font-medium text-slate-800">
                        Endereço de entrega{' '}
                        <span className="text-red-700" aria-hidden="true">
                          *
                        </span>
                      </legend>
                      <div className="mt-2 space-y-2">
                        {cliente.enderecos.map((e) => (
                          <label
                            key={e.id}
                            className={`flex cursor-pointer gap-3 rounded-lg border p-3 ${
                              String(e.id) === enderecoId
                                ? 'border-marca-700 bg-marca-50'
                                : 'border-slate-300'
                            }`}
                          >
                            <input
                              type="radio"
                              name="endereco"
                              value={e.id}
                              checked={String(e.id) === enderecoId}
                              onChange={() => setEnderecoId(String(e.id))}
                              className="mt-1 accent-marca-700"
                            />
                            <span>
                              <span className="block font-semibold">{e.apelido || 'Endereço'}</span>
                              <span className="text-sm text-slate-700">{enderecoEmTexto(e)}</span>
                            </span>
                          </label>
                        ))}
                      </div>
                      {erros.endereco && (
                        <p id="erro-endereco" className="mt-1 text-sm font-medium text-red-700">
                          {erros.endereco}
                        </p>
                      )}
                    </fieldset>
                  )}
                  <div className="flex justify-end">
                    <Button type="submit">Continuar para pagamento</Button>
                  </div>
                </form>
              )}
            </>
          )}

          {etapa === 1 && (
            <form noValidate onSubmit={confirmarPagamento} className="space-y-5">
              {pedido && (
                <Alert tipo="aviso" titulo={`Pedido #${pedido.id} aguardando pagamento`}>
                  Escolha outra forma de pagamento ou tente novamente.
                </Alert>
              )}
              <p className="text-sm text-slate-700">
                Entrega para <strong>{cliente?.nome}</strong>:{' '}
                {endereco && enderecoEmTexto(endereco)}
              </p>
              <fieldset>
                <legend className="text-sm font-medium text-slate-800">Forma de pagamento</legend>
                <div className="mt-2 grid gap-2 sm:grid-cols-3">
                  {Object.entries(FORMAS_PAGAMENTO).map(([valor, rotulo]) => (
                    <label
                      key={valor}
                      className={`flex cursor-pointer items-center gap-2 rounded-lg border p-3 font-medium ${
                        forma === valor ? 'border-marca-700 bg-marca-50' : 'border-slate-300'
                      }`}
                    >
                      <input
                        type="radio"
                        name="forma"
                        value={valor}
                        checked={forma === valor}
                        onChange={() => {
                          setForma(valor);
                          setErros({});
                        }}
                        className="accent-marca-700"
                      />
                      {rotulo}
                    </label>
                  ))}
                </div>
              </fieldset>

              {forma === 'CARTAO' && (
                <div className="grid gap-4 sm:grid-cols-2">
                  <Input
                    label="Número do cartão"
                    obrigatorio
                    inputMode="numeric"
                    autoComplete="cc-number"
                    placeholder="0000 0000 0000 0000"
                    value={cartao.numero}
                    erro={erros.numero}
                    onChange={(e) => alterarCartao('numero', mascararCartao(e.target.value))}
                    dica="Simulação: cartões terminados em 0000 são recusados."
                    className="sm:col-span-2"
                  />
                  <Input
                    label="Nome impresso no cartão"
                    obrigatorio
                    autoComplete="cc-name"
                    value={cartao.titular}
                    erro={erros.titular}
                    onChange={(e) => alterarCartao('titular', e.target.value)}
                    className="sm:col-span-2"
                  />
                  <Input
                    label="Validade (MM/AA)"
                    obrigatorio
                    inputMode="numeric"
                    autoComplete="cc-exp"
                    placeholder="MM/AA"
                    value={cartao.validade}
                    erro={erros.validade}
                    onChange={(e) => {
                      const d = somenteDigitos(e.target.value).slice(0, 4);
                      alterarCartao(
                        'validade',
                        d.length > 2 ? `${d.slice(0, 2)}/${d.slice(2)}` : d,
                      );
                    }}
                  />
                  <Input
                    label="CVV"
                    obrigatorio
                    inputMode="numeric"
                    autoComplete="cc-csc"
                    value={cartao.cvv}
                    erro={erros.cvv}
                    onChange={(e) =>
                      alterarCartao('cvv', somenteDigitos(e.target.value).slice(0, 4))
                    }
                  />
                  <Select
                    label="Parcelas"
                    value={cartao.parcelas}
                    onChange={(e) => alterarCartao('parcelas', e.target.value)}
                    opcoes={Array.from({ length: 12 }, (_, i) => ({
                      valor: String(i + 1),
                      rotulo: `${i + 1}x de ${formatarMoeda(valores.total / (i + 1))} sem juros`,
                    }))}
                    className="sm:col-span-2"
                  />
                </div>
              )}

              {forma === 'PIX' && (
                <Input
                  label="Chave Pix do pagador"
                  obrigatorio
                  value={chavePix}
                  erro={erros.chavePix}
                  onChange={(e) => setChavePix(e.target.value)}
                  dica="Pode ser e-mail, CPF, telefone ou chave aleatória."
                />
              )}

              {forma === 'BOLETO' && (
                <Alert tipo="info" titulo={`Vencimento: ${vencimentoBoleto()}`}>
                  O pedido fica pendente até a compensação do boleto (até 3 dias úteis).
                </Alert>
              )}

              {erroEnvio && (
                <Alert tipo="erro" titulo="Não foi possível concluir">
                  {erroEnvio.message}
                </Alert>
              )}

              <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-between">
                {!pedido ? (
                  <Button variante="secundario" onClick={() => irPara(0)}>
                    Voltar
                  </Button>
                ) : (
                  <span />
                )}
                <Button type="submit" carregando={enviando} tamanho="lg">
                  {pedido
                    ? 'Tentar pagamento'
                    : `Confirmar e pagar ${formatarMoeda(valores.total)}`}
                </Button>
              </div>
            </form>
          )}

          {etapa === 2 && resultado && (
            <div className="space-y-5">
              <Alert
                tipo={
                  { APROVADO: 'sucesso', RECUSADO: 'erro', PENDENTE: 'aviso' }[resultado.resultado]
                }
                titulo={RESULTADOS_PAGAMENTO[resultado.resultado].rotulo}
              >
                <p>{resultado.mensagem}</p>
              </Alert>
              <dl className="grid gap-2 text-sm sm:grid-cols-2">
                <div>
                  <dt className="text-slate-600">Pedido</dt>
                  <dd className="font-semibold">#{resultado.pedidoId}</dd>
                </div>
                <div>
                  <dt className="text-slate-600">Forma de pagamento</dt>
                  <dd className="font-semibold">{resultado.descricao}</dd>
                </div>
                <div>
                  <dt className="text-slate-600">Comprovante</dt>
                  <dd className="font-mono font-semibold">{resultado.comprovante}</dd>
                </div>
              </dl>
              <div className="flex flex-col gap-2 sm:flex-row">
                {resultado.resultado === 'RECUSADO' && (
                  <Button onClick={() => irPara(1)}>Tentar outra forma de pagamento</Button>
                )}
                <ButtonLink
                  to={`/pedidos/${resultado.pedidoId}`}
                  variante={resultado.resultado === 'RECUSADO' ? 'secundario' : 'primario'}
                >
                  Acompanhar pedido
                </ButtonLink>
                <ButtonLink to="/" variante="secundario">
                  Voltar ao catálogo
                </ButtonLink>
              </div>
            </div>
          )}
        </Card>

        <aside aria-labelledby="titulo-resumo-checkout">
          <Card className="space-y-4 p-6">
            <h2 id="titulo-resumo-checkout" className="text-lg font-bold">
              {pedido ? `Pedido #${pedido.id}` : 'Resumo da compra'}
            </h2>
            <ul className="space-y-2 text-sm">
              {(pedido?.itens ?? carrinho.itens).map((i) => (
                <li key={i.id} className="flex justify-between gap-2">
                  <span>
                    {i.quantidade}× {i.nome}
                  </span>
                  <span className="font-medium">
                    {formatarMoeda((i.precoUnitario ?? i.preco) * i.quantidade)}
                  </span>
                </li>
              ))}
            </ul>
            <ResumoValores
              subtotal={valores.subtotal}
              frete={valores.frete}
              total={valores.total}
            />
          </Card>
        </aside>
      </div>
    </>
  );
}
