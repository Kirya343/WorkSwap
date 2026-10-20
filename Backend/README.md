# WorkSwap Backend

WorkSwap on alusta lisätyön, palveluiden, tuotteiden ja ansioluetteloiden etsimiseen.

Tämä repositorio sisältää WorkSwap-alustan backend-osan. Backend on toteutettu Javalla ja Spring Bootilla, ja se on rakennettu vertikaalista arkkitehtuuria käyttäväksi monimoduuliseksi Maven-projektiksi.

Frontend ja muut WorkSwap-ekosysteemin osat sijaitsevat erillisissä repositorioissa.

Frontend-repo: https://github.com/salavion/WorkSwapFrontend

---

## Teknologiat

* Java 21
* Spring Boot

  * Spring Web
  * Spring Security
  * Spring Data JPA
  * Spring OAuth2
  * Spring WebSocket
  * Spring RabbitMQ
* Hibernate
* MySQL / MariaDB
* RabbitMQ

### Tiedostojen tallennus

Käyttäjien ja järjestelmän tiedostojen tallennukseen käytetään **Cloudflare R2** -objektitallennusta.

### Koonti

* Maven
* Multi-module Maven project

### Käyttöönotto

Sovellukset otetaan käyttöön **Dockerin** avulla.

---

## Arkkitehtuuri

Backend käyttää **vertikaalista arkkitehtuuria (Vertical Slice Architecture)**.

Projektia ei ole jaettu ainoastaan teknisten kerrosten perusteella, vaan toiminnallisuus on organisoitu erillisiin toimialueisiin. Jokainen toimialue sisältää siihen liittyvät komponentit ja liiketoimintalogiikan.

Projekti on myös monimoduulinen Maven-projekti. Sovellukset koostetaan uudelleenkäytettävistä moduuleista.

Projektin pääasiallinen rakenne:

```text
/
├── apps/
│   ├── server/
│   ├── sso/
│   └── statistic/
│
├── domains/
│   ├── category/
│   ├── chat/
│   ├── forum/
│   ├── listing/
│   ├── location/
│   ├── notification/
│   ├── order/
│   ├── review/
│   ├── statistic/
│   ├── subscription/
│   ├── task/
│   └── user/
│
└── infrastructure/
    ├── datasource/
    ├── security/
    ├── sso-security-core/
    ├── shared/
    ├── storage/
    └── webflux/
```

---

## Sovellukset

Hakemisto `/apps/` sisältää suoritettavat sovellukset.

Molemmat sovellukset käyttävät tarvittavia toimialue- ja infrastruktuurimoduuleja Maven-riippuvuuksina.

### Server

WorkSwapin pääasiallinen backend-sovellus.

Server toimii alustan pääasiallisena API-sovelluksena ja vastaa asiakaspyyntöjen käsittelystä sekä liiketoimintalogiikan suorittamisesta.

Käytetyt teknologiat:

* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Spring OAuth2
* Spring WebSocket
* Spring AMQP
* Hibernate
* MySQL / MariaDB
* RabbitMQ

Päätehtävät:

* REST API -pyyntöjen käsittely;
* autentikointi ja autorisointi;
* käyttäjien hallinta;
* ilmoitusten hallinta;
* tilausten käsittely;
* arvostelujen hallinta;
* chat-toiminnallisuus;
* ilmoitusten käsittely;
* tilausten ja tilaustietojen hallinta;
* foorumin toiminnallisuus;
* muiden toimialueiden käsittely;
* tiedostojen hallinta Cloudflare R2 -tallennuksessa;
* viestien välitys RabbitMQ:n kautta;
* WebSocket-yhteyksien käsittely.

### Statistic

Erillinen sovellus tilastotietojen käsittelyä varten.

Statistic käyttää `statistic`-toimialuemoduulia sekä tarvittavia infrastruktuurimoduuleja.

Sovelluksen päätehtävänä on alustan tilastotietojen käsittely ja tallennus sekä analytiikkaan liittyvien toimintojen suorittaminen.

Muiden järjestelmän komponenttien kanssa kommunikointiin käytetään RabbitMQ:ta.

---

## Toimialueet

Hakemisto `/domains/` sisältää järjestelmän toimialuekohtaiset moduulit.

Jokainen moduuli edustaa erillistä liiketoiminta-aluetta ja kapseloi siihen liittyvän liiketoimintalogiikan.

### Category

Ilmoitusten ja muiden alustan kategorioiden hallinta.

### Chat

Keskustelujen ja käyttäjien välisen viestinnän toiminnallisuus.

### Forum

Foorumin ja siihen liittyvien entiteettien toiminnallisuus.

### Listing

Ilmoitusten hallinta.

Sisältää ilmoitusten luomisen, muokkaamisen, julkaisemisen, hakemisen ja muut ilmoituksiin liittyvät toiminnot.

### Location

Alustan käyttämien maantieteellisten sijaintien hallinta.

### Notification

Käyttäjien ilmoitusten hallinta.

### Order

Tilausten ja niihin liittyvien toimintojen hallinta.

### Review

Käyttäjien kirjoittamien arvostelujen hallinta.

### Statistic

Tilastoihin ja analytiikkaan liittyvä liiketoimintalogiikka.

### Subscription

Käyttäjien tilausten ja niihin liittyvien toimintojen hallinta.

### Task

Tehtävien ja niihin liittyvien toimintojen hallinta.

### User

Käyttäjien, heidän tietojensa ja niihin liittyvien entiteettien hallinta.

---

## Infrastruktuuri

