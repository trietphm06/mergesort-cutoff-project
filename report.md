# BÁO CÁO THÍ NGHIỆM: TÌM CUTOFF TỐI ƯU CHO MERGESORT

## 1. Mục tiêu và câu hỏi nghiên cứu

**Câu hỏi nghiên cứu:** Với thuật toán MergeSort tự cài đặt trên MacBook M1, giá trị CUTOFF nào giúp thời gian chạy thấp nhất?

**Giả thuyết ban đầu:**

Giá trị CUTOFF nằm trong khoảng 10–20 có thể mang lại hiệu năng tốt. Lý do là Insertion Sort thường có chi phí thấp hơn MergeSort khi xử lý các mảng nhỏ. Tuy nhiên, nếu CUTOFF quá lớn, độ phức tạp O(n²) của Insertion Sort có thể làm tăng thời gian chạy.

**Phạm vi kết luận:**

Thí nghiệm tập trung so sánh thời gian thực thi của các giá trị CUTOFF trên một máy MacBook M1, với nhiều kích thước và dạng dữ liệu đầu vào. Kết quả chỉ phản ánh cấu hình phần cứng, phần mềm và cách cài đặt được sử dụng trong thí nghiệm, không khẳng định một giá trị tối ưu cho mọi hệ thống.

## 2. Cấu hình phần cứng và phần mềm

| Thành phần | Thông tin |
|---|---|
| Máy / model | MacBook Air Apple M1 |
| CPU / chip | Apple M1 |
| RAM | 8 GB |
| Hệ điều hành | macOS 15.7.3 |
| Java JDK | 25.0.2 |
| Python | 3.14.0 |
| Phiên bản code / commit | Bản code thực nghiệm MergeSort tự cài đặt, chạy ngày 11/11/2026 |

## 3. Thuật toán và thiết kế CUTOFF

Thí nghiệm sử dụng thuật toán MergeSort top-down kết hợp với Insertion Sort để tối ưu việc sắp xếp các đoạn mảng nhỏ.

- **Cách chia mảng:** Sử dụng chỉ số `mid` để chia mảng thành hai nửa, sau đó tiếp tục đệ quy cho đến khi đạt điều kiện dừng.
- **Cách merge:** Hai nửa đã được sắp xếp được trộn lại thành một đoạn có thứ tự. Khi hai phần tử bằng nhau, thuật toán ưu tiên phần tử ở nửa trái bằng phép so sánh `<=`, do đó vẫn bảo đảm tính ổn định (stable).
- **Mảng phụ:** Một mảng phụ `aux` được cấp phát duy nhất ở hàm bên ngoài và tái sử dụng trong quá trình merge.
- **Insertion Sort cho đoạn nhỏ:** Khi độ dài đoạn mảng nhỏ hơn hoặc bằng CUTOFF, thuật toán sử dụng Insertion Sort thay vì tiếp tục chia đệ quy.
- **Định nghĩa CUTOFF:** Là ngưỡng kích thước đoạn mảng mà tại đó thuật toán chuyển từ MergeSort sang Insertion Sort. Với CUTOFF = 1, thuật toán chính là MergeSort thuần túy (đoạn một phần tử vốn đã có thứ tự).
- **Tối ưu bỏ qua merge:** Không sử dụng tối ưu bỏ qua bước merge khi hai nửa đã có thứ tự.

Dữ liệu được biểu diễn bằng kiểu `int[]`.

## 4. Thiết kế dữ liệu

Thí nghiệm sử dụng bốn dạng dữ liệu đầu vào. Dữ liệu được sinh bằng `scripts/generate_data.py` với seed gốc 20261111; với mỗi kích thước N, các dạng dữ liệu có yếu tố ngẫu nhiên dùng seed = 20261111 + N nên có thể sinh lại giống hệt.

| Pattern | Mô tả | Kích thước N | Seed |
|---|---|---|---|
| random | Số nguyên sinh ngẫu nhiên trong khoảng từ −10.000.000 đến 10.000.000 | 10.000; 50.000; 100.000; 250.000; 500.000 | 20261111 + N |
| sorted | Mảng đã được sắp xếp tăng dần (0, 1, ..., N−1) | 10.000; 50.000; 100.000; 250.000; 500.000 | không dùng (xác định) |
| reverse | Mảng sắp xếp theo thứ tự giảm dần (N, N−1, ..., 1) | 10.000; 50.000; 100.000; 250.000; 500.000 | không dùng (xác định) |
| nearly_sorted | Mảng sắp tăng dần, sau đó hoán đổi ngẫu nhiên khoảng 1% số cặp phần tử | 10.000; 50.000; 100.000; 250.000; 500.000 | 20261111 + N |

