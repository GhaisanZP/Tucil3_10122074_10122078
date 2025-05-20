# 🚗 Rush Hour Puzzle Solver

Program ini merupakan solver untuk permainan puzzle **Rush Hour**, yang ditulis dalam bahasa **Java**. Program akan membaca konfigurasi awal puzzle dari sebuah file teks, lalu menyelesaikannya menggunakan algoritma pencarian (seperti A*, Greedy, atau Branch and Bound) yang diimplementasikan pada kelas `Solver`.

---

## ⚙️ Requirements

- Java JDK versi 11 atau lebih baru
- Terminal / Command Prompt
- File input puzzle dengan format teks

---

## 🛠️ Cara Kompilasi

Jalankan perintah berikut dari direktori root (yang memuat folder `src/`):

```bash
javac -d bin src/RushHour.java src/model/*.java src/solver/*.java src/util/*.java
````

File hasil kompilasi akan disimpan dalam folder `bin/`.

---

## ▶️ Cara Menjalankan Program

Setelah proses kompilasi berhasil, kamu bisa menjalankan program dengan perintah:

```bash
java -cp bin RushHour path/to/input.txt
```

Contoh:

```bash
java -cp bin RushHour test/input1.txt
```

Program akan menampilkan langkah-langkah penyelesaian puzzle dari kondisi awal ke kondisi goal.

---

## 📄 Format File Input

File input harus berformat sebagai berikut:

```
6 6
X 2 0 H 2
A 0 0 V 3
B 3 1 V 2
...
```

Penjelasan:

* Baris pertama: ukuran papan (baris kolom), misalnya `6 6` berarti papan 6x6.
* Baris selanjutnya: daftar kendaraan dalam format:

  ```
  ID row col orientasi panjang
  ```

  * `ID`        = huruf kapital (contoh: X, A, B, ...)
  * `row col`   = posisi awal (baris, kolom) kendaraan
  * `orientasi` = `H` (horizontal) atau `V` (vertikal)
  * `panjang`   = panjang kendaraan dalam satuan kotak

Mobil `X` adalah mobil merah yang harus dikeluarkan ke sisi kanan papan.

---

## 👨‍💻 Author

* Muhammad Zakkiy (10122074)
* Ghaisan Zaki Pratama (10122078)
