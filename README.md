# 🗂️ Taskboard em Java (Spring Boot + Maven)

<a href="https://github.com/Caroline-Teixeira/taskboard/blob/main/README_ENG.md"><img src="https://raw.githubusercontent.com/yammadev/flag-icons/refs/heads/master/png/US%402x.png" alt="Inglês" ></a>

![Feito com Java](https://img.shields.io/badge/Feito%20com-Java-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-green?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-important?style=for-the-badge&logo=apachemaven)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql)


## 📖 Descrição

Este **Taskboard** é uma aplicação de gerenciamento de tarefas desenvolvida em **Java 21** com **Spring Boot**, utilizando **Maven** para gerenciamento de dependências e **MySQL 8** como banco de dados. A aplicação permite criar quadros, gerenciar colunas, adicionar, mover, bloquear, desbloquear e deletar cartões, além de listar movimentações e histórico de bloqueios. A interface é baseada em console, com menus interativos para facilitar a interação do usuário.

O projeto segue uma arquitetura em camadas (Model, Repository, Service, View) e utiliza **Spring Data JPA** para persistência, com suporte a transações e validações robustas.

## 🎯 Funcionalidades

- ✅ Criar e deletar quadros 
- ✅ Adicionar e deletar cartões
- ✅ Mover cartões entre colunas 
- ✅ Bloquear e desbloquear cartões
- ✅ Listar colunas de um quadro
- ✅ Listar movimentações de um cartão
- ✅ Listar histórico de bloqueios e bloqueios ativos
- ✅ Validação de regras (ex.: não deletar cartões bloqueados)
- ✅ Persistência de dados com MySQL 8.0
- ✅ Interface de console com menus interativos

## 📂 Estrutura do Projeto

```
.
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/board/taskboard/
│   │   │       ├── config/
│   │   │       │   └── AppConfig.java
│   │   │       ├── dto/
│   │   │       │   ├── BlockHistoryDTO.java
│   │   │       │   ├── BoardDTO.java
│   │   │       │   ├── CardDTO.java
│   │   │       │   ├── CardMovementDTO.java
│   │   │       │   └── TaskStatusDTO.java
│   │   │       ├── exception/
│   │   │       │   └── TaskboardException.java
│   │   │       ├── model/
│   │   │       │   ├── BlockHistory.java
│   │   │       │   ├── Board.java
│   │   │       │   ├── Card.java
│   │   │       │   ├── CardMovement.java
│   │   │       │   ├── Status.java
│   │   │       │   └── TaskStatus.java
│   │   │       ├── repository/
│   │   │       │   ├── BlockHistoryRepository.java
│   │   │       │   ├── BoardRepository.java
│   │   │       │   ├── CardMovementRepository.java
│   │   │       │   ├── CardRepository.java
│   │   │       │   └── TaskStatusRepository.java
│   │   │       ├── service/
│   │   │       │   ├── BlockHistoryService.java
│   │   │       │   ├── BoardService.java
│   │   │       │   ├── CardMovementService.java
│   │   │       │   ├── CardService.java
│   │   │       │   └── TaskStatusService.java
│   │   │       ├── util/
│   │   │       │   ├── AnsiColors.java
│   │   │       │   ├── ConsolePrinter.java
│   │   │       │   └── DateUtil.java
│   │   │       ├── view/
│   │   │       │   ├── BoardMenu.java
│   │   │       │   ├── CardActionsMenu.java
│   │   │       │   ├── CardMenu.java
│   │   │       │   ├── ListOptionsMenu.java
│   │   │       │   └── MainMenu.java
│   │   │       └── TaskboardApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── br/com/board/taskboard/
│               ├── service/
│               │   ├── BlockHistoryServiceTest.java
│               │   ├── BoardServiceTest.java
│               │   ├── CardMovementServiceTest.java
│               │   ├── CardServiceTest.java
│               │   └── TaskStatusServiceTest.java
│               └── TaskboardApplicationTests.java
└── target/
    └── classes/
        └── [arquivos compilados]
```

## 🚀 Como Executar

### 🖥️ Via IDE
1. Importe o projeto como um projeto Maven em sua IDE (ex.: IntelliJ IDEA, Eclipse).
2. Configure o banco de dados MySQL conforme a seção **Configurações**.
3. Execute a classe `TaskboardApplication.java` como uma aplicação Spring Boot.

### 🖥️ Via Terminal
1. Siga os passos da seção **Configurações** para clonar o repositório e configurar o banco de dados.
2. Compile e execute:
```bash
mvn clean install
mvn spring-boot:run
```

### Pré-requisitos:
- **Java 21** ou superior instalado
- **Maven 3.9.6** instalado e configurado no PATH
- **MySQL 8.0** instalado e configurado

## 🛠️ Configurações

### Clone o repositório:
```bash
git clone https://github.com/[seu-usuario]/[seu-repositorio].git
```

### Navegue até o diretório:
```bash
cd [seu-repositorio]
```

### Configure o banco de dados (MySQL):
```sql
CREATE DATABASE taskboard;
```

### Atualize o arquivo `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskboard
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Compile e execute:
```bash
mvn clean install
mvn spring-boot:run
```

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.x**
- **Maven 3.9.x**
- **MySQL 8** 
- **Spring Data JPA** 
- **Jakarta Transactions** para gerenciamento de transações
- **Streams API** para manipulação de coleções

## 📖 Explicação das Principais Classes e Pacotes

**Pacotes**
- `config` → Configurações da aplicação, no caso o Scanner.
- `dto` → Objetos de transferência de dados (DTOs) para comunicação entre camadas.
- `exception` → Exceções personalizadas, como `TaskboardException`.
- `model` → Entidades JPA representando as tabelas do banco (Board, Card, TaskStatus, etc.).
- `repository` → Interfaces JPA para operações de banco de dados.
- `service` → Lógica de negócios para manipulação de quadros, cartões e colunas.
- `util` → Classes utilitárias para formatação de console, cores e manipulação de datas.
- `view` → Classes de interface de usuário baseadas em console, com menus interativos.

**Classes Principais**
- `TaskboardApplication` → Ponto de entrada da aplicação Spring Boot.
- `BoardService` → Gerencia criação, listagem e exclusão de quadros.
- `CardService` → Gerencia criação, exclusão e manipulação de cartões.
- `TaskStatusService` → Gerencia colunas (TaskStatus) associadas aos quadros.
- `CardMovementService` → Registra movimentações de cartões entre colunas.
- `BlockHistoryService` → Gerencia histórico de bloqueios de cartões.
- `MainMenu` → Interface principal de console, com opções para todas as funcionalidades.
- `ConsolePrinter` → Utilitário para impressão formatada no console.
- `DateUtil` → Utilitário para manipulação de datas.

## 📌 Dicas para Melhorar ou Expandir

- 🖥️ Implementar uma interface gráfica (ex.: JavaFX ou Swing) em vez de console.
- 📊 Adicionar relatórios ou estatísticas de tarefas (ex.: cartões por status).
- 🔒 Implementar autenticação de usuários para quadros privados.
- 🧪 Expandir testes unitários com JUnit para cobrir mais casos.
- 💾 Suportar outros bancos de dados (ex.: PostgreSQL).
- 📱 Criar uma API REST para integração com frontends web ou mobile.

## 📄 Licença

Este projeto está sob a licença [MIT](LICENSE).

## 👨‍💻 Autor

<a href="https://github.com/Caroline-Teixeira">Caroline 💙</a>

---

📌 *Projeto desenvolvido para o desafio da DIO (Digital Innovation One).*