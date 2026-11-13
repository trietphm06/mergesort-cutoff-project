# Thí nghiệm CUTOFF cho MergeSort

Project tìm giá trị CUTOFF (ngưỡng chuyển sang Insertion Sort cho đoạn mảng nhỏ) cho MergeSort cho thời gian chạy ngắn nhất trên máy MacBook Air Apple M1 (Java 25.0.2). Báo cáo đầy đủ nằm trong `report.md`.

## Cấu trúc thư mục

```
src/
  MergeSort.java        MergeSort top-down, một mảng phụ aux, tham số cutoff
  InsertionSort.java    Insertion Sort trên đoạn a[lo..hi]
  TestMergeSort.java    kiểm thử tính đúng đắn (mảng rỗng, 1 phần tử, trùng, âm, ...)
  Main.java             chương trình đo thời gian
scripts/
  generate_data.py      sinh bộ dữ liệu thí nghiệm (seed cố định)
  analyze_results.py    thống kê, kiểm định, vẽ đồ thị
data/                   bộ dữ liệu: random, sorted, reverse, nearly_sorted × 5 kích thước
results/
  benchmark.csv         kết quả đo thô (10.800 lượt đo)
  summary.csv           bảng thống kê theo (pattern, N, cutoff)
  best_cutoff.md        cutoff tốt nhất cho từng (pattern, N), so với cutoff 7 và 10
  plots/                đồ thị
report.md               báo cáo thí nghiệm
requirements.txt        thư viện Python
```

## Yêu cầu

- Java JDK 17 trở lên (bản đã dùng: 25.0.2)
- Python 3.10 trở lên (bản đã dùng: 3.14.0)

## Cách chạy lại thí nghiệm

Chạy từ thư mục gốc của project:

```bash
pip3 install -r requirements.txt       # matplotlib, scipy, pandas
python3 scripts/generate_data.py       # sinh dữ liệu vào data/
mkdir -p out
javac -d out src/*.java                # biên dịch
java -cp out TestMergeSort             # kiểm thử, phải in "All tests passed!"
java -cp out Main data results/benchmark.csv
python3 scripts/analyze_results.py results/benchmark.csv
```

`Main` nhận thêm hai tham số tùy chọn là số vòng khởi động và số vòng đo (mặc định 5 và 30). Ví dụ chạy thử nhanh: `java -cp out Main data results/benchmark.csv 2 3`.

Nên cắm sạc, đóng các ứng dụng nặng và không dùng máy trong lúc chạy.

## Thiết kế thí nghiệm (tóm tắt)

- 4 dạng dữ liệu (random, sorted, reverse, nearly_sorted) × 5 kích thước (10k đến 500k) × 18 giá trị CUTOFF (1 đến 160).
- 5 vòng khởi động bỏ đi, 30 vòng đo; thứ tự CUTOFF xáo ngẫu nhiên mỗi vòng; việc sao chép mảng và kiểm tra đúng đắn nằm ngoài thời gian đo.
- Quy ước: đoạn có độ dài ≤ CUTOFF thì dùng Insertion Sort; CUTOFF = 1 là MergeSort thuần.
- Thống kê: mean, median, std, khoảng tin cậy 95%, Welch t-test so với CUTOFF 7 và 10.

## Kết quả chính (dữ liệu random, chi tiết trong `report.md`)

Vùng CUTOFF tốt nhất là 40–64; CUTOFF = 50 chênh dưới 1% so với mức tốt nhất ở cả năm kích thước. CUTOFF 7 và 10 của giáo trình chậm hơn khoảng 8–20% trên máy này. Kết quả chỉ áp dụng cho cấu hình máy, JVM và cách cài đặt đã dùng.

## Ghi chú về việc dùng AI
AI trợ giúp: Claude, ChatGPT:
Script chạy thí nghiệm, phân tích, soát báo cáo.