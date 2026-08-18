# CLAUDE.md
Este arquivo fornece orientações ao Claude Code (claude.ai/code) ao trabalhar com o código deste repositório.

## Comandos

```bash
# Subir o banco de dados (Postgres via Docker)
docker-compose up -d
# Rodar o app
./mvnw spring-boot:run
# Rodar os testes
./mvnw test
# Rodar uma classe de teste específica
./mvnw test -Dtest=NomeDaClasseTest
# Empacotar
./mvnw clean package
```

## Arquitetura
Spring Boot (Java 21), organizado por módulo de domínio em `src/main/java/paulodev/sentinel_api/modules/<dominio>/`:
- **`entity/`** — entidades JPA (`@Entity`)
- **`repository/`** — Spring Data JPA repositories
- **`service/`** — regras de negócio (`@Service`, `@Transactional` quando escreve)
- **`controller/`** — endpoints REST (`@RestController`)
- **`dto/`** — DTOs em `record` (request/response)
- **`documentation/`** — interfaces `*DocApi` com anotações Swagger/OpenAPI, separadas do controller
- **`exception/custom/<dominio>/`** — exceções de domínio, tratadas globalmente por `exception/handler/GlobalExceptionHandler.java`

Módulos ainda incompletos (só têm `entity`, sem repository/service/controller): `tenant`, `contract`, `billing`, `maintenance_ticket`. É o próximo trabalho — ver `ROADMAP.md`.

### Segurança
Spring Security + JWT (`com.auth0:java-jwt`), stateless (`SessionCreationPolicy.STATELESS`). `SecurityFilter` intercepta cada request, valida o token via `TokenService` e popula o `SecurityContextHolder`. Regras de rota em `config/security/SecurityConfig.java`. Erros de autenticação/autorização tratados por `CustomAuthenticationEntryPoint` + `GlobalExceptionHandler`.

**Padrão de escopo por dono do recurso**: ver `CondominiumService` (`findByIdAndUser`) — todo módulo novo que expõe dado do usuário deve seguir esse padrão (hoje `ApartmentService`/`ApartmentController` ainda não seguem — bug conhecido, ver `ROADMAP.md`).

### Banco de dados
PostgreSQL via Docker (`docker-compose.yml`, serviço `db`). `spring.jpa.hibernate.ddl-auto=update` (schema evolui automaticamente em dev). `DatabaseSeeder` popula dados fake (Datafaker) na primeira subida, se o banco estiver vazio.

### Testes
JUnit 5 + Mockito. Convenção: `<Classe>Test.java` espelhando o módulo em `src/test/java/.../modules/<dominio>/{service,controller}/`. Ver `AuthServiceTest`/`UserServiceTest`/`AuthControllerTest`/`UserControllerTest` como referência de estilo.

---

## Sobre este projeto
Sentinel API é o projeto-referência da stack Java no portfólio do usuário (equivalente ao papel do AutoLog no Flutter). Resolve uma dor real: uma amiga do usuário gerencia vários apartamentos alugados em várias regiões da cidade e hoje organiza tudo em planilha Excel, sempre se perdendo. É **só backend por enquanto** — sem front (mobile fica pra depois) e sem uso pelos inquilinos, só a gerente (dona dos imóveis) usa.

O objetivo do projeto é ser um checklist vivo dos principais tópicos pedidos em vagas Java Estágio/Jr hoje: Java Core, POO/SOLID, Spring Boot REST, Spring Data JPA/Hibernate, Spring Security+JWT, banco relacional, JUnit5+Mockito, Maven, Git, Docker/docker-compose, mensageria (RabbitMQ/Kafka), Clean Architecture/DTOs/Mappers/padrões de projeto, deploy em nuvem, observabilidade (métricas/tracing). **A meta não é dominar cada tópico — é entender o conceito e aplicar o básico de todos**, dentro deste único projeto. Progresso detalhado em `ROADMAP.md`.

**Restrição de orçamento**: o usuário não quer gastar mais que ~R$100 no total com este projeto. Sempre priorizar opções gratuitas/free tier (ex.: Grafana+Prometheus self-hosted em vez de Datadog, Render/Railway free tier em vez de nuvem paga).

## Meu papel nesse projeto (Claude)
Atuo como **tech lead**: defino a ordem das tarefas (`ROADMAP.md`) e explico o porquê de cada decisão de arquitetura/padrão antes de qualquer código. Por padrão, **o usuário implementa ele mesmo** — só implemento quando ele pede explicitamente. Priorizar sempre entendimento sobre velocidade.

## Meu perfil como desenvolvedor
- Formado em ADS, cursando pós-graduação em Engenharia de Software
- Pivotou de Flutter (não achou vaga na área) para foco em Java backend
- Vários projetos Flutter completos; em Java, este é o mais avançado

## Formato de planejamento
Quando eu pedir um diagnóstico ou planejamento, responda neste formato:
- **O que já foi feito**
- **O que está incompleto ou com problema**
- **Próximas tarefas sugeridas** (em ordem de prioridade)
- **Conceito importante** que devo entender antes de começar
