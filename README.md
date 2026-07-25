# Stock Manager API

API REST para gestão de estoque, com foco em visibilidade, controle de movimentações e segurança de saldo. O objetivo do projeto é ajudar usuários a entenderem a saúde do seu estoque de forma simples e confiável, permitindo registrar entradas, saídas e reservas sem perder o histórico e sem comprometer a integridade dos dados.

## O problema que o projeto resolve

Gerenciar estoque de forma manual costuma gerar problemas como:

- falta de visibilidade sobre o saldo real em tempo útil;
- risco de lançar saídas ou reservas sem saldo suficiente;
- dificuldade de acompanhar o histórico de movimentações e o preço do produto no momento em que ele foi movimentado;
- inconsistência entre o que foi lançado e o que realmente está disponível.

A API resolve isso centralizando o controle do estoque em um fluxo consistente, com registro detalhado das movimentações e validação de disponibilidade antes de qualquer saída ou reserva.

## Principais funcionalidades

- lançamento de entradas de estoque;
- lançamento de saídas de estoque;
- lançamento de reservas de estoque;
- registro das movimentações em uma tabela de ledger com data, hora, quantidade e preço do produto no momento da movimentação;
- manutenção do estado atual do estoque com saldo calculado a cada movimentação;
- implementação transacional e atômica para evitar operações inválidas quando não há saldo suficiente.

## Como o projeto funciona

O fluxo de estoque é baseado em duas estruturas principais:

- tabela de movimentações: armazena o histórico de cada operação realizada;
- tabela de estado atual do estoque: mantém o saldo consolidado e atualizado após cada movimentação.

Isso permite não só acompanhar o que aconteceu, mas também ter uma visão mais confiável do estado atual do estoque.

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- Flyway
- PostgreSQL
- Docker
- Springdoc OpenAPI
- JUnit 5

## Arquitetura e boas práticas

- arquitetura em camadas com controller, service e repository;
- regras de negócio centralizadas na camada de serviço;
- uso de transações atômicas para garantir integridade de estoque;
- migrações de banco com Flyway;
- API documentada com OpenAPI.

## Como rodar o projeto

### Requisitos

- Docker
- Java 21
- Maven ou Maven Wrapper

### Passos

1. Clone o repositório

```bash
git clone https://github.com/j0n4t45d3v/stock-manager-api.git
cd stock-manager-api
```

2. Suba o banco de dados

```bash
docker compose up -d
```

3. Execute a aplicação

```bash
./mvnw spring-boot:run
```

4. Acesse a API

A aplicação ficará disponível localmente e a documentação Swagger poderá ser consultada conforme a configuração da aplicação.

## Testes

Para executar os testes:

```bash
./mvnw test
```
