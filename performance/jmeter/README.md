# Tests de charge JMeter — CESIZen

## Objectif

Ces tests évaluent le comportement de l’API CESIZen sous charge et permettent de détecter les dégradations de performances.

## Scénario testé

Le scénario `information-load-test.jmx` sollicite la route publique :

```text
GET /api/information
