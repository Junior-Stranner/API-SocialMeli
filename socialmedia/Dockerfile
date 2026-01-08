# ==============================================================================
# STAGE 1: BUILD
# ==============================================================================
# Usa a imagem do Maven com Java 21 para compilar o projeto
# Esta imagem é grande (~400MB), mas só é usada para gerar o .jar
FROM maven:3.9.7-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro (otimização de cache)
# Se as dependências não mudarem, o Docker usa o cache
COPY pom.xml ./

# Baixa as dependências antes de copiar o código
# Isso evita baixar tudo novamente se só o código mudar
RUN mvn dependency:resolve

# Agora copia o código fonte
COPY src ./src

# Compila o projeto e gera o .jar
# -DskipTests: pula os testes para buildar mais rápido
# -q: modo silencioso (menos logs)
RUN mvn clean package -DskipTests -q

# ==============================================================================
# STAGE 2: RUNTIME
# ==============================================================================
# Usa uma imagem mínima apenas com JRE (Java Runtime Environment)
# Alpine é uma distro Linux super leve (~5MB)
# Resultado: imagem final ~150MB em vez de ~400MB
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia APENAS o .jar gerado no stage anterior
# Não traz o Maven, código fonte, ou dependências de build
COPY --from=build /app/target/*.jar app.jar

# Documenta a porta que a aplicação usa
# (não abre a porta, só documenta)
EXPOSE 8080

# Configurações de memória da JVM (opcional mas recomendado)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando que executa quando o container inicia
# Usa shell form para expandir a variável JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Metadados da imagem
LABEL maintainer="Heinz Stranner Junior"
LABEL description="API Social Meli - Mecado Livre"
LABEL version="1.0"