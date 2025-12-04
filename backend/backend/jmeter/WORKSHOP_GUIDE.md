# Guide d'Utilisation JMeter - Basé sur l'Atelier Performance

Ce guide est basé sur les meilleures pratiques de l'atelier de tests de performance avec JMeter.

## Structure des Fichiers

```
backend/backend/jmeter/
├── nassnews-api-test-plan.jmx      # Plan de test principal
├── auth-test-plan.jmx              # Plan de test authentification
├── test-data/
│   ├── users.csv                   # Données de test utilisateurs
│   └── cities.csv                  # Données de test villes
├── scripts/
│   ├── start-full-test.bat         # Test complet (50 users, 5 min)
│   ├── quick-test.bat              # Test rapide (10 users, 1 min)
│   └── analyze-results.bat         # Analyse des résultats
├── results/                         # Fichiers JTL bruts
└── reports/                         # Rapports HTML générés
```

## Configuration Initiale

### 1. Vérifier l'Installation de JMeter

Assurez-vous que JMeter est installé et configuré:

```cmd
set JMETER_HOME=C:\JMeter\apache-jmeter-5.6.3
```

Ou modifiez les scripts pour pointer vers votre installation.

### 2. Démarrer le Backend

Avant de lancer les tests, démarrez votre application Spring Boot:

```cmd
cd backend/backend
mvn spring-boot:run
```

Vérifiez que l'API est accessible:
- http://localhost:8080/api/cities
- http://localhost:8080/api/actualites
- http://localhost:8080/api/events

## Exécution des Tests

### Test Rapide (Développement)

Pour un test rapide pendant le développement:

```cmd
cd backend/backend/jmeter/scripts
quick-test.bat
```

**Configuration:**
- 10 utilisateurs simultanés
- Montée en charge: 30 secondes
- 5 itérations par utilisateur
- Durée totale: ~1-2 minutes

### Test Complet (Performance)

Pour un test de performance complet:

```cmd
cd backend/backend/jmeter/scripts
start-full-test.bat
```

**Configuration:**
- 50 utilisateurs simultanés
- Montée en charge: 60 secondes
- Durée totale: 5 minutes
- Tous les endpoints testés

### Test Personnalisé

Pour personnaliser les paramètres:

```cmd
cd backend/backend/jmeter
run-tests.bat [threads] [rampUp] [loops] [baseUrl]
```

**Exemples:**
```cmd
REM Test avec 20 utilisateurs, montée en 20s, 10 itérations
run-tests.bat 20 20 10

REM Test avec URL personnalisée
run-tests.bat 30 30 5 http://192.168.1.100:8080
```

## Analyse des Résultats

### 1. Rapport HTML Automatique

Après chaque test, un rapport HTML est généré automatiquement dans `reports/`.

Ouvrez `reports/[timestamp]/index.html` dans votre navigateur pour voir:
- Dashboard avec métriques clés
- Graphiques de temps de réponse
- Répartition des codes HTTP
- Statistiques par endpoint
- Analyse des erreurs

### 2. Analyse Rapide

Pour une analyse rapide des résultats:

```cmd
cd backend/backend/jmeter/scripts
analyze-results.bat
```

### 3. Métriques Clés à Surveiller

| Métrique | Valeur Cible | Description |
|----------|--------------|-------------|
| **Temps de réponse moyen** | < 500ms | Temps moyen pour toutes les requêtes |
| **90ème percentile** | < 1000ms | 90% des requêtes sont plus rapides |
| **Taux d'erreur** | < 1% | Pourcentage de requêtes échouées |
| **Débit (Throughput)** | > 50 req/s | Nombre de requêtes par seconde |
| **Temps de connexion** | < 100ms | Temps pour établir la connexion |

## Scénarios de Test

### Scénario 1: Charge Normale

**Objectif:** Tester le comportement sous charge normale

```cmd
run-tests.bat 20 30 10
```

**Endpoints testés:**
- GET /api/cities
- GET /api/actualites
- GET /api/events
- POST /api/utilisateurs/register
- POST /api/utilisateurs/login

### Scénario 2: Pic de Charge

**Objectif:** Tester la réaction à un pic soudain de trafic

```cmd
run-tests.bat 100 10 5
```

**Caractéristiques:**
- 100 utilisateurs
- Montée rapide (10 secondes)
- Teste la capacité de montée en charge

### Scénario 3: Charge Soutenue

**Objectif:** Tester la stabilité sur une longue période

```cmd
run-tests.bat 30 60 50
```

