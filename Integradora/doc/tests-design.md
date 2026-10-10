### Diseño de las pruebas del Problema 1

### Qué pruebo y cómo

Pruebo tres funciones: countInversions, que es la que usa el que llama al algoritmo, y merge y mergeSortCount, para revisarlas cada una por separado. Uso munit y las corro con sbt test. Cada prueba llama a la función con una entrada y compara el resultado con el valor esperado. Los valores esperados salen de contar las parejas una por una.

Escogí las entradas en grupos, para que cada rama del código se ejecute por lo menos una vez:

- Los ejemplos del enunciado.
- Casos borde: lista vacía, un solo elemento y una cantidad impar de elementos.
- Listas con una forma especial: ordenada, al revés y con todos los elementos iguales.
- Listas con números negativos.
- Pruebas directas de merge, una por cada caso de loop.
- Una lista muy grande, para ver que el conteo no se desborda.

### Las pruebas de countInversions

1. Ejemplo 1 del enunciado: la lista 2, 3, 9, 2, 9 da 2.
2. Ejemplo 2 del enunciado: la lista 9, 7, 5, 3 da 6, que es 4·3/2.
3. Lista vacía: da 0. Es el caso base de mergeSortCount cuando la lista es Nil.
4. Lista con un solo elemento, 5: da 0. Es el otro caso base de mergeSortCount.
5. Lista ordenada, 1, 2, 3, 4, 5: da 0, porque nunca un número grande va antes que uno pequeño.
6. Todos iguales, 4, 4, 4, 4: da 0. Comprueba que los iguales no cuentan como inversión, o sea que merge toma de la izquierda cuando las cabezas son iguales.
7. Con negativos, -1, -5, 3, -2: da 3, por las parejas (-1, -5), (-1, -2) y (3, -2).
8. Lista al revés de 5 elementos, 5, 4, 3, 2, 1: da 10, que es 5·4/2. Es el peor caso: todas las parejas son inversiones.
9. Cantidad impar de elementos, 3, 1, 2: da 2. Con 3 elementos splitAt parte la lista en 1 y 2, y así compruebo que las mitades desiguales funcionan.
10. Lista grande al revés, de 100000 a 1: da 4 999 950 000, que es n(n - 1)/2. Ese número es más grande que el máximo de un Int (2 147 483 647), así que la prueba falla si count no es Long.

### Las pruebas de merge

Estas pruebas llaman a merge directamente con dos listas ya ordenadas y revisan la lista que devuelve y el conteo.

11. Izquierda vacía, merge(List(), List(1, 2)): da (List(1, 2), 0). Prueba el caso de loop donde la izquierda es Nil.
12. Derecha vacía, merge(List(1, 2), List()): da (List(1, 2), 0). Prueba el caso de loop donde la derecha es Nil.
13. Cabezas iguales, merge(List(2), List(2)): da (List(2, 2), 0). Prueba que con a <= c no se suma ninguna inversión.
14. Todos los de la derecha son menores, merge(List(4, 5, 6), List(1, 2, 3)): da (List(1, 2, 3, 4, 5, 6), 9). Es el peor caso de merge, 3 por 3 inversiones, y en cada paso sumo leftSize completo.
15. Elementos intercalados, merge(List(1, 5, 9), List(2, 6)): da (List(1, 2, 5, 6, 9), 3). Prueba que leftSize baja bien cuando salen elementos de la izquierda: el 5 forma 1 inversión con el 2, y el 9 forma 2, con el 2 y con el 6.
16. El ejemplo de las cartas, merge(List(3, 5), List(1, 4)): da (List(1, 3, 4, 5), 3).

### La prueba de mergeSortCount

17. La lista 2, 3, 9, 2, 9: la primera parte del resultado debe ser la lista ordenada 2, 2, 3, 9, 9. Las demás pruebas solo miran el conteo, y esta comprueba que mergeSortCount también ordene bien.

### Qué cubren

Con estas 17 pruebas se ejecutan los cuatro casos de loop (izquierda vacía, derecha vacía, a <= c y a > c) y los tres casos de mergeSortCount (lista vacía, un elemento y el caso recursivo). Además cubren las entradas más difíciles: todo ordenado, todo al revés, todo igual, negativos, cantidad impar y tamaño grande.