# File Searcher Application

Aplikasi desktop yang dirancang untuk membantu pengguna mencari kata kunci atau kalimat tertentu di dalam berbagai jenis file dalam sebuah folder yang dipilih. Aplikasi ini dibangun menggunakan JavaFX untuk antarmuka pengguna grafis.

## Deskripsi Singkat

Aplikasi ini memungkinkan pengguna untuk:
1.  Memilih sebuah folder di sistem mereka.
2.  Memasukkan kata kunci tunggal atau sebuah kalimat untuk dicari.
3.  Memilih mode pencarian (per kata atau per kalimat).
4.  Menampilkan hasil pencarian secara terstruktur, menunjukkan nama file, path lengkap, konteks (baris/paragraf/sel), dan isi dari konteks tersebut dengan kata kunci/kalimat yang dicari ditandai (menggunakan format `**teks**` untuk menandakan penebalan).

## Fitur Utama

* **Pemilihan Folder Dinamis:** Pengguna dapat dengan mudah memilih folder target melalui dialog pemilihan direktori.
* **Input Kata Kunci Fleksibel:** Mendukung pencarian untuk kata tunggal maupun frasa/kalimat.
* **Mode Pencarian:** Opsi untuk mencari sebagai "Kata" atau "Kalimat".
* **Dukungan Berbagai Jenis File:**
    * File Teks Biasa (`.txt`)
    * Dokumen Microsoft Word (`.docx`)
    * Spreadsheet Microsoft Excel (`.xlsx`)
    * File berbasis teks lainnya (seperti `.csv`, `.json`, `.xml`, `.log`, `.html`, `.java`, `.py`) melalui handler generik.
* **Tampilan Hasil Terstruktur:** Hasil pencarian ditampilkan dalam format kartu yang informatif, mencakup:
    * Nama File
    * Path File Lengkap
    * Konteks Lokasi (misalnya, Baris ke-X, Paragraf ke-Y, atau Sheet/Baris/Kolom untuk Excel)
    * Isi dari baris/paragraf/sel tempat kata kunci ditemukan, dengan kata kunci/kalimat yang dicari ditandai `**seperti ini**`.
* **Antarmuka Pengguna Grafis (GUI):** Dibangun dengan JavaFX, memberikan pengalaman pengguna yang interaktif.
* **Pencarian Asinkron:** Proses pencarian dijalankan di thread terpisah untuk menjaga UI tetap responsif.
* **Notifikasi dan Pesan Status:** Memberikan feedback kepada pengguna selama proses pencarian (misalnya, "Mencari...", "Pencarian selesai.", pesan error).
* **Window Aplikasi Terpusat:** Window akan muncul di tengah layar dan kembali ke tengah setelah di-minimize dan di-restore.

## Tangkapan Layar (Screenshots)

## Teknologi yang Digunakan

* **Java Development Kit (JDK):** Versi 11 atau lebih baru (disarankan JDK 21 berdasarkan log error sebelumnya).
* **JavaFX:** Untuk membangun antarmuka pengguna grafis.
* **Apache POI:** Library untuk membaca konten dari file Microsoft Word (`.docx`) dan Excel (`.xlsx`).
* **Gradle:** Sebagai build tool untuk manajemen dependensi dan proses build proyek (berdasarkan log error sebelumnya).

## Prasyarat

* **JDK (Java Development Kit)** versi 11 atau lebih tinggi terinstal.
* (Opsional, jika menjalankan dari source) **Gradle** terinstal atau menggunakan Gradle Wrapper (`gradlew`) yang ada di proyek.

## Cara Menjalankan Proyek (Gradle)

1.  **Clone atau Unduh Proyek:**
    Pastikan Anda memiliki semua file proyek di komputer Anda.

2.  **Buka Terminal atau Command Prompt:**
    Navigasikan ke direktori root proyek Anda (folder yang berisi file `build.gradle`).

3.  **Jalankan Aplikasi:**
    Gunakan perintah berikut. Disarankan menggunakan `--no-configuration-cache` jika Anda mengalami masalah terkait cache Gradle:
    ```bash
    ./gradlew run --no-configuration-cache
    ```
    Atau jika Anda tidak menggunakan wrapper:
    ```bash
    gradle run --no-configuration-cache
    ```
    Perintah ini akan mengompilasi dan menjalankan aplikasi JavaFX.

## Cara Menggunakan Aplikasi

1.  **Jalankan Aplikasi:** Ikuti langkah-langkah pada bagian "Cara Menjalankan Proyek".
2.  **Pilih Folder:** Klik tombol "**📁 Pilih Folder**" untuk membuka dialog pemilihan direktori. Pilih folder yang berisi file-file yang ingin Anda cari. Path folder yang dipilih akan ditampilkan.
3.  **Pilih Tipe Pencarian:**
    * Pilih "**Kata**" jika Anda ingin mencari satu kata spesifik.
    * Pilih "**Kalimat**" jika Anda ingin mencari frasa atau kalimat lengkap.
4.  **Masukkan Kata Kunci:** Ketikkan kata atau kalimat yang ingin Anda cari pada field "**Kata Kunci**".
5.  **Mulai Pencarian:** Klik tombol "**🔍 Mulai Pencarian**".
6.  **Lihat Hasil:** Hasil pencarian akan ditampilkan di area "**Hasil pencarian:**". Setiap temuan akan disajikan dalam format kartu yang menampilkan nama file, path, lokasi (baris/paragraf/sel), dan konten dengan kata kunci yang ditandai.
    * Pesan status seperti "Mencari..." dan "Pencarian selesai." juga akan muncul di area hasil.

## Handler File yang Didukung

Aplikasi ini memiliki handler spesifik untuk beberapa tipe file dan handler generik untuk file berbasis teks lainnya:

* **`TextFileHandler`:** Untuk file `.txt`. Menampilkan baris tempat kata kunci ditemukan.
* **`WordFileHandler`:** Untuk file `.docx`. Menampilkan paragraf atau konteks sel tabel tempat kata kunci ditemukan.
* **`ExcelFileHandler`:** Untuk file `.xlsx`. Menampilkan isi sel (jika berupa string) tempat kata kunci ditemukan, beserta informasi Sheet, Baris, dan Kolom.
* **`TextBasedFileHandler`:** Untuk file dengan ekstensi `.csv`, `.json`, `.xml`, `.log`, `.html`, `.java`, `.py`. Bekerja mirip dengan `TextFileHandler` dan menampilkan baris tempat kata kunci ditemukan.
    *(Catatan: Pastikan handler ini sudah diaktifkan dan dikonfigurasi untuk output ke UI di `MainApp.java` jika diinginkan).*

## Kontributor

* Mahesa Putri Lukman - H071241009
* Akhmad Hidayat - H071241003

---