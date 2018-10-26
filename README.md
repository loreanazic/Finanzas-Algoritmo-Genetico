# Algoritmo Genético

Se Investigo los precios de los últimos 5 años (Cortes Mensuales) de los siguientes fondos:
- Permanent Portfolio PRPFX
- API Efficient Frontier Income Instl APIIX
- API Efficient Frontier Income A APIUX
- Midas Perpetual Portfolio MPERX
- API Efficient Frontier Income C AFFIX

### Modelo de Markowitz:

Markowitz desarrolla su modelo sobre la base del comportamiento racional del inversor. Es decir, el inversor desea la rentabilidad y rechaza el riesgo. Por lo tanto, para él una cartera será eficiente si proporciona la máxima rentabilidad posible para un riesgo dado, o de forma equivalente, si presenta el menor riesgo posible para un nivel determinado de rentabilidad.

  Rendimiento esperado Portafolio:
  
  <img src="https://latex.codecogs.com/gif.latex?\inline&space;\textrm{E(Rp)}=&space;\sum_{i=1}^{n}&space;x_i*E(ri)" title="Rendimiento Portafolio" />

  Donde:
- X es la proporción del presupuesto del inversor destinado al activo financiero i e incógnita del problema, 
- E(Rp), es la rentabilidad o rendimiento esperado de la cartera p.

Se elaborara un Algoritmo genético colocando como función de Aptitud (Fitness) al coeficiente de prima riesgo para Maximizar:

<img src="https://latex.codecogs.com/gif.latex?\inline&space;MaxCoef=&space;\frac{E(Rp)}{\delta&space;^{2}(Rp)}" title="Rendimiento Portafolio" />
