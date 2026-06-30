# 🐾 VetClinic API

API RESTful multi-tenant para gerenciamento de clínicas veterinárias.  
Inspirado no modelo SaaS do SimplesVet: um único sistema onde o dono da plataforma cadastra clínicas, e cada clínica gerencia seus próprios dados de forma isolada.

---

## 🏗️ Arquitetura Multi-Tenant

Cada clínica cadastrada no sistema acessa **apenas seus próprios dados**.  
O isolamento é garantido por tenant ID em todas as operações, impedindo que uma clínica visualize informações de outra.

Empresa (dono do sistema)
└── Clínica A
│   ├── Recepcionistas
│   ├── Veterinários
│   ├── Clientes
│   └── Pets / Agendamentos
└── Clínica B
│   ├── Recepcionistas
│   ├── Veterinários
│   ├── Clientes
│   └── Pets / Agendamentos

---

## 🚀 Tecnologias

- **Java 17+** com **Spring Boot**
- **Spring Security** + **JWT** para autenticação e autorização
- **PostgreSQL** como banco de dados relacional
- **Maven** para gerenciamento de dependências
- **Docker** + **Docker Compose** para containerização

---

## 🔐 Autenticação

O sistema utiliza **JWT (JSON Web Token)** via Spring Security.  
Cada token carrega o tenant (clínica) do usuário, garantindo o isolamento dos dados.

**Perfis de acesso:**
| Perfil | Permissões |
|---|---|
| `ADMIN` (dono do sistema) | Cadastrar e gerenciar clínicas |
| `GESTOR` (dono da clínica) | Cadastrar recepcionistas e veterinários |
| `RECEPCIONISTA` | Cadastrar clientes, pets e agendamentos |
| `VETERINARIO` | Visualizar e atender agendamentos |

---

## ⚙️ Como rodar localmente

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Maven

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

### 2. Configure as variáveis de ambiente
```bash
cp application.properties src/main/resources/application-local.properties
# Edite o arquivo com suas credenciais
```

### 3. Suba o banco com Docker
```bash
docker-compose up -d
```

### 4. Rode a aplicação
```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`
Na classe AdminUserConfig, você pode configurar o usuário administrador.

---

## 📌 Principais Endpoints

### Autenticação
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Login e geração do token JWT |

### Empresa (ADMIN)
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/empresa/clinicas` | Cadastrar nova clínica |

### Gestão da Clínica (GESTOR)
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/clinica/recepcionistas` | Cadastrar recepcionista |
| `POST` | `/clinica/veterinarios` | Cadastrar veterinário |

### Recepcionista
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/clientes` | Cadastrar cliente |
| `POST` | `/pets` | Cadastrar pet |

> ⚠️ Todos os endpoints (exceto `/auth/login`) exigem o header `Authorization: Bearer <token>`

---

## 🐳 Docker

O projeto inclui `docker-compose.yml` com PostgreSQL configurado:

```bash
docker-compose up -d    # Sobe o banco
docker-compose down     # Para os containers
```

---

## 📄 Licença

MIT License — sinta-se livre para usar e modificar.