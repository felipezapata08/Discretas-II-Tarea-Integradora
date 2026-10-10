# Diseños de Pruebas Unitarias: Problema 2 (QuickSort 3-Way)

## Objetivo del Diseño de Pruebas
Garantizar la estabilidad y correctitud del algoritmo `QuickSort3Way` evaluando los casos, ejemplos del enunciado y escenarios de rendimiento optimizado (listas con repetidos).

---

## Matriz de Casos de Prueba

### 1. Casos Base (`Nil` y Lista Unitari)
* **Objetivo:** Verificar que el algoritmo maneje correctamente estructuras vacías o de un solo elemento sin realizar llamadas recursivas fallidas o infinitas.
* **Entradas:** 
  - `Nil`
  - `List(5)`
* **Salidas Esperadas:** 
  - `Nil`
  - `List(5)`

---

### 2. Ejemplo 1 del Enunciado
* **Objetivo:** Validar la partición de 3 vías con una lista desordenada donde el pivote inicial (`2`) tiene duplicados.
* **Entrada:** `List(2, 3, 9, 2, 2)`
* **Salida Esperada:** `List(2, 2, 2, 3, 9)`
* **Comportamiento esperado:** En el primer paso, `2` se establece como pivote, agrupando los tres números `2` en la lista intermedia `equal` sin volver a procesarlos recursivamente.

---

### 3. Ejemplo 2 del Enunciado
* **Objetivo:** Probar una lista con múltiples conjuntos de elementos duplicados (`1` y `4`).
* **Entrada:** `List(4, 4, 1, 9, 4, 1, 4, 4)`
* **Salida Esperada:** `List(1, 1, 4, 4, 4, 4, 4, 9)`
* **Comportamiento esperado:** Demuestra la capacidad del algoritmo para agrupar rápidamente varios duplicados de distintos valores durante los subproblemas recursivos.

---

### 4. Lista con Todos los Elementos Iguales
* **Objetivo:** Validar el comportamiento en el **mejor caso de rendimiento** del algoritmo.
* **Entrada:** `List(7, 7, 7, 7, 7, 7)`
* **Salida Esperada:** `List(7, 7, 7, 7, 7, 7)`
* **Comportamiento esperado:** La función `partition3Way` envía todos los elementos a la partición `equal`, dejando `less` y `greater` vacías. El proceso termina en tiempo lineal $O(n)$ sin profundizar en la pila de recursión.

---

### 5. Lista Ya Ordenada
* **Objetivo:** Asegurar que el algoritmo no altere ni corrompa el orden de una secuencia que ya se encuentra ordenada.
* **Entrada:** `List(1, 2, 3, 4, 5, 6)`
* **Salida Esperada:** `List(1, 2, 3, 4, 5, 6)`
