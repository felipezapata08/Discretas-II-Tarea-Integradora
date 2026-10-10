# Problema 2: QuickSort mediante Partición de 3 Vías

## 1. Explicación del Algoritmo

El algoritmo QuickSort tradicional funciona eligiendo un elemento de la lista denominado **pivote** y dividiendo la lista en 2 partes:
- Elementos **menores o iguales** al pivote.
- Elementos **mayores** al pivote.

Cuando una lista contiene **muchos elementos repetidos**, este enfoque de 2 vías se vuelve ineficiente $O(n^2)$ porque vuelve a procesar los mismos elementos duplicados una y otra vez en las llamadas recursivas siguientes.

### La Solución: Partición de 3 Vías (*3-Way Partitioning*)
Para resolver esto, rediseñamos el algoritmo dividiendo la lista en **3 grupos distintos**:
1. **`less` (`< x`):** Elementos estrictamente menores que el pivote.
2. **`equal` (`= x`):** Elementos exactamente iguales al pivote (incluyendo al pivote mismo).
3. **`greater` (`> x`):** Elementos estrictamente mayores que el pivote.

El grupo de elementos iguales (`equal`) ya se encuentra en su posición final definitiva. Por lo tanto, en los siguientes pasos recursivos **solo ordenamos las sublistas `less` y `greater`**, ignorando por completo todos los elementos duplicados del pivote.

### Ejemplo:
Dada la lista `[4, 4, 1, 9, 4, 1, 4, 4]` y tomando como pivote el primer `4`:

 **Partición (`partition3Way`):**
   - Menores (`less`): `[1, 1]`
   - Iguales (`equal`): `[4, 4, 4, 4, 4, 4]`
   - Mayores (`greater`): `[9]`

 **Llamadas Recursivas:**
   - `quickSort([1, 1])` $\rightarrow$ produce `[1, 1]`.
   - La lista de iguales `[4, 4, 4, 4, 4, 4]` **no se vuelve a procesar**.
   - `quickSort([9])` $\rightarrow$ produce `[9]`.

 **Resultado:**
   Se concatenan los tres resultados: `[1, 1] ++ [4, 4, 4, 4, 4, 4] ++ [9]` = `[1, 1, 4, 4, 4, 4, 4, 4, 9]`.

---

## 2. Prueba de Correctitud mediante Inducción Estructural

Demostramos que `quickSort(list)` ordena correctamente cualquier lista de enteros $L$.

### Lema Auxiliar: Correctitud de `partition3Way`
`partition3Way` procesa la lista elemento por elemento usando recursión de cola (`@tailrec`). Garantiza que al finalizar retorna una tupla $(L_{less}, L_{equal}, L_{greater})$ con:
- $L_{less} = \{x \in L \mid x < pivot\}$
- $L_{equal} = \{pivot\} \cup \{x \in L \mid x = pivot\}$
- $L_{greater} = \{x \in L \mid x > pivot\}$

Dado que reduce la lista de entrada en 1 elemento en cada paso recursivo y termina cuando la lista es `Nil`, la función finaliza y es correcta.

---

### Demostración de correctitud

#### **Paso 1. Definir la propiedad $P(L)$**
$P(L)$: Para cualquier lista $L$, `quickSort(L)` produce una permutación de $L$ ordenada en orden no decreciente.

#### **Paso 2. Caso Base**
- **Lista vacía (`Nil`):** `quickSort(Nil)` retorna `Nil`, la cual está vacía y por ende ordenada.
- **Lista de un solo elemento (`[x]`):** `quickSort([x])` selecciona `pivot = x` y `tail = Nil`. `partition3Way` devuelve `less = Nil`, `equal = List(x)`, `greater = Nil`. El resultado `Nil ++ List(x) ++ Nil` = `List(x)` está correctamente ordenado.

#### **Paso 3. Hipótesis Inductiva (HI)**
Asumimos que la propiedad $P(K)$ se cumple para toda lista $K$ de tamaño estrictamente menor a la lista actual $L$ ($\vert{}K\vert{} < \vert{}L\vert{}$).

#### **Paso 4. Paso Inductivo ($L = pivot :: tail$)**
1. La función `partition3Way` divide a $tail$ en tres sublistas: $L_{less}$, $L_{equal}$ y $L_{greater}$.
2. Dado que el pivote está en $L_{equal}$ y no en $tail$, el tamaño de las sublistas $L_{less}$ y $L_{greater}$ cumple:
   - $\vert{}L_{less}\vert{} \le \vert{}tail\vert{} < \vert{}L\vert{}$
   - $\vert{}L_{greater}\vert{} \le \vert{}tail\vert{} < \vert{}L\vert{}$
3. Por **Hipótesis Inductiva**, las llamadas recursivas producen:
   - $S_{less} = \text{quickSort}(L_{less})$, la cual es una lista ordenada con todos los elementos $< pivot$.
   - $S_{greater} = \text{quickSort}(L_{greater})$, la cual es una lista ordenada con todos los elementos $> pivot$.
4. **Combinación:** Se concatenan $S_{less} \mathbin{+\mkern-10mu+} L_{equal} \mathbin{+\mkern-10mu+} S_{greater}$. Como $S_{less}$ y $S_{greater}$ están ordenadas, y se cumple que todo elemento de $S_{less} < pivot$ y todo elemento de $S_{greater} > pivot$, la lista unida mantiene el orden no decreciente.