Các giá trị CUTOFF được thử nghiệm:

1, 3, 5, 7, 8, 10, 12, 15, 20, 25, 30, 40, 50, 64, 80, 100, 128, 160.

Việc lựa chọn nhiều dạng dữ liệu giúp đánh giá ảnh hưởng của đặc điểm đầu vào đến hiệu năng thuật toán. Cùng một giá trị CUTOFF có thể mang lại kết quả khác nhau trên dữ liệu ngẫu nhiên, đã sắp xếp hoặc đảo ngược.

Với 4 dạng dữ liệu, 5 kích thước và 18 giá trị CUTOFF, thí nghiệm có tổng cộng 360 cấu hình thử nghiệm.

## 5. Quy trình đo

Quy trình đo được thực hiện như sau:

- **Giá trị CUTOFF:** 18 giá trị, từ 1 đến 160.
- **Số lần warm-up:** 5 vòng khởi động đầu tiên cho mỗi bộ dữ liệu; kết quả warm-up không được tính vào kết quả chính thức.
- **Số lần đo chính thức:** 30 lần cho mỗi cấu hình.
- **Đơn vị đo:** Millisecond (ms).
- **Vị trí đo thời gian:** Chỉ đo thời gian thực thi thuật toán sắp xếp, không tính thời gian sao chép dữ liệu và kiểm tra tính đúng đắn của kết quả.
- **Dữ liệu đầu vào:** Mỗi lần chạy sử dụng bản sao của cùng một mảng đầu vào để bảo đảm tính công bằng giữa các giá trị CUTOFF.
- **Kiểm tra kết quả:** Sau mỗi lần chạy, kiểm tra mảng đầu ra đã được sắp xếp đúng thứ tự.
- **Giảm sai lệch môi trường:** Thứ tự chạy các giá trị CUTOFF được xáo trộn ngẫu nhiên ở mỗi vòng đo nhằm hạn chế ảnh hưởng có hệ thống từ JIT compilation và nhiệt độ máy.

Kết quả được tổng hợp bằng các chỉ số:

- **Mean:** Thời gian chạy trung bình.
- **Median:** Thời gian chạy trung vị.
- **Standard Deviation (Std):** Độ lệch chuẩn, thể hiện mức độ biến động của thời gian chạy.
- **Min:** Thời gian chạy nhỏ nhất.
- **95% CI:** Khoảng tin cậy 95% của giá trị trung bình.

Để đánh giá ý nghĩa thống kê, chương trình `scripts/analyze_results.py` so sánh CUTOFF có median thấp nhất với CUTOFF = 7 và CUTOFF = 10 bằng kiểm định Welch t-test (hai mẫu độc lập, không giả định phương sai bằng nhau), với mức ý nghĩa 0,05.

Tổng cộng có 360 × 30 = 10.800 lượt đo chính thức.

## 6. Kết quả

### 6.1. Kết quả trên dữ liệu random

Dữ liệu random được sử dụng làm nhóm chính để đánh giá hiệu năng tổng quát của thuật toán trên dữ liệu không có thứ tự đặc biệt.

| N | CUTOFF tốt nhất | Median tốt nhất (ms) | CUTOFF = 7 chậm hơn | CUTOFF = 10 chậm hơn |
|---|---|---|---|---|
| 10.000 | 64 | 0,4259 | 19,7% | 10,9% |
| 50.000 | 64 | 2,8107 | 12,8% | 12,9% |
| 100.000 | 64 | 5,9995 | 11,7% | 11,8% |
| 250.000 | 50 | 16,6083 | 12,5% | 8,4% |
| 500.000 | 40 | 35,3439 | 11,3% | 8,1% |

Đáng chú ý, CUTOFF = 50 cho kết quả rất gần với giá trị tốt nhất quan sát được ở cả năm kích thước N. Mức chênh lệch so với giá trị tốt nhất ở mỗi kích thước đều dưới 1%.

Các biểu đồ tương ứng:

- `results/plots/random_n10000.png`
- `results/plots/random_n50000.png`
- `results/plots/random_n100000.png`
- `results/plots/random_n250000.png`
- `results/plots/random_n500000.png`
- `results/plots/relative_random.png`

