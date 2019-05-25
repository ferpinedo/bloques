# Mundo de bloques
#### Proyecto de Inteligencia Artificial sobre un Sistema Basado en Conocimiento

## Planteamiento del proyecto

“Realizar una aplicación en prolog en donde se defina escenarios de bloques colocados sobre un piso, las órdenes se harán en lenguaje natural. El escenario inicial puede ser cargado desde un archivo texto en donde se especifique en lenguaje natural el escenario. El conocimiento que se aplica para realizar los movimientos de bloques estará en una base de conocimiento en reglas definidas en texto (no código), este archivo se cargará y se recibirá la orden por el usuario en lenguaje natural y el sistema debe realizar los movimientos que corresponden para ejecutar la orden aplicando las reglas establecidas en la base de conocimiento en reglas, esto lo hará el proceso inteligente.

Los objetos pueden ser de cualquier tipo, por ejemplo, cubos, cilindros, caja. Estos objetos pueden tener atributos, por ejemplo, color, tamaño, etc. Las reglas deben establecer la lógica de los movimientos que se deben aplicar al escenario de bloques de una manera lo más inteligente posible.”

## Solución propuesta

Programa en Java que procese las reglas, instrucciones y escenarios dados en lenguaje natural para convertirlos a instrucciones de Prolog, ejecutarlo y probarlo.

## Desarrollo de la solución

### Análisis

Lo primero que hice fue analizar el planteamiento del proyecto. Me di cuenta de que prácticamente iba a desarrollar un “compilador” de lenguaje natural para Prolog. Por ello el usuario debería de respetar ciertas reglas y estructuras básicas. Estas fueron las que yo definí:

1. Se deben de declarar las características y estados de las entidades (si la entidad tiene color, tamaño, la facultad de ponerse sobre un objeto, ...). Estas características y estados son “relaciones”, pues nos permiten relacionar a cierta entidad con cierta capacidad o característica.
2. Se deben declarar las reglas incluyendo sus condiciones (colocadas después de “si” o “cuando” y antes de “entonces”) y por supuesto, también sus acciones resultantes.
3. Las reglas más básicas (yo las llamé “primitivas”) deberán de ser sobre crear y destruir relaciones, es decir vincular y desvincular entidades a través de una facultad o vincular una característica a una entidad.

Después, comencé a desarrollar el *traductor* de lenguaje natural a Prolog. Entendí que debía de crear un diccionario de conceptos que estuvieran relacionados con una lista de sinónimos que significaran lo mismo, para entonces poder identificar conceptos claves.

### Código en Java

Mi primer idea fue utilizar la técnica “*Word Vectors*”, sin emarbgo, para ello necesitaba una tarjeta gráfica que permitiera analizar millones de palabras, de lo contrario el tiempo de ejecucución se excedería a horas.  


Por ello, para poder procesar el texto en lenguaje natural, recurrí a una librería de Stanford llamada *StanfordCoreNLP*, que me permitió hacer uso de las siguientes funcionalidades:

- *Tokenizing*: obtener los elementos de una oración (puntos y palabras) de una manera especializada, que puede detectar que un correo [fer@mail.com](mailto:fer@mai.com) no se debe de separar en “fer@mail”, “.” y “com”.
- *POSTagging:* position-of-sentence tagging. Esta herramienta fue demasiado útil pues me permitió identificar la categoría gramatical de las oraciones procesadas, permitiendo filtrarlas y hacer una traducción más certera.
- La librería incluye muchas otras funcionalidades que no utilice, pues algunas no eran necesarias y otras sólo tenían modelos disponibles en inglés (si quería trabajar con el lenguaje Español, tenía que entrenar mi propio modelo).

### Proceso de *traducción* de reglas

1. Entrada de texto de reglas en lenguaje natural de un archivo .*txt*.

2. Separar palabras por línea y oraciones detectadas.

3. Procesar línea por línea:

