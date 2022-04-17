# sicred-sync

Aplicação SprintBoot standalone com a seguinte funcionalidade:
1. Processa um arquivo CSV de entrada com o formato abaixo.
2. Envia a atualização para a Receita através do serviço (SIMULADO pela classe ReceitaService).
3. Retorna um arquivo com o resultado do envio da atualização da Receita. Mesmo formato adicionando o resultado em uma 
nova coluna.

Formato CSV:
```
agencia;conta;saldo;status
0101;12225-6;100,00;A
0101;12226-8;3200,50;A
3202;40011-1;-35,12;I
3202;54001-2;0,00;P
3202;00321-2;34500,00;B
```
## 💻 Pré-requisitos
```
Java 1.8
Maven
```

## 🚀 Criando Jar 

Para criar o sicred-sync, execute o seguinte comando na raiz do projeto:

```
mvn package
```
Será criado um jar no diretório target. Segue um diretório como exemplo exemplo:

```
\sicred-sync\target\sicredi-synchronizer-0.0.1-SNAPSHOT.jar
```

## ☕ Usando sicred-sync

Para usar sicred-sync, execute o comando abaixo, passando como parametro o path do csv:

```
java -jar sicredi-synchronizer-0.0.1-SNAPSHOT.jar <input-file>
```

Será gerado um arquivo com o mesmo nome, acrecido com datetime, no mesmo diretório do arquivo original, segue exemplo
```
csvfile_1650231873367.csv
```


[⬆ Voltar ao topo](#nome-do-projeto)<br>
