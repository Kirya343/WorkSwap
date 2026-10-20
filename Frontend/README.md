# WorkSwap Frontend

WorkSwap on alusta lisätyön, palveluiden, tuotteiden ja ansioluetteloiden etsimiseen.

Tämä repositorio sisältää WorkSwap-alustan frontend-osan. Frontend on toteutettu TypeScriptillä, Reactilla ja Next.js:llä. Projekti on rakennettu monorepona, joka sisältää kaksi erillistä frontend-sovellusta sekä niiden yhteisen koodin.

Backend ja muut WorkSwap-ekosysteemin osat sijaitsevat erillisissä repositorioissa.

Backend-repo: https://github.com/salavion/WorkSwap-Backend

---

## Teknologiat

* TypeScript
* React
* Next.js
* Sass / SCSS
* i18next
* WebSocket
* Radix UI
* React Swipeable

### Käyttöliittymä

Käyttöliittymä on toteutettu Reactilla ja Next.js:llä.

Tyylittelyssä käytetään Sass / SCSS -teknologiaa. Sass on valittu erityisesti sisäkkäisen syntaksin, selkeän rakenteen ja tyylien paremman organisoinnin vuoksi.

Projektissa käytetään aktiivisesti Sass Modules -ominaisuutta. Komponenttien tyylit on kapseloitu omiin moduuleihinsa, jolloin komponentit voivat hallita omia CSS-luokkiaan ilman tarpeettomia globaaleja tyylejä.

### Monikielisyys

Frontend tukee neljää kieltä:

* suomi;
* englanti;
* venäjä;
* italia.

Monikielisyys on toteutettu `i18next`-kirjastolla. Käyttöliittymän tekstit on erotettu sovelluksen muusta logiikasta, mikä mahdollistaa uusien kielten lisäämisen ilman käyttöliittymälogiikan muuttamista.

### WebSocket

WebSocket-yhteyksiä käytetään reaaliaikaisiin toimintoihin.

Niitä hyödynnetään erityisesti:

* chat-viestien välitykseen;
* käyttäjien ilmoitusten päivittämiseen;
* muiden reaaliaikaisten käyttöliittymätapahtumien käsittelyyn.

Frontend kommunikoi WebSocket-yhteyksien kautta backendin kanssa ja päivittää käyttöliittymän vastaanotettujen tapahtumien perusteella.

---

## Arkkitehtuuri

Frontend käyttää monorepo-arkkitehtuuria, jossa kaksi erillistä Next.js-sovellusta jakavat yhteisen `core`-moduulin.

Projektin pääasiallinen rakenne:

```text
/

└── packages/

    ├── admin/
    │
    ├── core/
    │
    └── marketplace/
```

`admin` ja `marketplace` ovat itsenäisiä frontend-sovelluksia.

`core` sisältää sovellusten välillä jaettavan koodin ja yhteiset komponentit.

Yhteisen koodin keskittäminen `core`-moduuliin vähentää päällekkäistä toteutusta ja mahdollistaa samojen komponenttien, tyyppien ja muiden frontend-toiminnallisuuksien käyttämisen molemmissa sovelluksissa.

---

## Sovellukset

Hakemisto `/packages/` sisältää frontend-sovellukset ja niiden yhteisen koodin.

### Marketplace

WorkSwapin pääasiallinen käyttäjille tarkoitettu frontend-sovellus.

Marketplace vastaa alustan varsinaisesta käyttöliittymästä ja käyttäjien päivittäisestä vuorovaikutuksesta palvelun kanssa.

Sovellus sisältää muun muassa:

* ilmoitusten selaamisen ja hakemisen;
* ilmoitusten luomisen ja muokkaamisen;
* käyttäjäprofiilien käsittelyn;
* chat-toiminnallisuuden;
* ilmoitusten näyttämisen;
* tilauksiin liittyvät käyttöliittymät;
* arvosteluihin liittyvät käyttöliittymät;
* foorumin käyttöliittymän;
* muut WorkSwap-alustan käyttäjille tarkoitetut toiminnot.

Marketplace käyttää `core`-moduulissa määriteltyjä yhteisiä komponentteja, tyyppejä ja toiminnallisuuksia.

### Admin

WorkSwapin hallintakäyttöliittymä.

Admin on erillinen Next.js-sovellus, joka on tarkoitettu alustan hallintaan ja ylläpitoon.

