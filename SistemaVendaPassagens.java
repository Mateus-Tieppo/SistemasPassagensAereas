import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

public class SistemaVendaPassagens implements Serializable {
    private static final long serialVersionUID = 1L;
    public static class SistemaException extends Exception {
        public SistemaException(String message) { super(message); }
    }
    public static class ClienteExistenteException extends SistemaException {
        public ClienteExistenteException(String rg) { super("Cliente com RG " + rg + " ja cadastrado!"); }
    }
    public static class ClienteNaoEncontradoException extends SistemaException {
        public ClienteNaoEncontradoException(String rg) { super("Cliente com RG " + rg + " nao encontrado!"); }
    }
    public static class AviaoExistenteException extends SistemaException {
        public AviaoExistenteException(String codigo) { super("Aviao com codigo " + codigo + " ja cadastrado!"); }
    }
    public static class AviaoNaoEncontradoException extends SistemaException {
        public AviaoNaoEncontradoException(String codigo) { super("Aviao com codigo " + codigo + " nao encontrado!"); }
    }
    public static class VooExistenteException extends SistemaException {
        public VooExistenteException(String codigo) { super("Voo com codigo " + codigo + " ja cadastrado!"); }
    }
    public static class VooNaoEncontradoException extends SistemaException {
        public VooNaoEncontradoException(String codigo) { super("Voo com codigo " + codigo + " nao encontrado!"); }
    }
    public static class AssentoIndisponivelException extends SistemaException {
        public AssentoIndisponivelException(String codigoVoo) { 
            super("Nao ha assentos disponiveis para o voo " + codigoVoo + "! Cliente adicionado na fila de espera.");
        }
    }
    public static class OperacaoInvalidaException extends SistemaException {
        public OperacaoInvalidaException(String message) { super(message); }
    }
    public static class DadoInvalidoException extends SistemaException {
        public DadoInvalidoException(String campo) { super("Dado invalido para o campo: " + campo); }
    }

    // Constante para CSV
    private static final String CSV_FILE = "sistema_passagens.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Mapas de dados
    private final Map<String, Cliente> clientes = new HashMap<>();
    private final Map<String, Aviao> avioes = new HashMap<>();
    private final Map<String, Voo> voos = new HashMap<>();
    private final List<Passagem> passagens = new ArrayList<>();
    private int contadorPassagens = 1;

    public void cadastrarCliente(String rg, String nome, String telefone) throws SistemaException {
        try {
            if (rg == null || !rg.matches("\\d{7,10}")) 
                throw new DadoInvalidoException("RG (7-10 digitos)");
            
            if (nome == null || nome.length() < 3 || nome.length() > 100) 
                throw new DadoInvalidoException("Nome (3-100 caracteres)");
            
            if (telefone == null || !telefone.matches("\\d{10,11}")) 
                throw new DadoInvalidoException("Telefone (10-11 digitos)");
            
            if (clientes.containsKey(rg)) 
                throw new ClienteExistenteException(rg);
                
            clientes.put(rg, new Cliente(rg, nome, telefone));
        } catch (NullPointerException e) {
            throw new SistemaException("Valores nulos nao sao permitidos");
        }
    }

    public void cadastrarAviao(String codigo, String nome, int capacidade) throws SistemaException {
        try {
            codigo = codigo.toUpperCase();
            if (codigo == null || !codigo.matches("[A-Z]\\d{3}")) 
                throw new DadoInvalidoException("Codigo (formato A999)");
            
            if (nome == null || nome.length() < 2 || nome.length() > 50) 
                throw new DadoInvalidoException("Nome (2-50 caracteres)");
            
            if (capacidade <= 0 || capacidade > 1000) 
                throw new DadoInvalidoException("Capacidade (1-1000)");
            
            if (avioes.containsKey(codigo)) 
                throw new AviaoExistenteException(codigo);
                
            avioes.put(codigo, new Aviao(codigo, nome, capacidade));
        } catch (NullPointerException e) {
            throw new SistemaException("Valores nulos nao sao permitidos");
        }
    }

    public void cadastrarVoo(String codigo, String origem, String destino, String horarioStr, String codigoAviao) throws SistemaException {
    try {
        codigo = codigo.toUpperCase();
        codigoAviao = codigoAviao.toUpperCase();
        
        if (codigo == null || !codigo.matches("[A-Z]\\d{3}")) 
            throw new DadoInvalidoException("Codigo do voo deve ter formato LETRAddd (ex: Q123)");
        
        if (origem == null || origem.length() < 3 || origem.length() > 50) 
            throw new DadoInvalidoException("Origem (3-50 caracteres)");
        
        if (destino == null || destino.length() < 3 || destino.length() > 50) 
            throw new DadoInvalidoException("Destino (3-50 caracteres)");
        
        Aviao aviao = avioes.get(codigoAviao);
        if (aviao == null) 
            throw new AviaoNaoEncontradoException(codigoAviao);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime horario = LocalDateTime.parse(horarioStr, formatter);
        
        if (horario.isBefore(LocalDateTime.now().plusHours(2))) 
            throw new OperacaoInvalidaException("O voo deve ser agendado com pelo menos 2 horas de antecedencia");
        
        if (voos.containsKey(codigo)) 
            throw new VooExistenteException(codigo);
            
        voos.put(codigo, new Voo(codigo, origem, destino, horario, aviao));
    } catch (DateTimeParseException e) {
        throw new SistemaException("Formato de horario invalido! Use dd/MM/yyyy HH:mm");
    } catch (NullPointerException e) {
        throw new SistemaException("Valores nulos nao sao permitidos");
    }
    }

