
![hotdoctor_plugin](https://user-images.githubusercontent.com/20392025/234732022-c5973c27-3288-4eb6-b7f8-5ceae234cf32.png)
# PAPIBridge Español



## Tabla de Contenidos

- [Descripción](#descripción)
- [Dependencias](#dependencias)
- [Instalación](#instalación)
- [Establecer conexión con base de datos](#establecer-conexión-con-base-de-datos)
- [Subir información a base de datos](#subir-información-a-base-de-datos)
- [Descargar información de la base de datos](#descargar-información-de-la-base-de-datos)
- [Establecer comunicación entre servidores mediante Redis](#establecer-comunicación-entre-servidores-mediante-redis)
- [Compilación y Construcción de Proyecto](#compilación-y-construcción-de-proyecto)

## Descripción

Proyecto realizado en JAVA 8 con Maven en Minecraft, cuyo propósito principal es el envío de información de jugadores de forma masiva dentro de un servidor X, y plasmarla en una base de datos SQL o no SQL (Actualmente compatible: MongoDB y Redis), de misma manera, se mantiene como objetivo permitir que esta información la cual está dentro de la instancia de base de datos, pueda reenviarsela a un servidor de Minecraft diferente al que se envió la información, para mantener la comunicación entre servidores.

## Dependencias

La única dependencia que tiene este proyecto es la siguiente:

[PlaceholderAPI](https://www.spigotmc.org/resources/6245/)

Este proyecto es el que nos facilitará la obtención de estadísticas de jugador o información del mismo en manera de texto, y el proyecto se encargará de obtenerla para luego subirla a la base de datos deseada.

ADVERTENCIA: Usted debe asegurarse que su servidor tenga instanciada la expansión de Jugador, si no la tiene o no está seguro, se le sugiere ejecutar el siguiente comando dentro del videojuego en su servidor:

```
/papi ecloud download Player
```
y, finalmente
```
/papi reload
```

## Instalación

1) Descargar este proyecto
2) Descargar las dependencias del proyecto
3) En tu servidor de Minecraft, ir a la carpeta /plugins/ y aquí soltar los dos archivos descargados
4) Reiniciar tu servidor
5) Una vez reiniciado su servidor y las debidas carpetas de configuración de ambos proyectos se hayan generado, seguir los pasos de establecimiento de conexión con base de datos de este repositorio, y luego establecer la comunicación de subida y descarga de esta información.


## Establecer conexión con base de datos

Para poder inicializar una conexión entre este proyecto y su base de datos, debe seguir estos pasos:

1) Ir al archivo /plugins/UltraMySQLPAPIBridge/config.yml y abrirlo con su editor de texto de preferencia.
2) Seguir los siguientes pasos dependiendo su base de datos:

Si queremos establecer conexión con una base de datos MySQL, tendremos que seguir la siguiente sintaxis:
```
placeholders:
  '1':
    TYPE: 'MySQL' 
    Host: 'localhost'  ## Host, puede ser también numérico.
    Port: '3306' 
    User: 'root'  
    Password: 'password' 
    Database: 'PAPIBridge'  
    useSSL: true  ## Depende en caso de tener certificado o no al momento de establecer una conexión, puede causar errores en caso de estar mal configurado.
```

Si queremos establecer conexión con una base de datos MongoDB, tendremos que seguir la siguiente sintaxis:
```
placeholders:
  '1':
    TYPE: 'MongoDB' 
    URL: 'mongodb://<user1>:<password>@<port>/<database>?ssl=<true/false>' ## De preferencia colocar la URL que otorga un cluster de Monogo en la conexión con Drivers mediante JAVA.
    Database: 'test' ## Debe ser la misma que está en la URL.
```

Si queremos establecer conexión con una base de datos Redis, tendremos que seguir la siguiente sintaxis:
```
placeholders:
  '1':
    TYPE: 'Redis' 
    Host: 'localhost' 
    Port: '3306' 
    Password: 'password' 
    server: hub ## Aquí no debemos confundirnos, aquí debe ser el nombre en concreto que tenga el servidor en la config.yml del servidor proxy BUNGEECORD. Por lo tanto, debe variar cada vez que se desee instalar el proyecto en dos servidores diferentes.
```
3) Una vez usted haya configurado con éxito el proyecto, deberá reiniciar el servidor.
4) Una vez reiniciado, si no tiene errores de CONEXIÓN, puede seguir con el siguiente paso, que es la configuración de subida o descarga de la información dentro de la base de datos.

## Subir información a base de datos

Para poder configurar que la información de una variable se suba con éxito a su instancia, debemos configurarla como tipo SUBIDA.
Si usted está usando una instancia MySQL, debe seguir la siguiente sintaxis:
```
identifiers:
  'kitbattle_coins':  ## Este debe ser igual a la variable que usted desea subir a su base de datos
    TYPE: "UPLOAD" ## Inicialización del tipo.
    TABLE: 'coins' ## Tabla en la que se guardará la información (Se crea automáticamente)
    COLUMN-TO-SAVE-INFO: 'Coins'  ## Columna en la que se guardará la información que nos trae la variable
    TIMER: 180 ## Tiempo en segundos, determinará cada cuanto segundos subirá la información a la respectiva base de datos.
```

Si usted está usando una instancia MongoDB, debe seguir la siguiente sintaxis:
```
identifiers:
  'player_health': ## Este debe ser igual a la variable que usted desea subir a su base de datos
    TYPE: "UPLOAD" ## Inicialización del tipo.
    Collection: healthCollection ## Colección en la que se guardará la información (Se crea automáticamente)
    KEY-TO-SAVE-INFO: 'Value' ## Key en la que se guardará la información que nos trae la variable (key: valor)
    TIMER: 160 ## Tiempo en segundos, determinará cada cuanto segundos subirá la información a la respectiva base de datos.
```

Si usted está usando una instancia Redis, lamentablemente, debido a que Redis guarda su información en caché, no es compatible.

Finalmente, como ejemplo, debería verse así dentro de la base de datos:
![mysql](https://user-images.githubusercontent.com/20392025/234730600-4dab8276-4d46-485c-9810-6a94f65858f0.png)



## Descargar información de la base de datos

Para poder configurar que la información de una variable se descargue con éxito de su instancia, debemos configurarla como tipo DESCARGA.
Si usted está usando una instancia MySQL, debe seguir la siguiente sintaxis:
```
identifiers:
  'kitbattle_deads': ## Este será el nombre en el que se pegará dentro del servidor destino, bajo la variable de %MySQLBridge_<placeholder> (En este caso, %MySQLBridge_deads%) 
    TYPE: "DOWNLOAD" ## Inicialización del tipo.
    TABLE: 'deads' ## Tabla en la que está la información
    COLUMN: 'Deads' ## Columna la cual se agarrará la información en concreto.
    WHERE: 'player_uuid="%player_uuid%" AND player_name="%player_name%" ##Condicional, la cual ayudará a especificar y obtener la información de usuarios en concretos, la cual, se buscará bajo una columna X llamada "player_uuid" a un usuario cuya variable es "%player_uuid%" (Esta se reemplaza al buscar), para obtener la información concreta de ese usuario, lo mismo pasa con player_name.
    TIMER: 200 ## Tiempo en segundos, determinará cada cuanto segundos descargará la información a la respectiva base de datos.
    loading: '&cLoading information...' ## (OPCIONAL) Reemplazará la información si el proyecto todavía no ha consultado en la base de datos, bajo ese texto.
    result-not-found: '0' ## (OPCIONAL) Reemplazará la información si el proyecto ya consultó en la base de datos, pero no encontró información.
```

Si usted está usando una instancia MongoDB, debe seguir la siguiente sintaxis:
```
identifiers:
  'savedHealth':  ## Este será el nombre en el que se pegará dentro del servidor destino, bajo la variable de %MySQLBridge_<placeholder> (En este caso, %MySQLBridge_savedHealth%) 
    TYPE: "DOWNLOAD" ## Inicialización del tipo.
    Collection: healthCollection ## Colección en la que está la información
    result-not-found: 'Not Found' ## (OPCIONAL) Reemplazará la información si el proyecto ya consultó en la base de datos, pero no encontró información.
    loading: '&cLoading information...' ## (OPCIONAL) Reemplazará la información si el proyecto todavía no ha consultado en la base de datos, bajo ese texto.
    KEY: 'Value' ## Key la cual se agarrará la información en concreto. (Key: value)
    TIMER: 160 ## Tiempo en segundos, determinará cada cuanto segundos descargará la información a la respectiva base de datos.
    WHERE: ##Condicional, la cual ayudará a especificar y obtener la información de usuarios en concretos, la cual, se buscará bajo una columna X llamada "player_uuid" a un usuario cuya variable es "%player_uuid%" (Esta se reemplaza al buscar), para obtener la información concreta de ese usuario, lo mismo pasa con player_name.
    - 'playerName:%player_name%'
    - 'playerUUID:%player_uuid%'
```

Si usted está usando una instancia Redis, lamentablemente, debido a que Redis guarda su información en caché, no es compatible.

Finalmente, como ejemplo, debería verse así la información dentro del juego:
![giphy](https://user-images.githubusercontent.com/20392025/234730665-873ede83-13ad-40aa-9aeb-12067b0bac25.gif)




## Establecer comunicación entre servidores mediante Redis

Debido a que, Redis es una base de datos que almacena la información en caché, lamentablemente no puede guardar la información de los jugadores como tal dentro de su base de datos, más sin embargo, lo que si podemos incluir, es que se permita la comunicación entre dos servidores mediante Redis.

Por lo tanto, se deberá utilizar la siguiente sintaxis:
```
identifiers:
  'skywars_wins': ## Este debe ser igual a la variable que usted desea subir a su base de datos, y será la misma en la que usted deberá pegar en su servidor de destino, lo único que usted necesita es que ambos servidores estén entrelazados y conectados a su servidor de Redis mediante este proyecto.
    targetServer: hub ## Este es el servidor destino, el cual recibirá la información de %skywars_wins% del jugador. Y para acceder a la misma infoormación, se usará la misma variable, solo incluyendo el identificador de este proyecto, el que es, %MySQLBridge_<placeholder> (En este caso, %MySQLBridge_skywars_wins%)
    TIMER: 20  ## Tiempo en segundos, determinará cada cuanto segundos preguntará por la información a la respectiva base de datos y a los servidores conectados.
``` 

## Compilación y Construcción de Proyecto

1) Clonar este repositorio.
2) En caso de hacer ediciones, hágalo.
3) Para construir el archivo JAR de este proyecto, se debe ejecutar el siguiente comando en consola:
```
mvn package
```
4) Se creará una carpeta /target/ y allí estará el archivo JAR del proyecto.


