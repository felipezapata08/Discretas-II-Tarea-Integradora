### Problema 1: Número de inversiones

### Qué hace el algoritmo

Una inversión es cuando, en una lista de números, uno más grande aparece antes que uno más pequeño. Sirve para medir qué tan desordenada está la lista: si está ordenada de menor a mayor no tiene ninguna, y si está al revés, todas las parejas de números son inversiones.

Por ejemplo, en la lista 2, 3, 9, 2, 9 hay 2 inversiones: el 3 está antes del segundo 2 y es más grande, y el primer 9 también está antes de ese mismo 2 y es más grande.

La forma directa de contarlas sería comparar cada número con todos los que vienen después, pero eso se demora muchísimo cuando la lista es larga. Por eso lo hago de otra forma: parto la lista por la mitad, resuelvo cada mitad por separado, y después junto las dos mitades. Mientras las junto, cuento las inversiones que hay entre una mitad y la otra.

Para entender cómo se cuentan al juntar, me imagino dos montones de cartas, cada uno ya ordenado de menor a mayor, que voy pasando a un montón nuevo. Siempre miro la carta de arriba de cada montón.

Si la del montón izquierdo es menor o igual, la paso al montón nuevo y no pasa nada más.

Si la del montón derecho es menor, la paso al montón nuevo. Como el montón izquierdo está ordenado, esa carta es menor que todas las que todavía le quedan al izquierdo, y cada una de ellas forma una inversión con ella. Entonces sumo tantas inversiones como cartas le queden al montón izquierdo.

Con el montón izquierdo 3, 5 y el derecho 1, 4:

- Comparo 3 con 1. Gana el 1 y al izquierdo le quedan 2 cartas, entonces sumo 2.
- Comparo 3 con 4. Paso el 3 y no sumo nada.
- Comparo 5 con 4. Gana el 4 y al izquierdo le queda 1 carta, entonces sumo 1.
- Ya no quedan cartas a la derecha, así que paso el 5 que faltaba.

En total son 3 inversiones: (3,1), (5,1) y (5,4), y la lista queda ordenada: 1, 3, 4, 5.

Si dos números son iguales no cuentan como inversión, porque una inversión pide que el de la izquierda sea más grande, no igual.

### Qué hace el algoritmo, explicado con los términos del curso

Dada una secuencia a_0, a_1, ..., a_(n-1), una inversión es un par de posiciones (i, j) con 0 <= i < j < n tal que a_i > a_j. El problema pide devolver cuántas inversiones tiene la secuencia.

Lo resuelvo con divide y vencerás, modificando Merge Sort. La secuencia es una lista inmutable de enteros (una lista que no se modifica: cada operación crea una nueva) y la proceso con pattern matching, que es revisar si la lista está vacía (Nil) o si tiene cabeza y cola (head :: tail).

La función merge(left, right) recibe dos listas ya ordenadas y devuelve una pareja: la lista mezclada y ordenada, y la cantidad de pares (b, c) con b en left, c en right y b > c. Por dentro usa una función auxiliar llamada loop, que es recursiva de cola (@tailrec), o sea que la llamada recursiva es lo último que hace y por eso se ejecuta como un ciclo y no se llena la memoria. Esa función lleva tres datos anotados:

- acc: la lista mezclada que voy armando, al revés, porque agregar al inicio de una lista es lo más rápido. Al final le doy la vuelta con reverse.
- count: las inversiones que llevo contadas.
- leftSize: cuántos elementos le quedan a la lista izquierda. Lo llevo anotado para no tener que contarlos en cada paso, porque eso haría el algoritmo mucho más lento.

Los casos de loop son estos:

- Si la izquierda está vacía, agrego lo que quede de la derecha. Si la derecha está vacía, agrego lo que quede de la izquierda.
- Si las dos tienen elementos y la cabeza de la izquierda es menor o igual que la de la derecha, paso la de la izquierda y leftSize baja en uno. No sumo inversiones.
- Si la cabeza de la derecha es menor, la paso a acc y sumo leftSize a count, porque es menor que todos los que le quedan a la izquierda.

La función mergeSortCount(seq) devuelve la lista ordenada y el total de inversiones:

- Caso base: si la lista está vacía o tiene un solo elemento, ya está ordenada y tiene 0 inversiones.
- Caso recursivo: parto la lista por la mitad con splitAt, me llamo a mí misma con cada mitad, junto los resultados con merge y sumo: inversiones de la izquierda, más inversiones de la derecha, más las que contó merge.

Esta suma sirve porque toda inversión (i, j) cae en uno de tres casos: las dos posiciones están en la mitad izquierda, las dos están en la derecha, o i está en la izquierda y j en la derecha. Los dos primeros casos los cuentan las llamadas recursivas, y el tercero lo cuenta merge. Además, ordenar las mitades antes de juntarlas no cambia el conteo, porque las inversiones entre una mitad y la otra solo dependen de qué elementos hay en cada mitad, no del orden en que estén.

Por último, countInversions(seq) llama a mergeSortCount y se queda solo con el segundo valor de la pareja, que es el número de inversiones. Uso Long y no Int porque con muchos elementos la cuenta puede pasarse del límite de Int: con un millón de números en orden inverso son casi 500 mil millones de inversiones.