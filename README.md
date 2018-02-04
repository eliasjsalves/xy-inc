# XY-INC

Aplicação back-end Restful que suporta o gerenciamento de entidades dinâmicas.

## Abordagem

Tendo em mente a simplicidade e dinamicidade da solução, optei pelas seguintes tecnologias:

Como o problema exige que sejam criadas entidades dinâmicas para atender a necessidade do desenvolvedor (cliente), decidi que seria mais interessante usar uma forma de armazenamento mais dinâmica, usando uma solução NoSQL (MongoDB), que é baseado em documentos e permite regras de validação das entidas para que, seguindo o requisito de definição de modelos, respeitar o esquema definido na criação.

Para a questão da API Restful que deve ser exposta, minha escolha foi uma implementação da expecificação JSR-311 (JAX-RS) que permite que sejam criados recursos simplesmente adicionando anotaçẽos em classes e métodos. Segui o padrão de respostas especificado pela [jsonapi.org](http://jsonapi.org).

Como servidor de aplicação, optei pelo Wildfly 11, pois implementa a especificação JavaEE, é open-source e é reconhecido por sua estabilidade e grande comunidade ativa.

## Recursos

### /management

Possui os recursos para a criação, listagem e remoção de modelos

Ex:


| Verbo | Recurso             | Descrição |
| :---- |:--------------------|:--------- |
| `GET`|`/management`|lista todos os modelos e seus atributos |
| `GET`|`/management/venda`|lista os atributos do modelo venda |
| `POST`|`/management/venda`|cria o modelo venda |
| `DELETE`|`/management/venda`|remove o modelo venda |

### /data

Possui os recursos para criação, listagem, edição e remoção de entidades

Ex:

| Verbo | Recurso             | Descrição |
| :---- |:--------------------|:--------- |
| `GET`|`/data/venda`|lista todos os objetos do modelo venda |
| `GET`|`/data/venda/{id}`|lista o objeto do modelo venda e id específico |
| `POST`|`/data/venda/{id}`|cria um objeto do modelo venda |
| `PUT`|`/data/venda/{id}`|edita um ou mais atributos de um objeto do modelo venda |
| `DELETE`|`/data/venda/{id}`|remove um objeto do modelo venda e id especifico |

## Build

Como é um projeto maven, é necessário apenas ter o Java 8 e executar o seguinte comando na raiz

```bash
$ mvn clean install
```

## Execução

Baixar e executar o mongodb, e criar a database `xy-inc`

```bash
$ ./mongod --dbpath /opt/db/xy-inc
```

Mover o artefato para a pasta de deployment e executar o Wildfly 11

```bash
$ cp target/xy-inc-0.0.1-SNAPSHOT.war /opt/wildfly-11.0.0.Final/standalone/deployments/
$ cd /opt/wildfly-11.0.0.Final/bin
$ ./standalone.sh
```

## Testes

Para os testes, foi criada uma classe com testes unitários (que são executados no momento do build) para testar a conexão com o MongoDb, e também estão anexos dois projetos do Postman com vários casos de uso dos recursos.

Obs.: Os testes unitários apenas obtém exito se o banco estiver em execução. Para pular os testes, adicionar a opção `-Dmaven;test.skip=true` ao build.


