                      CREATE TABLE tb_tenant (
                      	id SERIAL PRIMARY KEY,
                      	cpf VARCHAR(11),
                          nomedatabase VARCHAR(255),
                          usernamedatabase VARCHAR(255),
                          passworddatabase VARCHAR(255),
                      	email VARCHAR(255)
                      );

                      CREATE TABLE tb_endereco (
                          id SERIAL PRIMARY KEY,
                          rua VARCHAR(100) NOT NULL,
                      	numero INT NOT NULL,
                          bairro VARCHAR(100) NOT NULL,
                          cidade VARCHAR(15) NOT NULL,
                      	estado VARCHAR(15) NOT NULL,
                          cep INT NOT NULL,
                      	complemento VARCHAR(15) NOT NULL
                      );

                      CREATE TABLE tb_administrador (
                      	id SERIAL PRIMARY KEY,
                          cpf VARCHAR(11),
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL,
                      	senha VARCHAR(100) NOT NULL,
                          telefone VARCHAR(15) NOT NULL,
                          endereco INT REFERENCES tb_endereco(id) ON DELETE CASCADE,
                          data_nascimento DATE NOT NULL,
                      	role SMALLINT CHECK (role >= 0 AND role <= 6)
                      );