Hakemisto `/infrastructure/` sisältää tekniset moduulit, joita toimialuemoduulit ja sovellukset käyttävät.

Infrastruktuurikomponentit eivät kuulu tiettyyn liiketoiminta-alueeseen, vaan tarjoavat järjestelmän tarvitsemia yleisiä teknisiä toimintoja.

### Datasource

Tietokantayhteyksistä ja tietokantaan liittyvistä komponenteista vastaava moduuli.

Sisältää MySQL / MariaDB -tietokantojen ja Hibernateen liittyvän toiminnallisuuden.

### Security

Sovelluksen tietoturvasta vastaava moduuli.

Sisältää yleiset komponentit, jotka liittyvät:

* Spring Securityyn;
* OAuth2:een;
* autentikointiin;
* autorisointiin;
* tokenien käsittelyyn;
* API:n suojaamiseen.

### Shared

Projektin eri moduulien yhteiset komponentit.

Moduuli on tarkoitettu komponenteille, jotka eivät kuulu tiettyyn toimialueeseen tai yksittäiseen infrastruktuurimekanismiin.

### Storage

Tiedostojen tallennukseen liittyvät abstraktiot ja toteutukset.

Moduulia käytetään Cloudflare R2 -objektitallennuksen kanssa kommunikointiin.

### WebFlux

Spring WebFlux -reaktiiviseen HTTP-teknologiaan liittyvät yhteiset komponentit.

---

## Moduulirakenne

Projekti käyttää **Maven Multi-Module Architecture** -rakennetta.

Hakemiston `/apps/` sovellukset eivät sisällä kaikkea liiketoimintalogiikkaa itse. Ne koostetaan erillisistä toimialue- ja infrastruktuurimoduuleista.

Moduulien väliset varsinaiset riippuvuudet määritellään projektin Maven-konfiguraatiossa.

Tämä rakenne mahdollistaa:

* toimialueiden eristämisen;
* toimialuemoduulien uudelleenkäytön eri sovelluksissa;
* liiketoimintalogiikan ja infrastruktuurin erottamisen;
* moduulien välisen riippuvuuden vähentämisen;
* sovellusten koostamisen ainoastaan tarvittavista moduuleista;
* projektin eri osien itsenäisemmän kehittämisen.

---

## Ulkoiset palvelut

Backend kommunikoi useiden ulkoisten infrastruktuuripalveluiden kanssa.

### MySQL / MariaDB

Projektin pääasiallinen relaatiotietokanta.

Tietokannan kanssa työskentelyyn käytetään:

* Spring Data JPA:ta;
* Hibernatea.

### RabbitMQ

RabbitMQ:ta käytetään asynkroniseen viestien välitykseen ja sovellusten väliseen kommunikointiin.

Sitä käytetään erityisesti `Server`- ja `Statistic`-sovellusten väliseen tiedonsiirtoon.

### Cloudflare R2

Cloudflare R2 toimii projektin objektitallennuksena.

Sitä käytetään tiedostoille, joita ei ole tarkoituksenmukaista tallentaa suoraan relaatiotietokantaan.

---

## Käyttöönotto

Sovellukset suoritetaan Docker-konteissa.

Molemmat sovellukset voidaan rakentaa ja käynnistää itsenäisesti niiden Maven-moduulien ja Docker-konfiguraation mukaisesti.

---

## Tietokanta-arkkitehtuuri

Projektissa käytetään kahta erillistä tietokantaa, joilla on eri käyttötarkoitukset.

### Pääasiallinen tietokanta

Pääasiallinen tietokanta sisältää WorkSwapin ydintiedot ja sovelluksen päivittäiseen toimintaan tarvittavat tiedot.

Tietokantaan tallennetaan esimerkiksi:

* käyttäjät ja heidän asetuksensa;
* ilmoitukset;
* kategoriat ja sijainnit;
* tilaukset;
* arvostelut;
* viestit ja chatit;
* ilmoitukset;
* muut sovelluksen päätoiminnallisuuteen liittyvät tiedot.

Tätä tietokantaa käytetään pääasiassa `Server`-sovelluksen kautta.

### Tilastotietokanta

Erillinen tietokanta on tarkoitettu tilastoille, analytiikalle sekä suurille tietomäärille, joita ei ole tarkoituksenmukaista säilyttää pääasiallisessa tietokannassa.

Se sisältää esimerkiksi:

* ilmoitusten katselutapahtumia;
* tilastollisia snapshot-tietoja;
* käyttäjien aktiivisuuteen liittyviä tietoja;
* muita analytiikkaan liittyviä tapahtuma- ja historiatietoja.

Tilastotietokannassa voidaan säilyttää huomattavasti yksityiskohtaisempaa ja suurempaa tietomäärää kuin pääasiallisessa tietokannassa. Tällaiset tiedot ovat hyödyllisiä analytiikkaa varten, mutta eivät välttämättä ole tarpeellisia yksittäisen pääentiteetin päivittäisessä käsittelyssä.

`Statistic`-sovellus vastaa tämän tietokannan käsittelystä ja analytiikan muodostamisesta.

Tietokantojen erottaminen vähentää pääasiallisen tietokannan kuormitusta ja mahdollistaa suurten analytiikka- ja historiatietomäärien käsittelyn vaikuttamatta merkittävästi sovelluksen päätoimintoihin.

---

## Projektin tila

Projekti on aktiivisessa kehityksessä.

Uusia toiminnallisuuksia ja muutoksia lisätään projektin kehittymisen myötä.