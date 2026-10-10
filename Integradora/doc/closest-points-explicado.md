# Closest Points explicado paso a paso

Este documento junta en un solo lugar todo lo del Problema 3: cómo funciona el algoritmo, por qué es correcto (la demostración), cuánto se demora (la complejidad) y cómo lo probamos.

**Índice**

1. [¿Qué problema estamos resolviendo?](#1-qué-problema-estamos-resolviendo)
2. [La idea en pocas palabras](#2-la-idea-en-pocas-palabras)
3. [Mapa del código: quién hace qué](#3-mapa-del-código-quién-hace-qué)
4. [Las herramientas de base](#4-las-herramientas-de-base)
5. [Ordenar con Merge Sort](#5-ordenar-con-merge-sort)
6. [El algoritmo principal paso a paso](#6-el-algoritmo-principal-paso-a-paso)
7. [Ejemplo 1: tres puntos](#7-ejemplo-1-tres-puntos)
8. [Ejemplo 2: seis puntos y el árbol de llamadas](#8-ejemplo-2-seis-puntos-y-el-árbol-de-llamadas)
9. [¿Por qué funciona? (la demostración en palabras simples)](#9-por-qué-funciona-la-demostración-en-palabras-simples)
10. [¿Cuánto se demora? (la complejidad)](#10-cuánto-se-demora-la-complejidad)
11. [¿Cómo lo probamos? (diseño de pruebas)](#11-cómo-lo-probamos-diseño-de-pruebas)

---

## 1. ¿Qué problema estamos resolviendo?

Nos dan una lista de puntos en un plano, cada uno como una lista de dos enteros **[x, y]**, y tenemos que encontrar la distancia más pequeña entre cualquier par de puntos distintos.

Ejemplos del enunciado:

| Entrada | Salida | Por qué |
|---|---|---|
| `(0,0), (3,4)` | `5.0` | Solo hay un par, y su distancia es 5 |
| `(0,0), (3,4), (1,1)` | `1.4142` | El par más cerca es `(0,0)` y `(1,1)`, y su distancia es √2 |

La distancia entre dos puntos es la de siempre (Pitágoras):

```
distancia = raíz cuadrada de ( (x1 - x2)² + (y1 - y2)² )
```

El resultado se entrega con **máximo 4 decimales**.

### Lo que implementamos

**Divide y vencerás**: partimos el problema en dos mitades, resolvemos cada mitad por separado y después arreglamos la parte que quedó pendiente.

---

## 2. La idea en pocas palabras

Imagina que los puntos son casas en un pueblo.

1. **Ordenamos las casas de izquierda a derecha** (por la coordenada x).
2. **Trazamos una calle imaginaria justo en el medio** y dividimos el pueblo en dos barrios: el de la izquierda y el de la derecha.
3. **Le preguntamos a cada barrio** (con la misma receta, otra vez): "¿cuál es el par de casas más cercanas que tienes?". Los dos barrios responden con una distancia, llamémoslas **d1** y **d2**.
4. Nos quedamos con la menor de las dos. A esa la llamamos **delta** (**δ**). Ya sabemos que el par más cercano del pueblo **no puede ser peor que delta**.
5. Pero ojo: **falta un caso**. Puede haber dos casas muy cerquita **una a cada lado de la calle**. Ningún barrio pudo verlas juntas, porque cada una vive en un barrio distinto.
6. Para revisar eso no hace falta mirar todo el pueblo. Solo importan las casas que están **a menos de delta de la calle del medio**. Esa es **la franja**.
7. Ordenamos las casas de la franja **de abajo hacia arriba** (por la coordenada y) y las recorremos comparando cada una con unas pocas siguientes.
8. La respuesta final es la menor entre **delta** y lo que encontramos en la franja.

¿Por qué sirve la franja? Si dos casas, una de cada lado, están a distancia **menor que delta**, entonces cada una está a menos de delta de la calle. Si estuvieran más lejos, ya estarían a distancia mayor o igual que delta y no mejorarían nada. Por eso las casas fuera de la franja se pueden ignorar con tranquilidad.

---

## 3. Mapa del código: quién hace qué

Todo está en `ClosestPoints.scala`. Un punto es una lista `[x, y]` (`type Point = List[Int]`).

| Función | Qué hace, dicho fácil |
|---|---|
| `getX`, `getY` | Sacan la coordenada x o y de un punto |
| `distance` | Calcula la distancia entre dos puntos |
| `lengthTR` | Cuenta cuántos elementos tiene una lista |
| `reverseTR` | Le da la vuelta a una lista |
| `splitPoints` | Parte una lista en dos mitades |
| `mergeByX` / `mergeByY` | Mezclan dos listas ya ordenadas en una sola ordenada |
| `sortByX` / `sortByY` | Ordenan una lista por x o por y (Merge Sort) |
| `closestRecursive` | El corazón: calcula la distancia mínima dividiendo en mitades |
| `buildStrip` | Arma la franja (los puntos cerca de la calle del medio) y la ordena por y |
| `stripMinDistance` | Recorre la franja buscando la distancia mínima |
| `closestPoints` | La función que usa el usuario: ordena, llama al corazón y redondea |

Cómo se llaman entre sí:

```
closestPoints
 ├── sortByX                 (ordena todo por x)
 │    ├── splitPoints        (parte en dos)
 │    └── mergeByX           (mezcla)
 └── closestRecursive        (el corazón, se llama a sí mismo)
      ├── splitPoints
      ├── closestRecursive   (mitad izquierda)
      ├── closestRecursive   (mitad derecha)
      ├── buildStrip
      │    └── sortByY       (ordena la franja por y)
      └── stripMinDistance   (recorre la franja)
```

---

## 4. Las herramientas de base

Antes del algoritmo grande hay unas funciones pequeñas que se usan por todos lados. Todas siguen el estilo funcional: no hay `var`, ni `for`, ni `while`. Solo recursión, listas que no se modifican y pattern matching.

### 4.1 El truco del acumulador y `@tailrec`

Varias funciones tienen un parámetro llamado `accumulator`. Es como una **libreta** donde se va anotando el resultado parcial mientras se avanza por la lista. Al final se devuelve lo anotado.

Esto permite la **recursión de cola**: la llamada recursiva es lo último que hace la función, así que Scala la convierte internamente en un ciclo y no se acumulan llamadas pendientes. Por eso llevan la etiqueta `@tailrec`.

### 4.2 `lengthTR`: contar

```scala
lengthTR(points, accumulator = 0)
```

Cada vez que quita un elemento, suma 1 a la libreta.

```
lengthTR([A, B, C], 0)
→ lengthTR([B, C], 1)
→ lengthTR([C], 2)
→ lengthTR([], 3)
→ devuelve 3
```

### 4.3 `reverseTR`: dar la vuelta

Va pasando elementos de una lista a la libreta. Como cada elemento nuevo se pone al frente, el orden queda invertido.

```
reverseTR([A, B, C], [])
→ reverseTR([B, C], [A])
→ reverseTR([C], [B, A])
→ reverseTR([], [C, B, A])
→ devuelve [C, B, A]
```

Algo muy útil de esta función: si le das una libreta que **ya tiene cosas**, el resultado es *la lista invertida pegada delante de lo que había*. En fórmula:

```
reverseTR(xs, acc) = (xs al revés) ++ acc
```

### 4.4 `splitPoints`: partir en dos

Calcula mitad = largo / 2 (división entera) y pasa los primeros mitad elementos a la izquierda; el resto va a la derecha.

```
splitPoints([A, B, C, D, E])     largo 5, mitad 2
→ izquierda: [A, B]
→ derecha:   [C, D, E]
```

si hay un número impar, la derecha se queda con uno más. Y cuando la lista tiene al menos 2 elementos, las dos mitades nunca quedan vacías y siempre son más cortas que la original. Eso es lo que asegura que la recursión termine.

---

## 5. Ordenar con Merge Sort

Necesitamos ordenar por x (al inicio) y por y (la franja).

1. Si la lista tiene 0 o 1 elementos, ya está ordenada.
2. Si no, se parte en dos con splitPoints.
3. Se ordena cada mitad.
4. Se mezclan las dos mitades ya ordenadas con merge.

### 5.1 El merge, paso a paso

Mezclar dos listas ordenadas es como juntar dos filas de gente ordenadas por estatura: miras a la primera persona de cada fila y dejas pasar a la más baja. Repites hasta que una fila se acabe, y entonces pasa toda la otra.En el código se usa una libreta (accumulator) donde van quedando los que ya pasaron.
Ejemplo mezclando por x estas dos listas ya ordenadas:

- Izquierda: `(1,8), (5,2)`
- Derecha: `(2,7), (3,4)`

```
Paso 1: comparo x=1 con x=2  → pasa (1,8)   libreta: [(1,8)]
Paso 2: comparo x=5 con x=2  → pasa (2,7)   libreta: [(2,7), (1,8)]
Paso 3: comparo x=5 con x=3  → pasa (3,4)   libreta: [(3,4), (2,7), (1,8)]
Paso 4: la derecha se acabó. Queda (5,2) en la izquierda.
```

Cuando una lista se acaba, se hace:

```scala
reverseTR(accumulator, remaining)
```

Que según lo que vimos arriba es "la libreta al revés, pegada delante de lo que sobró":

```
[(1,8), (2,7), (3,4)] ++ [(5,2)]  =  [(1,8), (2,7), (3,4), (5,2)]
```

### 5.2 Las llamadas recursivas de `sortByX`

Ordenamos `(5,2), (1,8), (3,4), (2,7)` por x:

```
sortByX([(5,2), (1,8), (3,4), (2,7)])
│
├── parte en: [(5,2), (1,8)]  y  [(3,4), (2,7)]
│
├── sortByX([(5,2), (1,8)])
│    ├── parte en: [(5,2)] y [(1,8)]
│    ├── sortByX([(5,2)]) → [(5,2)]       (un solo elemento, ya está)
│    ├── sortByX([(1,8)]) → [(1,8)]       (un solo elemento, ya está)
│    └── mezcla → [(1,8), (5,2)]
│
├── sortByX([(3,4), (2,7)])
│    ├── parte en: [(3,4)] y [(2,7)]
│    ├── sortByX([(3,4)]) → [(3,4)]
│    ├── sortByX([(2,7)]) → [(2,7)]
│    └── mezcla → [(2,7), (3,4)]
│
└── mezcla de [(1,8), (5,2)] con [(2,7), (3,4)]
     → [(1,8), (2,7), (3,4), (5,2)]     ✔
```

`sortByY` funciona exactamente igual, solo que compara la coordenada y.

---

## 6. El algoritmo principal paso a paso

### 6.1 closestPoints: la puerta de entrada

```scala
def closestPoints(points) =
  ordena los puntos por x
  calcula la distancia mínima con closestRecursive
  si el resultado es infinito → lo devuelve tal cual
  si no → lo redondea a 4 decimales
```

El redondeo es `round(resultado * 10000) / 10000`. El caso "infinito" aparece cuando hay menos de dos puntos (no existe ningún par).

### 6.2 closestRecursive: el corazón

Recibe los puntos ya ordenados por x y responde distinto según cuántos puntos haya:

| Cuántos puntos | Qué devuelve | Por qué |
|---|---|---|
| 0 o 1 | Infinito | No hay ningún par |
| 2 | La distancia entre ellos | Solo existe un par |
| 3 o más | Sigue el procedimiento de abajo | Hay que dividir |

El procedimiento para 3 o más puntos:

**Paso 1. Dividir.** splitPoints parte la lista en izquierda y derecha.

**Paso 2. Resolver cada mitad.** Se llama a closestRecursive con la izquierda y con la derecha. Estas son las **dos llamadas recursivas**. Cada una trabaja con una lista más corta, hasta llegar a los casos de 0, 1 o 2 puntos.

**Paso 3. Sacar delta.** delta = el menor entre leftDistance y rightDistance.

**Paso 4. Armar la franja.** buildStrip mira el primer punto de la mitad derecha, toma su x como "la calle del medio" y se queda con todos los puntos de la lista cuya x esté a una distancia de la calle menor o igual a delta. Luego ordena esa franja por y.

**Paso 5. Recorrer la franja.** stripMinDistance busca la distancia mínima ahí dentro.

**Paso 6. Responder.** Se devuelve el menor entre delta y la distancia de la franja.

### 6.3 `buildStrip`: la franja

Recorre todos los puntos con una función interna de cola. Para cada punto calcula |x - calleDelMedio|:

- Si es **menor o igual a delta**, el punto entra a la franja.
- Si no, se descarta.

Al terminar, la lista de la franja está al revés (por la libreta), así que se invierte con reverseTR, y después se ordena por y con sortByY.

### 6.4 `stripMinDistance`: recorrer la franja sin comparar todo con todo

Esto es lo que evita volver a un algoritmo lento. Hay dos funciones internas:

**scan** va punto por punto: toma el primero de la lista y lo compara con los que vienen después, y luego sigue con el segundo, y así.

**compareWithFollowing** compara un punto con los siguientes, pero se detiene en cuanto la diferencia en y supera la mejor distancia encontrada hasta ahora.

¿Por qué se puede parar? Porque la lista está ordenada por y. Si el siguiente punto ya está a más de mejor de altura, su distancia real es **como mínimo** esa altura (la distancia nunca es menor que la diferencia en y). Y todos los que vienen después están todavía más arriba. Ninguno puede mejorar el resultado.

Hay otra parada más: si la distancia mínima llega a `0.0` (dos puntos iguales), se corta de una vez, porque menos de cero no se puede.

La libreta de **scan** empieza en infinito, para que la primera comparación real siempre la reemplace.

Ejemplo, con la franja ordenada por y: `(9,1), (4,3), (5,4), (2,7)` y mejor = infinito:

```
Punto (9,1):
  vs (4,3): diferencia en y = 2  → distancia 5.385  → mejor = 5.385
  vs (5,4): diferencia en y = 3  → distancia 5.0    → mejor = 5.0
  vs (2,7): diferencia en y = 6 > 5.0 → ¡paro!
Punto (4,3):
  vs (5,4): diferencia en y = 1  → distancia 1.4142 → mejor = 1.4142
  vs (2,7): diferencia en y = 4 > 1.4142 → ¡paro!
Punto (5,4):
  vs (2,7): diferencia en y = 3 > 1.4142 → ¡paro!
Punto (2,7): no tiene siguientes.
→ resultado de la franja: 1.4142
```

---

## 7. Ejemplo 1: tres puntos

Entrada: `(0,0), (3,4), (1,1)`

**1) Ordenar por x** con sortByX:

```
sortByX([(0,0), (3,4), (1,1)])
 ├── parte en [(0,0)] y [(3,4), (1,1)]
 ├── sortByX([(0,0)]) → [(0,0)]
 ├── sortByX([(3,4), (1,1)])
 │     ├── parte en [(3,4)] y [(1,1)]
 │     └── mezcla → [(1,1), (3,4)]
 └── mezcla → [(0,0), (1,1), (3,4)]
```

**2) `closestRecursive([(0,0), (1,1), (3,4)])`**: son 3 puntos, así que se divide.

```
closestRecursive([(0,0), (1,1), (3,4)])
 ├── izquierda: [(0,0)]            derecha: [(1,1), (3,4)]
 │
 ├── closestRecursive([(0,0)])        → infinito   (un solo punto)
 ├── closestRecursive([(1,1),(3,4)])  → 3.6056     (dos puntos: √13)
 │
 ├── delta = menor entre infinito y 3.6056 = 3.6056
 ├── calle del medio: x = 1   (primer punto de la derecha)
 ├── franja: puntos con |x - 1| ≤ 3.6056 → entran los tres
 │      ordenados por y: (0,0), (1,1), (3,4)
 ├── recorrido de la franja:
 │      (0,0) vs (1,1): distancia 1.4142 → mejor = 1.4142
 │      (0,0) vs (3,4): diferencia en y = 4 > 1.4142 → paro
 │      (1,1) vs (3,4): diferencia en y = 3 > 1.4142 → paro
 │      resultado de la franja = 1.4142
 └── respuesta = menor entre 3.6056 y 1.4142 = 1.4142
```

**3) Redondeo:** `1.4142`. Coincide con el ejemplo del enunciado.

Mira lo que pasó: las dos mitades no encontraron la mejor pareja, porque (0,0) quedó sola en la izquierda y (1,1) en la derecha. La encontró la franja. Por eso la franja es indispensable.

---

## 8. Ejemplo 2: seis puntos y el árbol de llamadas

Entrada (ya ordenada por x para ir directo a lo importante):

```
(0,0), (2,7), (4,3), (5,4), (9,1), (10,9)
```

La respuesta correcta es 1.4142, del par (4,3) y (5,4). Veamos cómo la encuentra el algoritmo.

### El árbol de llamadas

```
closestRecursive([(0,0), (2,7), (4,3), (5,4), (9,1), (10,9)])      ← llamada A
│
├── izquierda: [(0,0), (2,7), (4,3)]       derecha: [(5,4), (9,1), (10,9)]
│
├── llamada B: closestRecursive([(0,0), (2,7), (4,3)])
│     ├── izquierda: [(0,0)]    derecha: [(2,7), (4,3)]
│     ├── closestRecursive([(0,0)])        → infinito
│     ├── closestRecursive([(2,7),(4,3)])  → 4.4721
│     ├── delta = 4.4721, calle del medio x = 2
│     ├── franja por y: (0,0), (4,3), (2,7)
│     │     (0,0) vs (4,3): distancia 5.0   → mejor = 5.0
│     │     (0,0) vs (2,7): dif. en y = 7 > 5 → paro
│     │     (4,3) vs (2,7): dif. en y = 4 → distancia 4.4721 → mejor = 4.4721
│     │     resultado de la franja = 4.4721
│     └── respuesta de B = 4.4721
│
├── llamada C: closestRecursive([(5,4), (9,1), (10,9)])
│     ├── izquierda: [(5,4)]    derecha: [(9,1), (10,9)]
│     ├── closestRecursive([(5,4)])        → infinito
│     ├── closestRecursive([(9,1),(10,9)]) → 8.0623
│     ├── delta = 8.0623, calle del medio x = 9
│     ├── franja por y: (9,1), (5,4), (10,9)
│     │     (9,1) vs (5,4):  distancia 5.0   → mejor = 5.0
│     │     (9,1) vs (10,9): dif. en y = 8 > 5 → paro
│     │     (5,4) vs (10,9): dif. en y = 5, no es mayor que 5 → se calcula
│     │                       distancia 7.07 → no mejora, mejor sigue en 5.0
│     │     resultado de la franja = 5.0
│     └── respuesta de C = 5.0
│
├── delta de A = menor entre 4.4721 (B) y 5.0 (C) = 4.4721
│
├── calle del medio de A: x = 5   (primer punto de la derecha)
├── franja de A: puntos con |x - 5| ≤ 4.4721
│      (0,0)  → |0-5|  = 5   → NO entra (5 > 4.4721)
│      (2,7)  → |2-5|  = 3   → entra
│      (4,3)  → |4-5|  = 1   → entra
│      (5,4)  → |5-5|  = 0   → entra
│      (9,1)  → |9-5|  = 4   → entra
│      (10,9) → |10-5| = 5   → NO entra
│      ordenados por y: (9,1), (4,3), (5,4), (2,7)
│
├── recorrido de la franja:   (es el ejemplo de la sección 6.4)
│      (9,1) vs (4,3): 5.385   → mejor = 5.385
│      (9,1) vs (5,4): 5.0     → mejor = 5.0
│      (9,1) vs (2,7): dif. en y = 6 > 5 → paro
│      (4,3) vs (5,4): 1.4142  → mejor = 1.4142      ← ¡aquí está!
│      (4,3) vs (2,7): dif. en y = 4 > 1.4142 → paro
│      (5,4) vs (2,7): dif. en y = 3 > 1.4142 → paro
│      resultado de la franja = 1.4142
│
└── respuesta de A = menor entre 4.4721 y 1.4142 = 1.4142   
```

### Qué enseña este ejemplo

- `(4,3)` quedó en la mitad izquierda y `(5,4)` en la derecha. Ninguna mitad pudo verlos juntos. Solo la franja de A los encontró.
- Las franjas de B y de C no mejoraron nada (daban lo mismo o más que el delta), y está bien: la franja solo ayuda cuando hay un par cruzado más cercano.
- `(0,0)` y `(10,9)` quedaron **fuera** de la franja de A, porque estaban demasiado lejos de la calle del medio. Eso es ahorro de trabajo.
- Las llamadas B y C **no dependen una de otra**, solo se llaman y esperan su respuesta. Cuando las dos terminan, A sigue.

### Cómo se ve por niveles

```
Nivel 0:   [ 6 puntos ]
              /      \
Nivel 1: [ 3 puntos ] [ 3 puntos ]
           /    \        /    \
Nivel 2: [1]  [2 pts]  [1]  [2 pts]      ← aquí las llamadas se paran (casos base)
```

Cada vez la lista se parte por la mitad. Con n puntos la profundidad es de unos **log₂ n** niveles.

---

## 9. ¿Por qué funciona?

La técnica es **inducción**, que funciona como una fila de fichas de dominó:

1. **Caso base:** la primera ficha cae (el problema más chiquito funciona).
2. **Hipótesis inductiva:** suponemos que todas las fichas anteriores ya cayeron (el algoritmo funciona para listas más cortas).
3. **Paso inductivo:** probamos que, si las anteriores cayeron, esta también cae.

Si se cumplen los tres, el algoritmo funciona para cualquier tamaño.En este proyecto la inducción es "completa sobre el tamaño de la lista", porque al dividir en mitades no estamos usando "la lista menos un elemento", sino listas más cortas en general. La idea sigue siendo la misma de las fichas.

Antes de probar el algoritmo grande hay que asegurarse de que las piezas pequeñas están bien. Vamos de abajo hacia arriba.

### 9.1 Pieza 1: reverseTR hace lo que dice

**Lo que afirmamos:** **reverseTR(xs, acc)** devuelve **(xs al revés) ++ acc**.

- **Caso base:** si **xs** está vacía, devuelve **acc**. Y "vacía al revés pegada delante de acc" es justamente `acc`. 
- **Hipótesis:** suponemos que funciona para la lista sin su primer elemento.
- **Paso:** con **head :: tail**, la función pasa **head** al frente de la libreta y sigue con **tail**. Por la hipótesis eso da **(tail al revés) ++ (head :: acc), y eso es lo mismo que (head :: tail) al revés ++ acc**. 

### 9.2 Pieza 2: splitPoints parte bien

**Lo que afirmamos:** devuelve los primeros **largo/2** elementos a la izquierda y el resto a la derecha, en el mismo orden.

- **Caso base:** si ya no quedan elementos por pasar, o la lista se acabó, el resultado es el esperado. 
- **Paso:** cada vuelta pasa un elemento a la libreta y baja el contador. Con la propiedad de **reverseTR**, al terminar la izquierda queda en su orden original. 

Consecuencias que usamos después: si la lista tiene 2 o más elementos, las dos mitades no son vacías y son más cortas. Si tiene 3 o más, **la derecha tiene al menos 2**. Y si la lista estaba ordenada por x, las dos mitades también.

### 9.3 Pieza 3: el merge mezcla bien

**a) El resultado es una lista ordenada con los mismos elementos.** Si las dos listas de entrada están ordenadas, al tomar siempre el menor de los dos primeros elementos, ese elemento es menor o igual que todos los demás (porque cada lista está ordenada). Se pone al frente y se sigue con lo que queda. Por la hipótesis, lo que queda sale ordenado. 

**b) La libreta no arruina el orden.** Aquí está la parte delicada. Se demuestra que en cualquier momento se cumple:

```
resultado final = (libreta al revés) ++ (lo que falta por mezclar)
```

- Caso base: si no queda nada por mezclar, devuelve la libreta al revés. 
- Caso en que una lista se acabó: devuelve **reverseTR(libreta, lo que sobra)**, que por la Pieza 1 es "libreta al revés ++ lo que sobra". 
- Paso: tomar el menor y ponerlo en la libreta mantiene esa igualdad, porque pasar un elemento al frente de la libreta equivale a ponerlo al final de "libreta al revés". 

### 9.4 Pieza 4: `sortByX` y `sortByY` ordenan bien

- **Caso base:** una lista de 0 o 1 elementos ya está ordenada. 
- **Hipótesis:** suponemos que ordena bien todas las listas más cortas que la actual.
- **Paso:** se parte en dos mitades más cortas (Pieza 2). Por la hipótesis, cada una sale ordenada. Se mezclan (Pieza 3). Queda ordenada y con los mismos elementos. 

Esto implica que **closestPoints** puede ordenar la entrada por x sin perder ni inventar puntos, así que la distancia mínima no cambia.

### 9.5 Pieza 5: **buildStrip** arma la franja correcta

Es un filtro: recorre la lista y se queda con los puntos que cumplen **|x - calle| <= delta**. La libreta se invierte al final (Pieza 1) para conservar el orden, y luego se ordena por y (Pieza 4). 

### 9.6 Pieza 6: **stripMinDistance** encuentra el mínimo real de la franja

Se prueba que **las paradas no se saltan ningún par bueno**:

- **Parada por altura:** si el siguiente punto está más arriba que mejor, su distancia es mayor que mejor, y los siguientes igual de lejos o más. Ninguno mejora el resultado. 
- **Parada en cero:** las distancias nunca son negativas, así que 0 ya no se puede mejorar. 
- **scan + compareWithFollowing:** cada par de puntos se compara (o se descarta sin perder nada). Empezar en infinito no molesta, porque cualquier distancia real es menor. 

### 9.7 La pieza grande: `closestRecursive` da la distancia mínima

**Lo que afirmamos:** si los puntos vienen ordenados por x, **closestRecursive** devuelve la distancia mínima entre dos puntos distintos de la lista.

**Casos base**

- Con **0 o 1 punto**: no hay pares, devuelve infinito. Es lo correcto: "no existe distancia". 
- Con **2 puntos**: hay un solo par, y devuelve su distancia. 

**Hipótesis inductiva**

Suponemos que **closestRecursive** ya funciona bien para **cualquier lista más corta** que la actual.

**Paso inductivo (3 o más puntos)**

Se divide en izquierda **L** y derecha **R**. Las dos son más cortas y la derecha tiene al menos 2 puntos. Por la hipótesis:

- **leftDistance** es la verdadera distancia mínima de **L**.
- **rightDistance** es la verdadera distancia mínima de **R**.
- **delta** es la menor de las dos, y es un número real porque **R** tiene al menos un par.

Ahora, cualquier par de puntos de la lista cae en **uno de tres grupos**:

1. Los dos están en **L**.
2. Los dos están en **R**.
3. Uno está en **L** y el otro en **R** 

Los grupos 1 y 2 ya están cubiertos por **delta**. Solo falta el grupo 3, y eso es lo que revisa la franja. Hay que probar dos cosas:

**(a) El resultado nunca es más pequeño que la verdad.** Todos los valores que se manejan **delta** y las distancias de la franja salen de pares reales de puntos.  
**(b) El resultado nunca es más grande que la verdad.** Hay dos situaciones:

- Si el mejor par de toda la lista **no es cruzado**, entonces la respuesta verdadera es **delta**, y nuestro resultado es **min(delta, algo)**, que es **≤ delta**. 
- Si el mejor par **sí es cruzado**, y es mejor que **delta**, llamemos al par **a** (en **L**) y **b** (en **R**), con distancia **d < delta**. Sea **m** la x del primer punto de **R**. Como todo está ordenado por x:

  ```
  x(a)  ≤  m  ≤  x(b)
  ```

  La distancia horizontal entre **a** y **b** no puede ser mayor que su distancia real:

  ```
  x(b) - x(a)  ≤  d  <  delta
  ```

Como **m** está en medio de **x(a)** y **x(b)**, cada uno está **a menos de delta** de la calle. Es decir, **los dos entran a la franja**. Entonces **stripMinDistance** ve ese par y devuelve algo **≤ d**. 

Juntando (a) y (b), **closestRecursive** devuelve exactamente la distancia mínima. 

**La recursión termina** porque cada llamada recibe una lista estrictamente más corta (Pieza 2), y las funciones auxiliares recorren listas finitas.

### 9.8 Cierre: **closestPoints**

Se ordena por x (Pieza 4: no se pierde ni se agrega ningún punto), se aplica **closestRecursive** (Pieza 7: da el mínimo exacto) y se redondea a 4 decimales. El redondeo cambia el número en menos de **0.00005**. 

### 9.9 Resumen de la demostración

| Pieza | Qué se prueba | Idea clave |
|---|---|---|
| `reverseTR` | Devuelve "al revés ++ acumulador" | Cada vuelta pasa un elemento al frente de la libreta |
| `splitPoints` | Parte bien en dos | Los primeros `n/2` van a la izquierda |
| `merge` | Mezcla ordenado y sin perder nada | Siempre se toma el menor de los dos primeros |
| `sortByX/Y` | Ordena cualquier lista | Ordenar mitades + mezclar |
| `buildStrip` | Franja correcta | Es un filtro, luego se ordena |
| `stripMinDistance` | Mínimo exacto de la franja | Las paradas no pierden pares útiles |
| `closestRecursive` | Mínimo exacto de toda la lista | Si el mejor par cruza la línea, los dos puntos caen en la franja |

---

## 10. ¿Cuánto se demora? (la complejidad)

Sea **n** la cantidad de puntos. La complejidad responde a la pregunta: "si duplico los puntos, ¿cuánto más tarda?".

### 10.1 Lo que cuesta cada función

| Función | Tiempo | Por qué |
|---|---|---|
| `getX`, `getY`, `distance` | Constante | Un par de cuentas |
| `lengthTR` | Proporcional a n | Una vuelta por elemento |
| `reverseTR` | Proporcional a n | Una vuelta por elemento |
| `splitPoints` | Proporcional a n | Contar + recorrer hasta la mitad |
| `mergeByX/Y` | Proporcional a n | Cada paso saca un elemento |
| `buildStrip` (el filtro) | Proporcional a n | Una pasada por la lista |
| `stripMinDistance` | Proporcional a k | k es el tamaño de la franja (ver 10.3) |

### 10.2 Merge Sort (`sortByX` y `sortByY`)

En cada llamada se hace:

- Partir en dos: n pasos.
- Ordenar cada mitad: **T(n/2)** por mitad.
- Mezclar: n pasos.

Eso se escribe como una **ecuación de recurrencia**:

```
T(n) = 2·T(n/2) + n          con T(1) = 1
```

Leída en voz alta: "ordenar n elementos cuesta dos veces ordenar la mitad, más n pasos extra de partir y mezclar".

**Cómo se resuelve, por niveles.** Cada nivel del árbol de llamadas cuesta en total unos n pasos (las listas se parten pero sumadas siempre dan n), y hay unos **log₂ n** niveles:

```
Nivel 0:  1 lista de n         → n pasos
Nivel 1:  2 listas de n/2      → n pasos
Nivel 2:  4 listas de n/4      → n pasos
...
Último:   n listas de 1        → n pasos
```

Total: **n × (log₂ n)**. Por el Teorema Maestro (a = 2, b = 2, f(n) = n) también da:

```
T(n) = Θ(n log n)
```

### 10.3 Recorrer la franja es rápido

Esta parte tiene un truco, porque a primera vista parece que cada punto se compara con todos los demás de la franja (lo que sería lento). No es así, y el motivo es el empaquetamiento:

- Los puntos del lado izquierdo de la franja están a distancia **al menos delta** unos de otros (porque delta es la menor distancia dentro de cada mitad).
- Lo mismo vale para los del lado derecho.
- En un cuadradito de lado delta caben como máximo 4 puntos que estén separados por al menos delta (los cuatro rincones).

Entonces, cuando comparamos un punto con los siguientes y nos paramos al pasarnos de la altura permitida, nos pasamos de **muy pocos** puntos. La zona de comparación es una caja de ancho **2·delta** y alto aproximado **2·delta**, que son 4 cuadraditos de lado delta, y cada cuadradito tiene 4 puntos como máximo. Total: a lo sumo unos 16 puntos.

Dicho fácil: **cada punto se compara con una cantidad pequeña y constante de otros**, no con todos. Por eso recorrer la franja cuesta algo proporcional a k.

Un detalle técnico: scan empieza con mejor = infinito, no con **delta**. Se verifica que aun así la cantidad de comparaciones por punto sigue acotada: después de comparar un punto con el siguiente, mejor queda como máximo en **2·delta + (la diferencia de altura entre ambos)**, y eso deja la ventana dentro de una caja de alto **2·delta**. Además, si **delta** es 0 (puntos repetidos), el recorrido termina en cuanto se encuentra la pareja repetida.

### 10.4 El algoritmo completo (`closestRecursive`)

En cada llamada con n puntos se hace:

| Paso | Cuánto cuesta |
|---|---|
| Partir en dos | n |
| Las dos llamadas recursivas | 2 × T(n/2) |
| Filtrar la franja | n |
| **Ordenar la franja por y** | k log k (k = tamaño de la franja) |
| Recorrer la franja | k |

Como k puede llegar a ser n, esto queda:

```
T(n) = 2·T(n/2) + (algo del orden de n log n)
```

**Peor caso.** Si todos los puntos tienen **la misma x** por ejemplo, todos en una línea vertical, la distancia de cada punto a la calle del medio es 0, así que **todos entran a la franja en todos los niveles**. Entonces el costo por nivel es de unos **n log n**, y se repite en cada uno de los **log n** niveles.

Hagámoslo con números: con n = 1.024 puntos hay 10 niveles. Ordenar por y en el nivel 0 cuesta **1024 × 10**; en el nivel 1 son dos franjas de 512 que cuestan **2 × 512 × 9**; en el nivel 2, **4 × 256 × 8**; y así hasta el último:

```
Total = 1024 × (10 + 9 + 8 + ... + 1) = 1024 × 55 = 56.320 pasos
```

Esa suma **10 + 9 + ... + 1** es **log n × (log n + 1) / 2**, y en general:

```
T(n) = n × (log n)(log n + 1) / 2  =  Θ(n log² n)
```

Compárese con 1024 × 10 = 10.240 si fuera solo **n log n**. Por eso el peor caso es **n log² n**.

**Mejor caso.** Si la franja es chiquita en todos los niveles, el costo por nivel es proporcional a n, y:

```
T(n) = 2·T(n/2) + n   →   Θ(n log n)
```

### 10.5 Total de `closestPoints`

Se ordena por x (n log n) y luego se llama a **closestRecursive**:

| Caso | Complejidad |
|---|---|
| Peor caso | **Θ(n log² n)** |
| Mejor caso | Θ(n log n) |
| En cualquier caso | Entre `n log n` y `n log² n` |

Y comparado con la fuerza bruta (n²), seguimos ganando por mucho. Con un millón de puntos: **n²** son 10¹² pasos y **n log² n** son unos 4×10⁸.

### 10.6 ¿O(n log n)?

El algoritmo logra **n log n** exacto porque no vuelve a ordenar la franja por y en cada llamada. En vez de eso, cada **closestRecursive** devuelve, además de la distancia, la lista de sus puntos ya ordenada por y. Entonces para obtener la lista de una llamada basta mezclar **mergeByY** las dos listas que devolvieron las mitades. Mezclar cuesta n, no n log n.

El costo por llamada pasa a ser:

| Paso | Cuánto cuesta |
|---|---|
| Partir en dos | n |
| Las dos llamadas recursivas | 2 × T(n/2) |
| Mezclar las dos listas ordenadas por y | n |
| Filtrar la franja (ya viene ordenada) | n |
| Recorrer la franja | n |

```
T(n) = 2·T(n/2) + n    →    Θ(n log n)
```

Sumando el ordenamiento inicial por x, el total es `Θ(n log n)`.

| Versión | Recurrencia | Complejidad |
|---|---|---|
| La que está implementada | `T(n) = 2T(n/2) + n log n` | Θ(n log² n) |
| Variante con merge por y | `T(n) = 2T(n/2) + n` | Θ(n log n) |

### 10.7 Memoria

- **Pila de llamadas:** las funciones recursivas "normales" **sortByX**, **sortByY**, **closestRecursive** llegan hasta unos **log n** niveles de profundidad. Las funciones con **@tailrec** se ejecutan como ciclos y no gastan pila.
- **Listas nuevas:** como las listas no se modifican, cada operación crea listas nuevas. En una llamada se crean las dos mitades, la franja y las listas de mezcla, todo del orden de n. Como las dos llamadas recursivas se hacen una después de la otra, la memoria de la primera ya se puede liberar cuando empieza la segunda. La suma es **n + n/2 + n/4 + ... ≈ 2n**.

| Recurso | Cuánto |
|---|---|
| Tiempo (peor caso) | Θ(n log² n) |
| Tiempo (mejor caso) | Θ(n log n) |
| Memoria extra | Θ(n) |
| Profundidad de pila | O(log n) |

---

## 11. ¿Cómo lo probamos? (diseño de pruebas)
Las pruebas están en **ClosestPointsSuite.scala** con MUnit. 

### 11.1 Cómo pensamos las pruebas

Quisimos cubrir cuatro tipos de situaciones:

1. **Casos típicos y raros:** pocos puntos, puntos repetidos, coordenadas negativas, todos en una línea.
2. **Casos límite:** lista vacía, un solo punto, dos puntos, números enormes (los que podrían "desbordar" un Int).
3. **El caso más importante del algoritmo:** que la mejor pareja quede una a cada lado de la línea del medio, de modo que **solo la franja** pueda encontrarla.
4. **Una "respuesta de control":** comparar con el algoritmo lento (fuerza bruta) en muchos casos aleatorios. Si los dos dan lo mismo, buena señal. Se usa una semilla fija (42) para que siempre sea reproducible.

Para comparar números con decimales se usa una tolerancia de 0.0001, que va con los 4 decimales que pide el enunciado.

### 11.2 Pruebas de las funciones pequeñas

| # | Qué prueba | Entrada | Esperado | Para qué sirve |
|---|---|---|---|---|
| 1 | Distancia entre dos puntos | `(0,0)`, `(3,4)` | `5.0` | Que la fórmula esté bien |
| 2 | Distancia entre puntos iguales | `(2,3)`, `(2,3)` | `0.0` | El caso de distancia cero |
| 3 | Partir en dos | 4 puntos | Dos mitades de 2, en orden | Que `splitPoints` no desordene |
| 4 | Ordenar por x | `(5,2),(1,8),(3,4),(2,7)` | `(1,8),(2,7),(3,4),(5,2)` | `sortByX` y `mergeByX` |
| 5 | Ordenar por y | `(5,2),(1,8),(3,4),(2,1)` | `(2,1),(5,2),(3,4),(1,8)` | `sortByY` y `mergeByY` |
| 6 | Contar e invertir | `(1,1),(2,2),(3,3)` | Largo 3 y lista al revés | `lengthTR` y `reverseTR` |

### 11.3 Pruebas del algoritmo completo

| # | Qué prueba | Entrada | Esperado | Para qué sirve |
|---|---|---|---|---|
| 7 | Dos puntos | `(0,0),(3,4)` | `5.0` | Ejemplo 1 del enunciado |
| 8 | Tres puntos | `(0,0),(3,4),(1,1)` | √2 (con tolerancia) | Ejemplo 2 del enunciado |
| 9 | Redondeo a 4 decimales | igual que la 8 | `1.4142` exacto | Que el resultado se entregue redondeado |
| 10 | Puntos repetidos | `(0,0),(3,4),(0,0),(10,10)` | `0.0` | Duplicados y parada en cero |
| 11 | Coordenadas negativas | `(-5,-5),(0,0),(-4,-4),(10,10)` | √2 (con tolerancia) | Que los negativos no den problemas |
| 12 | Números enormes | `(0,0),(100000,0)` y `(-2000000000,0),(2000000000,0)` | `100000.0` y `4.0E9` | Que no haya desbordamiento |
| 13 | El mejor par cruza el medio | `(0,0),(10,10),(11,10),(30,30)` | `1.0` | Que la franja funcione |
| 14 | Todos en una línea vertical | `(0,0),(0,5),(0,2),(0,9)` | `2.0` | El peor caso (toda la lista entra a la franja) |
| 15 | Menos de dos puntos | Lista vacía y un punto | Infinito | Casos límite sin pares |
| 16 | Contra fuerza bruta | 200 listas aleatorias, de 2 a 41 puntos, con coordenadas entre -100 y 99 | Igual al algoritmo lento (con tolerancia) | Verificar la corrección en general |

**Sobre la prueba 12:** originalmente **distance** calculaba **dx * dx + dy * dy** con enteros, y cuando la diferencia pasaba de unos 46.340 el cuadrado se salía del rango de un Int y daba resultados sin sentido. Se arregló pasando a Double antes de multiplicar, y esta prueba existe para que no vuelva a pasar.

### 11.4 Qué prueba cubre qué función

| Función | Pruebas que la usan |
|---|---|
| `distance`, `getX`, `getY` | 1, 2, 7, 12 |
| `lengthTR`, `reverseTR` | 6 (y todas las demás, de forma indirecta) |
| `splitPoints` | 3, 8, 13, 16 |
| `mergeByX`, `sortByX` | 4, 8, 13, 16 |
| `mergeByY`, `sortByY` | 5, 13, 14, 16 |
| `buildStrip`, `stripMinDistance` | 13, 14, 16 |
| `closestRecursive` | 7, 8, 10, 11, 13, 14, 16 |
| `closestPoints` | 7 al 16 |

### 11.5 Cómo se conectan las pruebas con la demostración

- Las pruebas 4 y 5 comprueban en ejemplos concretos lo que la demostración dice de **merge** y de **sortByX/Y** (secciones 9.3 y 9.4).
- La prueba 13 es el ejemplo vivo del paso inductivo: el mejor par cruza la línea y solo la franja lo encuentra (sección 9.7).
- Las pruebas 7 y 15 son los casos base de la inducción.
- La prueba 14 es el peor caso de la complejidad (sección 10.4).

---
