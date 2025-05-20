# 🚗 Rush Hour Puzzle Solver

Program ini merupakan solver untuk permainan puzzle **Rush Hour**, yang ditulis dalam bahasa **Java**. Program akan membaca konfigurasi awal puzzle dari sebuah file teks, lalu menyelesaikannya menggunakan algoritma pencarian (seperti A*, Greedy, atau Branch and Bound) yang diimplementasikan pada kelas `Solver`.

---

## ⚙️ Requirements

- Java JDK versi 11 atau lebih baru
- Terminal / Command Prompt
- File input puzzle dengan format `.txt`

---

## 🛠️ Cara Kompilasi

Jalankan perintah berikut dari direktori root (yang memuat folder `src/`):

```bash
javac -d bin src/model/*.java src/util/*.java src/solver/*java src/RushHour.java src/RushHourGUI.java
````

File hasil kompilasi akan disimpan dalam folder `bin/`.

---

## ▶️ Cara Menjalankan Program

Setelah proses kompilasi berhasil, kamu bisa menjalankan program dengan perintah:

```bash
java -cp bin RushHour path/to/input.txt algorithm heuristic
```

Contoh:

```bash
java -cp bin RushHour src/test1.txt astar combined
```

Program akan menampilkan langkah-langkah penyelesaian puzzle dari kondisi awal ke kondisi goal. Langkah-langkah yang dihasilkan akan dimasukkan ke dalam folder test dengan nama file `solusi_nama-file-input.txt`.

Untuk menjalankan GUI, kamu bisa menjalankan perintah berikut:

```bash
java -cp bin RushHourGUI
```


---

## 📄 Format File Input

Konfigurasi permainan/test case dalam format ekstensi `.txt`. File test
case tersebut berisi:
1. Dimensi Papan terdiri atas dua buah variabel **A** dan **B** yang membentuk
papan berdimensi AxB.
2. Banyak piece BUKAN primary piece direpresentasikan oleh variabel
integer **N**.
3. Konfigurasi papan yang mencakup penempatan piece dan primary piece,
serta lokasi pintu keluar. Primary Piece dilambangkan dengan huruf **P** dan
pintu keluar dilambangkan dengan huruf **K**. Piece dilambangkan dengan
huruf dan karakter selain **P** dan **K**, dan huruf/karakter berbeda
melambangkan piece yang berbeda. Cell kosong dilambangkan dengan
karakter `.` (titik).

File `.txt` yang akan dibaca memiliki format sebagai berikut:

```
A B
N
konfigurasi_papan
```
Contoh input test case:
```
6 6
12
AAB..F
..BCDF
GPPCDFK
GH.III
GHJ...
LLJMM.
```

---

## 👨‍💻 Author

* Muhammad Zakkiy (10122074)
* Ghaisan Zaki Pratama (10122078)