### 6.2. So sánh trên sorted, reverse và nearly_sorted

Bảng dưới đây thể hiện giá trị CUTOFF có median thấp nhất quan sát được ở mỗi kích thước.

| Pattern | N = 10k | N = 50k | N = 100k | N = 250k | N = 500k |
|---|---|---|---|---|---|
| sorted | 160 | 160 | 128 | 160 | 160 |
| reverse | 10 | 7 | 10 | 12 | 10 |
| nearly_sorted | 160 | 100 | 128 | 128 | 128 |

Kết quả cho thấy hiệu năng phụ thuộc đáng kể vào dạng dữ liệu đầu vào.

- Với sorted và nearly_sorted, các giá trị CUTOFF lớn thường cho kết quả tốt hơn.
- Với reverse, các giá trị CUTOFF nhỏ, khoảng 7–12, thường mang lại hiệu năng tốt hơn.
- Với random, vùng CUTOFF từ 40–64 cho kết quả tốt nhất.

**Lưu ý:** Với sorted và nearly_sorted, giá trị tốt nhất ở một số trường hợp nằm tại giới hạn trên của phạm vi thử nghiệm. Vì vậy, chưa thể khẳng định đây là giá trị tối ưu toàn cục.

### 6.3. So sánh CUTOFF = 7 và CUTOFF = 10

Trên dữ liệu random, cả CUTOFF = 7 và CUTOFF = 10 đều cho thời gian chạy cao hơn đáng kể so với vùng tối ưu quan sát được.

- CUTOFF = 7 chậm hơn khoảng 11–20%.
- CUTOFF = 10 chậm hơn khoảng 8–13%.

Kết quả kiểm định Welch t-test (mục 5) cho thấy sự khác biệt này có ý nghĩa thống kê với p < 0,001 ở cả năm kích thước N trên dữ liệu random.

Ở kích thước N = 500.000, MergeSort thuần túy (CUTOFF = 1) có median khoảng 43,50 ms, trong khi CUTOFF = 50 có median khoảng 35,42 ms. Như vậy, việc kết hợp Insertion Sort giúp giảm thời gian chạy khoảng 18,6% so với MergeSort thuần túy trong trường hợp này.

## 7. Thảo luận

**Thứ nhất,** vùng CUTOFF tối ưu trên dữ liệu random nằm trong khoảng 40–64. Mặc dù giá trị tốt nhất thay đổi theo kích thước N, CUTOFF = 50 luôn cho kết quả rất gần với giá trị tốt nhất quan sát được, với mức chênh lệch dưới 1%. Vì vậy, đây là một lựa chọn cân bằng tốt cho dữ liệu random trên cấu hình máy được thử nghiệm.

**Thứ hai,** kết quả khác biệt so với giả thuyết ban đầu. Giả thuyết ban đầu cho rằng vùng 10–20 có thể tối ưu, nhưng kết quả thực nghiệm cho thấy vùng 40–64 hoạt động tốt hơn trên dữ liệu random. Cụ thể, CUTOFF = 7 chậm hơn khoảng 11–20%, còn CUTOFF = 10 chậm hơn khoảng 8–13%. Điều này cho thấy việc kết hợp Insertion Sort có thể tiếp tục mang lại lợi ích ở những đoạn mảng lớn hơn dự kiến ban đầu.

**Thứ ba,** đồ thị trên dữ liệu random có vùng đáy khá phẳng, sau đó thời gian chạy tăng lên theo từng bước khi CUTOFF vượt khoảng 80–128 (tùy N). Ở N = 500.000, thời gian gần như không đổi trong khoảng CUTOFF 40–100 (median từ 35,34 đến 35,54 ms), rồi tăng khoảng 7% khi CUTOFF chuyển từ 100 sang 128 (lên 38,17 ms). Hiện tượng tương tự xuất hiện ở N = 250.000 (bước nhảy giữa 100 và 128, khoảng 8%) và ở N = 50.000, 100.000 (bước nhảy giữa 80 và 100, khoảng 5–6%). Một giả thuyết có thể giải thích hiện tượng này là kích thước các đoạn mảng ở những tầng đệ quy khác nhau, xấp xỉ N/2ᵏ. Chẳng hạn với N = 500.000, các đoạn lá có kích thước khoảng 122 và 61: khi CUTOFF nằm trong khoảng 61–121 thuật toán dừng ở đoạn ~61, còn khi CUTOFF ≥ 122 thì dừng sớm hơn ở đoạn ~122, khiến Insertion Sort phải xử lý đoạn dài gần gấp đôi. Vị trí các bước nhảy phù hợp với giả thuyết này ở các kích thước N đã thử, tuy nhiên giả thuyết này cần được kiểm chứng thêm bằng thí nghiệm riêng, chưa thể xem là nguyên nhân đã được xác nhận.