4. 1. Si la línea contiene palabras relacionadas con el concepto de Característica (propiedad, estado, particularidad) o con el concepto de Entidad (objeto. variable), entonces traducir a predicado:

   2. 1. Filtrar verbos auxiliares, pronombres, concepto de entidad y artículos de la línea.
      2. Formar un predicado dinámico con las palabras restantes
      3. Guardar esos predicado

   3. Si la línea contiene palabras relacionadas con el concepto de “si” o “cuando” (condicional), entonces traducir las líneas que siguen a una  regla:

   4. 1. La primera deberá tener el nombre de la regla o cláusula. Para encontrarla buscamos palabras relacionadas con la palabra “orden”.

      2. 1. Se identifican y almacenan las variables que contiene la regla.

      3. Las siguientes líneas tendrán las premisas de la condición

      4. 1. Se buscan palabras que se relacionen con los predicados previamente almacenados. Se utilizan para nombrar la cláusula.
         2. Se buscan las variables de la regla previamente almacenadas y se agregan en el orden correspondiente a la cláusula.

      5. Continuar traduciendo hasta llega a palabras relacionadas con el concepto de “entonces”. Al llegar a ese punto, comenzar a traducir las siguientes líneas en acciones 

      6. 1. Se busca una palabra relacionada con los nombres de las reglas previamente almacenadas. En un principio las únicas reglas almacenadas son las existentes en Prolog: assert, retract, … Por ello se buscan también palabras que se relacionen con esos conceptos.
         2. Una vez encontradas se utilizan para nombrar la cláusula
         3. Se buscan las variables de la regla previamente almacenadas y se agregan en el orden correspondiente a la cláusula.
         4. Continuar traduciendo líneas a reglas hasta encontrarse con uno de los casos a) o b) del paso 3.

      7. Almacenar regla

   5. Continuar procesando líneas hasta que no quede ninguna sin ser procesada


![img](https://lh5.googleusercontent.com/vHnc1baWf1jwnM0118Sumrn0-kdUsVWozKzBvYdGQhB-fpu_jH1f2vWp4aZizLO8AlqL_IhW-69vRyHOb3JgM7DC-g5QGkjmEuIrImCThIo1FT6Gc6mD3wa_QmYVHYmN-aicZKY)
### Estructura de proyecto

Este es la estructura de mi proyecto en Java. 

- El paquete *controller* contiene clases relacionadas con la interfaz gráfica. Son las que le dan la funcionalidad a la ventana o vista de la aplicación

- El paquete *nlp* contiene las clases que permiten hacer la traducción de LN a Prolog.

- - Contiene la clase final Dictionary, que integra todos los sinónimos relacionados a los conceptos (de la *enum* Concept) que vi más se utilizarían en la definición de reglas.
  - Clase NLInterpreter que utiliza funciones de Stanford CoreNLP para Tokenizar, limpiar y preparar oraciones para su traducción.
  - La *enum* PrologKey contiene algunas  conceptos de funciones o palabras reservadas Prolog, como: *ATTACH, DETACH, ADD, SUBSTRACT, DEVIDE, NOT, MULTIPLY , EQUAL…*
  - La clase Token es una clase estructural que me permite asociar una palabra (Token) con su POS.
  - Por último la clase más importante: PrologTranslator. Esta clase es la columna vertebral de la traducción. Es quien llama a funciones de todas las demás clases para poder convertir el texto

- En el paquete de *prolog* tengo clases de modelo que sirven para ver un programa de Prolog como un objeto de una clase de Java. 

- - La clase Program contiene la estructura de un programa en Prolog.
  - La clase Clause corresponde a una cláusula en Prolog y de ella se derivan PredicateClause, Predicate, Fact y Rule, que a su vez corresponden a predicados, hechos y reglas en Prolog.
  - La *enum* Type contiene los conceptos de los tipos de elementos que se pueden encontrar en un program de prolog: *ENTITY, PREDICATE, RULE.*



## Referencias

- Word Vectors: https://gist.github.com/aparrish/2f562e3737544cf29aaf1af30362f469

- Stanford CoreNLP: https://github.com/stanfordnlp/CoreNLP

- Conceptos de Prolog: [http://www.projog.org/prolog-introduction.html](http://www.projog.org/prolog-introduction.html), [http://www.ablmcc.edu.hk/~scy/prolog/pro02.htm](http://www.ablmcc.edu.hk/~scy/prolog/pro02.htm), https://www.doc.gold.ac.uk/~mas02gw/prolog_tutorial/prologpages/rules.html, [http://www.ablmcc.edu.hk/~scy/prolog/pro01.htm](http://www.ablmcc.edu.hk/~scy/prolog/pro01.htm)
