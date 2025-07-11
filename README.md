🎧 Musicfy E-commerce API  
API RESTful para a plataforma de e-commerce **Musicfy**, construída com **Spring Boot**.  
A API gerencia produtos, carrinhos de compras, pedidos, usuários e pagamentos através da integração com o **Stripe**.

---

## ✨ Funcionalidades

- 📦 **Gerenciamento de Produtos**: CRUD completo para produtos e suas características.
- 🛒 **Carrinho de Compras**: Adicionar, remover, atualizar e limpar itens do carrinho.
- 👤 **Usuários**: Criação e autenticação com **Firebase Authentication**.
- 🏠 **Endereços**: Gerenciamento de endereços de entrega para os usuários.
- 💳 **Checkout e Pagamentos**: Integração com o **Stripe** para processar pagamentos com segurança.
- 📬 **Pedidos**: Acompanhamento do status dos pedidos desde o processamento até a entrega.
- 🔄 **Webhook Stripe**: Recebe e processa eventos do Stripe, como a confirmação de pagamento.

---

## 🛠️ Tecnologias Utilizadas

### 🔙 Backend
- Java 17  
- Spring Boot 3  
- Spring Data JPA  
- Maven  

### 🗄️ Banco de Dados
- PostgreSQL  

### 🔐 Autenticação
- Firebase Authentication  

### 💰 Pagamentos
- Stripe API  

### 📦 Outras Dependências
- Lombok  
- Dotenv-java (gerenciamento de variáveis de ambiente)

---

## 🚀 Como Executar o Projeto

### ✅ Pré-requisitos
- Java 17 ou superior  
- Maven 3.9 ou superior  
- PostgreSQL instalado e em execução  
- Conta no [Firebase](https://firebase.google.com/)  
- Conta no [Stripe](https://stripe.com/)  

---

### 1. 🔽 Clonar o Repositório
```bash
git clone <url-do-seu-repositorio>
cd back-musicfy-origin

```
### 2. 🧩 Configurar o Banco de Dados

Crie um banco no PostgreSQL com o nome desejado (ex: `musicfy`).

Configure o `application.properties` ou um arquivo `.env` com as credenciais:

```env
DB_URL=jdbc:postgresql://localhost:5432/musicfy
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
````

### 3. 🔐 Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
# Configuração do Firebase
FIREBASE_CONFIG={ "type": "service_account", "project_id": "...", ... }

# Stripe
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Banco de Dados (se necessário)
DB_URL=jdbc:postgresql://localhost:5432/musicfy
DB_USER=seu_usuario
DB_PASSWORD=sua_senha

````
### 4. ▶️ Executar a Aplicação

Use o **Maven Wrapper** incluído para executar a aplicação:

```bash
# No Linux ou macOS
./mvnw spring-boot:run

# No Windows
./mvnw.cmd spring-boot:run

