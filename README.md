# PAPIBridge

## Tabla de Contenidos

- [Descripción](#descripción)
- [Dependencias](#dependencias)
- [Instalación](#instalación)
- [Establecer conexión con base de datos](#establecer-conexión-con-base-de-datos)
- [Subir información a base de datos](#subir-información-a-base-de-datos)
- [Descargar información de la base de datos](#descargar-información-de-la-base-de-datos)
- [Complicación y Construcción de Proyecto](#complicación-y-construcción-de-proyecto)

## Descripción

Proyecto realizado en JAVA 8 con Maven en Minecraft, cuyo propósito principal es el envío de información de jugadores de forma masiva dentro de un servidor X, y plasmarla en una base de datos SQL o no SQL (Actualmente compatible: MongoDB), de misma manera, se mantiene como objetivo permitir que esta información la cual está dentro de la instancia de base de datos, pueda reenviarsela a un servidor de Minecraft diferente al que se envió la información, para mantener la comunicación entre servidores.

## Dependencias

La única dependencia que tiene este proyecto es la siguiente:

[PlaceholderAPI](https://www.spigotmc.org/resources/6245/)

Este proyecto es el que nos facilitará la obtención de estadísticas de jugador o información del mismo en manera de texto, y el proyecto se encargará de obtenerla para luego subirla a la base de datos deseada.

## Instalación

1) Descargar este proyecto
2) Descargar las dependencias del proyecto
3) En tu servidor de Minecraft, ir a la carpeta /plugins/ y aquí soltar los dos archivos descargados
4) Reiniciar tu servidor
5) Una vez reiniciado su servidor y las debidas carpetas de configuración de ambos proyectos se hayan generado, seguir los pasos de establecimiento de conexión con base de datos de este repositorio, y luego establecer la comunicación de subida y descarga de esta información.


## Establecer conexión con base de datos

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
    useSSL: true  ## Depende en caso de tener certificado o no al momento de establecer una conexión, puede causar errores en caso de estar mal configuradoo.
```

Si queremos establecer conexión con una base de datos MongoDB, tendremos que seguir la siguiente sintaxis:
```
placeholders:
  '1':
    TYPE: 'MongoDB' 
    URL: 'mongodb://<user1>:<password>@<port>/<database>?ssl=<true/false>' ## De preferencia colocar la URL que otorga un cluster de Monogo en la ocnexión con Drivers mediante JAVA.
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

## Subir información a base de datos


## Descargar información de la base de datos


## Complicación y Construcción de Proyecto

1) Clonar este repositorio.
2) En caso de hacer ediciones, hágalo.
3) Para construir el archivo JAR de este proyecto, se debe ejecutar el siguiente comando en consola:
```
mvn package
```
4) Se creará una carpeta /target/ y allí estará el archivo JAR del proyecto.


