### Problema 1: Número de inversiones

### Qué hace el algoritmo

Una inversión es una pareja de posiciones (i, j) con i < j donde el número de la posición i es más grande que el de la posición j. Sirve para saber qué tan desordenada está una lista: si está ordenada no tiene ninguna, y si está al revés todas las parejas son inversiones. En la lista 2, 3, 9, 2, 9 hay 2: el 3 y el 9 del medio están antes del segundo 2 y son más grandes que él.

Comparar cada número con todos los que le siguen funciona, pero se demora mucho en listas largas. Por eso uso Merge Sort con un cambio: parto la lista por la mitad, resuelvo cada mitad por separado y después las junto ya ordenadas. Mientras las junto voy contando las inversiones que hay entre una mitad y la otra.

Para entender cómo se cuenta al juntar, me imagino dos montones de cartas, cada uno ordenado de menor a mayor. Voy pasando cartas a un montón nuevo, siempre mirando la de arriba de cada montón. Si la de la izquierda es menor o igual, la paso y no pasa nada más. Si la de la derecha es menor, la paso, y como el montón izquierdo está ordenado, esa carta es menor que todas las que le quedan a la izquierda. Cada una de ellas forma una inversión, así que sumo tantas como cartas le queden al montón izquierdo.

Con 3, 5 a la izquierda y 1, 4 a la derecha pasa esto: gana el 1 y a la izquierda le quedan 2 cartas, entonces sumo 2. Paso el 3 y no sumo nada. Gana el 4 y a la izquierda le queda 1 carta, entonces sumo 1. Por último paso el 5. Son 3 inversiones, (3,1), (5,1) y (5,4), y la lista queda 1, 3, 4, 5. Los números iguales no cuentan como inversión, porque la inversión pide que el de la izquierda sea estrictamente más grande.

En el código esto está en tres funciones:

- merge(left, right): recibe dos listas ya ordenadas y devuelve la lista mezclada y ordenada, junto con la cantidad de parejas (b, c) con b en left, c en right y b > c.
- mergeSortCount(seq): parte la lista por la mitad, se llama a sí misma con cada mitad, las junta con merge y suma las inversiones.
- countInversions(seq): llama a mergeSortCount y se queda solo con el número de inversiones.

### Demostración de que merge es correcta

merge llama a una función auxiliar loop(left, right, leftSize, acc, count). La lista acc es la mezcla que voy armando, guardada al revés, count son las inversiones que llevo contadas y leftSize es cuántos elementos le quedan a la lista izquierda. Demuestro que loop es correcta y de ahí sale que merge lo es.

Notación que voy a usar:

- Una lista está ordenada si cada elemento es menor o igual que los que van después. Si a :: L2 está ordenada, L2 también lo está.
- |L| es la cantidad de elementos de L.
- Para dos listas ordenadas L y R, M(L, R) es la lista ordenada que tiene todos los elementos de L y de R.
- I(L, R) es la cantidad de parejas (b, c) con b en L, c en R y b > c.

Escribo loop(L, R, |L|, acc, count) porque merge siempre lo llama con leftSize igual al tamaño de la lista izquierda, y en cada llamada recursiva leftSize sigue siendo el tamaño de la lista izquierda que queda.

Proposición: para toda lista ordenada L, para toda lista ordenada R, y para todo acc y count,

loop(L, R, |L|, acc, count) = (acc.reverse ::: M(L, R), count + I(L, R))

Esto dice que loop devuelve lo que ya tenía armado en acc seguido de la mezcla ordenada de L y R, y que a count le suma las inversiones entre L y R.

Hago la demostración por inducción estructural sobre L. Una lista es Nil o es a :: L2, con a un elemento y L2 otra lista.

Paso 1. Identificar P(L)

P(L): para toda lista ordenada R, y para todo acc y count,
loop(L, R, |L|, acc, count) = (acc.reverse ::: M(L, R), count + I(L, R)).

