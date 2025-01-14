#  Sistema de Reserva e Avaliação de Restaurantes

Este projeto faz parte do *Tech Challenge - Fase 3*, nele foi construido um Sistema de Reserva e Avaliação de Restaurantes com sua construção baseada na estrutura *Clean Architecture*, utilizando tecnologias modernas como *Java*, *Spring Boot* e *Docker*, com foco na usabilidade e na escalabilidade. O sistema permite o cadastro de restaurantes com informações detalhadas, reserva de mesas para horários específicos, avaliação e comentários após visitas e o gerenciamento eficiente de reservas pelos estabelecimentos.

## Tecnologias Utilizadas

- **Java 17**: Versão de linguagem utilizada.
- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Swagger**: Para documentação e testes das APIs.
- **JUnit, AssertJ, Cucumber e Gatling**: Para criação de testes unitários, de comportamento (BDD) e de performance.


## Instruções para Acesso à Aplicação

A aplicação se encontra disponível no seguinte endereço:

DEPLOY AZURE (Container Instances): [[http://20.201.70.19:8080/swagger-ui/index.html](http://20.201.70.19:8080/swagger-ui/index.html)]

DEPLOY NUVEM GRATUITA (Render): [[https://restaurante-app-ql1h.onrender.com/swagger-ui/index.html](https://restaurante-app-ql1h.onrender.com/swagger-ui/index.html)]
Obs. Por se tratar de uma plataforma gratuita, pode haver lentidão no acesso.

URL LOCAL: [[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)]

## Para executar a aplicação via Docker, siga os comandos abaixo:

1. **Faça login no Docker:**
   ```bash
   docker login
    ```
2. **Baixe a imagem mais recente no Docker Hub:**
     ```bash
    docker pull romulosousa865/restaurante-app:latest
    ```
3. **Crie e inicie um container a partir da imagem com o seguinte comando**
     ```bash
    docker run -p 8080:8080 -d romulosousa865/restaurante-app:latest
    ```    
## Instruções para Execução dos Testes

- Comando para execução dos **Testes Unitários**:
   ```bash
    mvn test
    ```
- Comando para execução dos **Testes Integrados**:
   ```bash
    mvn test -P integration-test
    ```
- Comando para execução dos **Testes de Performance**:
   ```bash
    mvn gatling:test -P performance-test
    ```
- Comando para execução dos **Testes de Sistema (BDD)**:
*Obs. A aplicação deve estar em execução para que os testes de sistema possam ser executados corretamente.*
   ```bash
    mvn test -P system-test
    ```
## Documentação da API

A documentação da API é gerada automaticamente pelo Swagger. Você pode acessá-la inserindo /swagger-ui/index.html ao final da url ou no seguinte endereço após iniciar a aplicação localmente:

URL LOCAL: [[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)]

Consulte a documentação do Swagger UI para ver todos os endpoints disponíveis e detalhes sobre cada um deles.

## Configuração do Banco de Dados

Por padrão, o projeto utiliza o banco de dados H2.
# gerenciamento-pedidos-pedido
