# Auto nomas sistēma

## Sistēmas vispārīgs apraksts

---

Saite uz dokumentāciju:
https://docs.google.com/document/d/1BlUpxJKtoH2i8ZXjIvbwXyrtgDmS6EcQ_e8E6iXfgdU/edit?usp=sharing

---

Auto nomas sistēma ir Android lietotne, kas nodrošina iznomāšans opcijas priekš nomātājiem. Sistēma ļauj lietotājiem pārlūkot pieejamās automašīnas, veikt rezervācijas un pārvaldīt savas nomas darbības. Administratoriem ir papildu funkcionalitātes, lai pārvaldītu auto, skatītu visas rezervācijas un veiktu lietotāju  un rezervāciju pārvaldību.

## Vajadzību un prasību apraksts

### Lietotāju vajadzības

1. **Pārlūkot pieejamās automašīnas**: Lietotājiem ir jāspēj redzēt pieejamo automašīnu sarakstu, ieskaitot informāciju par marku, modeli, gadu, degvielas veidu, pārnesumkārbas tipu un cenu dienā.
2. **Veikt rezervācijas**: Lietotājiem ir jābūt iespējai veikt rezervācijas pieejamām automašīnām.
3. **Pārvaldīt rezervācijas**: Lietotājiem ir jābūt iespējai skatīt savas pašreizējās un pagātnes rezervācijas un atgriezt iznomātās automašīnas.
4. **Profila pārvaldība**: Lietotājiem ir jābūt iespējai atjaunināt savu e-pastu un paroli.

### Administratoru vajadzības

1. **Pārvaldīt automašīnas**: Administratoriem ir jābūt iespējai pievienot, rediģēt un dzēst automašīnas.
2. **Skatīt rezervācijas**: Administratoriem ir jābūt iespējai redzēt visas aktīvās un beigušās rezervācijas pārvaldības un atskaišu vajadzībām.
3. **Pārvaldīt lietotājus**: Administratoriem ir jābūt iespējai pārvaldīt lietotāju kontus, tostarp piešķirt administratora tiesības un atbloķēt/dzēst lietotājus.

### Sistēmas prasības

- **Autentifikācija**: Lietotāju autentifikācija, izmantojot Firebase Authentication.
- **Datu bāze**: Firebase Firestore, lai saglabātu automašīnu datus, lietotāju datus un rezervāciju ierakstus.
- **UI/UX**:  Intuitīva un draudzīgs interfeiss un navigācija.
- **Paziņojumi**: Toast ziņojumi, lai sniegtu atgriezenisko saiti lietotājiem par viņu darbībām.

---
>**Administrātora konta pieejas dati:**
>
>Ē-pasts: admin@gmail.com
>
>Parole: parole1234


---

Šo projektu izstrādāja: Kārlis Z. un Reinis B. no LU