Sovellus käyttää samaa `core`-moduulia kuin Marketplace, mutta sisältää hallintakäyttöliittymälle tarkoitetut näkymät ja toiminnallisuudet.

### Core

`core` on frontend-projektin yhteinen moduuli.

Se sisältää koodia, jota voidaan käyttää sekä `admin`- että `marketplace`-sovelluksissa.

Moduuliin voidaan sijoittaa esimerkiksi:

* yhteiset React-komponentit;
* TypeScript-tyypit ja rajapinnat;
* käyttöliittymän yhteiset komponentit;
* Sass-tyylit ja mixinit;
* API:n kanssa kommunikointiin liittyvät yhteiset toiminnallisuudet;
* yhteiset apufunktiot;
* muut sovellusten välillä jaettavat frontend-toiminnot.

`core` ei ole itsenäinen sovellus, vaan toimii yhteisenä frontend-kirjastona monorepon sisällä.

---

## TypeScript ja tyypitys

Koko frontend on toteutettu TypeScriptillä.

Sovelluksen rajapinnat, tietomallit, komponenttien propsit sekä backendin kanssa tapahtuva tiedonsiirto on tyypitetty.

Tyypityksen tavoitteena on pitää frontendin ja backendin väliset rajapinnat selkeinä sekä havaita mahdolliset yhteensopivuusongelmat jo kehitysvaiheessa.

Yhteisiä tyyppejä ja muita frontendin jaettavia rakenteita voidaan sijoittaa `core`-moduuliin, jolloin niitä voidaan käyttää molemmissa sovelluksissa.

---

## Käyttöliittymän rakenne

Käyttöliittymä on rakennettu uudelleenkäytettävistä React-komponenteista.

Komponenttien tyylit on pääasiassa kapseloitu Sass Modules -rakenteella. Jokainen komponentti voi siten sisältää omat tyylinsä ilman, että sen CSS-luokat muodostavat tarpeettomia riippuvuuksia muiden komponenttien kanssa.

Sassia käytetään muun muassa:

* sisäkkäiseen tyylirakenteeseen;
* mixineihin;
* yhteisiin muuttujaihin;
* komponenttikohtaisiin tyyleihin;
* tyylien uudelleenkäytettävyyteen.

Radix UI -kirjastoa käytetään saavutettavien ja uudelleenkäytettävien käyttöliittymäkomponenttien pohjana.

Käyttöliittymässä hyödynnetään myös `react-swipeable`-kirjastoa kosketus- ja pyyhkäisyeleiden käsittelyyn.

---

## Kommunikointi backendin kanssa

Frontend kommunikoi WorkSwapin backendin kanssa HTTP- ja WebSocket-yhteyksien kautta.

HTTP-yhteyksiä käytetään tavallisten API-pyyntöjen käsittelyyn, kuten tietojen hakemiseen, luomiseen, muokkaamiseen ja poistamiseen.

WebSocket-yhteyksiä käytetään tilanteissa, joissa käyttöliittymän on saatava tietoa reaaliaikaisesti ilman erillisiä toistuvia HTTP-pyyntöjä.

Frontendin ja backendin väliset rajapinnat on toteutettu siten, että frontendin käyttämät tietorakenteet ja API-kutsut vastaavat backendin tarjoamia rajapintoja.

---

## Monorepo-rakenne

Projekti käyttää monorepo-rakennetta, jossa kaksi sovellusta jaetaan samaan projektiin yhteisen koodimoduulin kanssa.

```text
packages/
├── admin/
├── core/
└── marketplace/
```

`admin` ja `marketplace` voivat kehittyä ja toimia erillisinä sovelluksina, mutta ne voivat hyödyntää samoja `core`-moduulin toteutuksia.

Tämä rakenne mahdollistaa:

* yhteisten komponenttien uudelleenkäytön;
* TypeScript-tyyppien keskitetyn hallinnan;
* päällekkäisen koodin vähentämisen;
* käyttöliittymälogiikan jakamisen sovellusten välillä;
* sovellusten itsenäisen kehittämisen;
* yhtenäisen käyttöliittymärakenteen ylläpitämisen.

---

## Projektin tila

Projekti on aktiivisessa kehityksessä.

Frontend kehittyy yhdessä WorkSwapin backendin ja muiden järjestelmän osien kanssa. Uusia toiminnallisuuksia, käyttöliittymäkomponentteja ja parannuksia lisätään projektin kehittymisen myötä.