**Conclusión:** Por inducción estructural, `quickSort(L)` es correcto para toda lista $L$. 

---
## 3. Análisis de Complejidad Teórica y Ecuaciones de Recurrencia

La función de partición de 3 vías recorre la lista de tamaño $n$ haciendo comparaciones y construyendo las sublistas en tiempo lineal:
$$W(n) = O(n)$$

A partir de este trabajo por nivel, definimos las ecuaciones de recurrencia y la complejidad para los distintos escenarios de ejecución.


### Desarrollo del Análisis por Casos

#### Caso 1: Caso Promedio (Elementos distintos)
Cuando los elementos de la lista son distintos y el pivote la divide de forma aproximadamente equilibrada ($\vert{}L_{< p}\vert{} \approx n/2$ y $\vert{}L_{> p}\vert{} \approx n/2$), obtenemos la siguiente relación de recurrencia:

$$T(n) = 2T\left(\frac{n}{2}\right) + O(n)$$

> **Ejemplo:**  
> Entrada: `[5, 2, 8, 1, 9, 3, 7]` con pivote `5`.  
> - $L_{< 5} = [2, 1, 3]$ (3 elementos)  
> - $L_{= 5} = [5]$ (1 elemento)  
> - $L_{> 5} = [8, 9, 7]$ (3 elementos)  
> Ambos lados quedan equilibrados con aproximadamente la mitad de los elementos ($n/2$), lo que genera un árbol de recursión balanceado de profundidad $\log_2 n$.

##### Resolución por Teorema Maestro:
Dada una recurrencia de la forma $T(n) = aT(n/b) + f(n)$:
1. Identificamos las constantes: $a = 2$, $b = 2$, y $f(n) = O(n)$.
2. Calculamos el valor crítico $n^{\log_b a}$:
   $$n^{\log_2 2} = n^1 = n$$
3. Comparamos $f(n)$ con $n^{\log_b a}$:
   Dado que $f(n) = \Theta(n) = \Theta(n^{\log_b a})$, aplicamos el **Caso 2 del Teorema Maestro**.
4. Por la fórmula del Caso 2, la solución es $T(n) = \Theta(n^{\log_b a} \log n)$:

$$\mathbf{T(n) = \Theta(n \log n)}$$

---

#### Caso 2: Mejor Caso (Alta duplicidad)
Cuando la mayoría de los elementos son iguales al pivote, la partición ubica casi todos los elementos en la sublista intermedia $L_{= p}$. Como resultado, las sublistas para llamadas recursivas $L_{< p}$ y $L_{> p}$ quedan vacías ($size = 0$) o con un número constante muy pequeño de elementos.

> **Ejemplo:**  
> Entrada: `[4, 4, 1, 4, 4, 9, 4]` con pivote `4`.  
> - $L_{< 4} = [1]$ (1 elemento)  
> - $L_{= 4} = [4, 4, 4, 4, 4]$ (5 elementos)  
> - $L_{> 4} = [9]$ (1 elemento)  
> La sublista de iguales retiene la mayoría de datos ($5$ de $7$), eliminando la necesidad de llamadas recursivas profundas sobre los duplicados.

##### Ecuación de recurrencia:
$$T(n) = T(0) + T(0) + O(n)$$

##### Resolución:
Como $T(0) = O(1)$:
$$T(n) = O(1) + O(1) + O(n) = O(n)$$

Por lo tanto, la complejidad en el mejor caso es lineal:

$$\mathbf{T(n) = \Theta(n)}$$

---

#### Caso 3: Peor Caso (Desbalance severo con elementos distintos)
Ocurre cuando todos los elementos son distintos y en cada paso recursivo el pivote elegido resulta ser el elemento mínimo o máximo de la lista. En este escenario, una de las sublistas recibe $n-1$ elementos mientras que la otra recibe $0$ elementos.

> **Ejemplo:**  
> Entrada ya ordenada con pivote inicial como el mínimo: `[1, 2, 3, 4, 5]` con pivote `1`.  
> - $L_{< 1} = []$ (0 elementos)  
> - $L_{= 1} = [1]$ (1 elemento)  
> - $L_{> 1} = [2, 3, 4, 5]$ ($n - 1 = 4$ elementos)  
> En cada nivel recursivo el problema solo disminuye en $1$ elemento, generando una pila recursiva de profundidad $n$.
##### Ecuación de recurrencia:
$$T(n) = T(n - 1) + T(0) + O(n) = T(n - 1) + O(n)$$

##### Resolución por Sustitución Iterativa :
Desenrollamos la recurrencia paso a paso:

$$
\begin{aligned}
T(n) &= T(n - 1) + c \cdot n \\
&= (T(n - 2) + c \cdot (n - 1)) + c \cdot n \\
&= (T(n - 3) + c \cdot (n - 2)) + c \cdot (n - 1) + c \cdot n \\
&\\\ ...\\
&= T(1) + c \sum_{i=2}^{n} i
\end{aligned}
$$

Usando la fórmula de la suma aritmética $\sum_{i=1}^{n} i = \frac{n(n+1)}{2}$:

$$T(n) = O(1) + c \left( \frac{n(n+1)}{2} - 1 \right) = \frac{c}{2}n^2 + \frac{c}{2}n + O(1)$$

Tomando el término de mayor orden:

$$\mathbf{T(n) = \Theta(n^2)}$$