    public void venderPassagem(String rgCliente, String codigoVoo) throws SistemaException {
        try {
            codigoVoo = codigoVoo.toUpperCase();
            if (rgCliente == null || codigoVoo == null)
                throw new DadoInvalidoException("RG ou codigo do voo");
            
            Cliente cliente = clientes.get(rgCliente);
            if (cliente == null) 
                throw new ClienteNaoEncontradoException(rgCliente);
            
            Voo voo = voos.get(codigoVoo);
            if (voo == null) 
                throw new VooNaoEncontradoException(codigoVoo);
            
            if (voo.getStatus() != StatusVoo.AGENDADO) {
                if (voo.getStatus() == StatusVoo.CANCELADO) {
                    throw new OperacaoInvalidaException("Voo cancelado! Nao e possivel vender passagens.");
                } else {
                    throw new OperacaoInvalidaException("Voo ja concluido! Nao e possivel vender passagens.");
                }
            }
            
            if (voo.getHorario().isBefore(LocalDateTime.now().plusHours(1))) 
                throw new OperacaoInvalidaException("Nao e possivel comprar passagem com menos de 1 hora para o voo");
            
            if (voo.getAssentosDisponiveis() <= 0) {
                voo.getFilaEspera().add(cliente);
                throw new AssentoIndisponivelException(codigoVoo);
            }
            
            String passagemId = "PASS-" + contadorPassagens++;
            Passagem passagem = new Passagem(passagemId, cliente, voo, LocalDateTime.now());
            passagens.add(passagem);
            voo.incrementarAssentosVendidos();
            
        } catch (NullPointerException e) {
            throw new SistemaException("Erro inesperado: " + e.getMessage());
        }
    }

    public void cancelarVoo(String codigoVoo) throws SistemaException {
    try {
        if (codigoVoo == null) 
            throw new DadoInvalidoException("Codigo do voo");
        
        codigoVoo = codigoVoo.toUpperCase();
            
        Voo voo = voos.get(codigoVoo);
        if (voo == null) 
            throw new VooNaoEncontradoException(codigoVoo);
        
        if (voo.getStatus() == StatusVoo.CONCLUIDO) 
            throw new OperacaoInvalidaException("Voo ja concluido nao pode ser cancelado");
        
        if (voo.getStatus() == StatusVoo.CANCELADO) 
            throw new OperacaoInvalidaException("Voo ja esta cancelado");
        
        voo.setStatus(StatusVoo.CANCELADO);
        
        List<Passagem> passagensCanceladas = new ArrayList<>();
        Iterator<Passagem> iterator = passagens.iterator();
        while (iterator.hasNext()) {
            Passagem p = iterator.next();
            if (p.getVoo().getCodigo().equals(codigoVoo)) {
                passagensCanceladas.add(p);
                iterator.remove(); 
            }
        }
        
        voo.getFilaEspera().clear();
    } catch (Exception e) {
        throw new SistemaException("Erro ao cancelar voo: " + e.getMessage());
        }
    }

    public Collection<Cliente> getClientes() { 
        return Collections.unmodifiableCollection(new ArrayList<>(clientes.values()));
    }
    
    public Collection<Aviao> getAvioes() { 
        return Collections.unmodifiableCollection(new ArrayList<>(avioes.values()));
    }
    
    public Collection<Voo> getVoos() { 
        return Collections.unmodifiableCollection(new ArrayList<>(voos.values()));
    }
    
    public List<Passagem> getPassagens() { 
        return Collections.unmodifiableList(new ArrayList<>(passagens));
    }
    
    public List<Voo> buscarVoos(String origem, String destino) throws SistemaException {
        try {
            if (origem == null || destino == null)
                throw new DadoInvalidoException("Origem/Destino");
                
            return voos.values().stream()
                .filter(v -> v.getOrigem().equalsIgnoreCase(origem) && 
                             v.getDestino().equalsIgnoreCase(destino) &&
                             v.getStatus() == StatusVoo.AGENDADO)
                .sorted(Comparator.comparing(Voo::getHorario))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new SistemaException("Erro na busca de voos: " + e.getMessage());
        }
    }

