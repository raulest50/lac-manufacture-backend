# Exotic Manufacture BackEnd

Backend para aplicacion que presta funcionalidades de MRP + otras funciones.
permite llevar control de inventarios, compras y produccion en una planta
que manufactura diferentes referencias de productos capilares y cosmeticos 
en general.


## Copiar archivos a remote

[documentacion de render.com](https://render.com/docs/ssh)

primero se debe generar una public key de manera local, por ejemplo como:
```
ssh-keygen -t rsa -b 1024 -C "correo" -f "file name"
```
se crean 2 archivos id_rsa, se ahi se debe copiar la key y se agrega a las public keys
en la cuenta de render.com. la public key no se agrega en los settings del proyecto
sino en los settings de la cuenta.

para connectarse a remote:
```
ssh -i "filename" "host"
```

copiar datos a remote
```
scp -i "filename" -r "source folder" "destination host":"destination folder"
```


## Scan Entidades 

el script scanEntities.jsh es un script para jshell que
realiza un resumen en txt en entity-summary.txt de las
entidades del backend en el package model.
```
jshell --startup PRINTING scanEntities.jsh
```