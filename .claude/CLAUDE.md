# CLAUDE.md
Este arquivo fornece orientações ao Claude Code (claude.ai/code) ao trabalhar com o código deste repositório.

## Comandos

```bash
docker-compose up -d                   # sobe o Postgres
./mvnw spring-boot:run                 # roda o app
./mvnw test                            # roda os testes
./mvnw test -Dtest=NomeDaClasseTest    # roda uma classe específica
./mvnw clean package                   # empacota
```

## Arquitetura
Spring Boot (Java 21), Maven. Organizado por módulo de domínio em `src/main/java/paulodev/sentinel_api/modules/<dominio>/`. Módulos com só `entity` (sem repository/service/controller ainda): `tenant`, `contract`, `billing`, `maintenance_ticket`.

### `entity/` — entidade JPA
```java
@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "tb_apartments")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Apartment {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condominium_id", nullable = false)
    private Condominium condominium;
}
```

### `repository/` — escopo por dono do recurso
Toda entidade que pertence (direta ou indiretamente) a um usuário expõe uma query `findByIdAndUser`:
```java
@Repository
public interface CondominiumRepository extends JpaRepository<Condominium, UUID> {

    @Query("SELECT c FROM Condominium c WHERE c.id = :condominiumId AND c.user.id = :userId")
    Optional<Condominium> findByIdAndUser(@Param("condominiumId") UUID condominiumId, @Param("userId") UUID userId);
}
```

### `service/` — regra de negócio
```java
@Service
@RequiredArgsConstructor
public class CondominiumService {

    private final CondominiumRepository condominiumRepository;

    @Transactional
    public CondominiumResponse getCondominiumInfo(UUID condominiumId, User authenticatedUser) {
        Condominium condominium = condominiumRepository.findByIdAndUser(condominiumId, authenticatedUser.getId())
                .orElseThrow(CondominiumNotFoundException::new);
        return new CondominiumResponse(condominium);
    }
}
```

### `controller/` — endpoint REST
```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/condominium")
public class CondominiumController implements CondominiumDocApi {

    private final CondominiumService condominiumService;

    @GetMapping("/summary/{condominiumId}")
    public ResponseEntity<CondominiumResponse> getCondominiumSummary(
            @PathVariable UUID condominiumId,
            @AuthenticationPrincipal User authenticatedUser)
    {
        var response = condominiumService.getCondominiumInfo(condominiumId, authenticatedUser);
        return ResponseEntity.ok(response);
    }
}
```

### `dto/` — request/response em `record`
```java
public record CondominiumRegisterRequest(String name, String address) {}
```

### `documentation/` — interface `XDocApi` (Swagger separado do controller)
```java
@Tag(name = "Condomínios", description = "Gerenciamento de condomínios")
public interface CondominiumDocApi {

    @Operation(summary = "Visualizar resumo do condomínio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Condomínio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    ResponseEntity<CondominiumResponse> getCondominiumSummary(@PathVariable UUID condominiumId, @AuthenticationPrincipal User authenticatedUser);
}
```

### `exception/custom/<dominio>/` — uma classe por erro de negócio
```java
public class CondominiumNotFoundException extends RuntimeException {
    public CondominiumNotFoundException() {
        super("Condomínio não encontrado");
    }
}
```

## Erros
Tratamento centralizado em `exception/handler/GlobalExceptionHandler.java` (`@RestControllerAdvice`), um `@ExceptionHandler` por tipo de exceção, agrupado por domínio com comentários `///`. Cada handler devolve um `ErrorResponse`:
```java
@ExceptionHandler(CondominiumNotFoundException.class)
public ResponseEntity<ErrorResponse> condominiumNotFound(CondominiumNotFoundException exception, HttpServletRequest request) {
    ErrorResponse error = new ErrorResponse(404, "Condominium not found", exception.getMessage(), request.getRequestURI(), Instant.now());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
```

## Segurança
Spring Security + JWT (`com.auth0:java-jwt`), stateless. `SecurityFilter` valida o token e popula `SecurityContextHolder`; regras de rota em `config/security/SecurityConfig.java`.
**Padrão de escopo por dono do recurso** já ilustrado acima (`findByIdAndUser` + `@AuthenticationPrincipal`) — todo módulo novo que expõe dado do usuário segue isso. `Apartment` ainda não segue esse padrão (bug conhecido).

## Banco de dados
PostgreSQL via Docker (`docker-compose.yml`, serviço `db`). `spring.jpa.hibernate.ddl-auto=update`. `DatabaseSeeder` popula dados fake com Datafaker na primeira subida, se o banco estiver vazio.

## Testes
JUnit 5 + Mockito, em `src/test/java/.../modules/<dominio>/{service,controller}/`.

### Teste de service
`@ExtendWith(MockitoExtension.class)`, `@Mock` nas dependências, `@InjectMocks` no service. Nomes de teste: `metodo_ComCondicao_DeveResultado`. Referência: `AuthServiceTest`, `UserServiceTest`.
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager auth;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private AuthService authService;

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        var request = new UserLoginRequest("paulo@test.com", "123456");
        var authTest = mock(Authentication.class);
        when(auth.authenticate(any())).thenReturn(authTest);
        when(authTest.getPrincipal()).thenReturn(new User());
        when(tokenService.tokenGenerate(any())).thenReturn("access-token-test");

        var result = authService.login(request);

        assertEquals("access-token-test", result.accessToken());
    }
}
```

### Teste de controller
`@WebMvcTest` excluindo autoconfig de segurança, `@MockitoBean` no service e em toda dependência da cadeia de segurança (`TokenService`, `XRepository`), `MockMvc` + `jsonPath`, usuário autenticado simulado via `SecurityMockMvcRequestPostProcessors.user(user)`. Casos agrupados em classes `@Nested` por endpoint. Referência: `UserControllerTest`.
```java
@WebMvcTest(value = UserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UserService userService;
    @MockitoBean private TokenService tokenService;
    @MockitoBean private UserRepository userRepository;

    @Nested
    class Register {
        @Test
        void withValidRequest() throws Exception {
            var request = new UserRegisterRequest("Name", "pass123", "email@test.com", UserRole.USER);
            var response = new UserResponse(new User("Name", "email@test.com", "pass", UserRole.USER));
            when(userService.createUser(request)).thenReturn(response);

            mockMvc.perform(post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("Name")));
        }
    }
}
```
