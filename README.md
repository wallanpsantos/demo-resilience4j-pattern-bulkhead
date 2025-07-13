# Bulkhead Pattern Demo - Resilience4j

Este projeto demonstra a implementação do padrão **Bulkhead** usando a biblioteca **Resilience4j** em Java. O padrão Bulkhead é uma técnica de resiliência que isola recursos críticos para evitar que falhas em um componente afetem todo o sistema.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Características](#características)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Uso](#uso)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Exemplo de Execução](#exemplo-de-execução)
- [Métricas e Monitoramento](#métricas-e-monitoramento)
- [Conceitos Demonstrados](#conceitos-demonstrados)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Contribuição](#contribuição)
- [Licença](#licença)

## 🎯 Visão Geral

O **Bulkhead Pattern** é inspirado nos compartimentos estanques dos navios. Assim como os compartimentos impedem que um vazamento afunde todo o navio, o padrão Bulkhead isola diferentes partes de um sistema para evitar que falhas se propaguem.

Este projeto simula dois serviços:
- **Serviço de Pedidos** (crítico) - Processamento rápido (500ms)
- **Serviço de Relatórios** (não-crítico) - Processamento lento (2s)

Cada serviço possui seu próprio pool de threads isolado, demonstrando como o Bulkhead previne que relatórios lentos afetem o processamento de pedidos.

## ✨ Características

- **Isolamento de Recursos**: Pools de threads separados para diferentes tipos de operações
- **Controle de Sobrecarga**: Rejeição controlada quando recursos estão esgotados
- **Fallback Graceful**: Degradação elegante quando serviços estão indisponíveis
- **Observabilidade**: Métricas detalhadas e logs estruturados
- **Configuração Flexível**: Parâmetros facilmente ajustáveis
- **Clean Code**: Arquitetura modular seguindo princípios SOLID

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌─────────────────┐
│   Client Apps   │    │   Client Apps   │
└─────────────────┘    └─────────────────┘
         │                       │
         ▼                       ▼
┌─────────────────┐    ┌─────────────────┐
│ Order Bulkhead  │    │ Report Bulkhead │
│ Max: 5 threads  │    │ Max: 2 threads  │
│ Queue: 10       │    │ Queue: 2        │
└─────────────────┘    └─────────────────┘
         │                       │
         ▼                       ▼
┌─────────────────┐    ┌─────────────────┐
│  Order Service  │    │ Report Service  │
│   (Fast - 500ms)│    │  (Slow - 2s)    │
└─────────────────┘    └─────────────────┘
```

## 🛠️ Pré-requisitos

- **Java 11** ou superior
- **Maven 3.6+** ou **Gradle 6+**
- **IDE** de sua preferência (IntelliJ IDEA, Eclipse, VS Code)

## 📦 Instalação

### Usando Maven

```xml
<dependencies>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-all</artifactId>
        <version>2.3.0</version>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.38</version>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.17</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.17</version>
    </dependency>
</dependencies>
```

### Usando Gradle

```gradle
dependencies {
    implementation 'io.github.resilience4j:resilience4j-all:2.3.0'
    providedCompile 'org.projectlombok:lombok:1.18.38'
    implementation 'org.slf4j:slf4j-api:2.0.17'
    implementation 'org.slf4j:slf4j-simple:2.0.17'
}
```

## 🚀 Uso

### Executando o Projeto

```bash
# Clonando o repositório
git clone https://github.com/wallanpsantos/demo-resilience4j-pattern-bulkhead.git
cd demo-resilience4j-pattern-bulkhead

# Compilando e executando
mvn compile exec:java -Dexec.mainClass="org.example.BulkheadDemo"
```

### Executando pela IDE

1. Abra o projeto em sua IDE
2. Execute a classe `BulkheadDemo.java`
3. Observe os logs no console

## 📁 Estrutura do Projeto

```
src/main/java/org/example/
├── BulkheadDemo.java              # Classe principal
├── config/
│   └── BulkheadConfig.java        # Configurações dos bulkheads
├── service/
│   ├── OrderService.java          # Serviço de pedidos
│   └── ReportService.java         # Serviço de relatórios
├── listener/
│   └── BulkheadEventListener.java # Monitoramento de eventos
├── executor/
│   └── TaskExecutor.java          # Executor de tarefas
├── reporter/
│   └── StatisticsReporter.java    # Relatórios de estatísticas
└── manager/
    └── ExecutorManager.java       # Gerenciador de threads
```

## 📊 Exemplo de Execução

```
=== INICIANDO SIMULAÇÃO DE BULKHEAD ===

--- Disparando 8 requisições de relatórios (Bulkhead deve rejeitar algumas) ---

[Relatórios] Chamada PERMITIDA - Pool size: 1
[Relatório 1] Iniciando geração... [Thread: reportBulkhead-1]
[Relatórios] Chamada PERMITIDA - Pool size: 2
[Relatório 2] Iniciando geração... [Thread: reportBulkhead-2]
[Relatórios] Chamada REJEITADA! Bulkhead está cheio.
[Relatório 3] REJEITADO - Bulkhead cheio
[Relatório 3] Fallback aplicado: Relatório 3 será gerado posteriormente

--- Disparando 18 requisições de pedidos (algumas devem ser rejeitadas) ---

[Pedidos] Chamada PERMITIDA - Pool size: 3
[Pedido 1] Iniciando processamento... [Thread: orderBulkhead-1]
[Pedido 1] ✓ Resultado: Pedido 1 processado com sucesso

=== ESTATÍSTICAS FINAIS ===

📊 BULKHEAD DE PEDIDOS:
- Pedidos processados: 15
- Pedidos rejeitados: 3
- Taxa de sucesso: 83.3%

📊 BULKHEAD DE RELATÓRIOS:
- Relatórios processados: 4
- Relatórios rejeitados: 4
- Taxa de sucesso: 50.0%

✅ BENEFÍCIOS DO BULKHEAD DEMONSTRADOS:
- Isolamento de recursos: Relatórios lentos não afetaram o processamento de pedidos
- Controle de sobrecarga: Requisições em excesso foram rejeitadas de forma controlada
- Observabilidade: Métricas detalhadas sobre o uso dos recursos
- Fallback: Degradação graceful quando recursos estão indisponíveis
```

## 📈 Métricas e Monitoramento

O projeto coleta e exibe as seguintes métricas:

### Métricas do Bulkhead
- **Thread Pool Size**: Número de threads no pool
- **Active Threads**: Threads atualmente em uso
- **Queue Size**: Tamanho da fila de espera
- **Completed Tasks**: Tarefas completadas
- **Rejected Tasks**: Tarefas rejeitadas

### Eventos Monitorados
- **Call Permitted**: Chamada aceita pelo bulkhead
- **Call Rejected**: Chamada rejeitada (bulkhead cheio)
- **Call Finished**: Chamada finalizada com sucesso

## 🎓 Conceitos Demonstrados

### 1. Isolamento de Recursos
```java
// Bulkhead para pedidos (mais recursos)
ThreadPoolBulkheadConfig orderConfig = ThreadPoolBulkheadConfig.custom()
    .maxThreadPoolSize(5)
    .queueCapacity(10)
    .build();

// Bulkhead para relatórios (menos recursos)
ThreadPoolBulkheadConfig reportConfig = ThreadPoolBulkheadConfig.custom()
    .maxThreadPoolSize(2)
    .queueCapacity(2)
    .build();
```

### 2. Tratamento de Sobrecarga
```java
try {
    CompletableFuture<String> future = bulkhead.submit(() -> service.process(id));
    // Processamento normal
} catch (BulkheadFullException e) {
    // Aplicar fallback
    String fallbackResult = service.fallback(id, e);
}
```

### 3. Observabilidade
```java
bulkhead.getEventPublisher()
    .onCallPermitted(event -> log.info("Chamada permitida"))
    .onCallRejected(event -> log.warn("Chamada rejeitada"))
    .onCallFinished(event -> log.info("Chamada finalizada"));
```

## 🔧 Configuração

### Parâmetros do Bulkhead

| Parâmetro | Pedidos | Relatórios | Descrição |
|-----------|---------|------------|-----------|
| `maxThreadPoolSize` | 5 | 2 | Máximo de threads concorrentes |
| `coreThreadPoolSize` | 3 | 1 | Threads sempre ativas |
| `queueCapacity` | 10 | 2 | Tamanho da fila de espera |
| `keepAliveDuration` | 100ms | 100ms | Tempo de vida de threads ociosas |

### Personalizando a Configuração

```java
ThreadPoolBulkheadConfig customConfig = ThreadPoolBulkheadConfig.custom()
    .maxThreadPoolSize(10)           // Aumentar pool
    .coreThreadPoolSize(5)           // Mais threads ativas
    .queueCapacity(20)               // Fila maior
    .keepAliveDuration(Duration.ofSeconds(1))
    .build();
```

## 🛡️ Vantagens do Bulkhead Pattern

1. **Isolamento de Falhas**: Problemas em um serviço não afetam outros
2. **Controle de Recursos**: Prevenção de esgotamento de recursos
3. **Degradação Graceful**: Sistema continua funcionando mesmo com falhas
4. **Observabilidade**: Visibilidade clara do estado dos recursos
5. **Configurabilidade**: Ajuste fino por tipo de operação

## 🔄 Quando Usar

### ✅ Use Bulkhead quando:
- Diferentes operações têm características distintas (rápidas vs lentas)
- Alguns serviços são mais críticos que outros
- Você precisa garantir disponibilidade de operações essenciais
- Sistema tem alta concorrência

### ❌ Evite Bulkhead quando:
- Recursos são limitados (pode causar subutilização)
- Operações são muito similares
- Sistema tem baixa concorrência
- Complexidade não justifica o benefício

## 🛠️ Tecnologias Utilizadas

- **Java 11+**: Linguagem de programação
- **Resilience4j**: Biblioteca de resiliência
- **Lombok**: Redução de boilerplate
- **SLF4J + Logback**: Sistema de logging
- **Maven/Gradle**: Gerenciamento de dependências

## 🤝 Contribuição

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

### Diretrizes de Contribuição

- Siga os princípios de Clean Code
- Adicione testes para novas funcionalidades
- Mantenha a documentação atualizada
- Use commits descritivos

## 📚 Referências

- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Bulkhead Pattern - Microsoft](https://docs.microsoft.com/en-us/azure/architecture/patterns/bulkhead)
- [Release It! - Michael Nygard](https://pragprog.com/titles/mnee2/release-it-second-edition/)
- [Building Microservices - Sam Newman](https://www.oreilly.com/library/view/building-microservices/9781491950340/)

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

⭐ **Se este projeto foi útil, deixe uma estrela!** ⭐

[//]: # ()
[//]: # (📧 **Dúvidas ou sugestões?** Abra uma [issue]&#40;https://github.com/seu-usuario/bulkhead-demo/issues&#41;)