Paso 2. Caso base (L = Nil)

Sean R una lista ordenada, acc y count cualesquiera.
loop(Nil, R, 0, acc, count) = (acc.reverse ::: R, count), por la definición de loop cuando la lista izquierda está vacía.
M(Nil, R) = R, porque R ya está ordenada, e I(Nil, R) = 0, porque no hay elementos en la izquierda.
Entonces (acc.reverse ::: M(Nil, R), count + I(Nil, R)) = (acc.reverse ::: R, count + 0).
Los dos lados son iguales, entonces P(Nil) se cumple.

Paso 3. Suponer el paso inductivo

Supongamos que la Proposición se cumple para alguna lista ordenada L2, es decir:

P(L2): para toda lista ordenada R, y para todo acc y count,
loop(L2, R, |L2|, acc, count) = (acc.reverse ::: M(L2, R), count + I(L2, R)).

Paso 4. Probar P(a :: L2)

Sea a cualquier elemento tal que L = a :: L2 está ordenada. Por eso L2 también está ordenada y a es menor o igual que todos los elementos de L2. Dejo L fija y tengo que probar la igualdad para toda lista ordenada R, con cualquier acc y count. Para eso hago una segunda inducción estructural, ahora sobre R.

Q(R): para todo acc y count,
loop(L, R, |L|, acc, count) = (acc.reverse ::: M(L, R), count + I(L, R)).

Caso base de Q (R = Nil)
loop(L, Nil, |L|, acc, count) = (acc.reverse ::: L, count), por la definición de loop cuando la derecha está vacía.
M(L, Nil) = L, porque L está ordenada, e I(L, Nil) = 0, porque no hay elementos en la derecha.
Entonces (acc.reverse ::: M(L, R), count + I(L, R)) = (acc.reverse ::: L, count + 0).
Los dos lados son iguales, entonces Q(Nil) se cumple.

Suponer el paso inductivo de Q

Supongamos que se cumple Q(R2) para alguna lista ordenada R2, es decir, para todo acc y count,
loop(L, R2, |L|, acc, count) = (acc.reverse ::: M(L, R2), count + I(L, R2)).

Probar Q(c :: R2)

Sea c un elemento tal que R = c :: R2 está ordenada. Por eso R2 también está ordenada y c es menor o igual que todos los elementos de R2. Como L y R tienen elementos, el código compara a con c y hay dos casos.

Caso 1: a <= c.
Por la definición de loop, loop(L, R, |L|, acc, count) = loop(L2, R, |L| - 1, a :: acc, count). Como |L2| = |L| - 1, el tamaño que le paso es |L2|, así que puedo usar la hipótesis inductiva de P (la del Paso 3), con R completa:

loop(L2, R, |L2|, a :: acc, count) = ((a :: acc).reverse ::: M(L2, R), count + I(L2, R))

Como (a :: acc).reverse = acc.reverse ::: List(a), el resultado queda
(acc.reverse ::: (a :: M(L2, R)), count + I(L2, R)).

Me falta ver dos cosas.

Primero, que a :: M(L2, R) = M(L, R). Como L está ordenada, a es menor o igual que todos los de L2. Y como a <= c y R está ordenada, a también es menor o igual que todos los de R. Entonces a es el menor de todos, y va de primero en la mezcla ordenada de L y R.

Segundo, que I(L, R) = I(L2, R). Una inversión con a de lado izquierdo necesitaría un x en R con a > x, pero a <= c <= x para todo x de R, así que no hay ninguna. Las demás inversiones son justo las de I(L2, R).

Con eso el resultado es (acc.reverse ::: M(L, R), count + I(L, R)).

Caso 2: a > c.
Por la definición de loop, loop(L, R, |L|, acc, count) = loop(L, R2, |L|, c :: acc, count + |L|). Aquí puedo usar la hipótesis inductiva de Q:

