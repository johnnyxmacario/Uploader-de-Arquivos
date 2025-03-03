# Documentação do Uploader de Arquivos

## Visão Geral

Este projeto implementa um serviço de upload e download de arquivos usando **Spring Boot**, **Amazon S3** para armazenamento na nuvem e **JDBC** para salvar informações dos arquivos no banco de dados.

## Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot**
- **Spring Web**
- **Spring JDBC**
- **Amazon S3 SDK**
- **PostgreSQL / MySQL**

## Endpoints da API

### Upload de Arquivo

- **Endpoint:** `POST /files/upload`
- **Parâmetro:** `file` (MultipartFile)
- **Descrição:** Envia um arquivo para o servidor, armazena localmente e no S3, e grava informações no banco de dados.
- **Resposta:** `200 OK` com mensagem de sucesso ou erro.

### Download de Arquivo

- **Endpoint:** `GET /files/download/{filename}`
- **Parâmetro:** `filename` (String)
- **Descrição:** Recupera um arquivo armazenado localmente.
- **Resposta:** Retorna o arquivo ou `404 Not Found` se não existir.

## Configuração do Banco de Dados

Crie a tabela para armazenar os metadados dos arquivos:

```sql
CREATE TABLE files (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    path TEXT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Configuração do Amazon S3

Adicione as configurações no `application.properties`:

```properties
aws.s3.bucket=nome-do-bucket
aws.access.key=SEU_ACCESS_KEY
aws.secret.key=SEU_SECRET_KEY
```

## Como Rodar o Projeto

1. Configure o banco de dados e o S3.
2. Compile e execute o projeto:
   ```sh
   mvn spring-boot:run
   ```
3. Use um cliente API como Postman para testar os endpoints.

## Melhorias Futuras

- Implementar autenticação JWT
- Adicionar suporte para armazenar arquivos diretamente no banco de dados
- Criar uma interface web para gerenciamento dos arquivos

