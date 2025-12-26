# API SocialMeli

API REST desenvolvida durante o Bootcamp do Mercado Livre (MeLi).  
A proposta do **SocialMeli** é aproximar compradores e vendedores: compradores podem seguir vendedores, acompanhar publicações (incluindo promoções) e consultar listas/contagens com ordenação.

---

## 📌 Índice

- [📋 Descrição](#-descrição)
- [🎯 Requisitos (User Stories)](#-requisitos-user-stories)
- [🧱 Arquitetura](#-arquitetura)
- [🛠️ Tecnologias](#️-tecnologias)
- [📦 Modelos (resumo)](#-modelos-resumo)
- [📚 Documentação (Swagger)](#-documentação-swagger)
- [🐳 Banco MySQL com Docker](#-banco-mysql-com-docker)
- [▶️ Como executar localmente](#️-como-executar-localmente)
- [🧪 Testes Unitários (Service)](#-testes-unitários-service)
- [⚠️ Tratamento de erros](#️-tratamento-de-erros)
- [✅ Checklist de entrega](#-checklist-de-entrega)
- [👤 Autor](#-autor)

---

## 📋 Descrição

O **SocialMeli** permite:

- Seguir e deixar de seguir vendedores (follow/unfollow)
- Consultar contagem e lista de seguidores/seguidos
- Criar publicações (post)
- Consultar feed das últimas 2 semanas (somente vendedores seguidos)
- Criar publicações promocionais e consultar promoções

---

## 🎯 Requisitos (User Stories)

### 👥 Seguidores
- **US-0001**: Seguir um vendedor
- **US-0007**: Deixar de seguir um vendedor
- **US-0002**: Contar seguidores de um vendedor
- **US-0003**: Listar seguidores (Quem me segue?)
- **US-0004**: Listar seguidos (Quem estou seguindo?)

### 📝 Publicações
- **US-0005**: Registrar nova publicação
- **US-0006**: Listar publicações das últimas 2 semanas dos vendedores seguidos (ordenado por data)

### 🔥 Promoções
- Publicar produto em promoção
- Obter quantidade de produtos em promoção de um vendedor
- (Extra) Listar promoções de um vendedor (ou restrito a seguidores, se implementado)

### 🔎 Ordenação
- **Alfabética**: `name_asc` | `name_desc`
- **Data**: `date_asc` | `date_desc`

---

## 🧱 Arquitetura

Projeto baseado em **Spring MVC** com camadas:

- **Controller**: entrada HTTP (endpoints REST)
- **Service**: regras de negócio
- **Repository**: persistência (Spring Data JPA)
- **DTOs**: contratos de entrada/saída (Request/Response)
- **Exception Handler**: padronização de erros

---

## 🛠️ Tecnologias

- Java 21
- Spring Boot
- Spring Web (Spring MVC)
- Spring Data JPA
- Bean Validation
- Maven
- MySQL (via Docker)
- H2 (opcional para desenvolvimento/testes)
- Swagger/OpenAPI (springdoc)

---

## 📦 Modelos (resumo)

### User
- `id` (PK)
- `name`
- `seller` (boolean)

### Post
- `postId` (PK)
- `user` (seller dono do post)
- `date`
- `category`
- `price`
- `hasPromo`
- `discount`
- `product` (embedded)

---

## 📚 Documentação (Swagger)

Após subir a aplicação:

- Swagger UI:  
  `http://localhost:8080/swagger-ui/index.html`

- OpenAPI JSON:  
  `http://localhost:8080/v3/api-docs`

---

## 🐳 Banco MySQL com Docker

Este projeto pode utilizar **MySQL em container** e rodar a aplicação **localmente**.

### 1) Subir MySQL
Crie (ou use) um `docker-compose.yml` com o serviço do MySQL:

```yaml
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


▶️ Como executar localmente
Pré-requisitos
Java 21
Maven
Docker (para subir o MySQL)
1) Configurar datasource (MySQL)
No application.properties:


spring.datasource.url=jdbc:h2:mem:socialmedia;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

2) Rodar aplicação
mvn clean spring-boot:run

🧪 Testes Unitários (Service)
Os testes unitários focam nas regras de negócio (ServiceImpl), usando JUnit 5 + Mockito.

Rodar testes:
mvn test

Recomendação (mínimo)
UserServiceImpl

follow sucesso
follow self → BusinessException
follow target não seller → BusinessException
follow duplicado → ConflictException
unfollow sucesso
unfollow sem seguir → ConflictException
PostServiceImpl

publish com seller → salva, seta hasPromo=false, discount=0
publish com buyer → BusinessException
publishPromo discount inválido → BusinessException
promoCount com buyer → BusinessException
feed últimas 2 semanas retorna lista (ou vazio)
order inválido → BusinessException
⚠️ Tratamento de erros
A API retorna erros padronizados (exemplo):

timestamp
status
error
message
path
fieldErrors (quando for validação)
✅ Checklist de entrega
 Endpoints funcionando conforme requisitos
 Swagger acessível e documentando endpoints
 Validações e regras de negócio corretas
 Ordenação funcionando (name/date asc/desc)
 Tratamento de erros padronizado
 Banco via Docker (MySQL) configurado
 Testes unitários (ServiceImpl) implementados
 README atualizado com instruções de execução
👤 Autor
Heinz Strabber Junior
Recomendação (mínimo)
UserServiceImpl

follow sucesso
follow self → BusinessException
follow target não seller → BusinessException
follow duplicado → ConflictException
unfollow sucesso
unfollow sem seguir → ConflictException
PostServiceImpl

publish com seller → salva, seta hasPromo=false, discount=0
publish com buyer → BusinessException
publishPromo discount inválido → BusinessException
promoCount com buyer → BusinessException
feed últimas 2 semanas retorna lista (ou vazio)
order inválido → BusinessException
⚠️ Tratamento de erros
A API retorna erros padronizados (exemplo):

timestamp
status
error
message
path
fieldErrors (quando for validação)
✅ Checklist de entrega
 Endpoints funcionando conforme requisitos
 Swagger acessível e documentando endpoints
 Validações e regras de negócio corretas
 Ordenação funcionando (name/date asc/desc)
 Tratamento de erros padronizado
 Banco via Docker (MySQL) configurado
 Testes unitários (ServiceImpl) implementados
 README atualizado com instruções de execução
👤 Autor
Heinz Strabber Junior
