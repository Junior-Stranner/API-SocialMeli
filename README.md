🛒 API SocialMeli
API REST desenvolvida durante o Bootcamp do Mercado Livre (MeLi).

O SocialMeli aproxima compradores e vendedores: compradores podem seguir vendedores, acompanhar publicações (incluindo promoções) e consultar listas/contagens com ordenação.

🛒 API SocialMeli
API REST desenvolvida durante o Bootcamp do Mercado Livre (MeLi).

O SocialMeli aproxima compradores e vendedores: compradores podem seguir vendedores, acompanhar publicações (incluindo promoções) e consultar listas/contagens com ordenação.

📌 Índice
📋 Descrição
🎯 Requisitos (User Stories)
🧱 Arquitetura
🛠️ Tecnologias
📦 Modelos
📚 Documentação (Swagger)
🐳 Banco MySQL com Docker
▶️ Como Executar
🧪 Testes
⚠️ Tratamento de Erros
✅ Checklist de Entrega
👤 Autor


📋 Descrição
O SocialMeli permite:

Funcionalidade	Descrição
👥 Follow/Unfollow	Seguir e deixar de seguir vendedores
📊 Contagens	Consultar contagem de seguidores/seguidos
📋 Listagens	Listar seguidores e vendedores seguidos
📝 Publicações	Criar e consultar publicações
📰 Feed	Consultar feed das últimas 2 semanas
🔥 Promoções	Criar e consultar publicações promocionais



🎯 Requisitos (User Stories)
👥 Seguidores
US	Descrição	Endpoint
US-0001	Seguir um vendedor	POST /users/{userId}/follow/{sellerId}
US-0007	Deixar de seguir	POST /users/{userId}/unfollow/{sellerId}
US-0002	Contar seguidores	GET /users/{sellerId}/followers/count
US-0003	Listar seguidores	GET /users/{sellerId}/followers/list
US-0004	Listar seguidos	GET /users/{userId}/followed/list

📝 Publicações
US	Descrição	Endpoint
US-0005	Registrar publicação	POST /products/post
US-0006	Feed últimas 2 semanas	GET /products/followed/{userId}/list

🔥 Promoções
US	Descrição	Endpoint
US-0010	Publicar produto em promoção	POST /products/promo-post
US-0011	Contar produtos em promoção	GET /products/promo-post/count?user_id={userId}

🔎 Ordenação
Tipo	Parâmetros
Alfabética	name_asc, name_desc
Data	date_asc, date_desc

🧱 Arquitetura
Projeto baseado em Spring MVC com camadas:

┌─────────────────────────────────────────────────────────┐
│                      Controller                         │
│              (Endpoints REST - entrada HTTP)            │
├─────────────────────────────────────────────────────────┤
│                       Service                           │
│                  (Regras de negócio)                    │
├─────────────────────────────────────────────────────────┤
│                      Repository                         │
│              (Persistência - Spring Data JPA)           │
├─────────────────────────────────────────────────────────┤
│                       Database                          │
│                    (H2 / MySQL)                         │
└─────────────────────────────────────────────────────────┘

📁 Estrutura de Pacotes
bash
Copiar código
src/main/java/br/com/socialmedia/socialmedia/
├── controller/          # Endpoints REST
├── service/             # Interfaces de serviço
│   └── serviceImpl/     # Implementações
├── repository/          # Repositórios JPA
├── entity/              # Entidades JPA
├── dto/                 # DTOs (Request/Response)
│   ├── request/
│   └── response/
├── mapper/              # Conversão Entity <-> DTO
└── exception/           # Exceções e Handler


🛠️ Tecnologias
Tecnologia	Versão	Descrição
Java	21	Linguagem
Spring Boot	3.4.x	Framework
Spring Web	-	REST APIs
Spring Data JPA	-	Persistência
Bean Validation	-	Validações
Maven	-	Build
H2	-	Banco em memória (dev)
MySQL	8.0	Banco de dados (prod)
Docker	-	Containerização
Swagger/OpenAPI	-	Documentação

JUnit 5	-	Testes unitários
Mockito	-	Mocks para testes

📦 Modelos
User
Campo	Tipo	Descrição
userId	int	PK - Identificador
name	String	Nome do usuário
seller	boolean	É vendedor?

