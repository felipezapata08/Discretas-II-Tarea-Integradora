# Tarea Integradora 1: Divide y vencerás en Scala

**Computación y Estructuras Discretas II, 2026-2** 

Este proyecto resuelve tres problemas clásicos con la estrategia **divide y vencerás**, usando **programación funcional pura en Scala 3**: listas inmutables, recursión, *pattern matching* y recursión de cola @tailrec. Además incluye la **experimentación con conjuntos grandes de datos**, donde se mide el tiempo de ejecución de cada algoritmo y se compara con su complejidad teórica.

## Integrantes

| Integrante | Usuario de GitHub |
|---|---|
| Daniel Felipe Herrera  | herreradaniel821-sudo |
| Sol Edith Ortiz| soleditho |
| Jider Felipe zapata | felipezapata08 |

**Equipo:** Claude, Gemini, y Chat

## Los tres problemas

| # | Problema | Estrategia | Código |
|---|---|---|---|
| 1 | **Número de inversiones**: cuántas parejas `i < j` cumplen `aᵢ > aⱼ` | `merge` y `mergeSort` modificados: al mezclar dos mitades ordenadas se cuentan las inversiones que cruzan entre ellas | [`Inversions.scala`](Integradora/src/main/scala/inversions/Inversions.scala) |
| 2 | **QuickSort mejorado**: ordenar rápido incluso con muchos elementos repetidos | Partición de **3 vías** (`< x`, `= x`, `> x`): la parte de iguales ya está en su lugar y no se vuelve a procesar | [`QuickSort3Way.scala`](Integradora/src/main/scala/quicksort/QuickSort3Way.scala) |
| 3 | **Puntos más cercanos**: distancia mínima entre dos puntos del plano | Ordenar por x, dividir en dos mitades, resolver cada una, revisar la franja central ordenada por y | [`ClosestPoints.scala`](Integradora/src/main/scala/closest/ClosestPoints.scala) |

### Entrada y salida

Todas las entradas se representan con listas de enteros (un punto es `List(x, y)`).

```scala
Inversions.countInversions(List(2, 3, 9, 2, 9))        // 2
QuickSort3Way.quickSort(List(4, 4, 1, 9, 4, 1, 4, 4))   // List(1, 1, 4, 4, 4, 4, 4, 9)
ClosestPoints.closestPoints(List(List(0, 0), List(3, 4), List(1, 1)))  // 1.4142
```

### Complejidad teórica

<!-- TODO: si se implementa la variante de Closest Points con mezcla por y, cambiar la fila 3 a T(n) = 2T(n/2) + O(n) → Θ(n log n) -->

| Problema | Recurrencia | Complejidad |
|---|---|---|
| 1. Inversiones | `T(n) = 2T(n/2) + O(n)` | Θ(n log n) |
| 2. QuickSort 3 vías | caso promedio `T(n) = 2T(n/2) + O(n)`; peor caso `T(n) = T(n−1) + O(n)` | Θ(n log n) promedio · Θ(n²) peor caso |
| 3. Puntos cercanos | `T(n) = 2T(n/2) + O(n log n)` (franja reordenada por y en cada nivel) | Θ(n log² n) |

Los desarrollos completos están en la carpeta [`doc`](#documentación).

## Condiciones de implementación

- **Paradigma funcional puro:** sin `var`, sin ciclos `while`/`for` y sin efectos colaterales en los algoritmos.
- **Estructuras inmutables:** todo se procesa con `List` y *pattern matching*.
- **Recursión de cola con `@tailrec`:** el proyecto tiene más de las cuatro funciones exigidas.

  | Archivo | Funciones con `@tailrec` |
  |---|---|
  | `Inversions.scala` | `loop` (dentro de `merge`) |
  | `QuickSort3Way.scala` | `partition3Way` |
  | `ClosestPoints.scala` | `lengthTR`, `reverseTR`, `loop` de `splitPoints`, `loop` de `mergeByX`, `loop` de `mergeByY`, `loop` de `buildStrip`, `compareWithFollowing` y `scan` (dentro de `stripMinDistance`) |

- **Identificadores y comentarios en inglés**, y código documentado con etiquetas Javadoc (`@param`, `@return`).

## Estructura del repositorio

```
.
├── README.md
└── Integradora/
    ├── build.sbt
    ├── project/build.properties
    ├── doc/                              
    └── src/
        ├── main/scala/
        │   ├── inversions/Inversions.scala
        │   ├── quicksort/QuickSort3Way.scala
        │   ├── closest/ClosestPoints.scala
        │   └── experiment/               
        │       ├── DataGenerator.scala
        │       ├── Timer.scala
        │       └── ExperimentRunner.scala
        └── test/scala/                   
            ├── inversions/InversionsSuite.scala
            ├── quicksort/QuickSort3WaySuite.scala
            └── closest/ClosestPointsSuite.scala
```