    // Métodos de persistência CSV
    public void salvarCSV() throws SistemaException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE))) {
            writer.write("TIPO;ID;NOME;TELEFONE;MODELO;CAPACIDADE;ORIGEM;DESTINO;HORARIO;AVIAO;STATUS;ASSENTOS_VENDIDOS;DATA_COMPRA;VOO_ID;RG_CLIENTE\n");
            
            // Clientes
            for (Cliente c : clientes.values()) {
                writer.write(String.format("CLIENTE;%s;%s;%s;;;;;;; ; ; ; ; ; \n", 
                    c.getRg(), c.getNome(), c.getTelefone()));
            }
            
            // Aviões
            for (Aviao a : avioes.values()) {
                writer.write(String.format("AVIAO;%s;%s; ; ;%d; ; ; ; ; ; ; ; ; ; \n", 
                    a.getCodigo(), 
                    a.getNome(), 
                    a.getCapacidade()));
            }

            // Voos
            for (Voo v : voos.values()) {
                writer.write(String.format("VOO;%s; ; ; ; ;%s;%s;%s;%s;%s;%d; ; ; ; \n", 
                    v.getCodigo(), 
                    v.getOrigem(), 
                    v.getDestino(), 
                    v.getHorario().format(DATE_FORMAT),
                    v.getAviao().getCodigo(), 
                    v.getStatus(),
                    v.getAssentosVendidos()));
            }
            
            // Passagens
            for (Passagem p : passagens) {
                writer.write(String.format("PASSAGEM;%s; ; ; ; ; ; ; ; ; ; ;%s;%s;%s\n", 
                    p.getId(),
                    p.getDataCompra().format(DATE_FORMAT),
                    p.getVoo().getCodigo(),
                    p.getCliente().getRg()));
            }
            
            // Filas de espera
            for (Voo v : voos.values()) {
                int ordem = 1;
                for (Cliente c : v.getFilaEspera()) {
                    writer.write(String.format("FILA_ESPERA; ; ; ; ; ; ; ; ; ; ; ; ;%s;%s;%d\n", 
                        v.getCodigo(), c.getRg(), ordem++));
                }
            }
        } catch (IOException e) {
            throw new SistemaException("Erro ao salvar CSV: " + e.getMessage());
        }
    }

    public void carregarCSV() throws SistemaException {
        limparDados();
        
        if (!Files.exists(Paths.get(CSV_FILE))) {
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CSV_FILE))) {
            String line;
            reader.readLine(); 
            
            while ((line = reader.readLine()) != null) {
                String[] dados = line.split(";", -1);
                if (dados.length < 15) continue;
                
                String tipo = dados[0];
                
                try {
                    switch (tipo) {
                        case "CLIENTE":
                            cadastrarCliente(dados[1], dados[2], dados[3]);
                            break;
                            
                        case "AVIAO":
                            cadastrarAviao(dados[1], dados[2], Integer.parseInt(dados[5]));
                            break;
                            
                        case "VOO":
                            LocalDateTime horario = LocalDateTime.parse(dados[8], DATE_FORMAT);
                            StatusVoo status = StatusVoo.valueOf(dados[10]);
                            int assentosVendidos = Integer.parseInt(dados[11]);
                            
                            Aviao aviao = avioes.get(dados[9]);
                            if (aviao == null) {
                                throw new SistemaException("Avião não encontrado: " + dados[9]);
                            }
                            
                            Voo voo = new Voo(dados[1], dados[6], dados[7], horario, aviao);
                            voo.setStatus(status);
                            voo.setAssentosVendidos(assentosVendidos);
                            voos.put(dados[1], voo);
                            break;
                            
                        case "PASSAGEM":
                            Voo v = voos.get(dados[13]);
                            Cliente c = clientes.get(dados[14]);
                            if (v != null && c != null) {
                                LocalDateTime dataCompra = LocalDateTime.parse(dados[12], DATE_FORMAT);
                                passagens.add(new Passagem(dados[1], c, v, dataCompra));
                            }
                            break;
                            
                        case "FILA_ESPERA":
                            Voo vooFila = voos.get(dados[13]);
                            Cliente clienteFila = clientes.get(dados[14]);
                            if (vooFila != null && clienteFila != null) {
                                vooFila.getFilaEspera().add(clienteFila);
                            }
                            break;
                    }
                } catch (SistemaException e) {
                    System.err.println("Erro ao carregar linha: " + line);
                    System.err.println("Motivo: " + e.getMessage());
                }
            }
        } catch (IOException | DateTimeParseException | NumberFormatException e) {
            throw new SistemaException("Erro ao carregar CSV: " + e.getMessage());
        }
    }
    
    private void limparDados() {
        clientes.clear();
        avioes.clear();
        voos.clear();
        passagens.clear();
        contadorPassagens = 1;
    }
}