**Caractéristiques:**
- 30 utilisateurs
- Montée progressive (60 secondes)
- 50 itérations par utilisateur
- Durée totale: ~10-15 minutes

### Scénario 4: Test d'Authentification

**Objectif:** Tester spécifiquement l'authentification et les favoris

```cmd
cd backend/backend/jmeter
C:\JMeter\apache-jmeter-5.6.3\bin\jmeter.bat -n -t auth-test-plan.jmx -l results/auth-test.jtl -e -o reports/auth-test
```

## Interprétation des Résultats

### ✅ Test Réussi

Un test est considéré comme réussi si:
- Taux d'erreur < 1%
- Temps de réponse moyen < 500ms
- 90ème percentile < 1000ms
- Pas d'erreurs serveur (500)

### ⚠️ Attention Requise

Signaux d'alerte:
- Taux d'erreur entre 1-5%
- Temps de réponse > 1000ms
- Erreurs 500 sporadiques
- Débit < 30 req/s

### ❌ Test Échoué

Le test est considéré comme échoué si:
- Taux d'erreur > 5%
- Temps de réponse > 2000ms
- Nombreuses erreurs 500
- Débit < 10 req/s

## Optimisations Basées sur les Résultats

### Si les temps de réponse sont élevés:

1. **Vérifier la base de données:**
   - Index manquants
   - Requêtes non optimisées
   - Connexions pool insuffisantes

2. **Vérifier le code:**
   - Requêtes N+1
   - Logique métier lourde
   - Manque de cache

3. **Vérifier l'infrastructure:**
   - CPU/Mémoire insuffisants
   - Réseau lent
   - Configuration JVM

### Si le taux d'erreur est élevé:

1. **Vérifier les logs:**
   ```cmd
   cd backend/backend
   tail -f logs/application.log
   ```

2. **Vérifier les ressources:**
   - Mémoire disponible
   - Connexions base de données
   - Threads disponibles

3. **Vérifier la configuration:**
   - Timeouts
   - Pool de connexions
   - Limites de threads

## Bonnes Pratiques

### 1. Tests Réguliers

Exécutez des tests de performance:
- Après chaque déploiement majeur
- Avant les releases
- Lors de changements d'infrastructure
- Après optimisations

### 2. Environnement de Test

Utilisez un environnement similaire à la production:
- Même configuration base de données
- Ressources similaires
- Données réalistes

### 3. Documentation

Documentez:
- Les résultats de chaque test
- Les optimisations appliquées
- Les métriques de référence
- Les problèmes identifiés

### 4. Surveillance Continue

Mettez en place:
- Monitoring en production
- Alertes sur les métriques clés
- Dashboards de performance
- Tests automatisés en CI/CD

## Intégration CI/CD

### GitHub Actions Example

```yaml
name: Performance Tests
on: [push]
jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup JMeter
        run: |
          wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.tgz
          tar -xzf apache-jmeter-5.6.3.tgz
      - name: Start Backend
        run: |
          cd backend/backend
          mvn spring-boot:run &
          sleep 30
      - name: Run Performance Tests
        run: |
          ./apache-jmeter-5.6.3/bin/jmeter -n -t backend/backend/jmeter/nassnews-api-test-plan.jmx -l results.jtl -e -o reports/
      - name: Upload Results
        uses: actions/upload-artifact@v2
        with:
          name: jmeter-results
          path: reports/
```

## Dépannage

### Problème: "JMeter not found"

**Solution:**
```cmd
set JMETER_HOME=C:\JMeter\apache-jmeter-5.6.3
```
Ou modifiez les scripts pour pointer vers votre installation.

### Problème: "Connection refused"

**Solution:**
- Vérifiez que le backend est démarré
- Vérifiez le port (8080 par défaut)
- Vérifiez le firewall

### Problème: "High error rate"

**Solution:**
- Vérifiez les logs du backend
- Vérifiez la base de données
- Réduisez le nombre d'utilisateurs
- Augmentez les timeouts

### Problème: "Slow response times"

**Solution:**
- Vérifiez les requêtes base de données
- Vérifiez les ressources serveur
- Optimisez le code
- Ajoutez du cache

## Ressources

- [Documentation JMeter Officielle](https://jmeter.apache.org/usermanual/)
- [Meilleures Pratiques JMeter](https://jmeter.apache.org/usermanual/best-practices.html)
- [Spring Boot Performance](https://spring.io/guides/gs/spring-boot-performance/)

## Support

Pour toute question ou problème:
1. Consultez les logs dans `backend/backend/logs/`
2. Vérifiez les rapports HTML générés
3. Consultez la documentation JMeter
4. Analysez les métriques dans les rapports

