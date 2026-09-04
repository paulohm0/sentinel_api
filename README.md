# <p align="center"> Sentinel API

API REST para gestão de imóveis alugados: condomínios, apartamentos e inquilinos organizados num só lugar, no lugar da planilha que sempre acaba desatualizada.

Este projeto é onde estou fixando, na prática, o que aparece com mais frequência em vagas Java — segurança com JWT, containerização com docker, testes automatizados e mensageria assíncrona.

## > Funcionalidades

- Cadastro e autenticação de usuário, com login via JWT
- Cadastro de condomínios, vinculado ao usuário dono
- Cadastro de apartamentos, vinculado a um condomínio — escopado por dono, um usuário nunca acessa dado de outro
- Edição e desativação (soft delete) de condomínios e apartamentos — nada é apagado de verdade, o dono continua vendo o que desativou
- Documentação interativa da API via Swagger/OpenAPI

## > Arquitetura

Organizado por módulo de domínio, cada um em camadas:

```
src/main/java/paulodev/sentinel_api/
  config/security/   # JWT, Spring Security, filtro de autenticação
  exception/         # Exceções de negócio por domínio + tratamento centralizado
  modules/
    <dominio>/
      entity/           # Entidade JPA
      repository/       # Spring Data JPA, com escopo por dono do recurso
      service/          # Regra de negócio
      controller/       # Endpoints REST
      dto/              # Request/response em record
      documentation/    # Interface Swagger, separada do controller
```

## > Tech Stack

- Java 21 / Spring Boot
- Spring Security + JWT (`java-jwt`)
- Spring Data JPA / Hibernate
- PostgreSQL
- Docker / docker-compose
- Springdoc OpenAPI (Swagger)
- Datafaker (seed de dados fake em desenvolvimento)
- Testes com JUnit 5 · Mockito

## > Padrões e decisões técnicas

- **Escopo de dado por dono do recurso**: toda entidade que pertence a um usuário expõe uma query `findByIdAndUser`, usada em todo service que lê, edita ou desativa um recurso — impede um usuário acessar dado de outro só sabendo o UUID
- **Soft delete em todo o domínio, sem exceção**: nenhum módulo faz hard delete; "excluir" sempre vira uma flag de status na entidade, mesmo padrão repetido em usuário, condomínio e apartamento
- **Tratamento de erro centralizado**: um `@RestControllerAdvice` único, um handler por tipo de exceção, sempre devolvendo o status HTTP correto — nunca `200 OK` com erro no corpo
- **Documentação Swagger isolada**: anotações OpenAPI ficam numa interface própria (`XDocApi`), implementada pelo controller, mantendo a classe do controller limpa
- **Autenticação stateless via JWT**: filtro próprio (`SecurityFilter`) valida o token e popula o contexto de segurança a cada request, sem sessão guardada no servidor

## > IA no fluxo de desenvolvimento

O projeto é desenvolvido com apoio do Claude Code, de forma estruturada.

- Três subagents próprios (.claude/agents/) — um só planeja e explica (nunca escreve código), um implementa só quando eu peço explicitamente, um projeta os casos de teste antes de codar
- Um CLAUDE.md documenta as convenções reais do projeto (padrão de camada, escopo por dono, tabela de status HTTP), pra qualquer sessão nova seguir o que já existe em vez de inventar de novo
- MCP conectado ao Postgres local, pra consultar schema e dado real do banco durante o desenvolvimento
- Todo código gerado foi **revisado e entendido** antes de ser incorporado — as decisões de arquitetura e o escopo do projeto foram definidos e validados por mim

## > Testes

Cobertura de service e controller pros módulos já completos:
- **Unitários de service**: JUnit 5 + Mockito, dependências mockadas
- **Controller**: `@WebMvcTest` + MockMvc, cobrindo o caminho feliz, validação de campos e o caso de segurança "recurso de outro dono → 404" em todo endpoint escopado

```bash
./mvnw test
```

## > Como rodar o projeto

Pré-requisitos: JDK 21, Docker.

```bash
git clone https://github.com/paulohm0/sentinel_api.git
cd sentinel_api
docker-compose up -d
./mvnw spring-boot:run
```

API em `http://localhost:8080`, documentação interativa em `http://localhost:8080/swagger-ui.html`.

## > Status do projeto

Em desenvolvimento ativo:

- ✅ Autenticação JWT e escopo de dado por dono do recurso
- ✅ CRUD completo de usuário, condomínio e apartamento, com soft delete e testes
- ⏳ Cadastro de inquilinos e contratos
- ⏳ Geração de cobrança, com processamento assíncrono via RabbitMQ
- ⏳ Observabilidade (métricas, tracing)
- ⏳ Deploy em nuvem

## > Habilidades Testadas

- ✅ Spring Security + JWT (autenticação stateless)
- ✅ Spring Data JPA (relacionamentos, queries derivadas e JPQL)
- ✅ Arquitetura em camadas por domínio
- ✅ Tratamento de erro centralizado (`@RestControllerAdvice`)
- ✅ Testes unitários de service e controller (JUnit 5, Mockito)
- ✅ Docker / docker-compose
- ✅ Documentação de API com Swagger/OpenAPI
- ⏳ Mensageria assíncrona (RabbitMQ) — planejado
- ⏳ Deploy em nuvem — planejado
- ⏳ Observabilidade — planejado