loop(L, R2, |L|, c :: acc, count + |L|) = ((c :: acc).reverse ::: M(L, R2), count + |L| + I(L, R2))

Como (c :: acc).reverse = acc.reverse ::: List(c), el resultado queda
(acc.reverse ::: (c :: M(L, R2)), count + |L| + I(L, R2)).

Me falta ver dos cosas.

Primero, que c :: M(L, R2) = M(L, R). Como c < a y a es menor o igual que todos los de L, entonces c es menor que todos los de L. Además c es menor o igual que todos los de R2. Entonces c es el menor de todos y va de primero en la mezcla.

Segundo, que I(L, R) = |L| + I(L, R2). Las parejas que tienen a c de lado derecho son (b, c) con b en L. Cada b de L cumple b >= a > c, así que las |L| parejas son inversiones. Las demás son justo las de I(L, R2).

Con eso el resultado es (acc.reverse ::: M(L, R), count + I(L, R)).

En los dos casos se cumple Q(c :: R2). Como Q(Nil) se cumple y Q(R2) implica Q(c :: R2), Q(R) se cumple para toda lista ordenada R. Eso es justo P(a :: L2).

Conclusión

Como P(Nil) se cumple y P(L2) implica P(a :: L2), por inducción estructural P(L) se cumple para toda lista ordenada L. Queda demostrada la Proposición.

merge(left, right) llama a loop(left, right, left.length, Nil, 0). Por la Proposición,

merge(left, right) = (Nil.reverse ::: M(left, right), 0 + I(left, right)) = (M(left, right), I(left, right))

O sea que merge devuelve la lista mezclada y ordenada junto con la cantidad de inversiones entre las dos listas. Por lo tanto, merge es correcta.

### Demostración de que mergeSortCount es correcta

Aquí uso que merge ya es correcta, que quedó demostrado arriba.

Notación que voy a usar:

- ordenada(S) es la lista ordenada que tiene los mismos elementos de S.
- inv(S) es la cantidad de inversiones de S, o sea las parejas (i, j) con i < j y S(i) > S(j).

Proposición: para toda lista S,

mergeSortCount(S) = (ordenada(S), inv(S))

Aquí no me sirve la inducción estructural de una sola cola (de a :: t a t), porque mergeSortCount no se llama con la cola de la lista sino con sus dos mitades, que tienen un tamaño más o menos n/2 y no n - 1. Por eso uso inducción fuerte sobre el tamaño de la lista: supongo que se cumple para todas las listas más cortas, y no solo para la que tiene un elemento menos.

Paso 1. Identificar Q(n)

Q(n): para toda lista S con |S| = n, mergeSortCount(S) = (ordenada(S), inv(S)).

Paso 2. Casos base (n = 0 y n = 1)

Si |S| = 0, S es Nil y el código devuelve (Nil, 0), por la definición de mergeSortCount. Se cumple porque ordenada(Nil) = Nil e inv(Nil) = 0.
Si |S| = 1, el código devuelve (S, 0). Se cumple porque una lista de un solo elemento ya está ordenada y no tiene parejas (i, j) con i < j, así que no tiene inversiones.

Paso 3. Suponer el paso inductivo

Supongamos que la Proposición se cumple para algún n ∈ N con n >= 1 y para todos los tamaños k con k <= n, es decir:

Q(k): para toda lista S con |S| = k, mergeSortCount(S) = (ordenada(S), inv(S)), para todo k <= n.

Paso 4. Probar Q(n+1)

Sea S una lista con |S| = n + 1. Como n >= 1, S tiene al menos 2 elementos, así que el código no entra en los casos base y hace esto: parte S con splitAt(|S| / 2) en dos listas S1 y S2 tales que S = S1 ::: S2. Como |S| >= 2, las dos mitades tienen al menos un elemento, o sea |S1| >= 1 y |S2| >= 1. Entonces las dos son más cortas que S, y sus tamaños son menores o iguales que n.

Por la hipótesis