Post
Campo	Tipo	Descrição
postId	int	PK - Identificador
user	User	Vendedor dono do post
date	LocalDate	Data da publicação
category	int	Categoria
price	double	Preço
hasPromo	boolean	É promoção?
discount	double	Desconto (%)
product	Product	Produto (embedded)

Product (Embedded)
Campo	Tipo	Descrição
productId	int	ID do produto
productName	String	Nome
type	String	Tipo
brand	String	Marca
color	String	Cor
notes	String	Observações

UserFollow
Campo	Tipo	Descrição
id	int	PK - Identificador
follower	User	Quem segue (buyer)
seller	User	Quem é seguido (seller)
followedAt	LocalDateTime	Data do follow

📚 Documentação (Swagger)
Após subir a aplicação, acesse:

Recurso	URL
Swagger UI	http://localhost:8080/swagger-ui/index.html
OpenAPI JSON	http://localhost:8080/v3/api-docs

🐳 Banco MySQL com Docker
docker-compose.yml

services:
  mysql:
    image: mysql:8.0
    container_name: socialmeli-mysql
    ports:
      - "3306:3306"
    environment:
      MYSQL_DATABASE: socialmeli
      MYSQL_USER: socialmeli
      MYSQL_PASSWORD: socialmeli
      MYSQL_ROOT_PASSWORD: root
    volumes:
      - mysql_data:/var/lib/mysql
    restart: unless-stopped

volumes:
  mysql_data:
Comandos

# Subir MySQL
docker-compose up -d

# Verificar status
docker ps

# Parar
docker-compose down
▶️ Como Executar

Pré-requisitos

Java 21
Maven
Docker (opcional, para MySQL)
Opção 1: Com H2 (Desenvolvimento)
1. Configure o application.properties:
   
spring.datasource.url=jdbc:h2:mem:socialmedia;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

2. Execute:

mvn clean spring-boot:run

3. Acesse:
API: http://localhost:8080
H2 Console: http://localhost:8080/h2-console
Swagger: http://localhost:8080/swagger-ui/index.html
Opção 2: Com MySQL (Docker)

1. Suba o MySQL:

docker-compose up -d mysql

3. Configure o application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/socialmeli
spring.datasource.username=socialmeli
spring.datasource.password=socialmeli
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true


3. Execute:
mvn clean spring-boot:run

🧪 Testes
# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=UserServiceImplTest
mvn test -Dtest=PostServiceImplTest


Cobertura de Testes

UserServiceImpl
Teste	Cenário
✅	Follow com sucesso
✅	Follow self → BusinessException
✅	Follow target não seller → BusinessException
✅	Follow duplicado → ConflictException
✅	Unfollow com sucesso
✅	Unfollow sem seguir → ConflictException
✅	Contagem de seguidores
✅	Listagem com ordenação

PostServiceImpl
Teste	Cenário
✅	Publish com seller → salva com hasPromo=false
✅	Publish com buyer → BusinessException
✅	PublishPromo discount inválido → BusinessException
✅	PromoCount com buyer → BusinessException
✅	Feed últimas 2 semanas
✅	Order inválido → BusinessException
⚠️ Tratamento de Erros
A API retorna erros padronizados:

json
{
  "timestamp": "2025-01-05T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "User cannot follow themselves",
  "path": "/api/v1/users/1/follow/1"
}

Códigos de Erro
Status	Exceção	Descrição
400	BusinessException	Regra de negócio violada
404	EntityNotFoundException	Recurso não encontrado
409	ConflictException	Conflito (ex: já segue)
422	ValidationException	Erro de validação

✅ Checklist de Entrega
 Endpoints funcionando conforme requisitos
 Swagger acessível e documentando endpoints
 Validações e regras de negócio corretas
 Ordenação funcionando (name/date asc/desc)
 Tratamento de erros padronizado
 Banco via Docker (MySQL) configurado
 Testes unitários (ServiceImpl) implementados
 Testes de integração implementados
 README atualizado com instruções de execução
 
👤 Autor
Heinz Strabber Junior

GitHub

📄 Licença
Este projeto foi desenvolvido durante o Bootcamp do Mercado Livre para fins educacionais.
