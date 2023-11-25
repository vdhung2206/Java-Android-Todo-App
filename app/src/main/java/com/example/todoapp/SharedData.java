package com.example.todoapp;

public class SharedData {
    private static SharedData instance;

    // Khai báo các biến hoặc dữ liệu bạn muốn chia sẻ
    private String data;

    // Phương thức private constructor để ngăn việc tạo đối tượng từ bên ngoài lớp
    private SharedData() {
        // Khởi tạo các biến hoặc thực hiện công việc ban đầu ở đây
        data = "Dữ liệu mẫu từ Singleton";
    }

    // Phương thức public để truy cập phiên bản duy nhất của Singleton
    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    // Getter và setter cho dữ liệu bạn muốn lưu trữ và chia sẻ
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
