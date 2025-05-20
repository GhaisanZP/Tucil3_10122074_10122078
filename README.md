🚗 Rush Hour Puzzle Solver
Program ini adalah solver untuk puzzle Rush Hour yang ditulis dalam bahasa Java. Program ini membaca konfigurasi awal puzzle dari file teks, kemudian menyelesaikan puzzle menggunakan algoritma pencarian seperti A*, Greedy, atau Branch and Bound, tergantung implementasi dalam Solver.java.

📋 Fitur
Parsing konfigurasi puzzle dari file.

Mencetak langkah-langkah solusi menuju goal state.

Struktur kode modular: model, solver, dan util.

⚙️ Requirement
Java JDK 11 atau lebih baru

Terminal / Command Prompt

File konfigurasi puzzle (.txt) dengan format yang sesuai

🛠️ Cara Kompilasi
Jalankan perintah berikut dari root directory project (direktori yang berisi folder src/):

bash
Copy
Edit
javac -d bin src/RushHour.java src/model/*.java src/solver/*.java src/util/*.java
File hasil kompilasi akan disimpan di dalam folder bin/.

▶️ Cara Menjalankan Program
Setelah kompilasi berhasil, jalankan program menggunakan perintah:

bash
Copy
Edit
java -cp bin RushHour path/to/input.txt
Contoh:

bash
Copy
Edit
java -cp bin RushHour test/input1.txt
Format File Input
Pastikan file input mengikuti format tertentu seperti:

css
Copy
Edit
6 6
X 2 0 H 2
A 0 0 V 3
B 3 1 V 2
...
Keterangan:

Baris pertama menyatakan ukuran papan (baris x kolom)

Baris selanjutnya menyatakan mobil-mobil: ID row col orientasi panjang

👨‍💻 Author
* Muhammad Zakkiy (10122074)
* Ghaisan Zaki Pratama (10122078)
