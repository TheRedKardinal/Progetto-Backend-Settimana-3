# Progetto Settimana 3 — Social Network API

API REST per la gestione di utenti, post e like, con autenticazione JWT stateless e autorizzazione basata su ruoli e ownership.

## Stack tecnico

- Java 25, Spring Boot 4.1.1 (`spring-boot-starter-data-jpa`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-boot-starter-webmvc`)
- PostgreSQL (`spring.jpa.hibernate.ddl-auto=update`)
- JWT stateless con `jjwt` 0.12.6
- Lombok
- Maven

## Avvio

```bash
./mvnw spring-boot:run
```

Richiede un'istanza PostgreSQL raggiungibile secondo quanto configurato in `src/main/resources/application.properties` (host, porta, nome database, credenziali).

Nella root del progetto è presente `ProgettoSettimana3.postman_collection.json`, una collection Postman con tutti gli endpoint pronti all'uso (register/login generano dati casuali via script, così è ripetibile senza conflitti su username/email duplicati).

## Autenticazione

Tutte le rotte tranne `/api/auth/register`, `/api/auth/login` e `/error` richiedono un header:

```
Authorization: Bearer <token>
```

Il token si ottiene da `POST /api/auth/login` e ha una validità di 31 giorni. Contiene come `subject` l'**id** dell'utente (non lo username), così l'identità resta stabile anche se in futuro lo username fosse modificabile.

## Regole di autorizzazione, endpoint per endpoint

Il progetto usa **tre meccanismi diversi** di autorizzazione, ciascuno scelto in base a *dove* vive l'informazione necessaria per decidere se permettere o negare l'accesso:

| Meccanismo | Dove vive la decisione | Quando si usa |
|---|---|---|
| Filtro globale (`JWTFilter` + `SecurityConfig`) | "sei autenticato o no?" — non serve sapere altro | Su ogni rotta protetta, a monte di tutto |
| `@PreAuthorize` (dichiarativo, sul controller) | Il ruolo dell'utente, da solo, basta a decidere | Quando la regola non dipende dalla risorsa specifica richiesta |
| Controllo imperativo nel service (Java puro) | Serve confrontare l'utente con **dati specifici della risorsa** (es. chi è l'autore di *questo* post) | Quando la regola dipende da un dato che va caricato dal DB, non deducibile dal solo token |

Di seguito il dettaglio per ogni operazione protetta.

---

### `GET /api/utenti/me`

**Regola**: autenticazione richiesta, nessun controllo di ruolo.

**Perché**: ogni utente autenticato deve poter vedere il proprio profilo — non c'è nessuna condizione da verificare oltre "sei loggato?". Il controllo è già garantito a monte dal `JWTFilter`: se la richiesta arriva al controller, l'utente è già autenticato e disponibile via `@AuthenticationPrincipal`.

### `GET /api/utenti/{id}`

**Regola**: autenticazione richiesta, nessun controllo di ruolo. Qualsiasi utente loggato può consultare il profilo di un altro utente.

**Perché**: è un social network dove i profili sono visibili tra utenti registrati (come una bacheca/rubrica interna), non un dato riservato al singolo proprietario. Non serve un controllo più stringente perché l'endpoint non espone la password (`@JsonIgnore` sull'entità) né altri dati sensibili.

### `GET /api/utenti`

**Regola**: autenticazione richiesta, nessun controllo di ruolo.

**Perché**: stessa motivazione di `GET /{id}` — è la vista "elenco" della stessa risorsa, con lo stesso livello di visibilità.

### `PATCH /api/utenti/{id}/role`

**Regola**: `@PreAuthorize("hasRole('MODERATOR')")`.

**Perché**: cambiare il ruolo di un utente è un'azione amministrativa, non dipende da *quale* utente la richiede rispetto a *quale* utente la subisce (un `MODERATOR` può cambiare il ruolo di chiunque, non solo del "proprio" qualcosa). È quindi una regola puramente basata sul ruolo di chi chiama, e per questo si presta bene a un controllo **dichiarativo**: `@PreAuthorize` legge `getAuthorities()` dell'utente autenticato (popolate dal token, senza bisogno di interrogare altro) e blocca la richiesta con `403` *prima* ancora che il metodo del controller venga eseguito, se il ruolo non corrisponde.

### `POST /api/post`

**Regola**: autenticazione richiesta, nessun controllo di ruolo.

**Perché**: qualunque utente registrato può pubblicare un post — non è un'azione riservata a un ruolo specifico.

### `GET /api/post`, `GET /api/post/{id}`

**Regola**: autenticazione richiesta, nessun controllo di ruolo.

**Perché**: i post sono contenuti visibili a tutti gli utenti autenticati, non solo all'autore.

### `PUT /api/post/{id}`

**Regola**: controllo **imperativo dentro `PostService.update(...)`** — l'operazione è permessa solo se `requester.getId()` coincide con l'id dell'autore del post, **oppure** se `requester.getRole() == Role.MODERATOR`. In caso contrario viene lanciata `org.springframework.security.access.AccessDeniedException`, che il `GlobalExceptionHandler` traduce in `403`.

**Perché non `@PreAuthorize`**: `@PreAuthorize` valuta l'espressione **prima** di eseguire il metodo, usando solo ciò che è disponibile nei parametri della richiesta o nel principal — non può, in modo semplice e leggibile, andare a caricare il post dal database e confrontarne l'autore con chi sta chiamando (si potrebbe fare con espressioni SpEL avanzate che referenziano bean, ma il codice diventa denso e difficile da testare in isolamento). Qui la regola dipende da un **dato specifico della risorsa** (`post.getUser().getId()`), che va necessariamente recuperato dal database — per questo il controllo vive come normale codice Java imperativo nel service, dove è esplicito, leggibile e facilmente testabile con uno unit test.

**Perché `AccessDeniedException` e non una eccezione custom**: è semanticamente corretta — l'utente *è* autenticato (altrimenti non sarebbe arrivato qui), ma non è autorizzato a questa azione specifica. Riusare la classe di Spring Security evita di reinventare qualcosa che il framework offre già, ed è già gestita dal `GlobalExceptionHandler` esistente.

### `POST /api/like/{postId}`, `DELETE /api/like/{postId}`

**Regola**: autenticazione richiesta, nessun controllo di ruolo — ma **l'utente su cui opera l'azione non è mai un parametro della richiesta**, è sempre e solo quello ricavato da `@AuthenticationPrincipal` (cioè dal token).

**Perché**: qui la "regola di autorizzazione" non è un controllo di ruolo o di ownership da verificare a runtime — è una scelta di **design dell'endpoint** che rende impossibile, per costruzione, mettere un like a nome di qualcun altro o rimuovere il like di un altro utente. Non esiste da nessuna parte (URL, body) un modo per il client di specificare "a nome di chi" agire: l'identità arriva sempre e solo dal token verificato dal `JWTFilter`. Il doppio-like è prevenuto separatamente (`existsByUserAndPost`, 400 se già presente) ed è una regola di business, non di autorizzazione.

---

## Riepilogo rapido

| Endpoint | Autenticazione | Ruolo richiesto | Controllo aggiuntivo |
|---|---|---|---|
| `POST /api/auth/register` | ❌ pubblico | — | — |
| `POST /api/auth/login` | ❌ pubblico | — | — |
| `GET /api/utenti/me` | ✅ | — | — |
| `GET /api/utenti/{id}` | ✅ | — | — |
| `GET /api/utenti` | ✅ | — | — |
| `PATCH /api/utenti/{id}/role` | ✅ | `MODERATOR` (`@PreAuthorize`) | — |
| `POST /api/post` | ✅ | — | — |
| `GET /api/post` | ✅ | — | — |
| `GET /api/post/{id}` | ✅ | — | — |
| `PUT /api/post/{id}` | ✅ | — | autore del post **oppure** `MODERATOR` (controllo nel service) |
| `POST /api/like/{postId}` | ✅ | — | agisce sempre sull'utente del token |
| `DELETE /api/like/{postId}` | ✅ | — | agisce sempre sull'utente del token |
