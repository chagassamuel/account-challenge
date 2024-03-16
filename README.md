# Projeto: API de Consulta de Saldo e Transferência Entre Contas

O objetivo deste projeto é apresentar uma proposta de microsserviço que expõe dois Endpoints,
um para consulta de saldo e outro para possibilitar a transferência de valores entre contas.

## 1. Plano de Trabalho

### 1.1. Desafio Engenharia de Software
Desenvolver uma API REST com os requisitos a seguir:

- Buscar nome do cliente na API de Cadastro (Mock);
- Validar se a Conta Corrente está ativa e se o cliente tem limite disponível para efetivar a transferência;
- Validar se a transferência excedeu o limite diário de R$1000;
- Após a transferência, notificar o Bacen (Mock) de forma síncrona que a transação foi concluída com sucesso;
  A API do BACEN tem controle de rate limit e pode retornar 429 em caso de chamadas que excedam o limite;
- Impedir que falhas momentâneas das dependências da aplicação impactem a experiência do cliente;
- Ser desenvolvida em linguagem Java/Spring Boot;
- Apresentar testes unitários e automatizados;
- Explorar Design Patterns;
- Implementar padrões de resiliência.

### 1.2. Desafio Arquitetura de solução
Criar um desenho de solução preferencialmente AWS para a API desenvolvida no desafio de Engenharia de
Software considerando os requisitos a seguir:

- Apresentar uma proposta de escalonamento para casos de oscilação de carga;
- Apresentar uma proposta de observabilidade;
- Justificar a escolha da solução de banco de dados;
- Caso utilizado, justificar uso de caching;
- O tempo total da requisição até a resposta ao cliente não deve exceder 100ms;
- A solução precisa ser capaz de suportar um alto throughput (6 mil tps);
- Em caso de falha de alguma dependência, apresentar uma estratégia para não impactar o cliente;
- Em caso de throttling (HTTP Status 429) do BACEN, apresentar uma estratégia para garantir a notificação
  ao órgao regulador.

## 2. Execução

### 2.1. Pré-Requisitos

- Ter instalado o Java 17+;
- Ter instalado o [Redis](https://redis.io/download/);
- Ter instalado o [Maven 3.8.1+](https://maven.apache.org/download.cgi).

### 2.2. Executar Projeto

Para que o projeto seja inicializado, basta executar o seguinte comando:

```sh
$ ./mvnw spring-boot:run
```

Após a execução do comando acima, a seguinte mensagem será apresentada:

```sh
[           main] b.c.i.a.c.AccountChallengeApplication    : ACCOUNT-CHALLENGE STARTED
```

### 2.3. Mocks

Para testar o desenvolvimento e as integrações com os sistemas externos, foi utilizado o [Mockoon](https://mockoon.com/download/#download-section).
Ele foi o responsável por mockar as chamadas as APIs de Cadastro e do BACEN. A collection utilizada durante o desenvolvimento se encontra
[aqui](https://github.com/chagassamuel/account-challenge/tree/main/artifacts/mockoon).

### 2.4. API Client

Para realizar as chamadas dos Endpoints, foi utilizado o [Insomnia](https://insomnia.rest/download). A collection utilizada durante o desenvolvimento
se encontra [aqui](https://github.com/chagassamuel/account-challenge/tree/main/artifacts/Insomnia_account-challenge.json).

### 2.5. Teste de performance

Para analisar/mensurar a performance da API, foi utilizado o [Apache JMeter](https://jmeter.apache.org/). 

## 3. Proposta de Solução

### 3.1. Banco de Dados

O Banco de Dados escolhindo foi o Amazon Aurora (MySQL), por ser um Banco de Dados Relacional, uma vez que os dados são estruturados (Contas/Transferências).
Além disso, a escolha por MySQL foi realizada dada a melhor taxa de processamento que ele possui em relação ao PostgreSQL.

### 3.2. Caching

A solução implementa o uso de Caching para melhorar a performance em relação a integração com a API de Cadastro. Como o desafio propõe que seja buscado o nome
do cliente na API de Cadastro, e o nome do cliente é um atributo perene, faz sentido a aplicação de Caching. O Caching foi implementado utilizando o Banco de
Dados Redis, o qual trabalha com seus dados em memória e disponibiliza leitura e escrita com grande desempenho.

### 3.3. Design Patterns

A implementação utilizou alguns Design Patterns que se encaixaram bem na proposta, dentre eles:
- Singleton: através de anotações do Spring (@Service, @Controller, etc) e através de classes utilitárias;
- Dependency Injection (DI): através do gerenciamento realizado pelo Spring (@Autowired), juntamente com o Lombok (@RequiredArgs);
- Builder: através do Lombok (@Builder), bastante utilizado nas implementações dos Mappers com MapStruct;
- Facade: isolando partes do sistema (sub-sistema) com o uso de uma fachada (Facade) e, somente através dela (passando por ela), é que temos acesso ao sub-sistema (Services);
- Repository: sendo uma abstração para a camada de dados e centralizando o tratamento dos objetos de domínio.

### 3.4. Padrões de Resiliência

Visando a alta disponibilidade da API e o impedimento de que falhas momentâneas ou rate limit das integrações impactem na experiência do cliente,
foram implementados dois padrões de resiliência nesse sentido, são eles:
- Retry: para realizar N chamadas às integrações que respondem com falha;
- Circuit Breaker: para analisar e controlar a quantidade de chamadas com falha nas integrações.

### 3.5. Proposta Para Oscilação de Carga

Dado o throughput de 6 mil tps e a arquitetura proposta no item 4, a medida que o tps aumenta, a ideia é que novos pods do microsserviço subam para atender
o desvio e, além disso, o time de sustentação seja notificado para analisar o real motivo. 

### 3.6. Proposta de Observabilidade

No sentido de Observabilidade, todos os logs relevantes serão disponibilizados no Splunk. Além disso, métricas da saúde da API serão criadas no Prometheus,
a fim de disponibilizar dashs e alertas (cloudwatch/grafana) para o time de sustentação atuar com tempestividade. Algumas métricas importantes para analisar:
- Erros com as integrações;
- Erros e saúde do Bancos de Dados;
- Quantidade de Pods;
- TPS.

### 3.7. Estratégia de Falha de Dependência

Como dito anteriormente, foram utilizados alguns recursos para mitigar o problema de falhas em alguma dependência da API:
- Cadastro: foi assumido que o nome do cliente seria enviado na notificação de transferência no BACEN. Caso a API de cadastro falhe, como dito
antes, foram implementados os recursos de Caching (Redis), Retry e Circuit Breaker. Se mesmo assim a API não conseguir identificar o nome do cliente,
será enviado ao BACEN uma String vazia, uma vez que o nome não é obrigatório.
- BACEN: também foram implementados os recursos de Retry e Circuit Breaker. Caso a integração continue em falha, a API salvará a notificação em uma
tabela do Aurora. Um Event Bridge deverá ser configurado para realizar consultas periodicamente nessa tabela de notificações pendentes, e, caso encontre registros,
acionar uma função lambda que tentará notificar o BACEN novamente. Uma outra opção que está disponibilizada na implementação, que substitui o uso do Event Bridge e
a tabela de notificações pendentes, seria a postagem da notificação em um tópico Kafka, o qual também teria uma função lambda para ler as
mensagens do tópico e reenviar a notificação para o BACEN.

## 4. Diagrama de arquitetura

<p align="center" >
<img src="https://github.com/chagassamuel/account-challenge/blob/main/artifacts/architecture-account-challenge.png"/>
</p>