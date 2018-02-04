# XY-INC

Aplicação back-end Restful que suporta o gerenciamento de entidades dinâmicas.

## Abordagem

Tendo em mente a simplicidade e dinamicidade da solução, optei pelas seguintes tecnologias:

Como o problema exige que sejam criadas entidades dinâmicas para atender a necessidade do desenvolvedor (cliente), decidi que seria mais interessante usar uma forma de armazenamento mais dinâmica, usando uma solução NoSQL (MongoDB), que é baseado em documentos e permite regras de validação das entidas para que, seguindo o requisito de definição de modelos, respeitar o esquema definido na criação.

Para a questão da API Restful que deve ser exposta, minha escolha foi uma implementação da expecificação JSR-311 (JAX-RS) que permite que sejam criados recursos simplesmente adicionando anotaçẽos em classes e métodos. Segui o padrão de respostas especificado pela jsonapi.org.

Como servidor de aplicação, optei pelo Wildfly 11, pois implementa a especificação JavaEE, é open-source e é reconhecido por sua estabilidade e grande comunidade ativa.

## Recursos

/management

Possui os recursos para a criação, listagem e remoção de modelos
Ex: 

POST /management/venda - cria o modelo 'venda'
GET  /management/venda - lista os atributos do modelo 'venda'


/data

Possui os recursos para criação, listage, edição e remoção de entidades

## Build

Como é um projeto maven, é necessário apenas ter o Java 8 e executar o seguinte comando na raiz

```bash
$ mvn clean install
```

## Testes

Para os testes, está anexo um projeto do Postman com vários casos de uso dos recursos. Outra abordagem para isto
