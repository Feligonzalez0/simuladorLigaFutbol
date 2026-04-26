IDEAS:
Simulación de partidos:
    influyen:
        Ataque, defensa y medio (propio)
        Ataque, defensa y medio (rival)
        Moral, forma y factor sorpresa (como multiplicadores [0.0-1.0])
        Localía

TODO:
    Generar fixture
    
IDEAS:
    Implementar Base de Datos con Ligas y equipos ya incluidos
    Armar esta base de datos calculando cada atributo de los equipos con datos de internet
    Ascensos y descensos


Simulación partidos:
    1. Se crean oportunidades de gol (xG) para cada equipo en base a: 
        a. Valor mediocampo local y visitante (posesionLocal = medioLocal/(medioLocal+medioVisitante))
        b. Localía
        c. Factores externos (Moral, Forma, Factor sorpresa, etc.)
    2. En base a las xG de cada equipo, se calcula cuantas se convierten en gol teniendo en cuenta:
        a. Valor ataque vs valor defensa rival
        b. Localía
        c. Factores externos
    Factores/detalles importantes a tener en cuenta:
        a. Un gol debería mejorar la moral del equipo que lo hace y disminuir la del que lo recibe
        b. La forma de los equipos puede ir disminuyendo conforme pasan las situaciones (transcurre el partido)
        c. Otros factores que se ocurran en la implementación