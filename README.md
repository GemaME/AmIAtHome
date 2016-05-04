# Am I at home?

![](/doc/pic.png =100x20)

Applicación para Android de ejemplo para mostrar como se usan y funcionas diversos servicios de Google para Android (localización, detección de actividades y comprobar si el usuario esta dentro de una zona geográfica concreta). En concreto sirve para informar a que distancia esta el usuario a una serie de destino preefinidos. Aunque sea un enfoque muy rígido lo que se prima es el entendimiento de uso de las APIs.

# Requisitos alcanzados (asignatura de Optimización del Master)
- Obtener información de la localización y actividad.
- Calcular la distancia desde la localización actual hasta un destino.
- Crear widgets en Ubidots para mostrar la información.
- Enviar vectores con información: Se usa la api de Java de Ubidots. Cuando se envía la información para una variable se adjunta un valor y un contexto que es un conjunto de datos clave-valor.

# Descraga APK

- [Descargue el APK desde aquí](docs/amiathome.apk) . Hay que dar al botón "raw" para descargarlo.

# Servicios & Herramientas

## Google APIs

- **Location:** Permite tanto pedir en un momento determinado en que coordenadas geográficas nos encontramos (latitud y longitud) como que automñaticamente recibamos notificaciones de los cambios de nuestra ubicación actual.
- **Activity Recognition:** Detecta que posibles actividades está realizando el portador del movil (andar, quito, correr, estar en un vehículo...) y su porcentaje de probabilidad

## Ubidots

### Datasets
Cuando creamos una cuenta podemos crear dataset que serían como contendores de datos asociados a una aplicación o módulo. En cada dataset se pueden definir diversas variables. En cada una guarda un valor y el contexto que son un conjunto de clave-valor.
 
### Widgets
Permite mostrar los datos recopilados en los datasets a una forma más clara y visual mediante gráficas, tablas, mapas... de esta forma es mñs fácil extraer conclusiones.

### Publicación de la información
Dispone tanto de una API REST como de una librería Java, para esta aplicación hemos usado la primera alternativa. En ambas hace falta un token para el acceso.

# Manual de Usuario

Hay 2 paneles informando de la distancia que queda para llegar al destino y que actividad se está realizando unto a una imágen.

Debajo hay 2 displayers:
- Permite indicar cada cuanto tiempo se publica la información a ubidots 
- Cada cuanto tiempo se comprueba si el usuario a entrado en un cercado.

Por último en la parte inferior hay unserie de botones que de izquierda a derecha realizan la siguiente función:
- **Mapa**: Se abre un navegador redireccionando a un widget de Ubidots. Consiste en un mapa que muestra un trazado por dodne el usuario se ha movido en el mapa.
- **Tabla**: Lo mismo pero el widget es una tabla con información de la actividad y distancia restante
- **Destino**: Se abre un dialogo para elegir otro lugar de destino de los que hay predefinidos.
- **Log**: Se abre una actividad con un listado de las distintas ctividades que han ocurrido (nueva localización e información de la actividad).

# Eficiencia

A diferencia de la aplicación [My Habits](https://github.com/GemaME/MyHabits), en esta nos hemos centrado en el cálculo de la distancia y experimentar con interfaces que no tengan que ver con la guía material design.