**Thứ tư,** dữ liệu đầu vào ảnh hưởng mạnh đến CUTOFF phù hợp. Với dữ liệu reverse, Insertion Sort phải thực hiện nhiều dịch chuyển phần tử hơn, khiến các giá trị CUTOFF lớn trở nên bất lợi do chi phí O(n²). Ở N = 500.000, CUTOFF = 160 chậm hơn khoảng 2,5 lần so với CUTOFF = 10. Ngược lại, với dữ liệu sorted và nearly_sorted, Insertion Sort có thể hoạt động gần tuyến tính nên CUTOFF lớn thường có lợi. Điều này cho thấy CUTOFF = 50 phù hợp với random nhưng không phải lựa chọn tối ưu cho mọi dạng dữ liệu. Nếu cần một lựa chọn thận trọng hơn cho nhiều loại đầu vào, vùng 7–10 có thể là phương án đáng cân nhắc.

## 8. Hạn chế của thí nghiệm

Thí nghiệm vẫn có một số hạn chế:

- Chỉ được thực hiện trên một máy MacBook M1, vì vậy chưa thể khái quát kết quả cho các dòng CPU khác.
- Chỉ sử dụng một phiên bản JVM, trong khi hiệu năng có thể thay đổi theo phiên bản Java và cơ chế tối ưu của JIT compiler.
- Thuật toán sử dụng `int[]`, do đó kết quả có thể khác nếu sử dụng `Comparable[]` (như trong bài giảng) hoặc các cấu trúc dữ liệu khác.
- Các giá trị CUTOFF trong vùng 40–100 chưa được kiểm tra với mật độ đủ dày để xác định chính xác điểm tối ưu.
- Median được sử dụng làm chỉ số chính để hạn chế ảnh hưởng của các lần đo bất thường, nhưng điều này cũng có nghĩa là các biến động lớn trong quá trình chạy không được phản ánh đầy đủ qua một con số duy nhất.
- Với dữ liệu sorted và nearly_sorted, một số giá trị tốt nhất nằm ở giới hạn trên của phạm vi thử nghiệm. Cần mở rộng phạm vi CUTOFF để đánh giá thêm.

## 9. Kết luận

Qua thí nghiệm, vùng CUTOFF = 40–64 cho hiệu năng tốt nhất trên dữ liệu random. Trong đó, CUTOFF = 50 là lựa chọn cân bằng vì kết quả gần với mức tốt nhất ở cả năm kích thước dữ liệu, với mức chênh lệch dưới 1%.

Tuy nhiên, không tồn tại một giá trị CUTOFF tối ưu cho mọi trường hợp. Giá trị phù hợp phụ thuộc vào kích thước dữ liệu, dạng dữ liệu đầu vào, phần cứng, JVM và cách cài đặt thuật toán.

Trong tương lai, có thể mở rộng thí nghiệm trên nhiều cấu hình máy khác nhau, thử thêm các giá trị CUTOFF trong vùng 40–100 và đánh giá thêm các kiểu dữ liệu đầu vào.

## 10. Tái lập thí nghiệm

Các lệnh sau được thực hiện từ thư mục gốc của project:

**Bước 1. Cài thư viện Python**

```bash
pip3 install -r requirements.txt
```

**Bước 2. Tạo dữ liệu đầu vào**

```bash
python3 scripts/generate_data.py
```

**Bước 3. Tạo thư mục chứa file biên dịch**

```bash
mkdir -p out
```

**Bước 4. Biên dịch chương trình Java**

```bash
javac -d out src/*.java
```

**Bước 5. Chạy benchmark**

```bash
java -cp out Main data results/benchmark.csv
```

**Bước 6. Phân tích kết quả và tạo biểu đồ**

```bash
python3 scripts/analyze_results.py results/benchmark.csv
```

Sau khi hoàn thành, kết quả benchmark được lưu trong `results/benchmark.csv`, bảng thống kê trong `results/summary.csv` và `results/best_cutoff.md`, còn các biểu đồ được lưu trong `results/plots/`.
