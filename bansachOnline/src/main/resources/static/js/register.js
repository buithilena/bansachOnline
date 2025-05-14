
    $(document).ready(function () {
    $('#registerForm').submit(function (e) {
        e.preventDefault();

        // Lấy dữ liệu từ form
        const fullName = $('#fullName').val().trim();
        const phoneNumber = $('#registerPhone').val().trim();
        const address = $('#address').val().trim();
        const email = $('#email').val().trim();
        const birthDate = $('#birthDate').val();
        const password = $('#registerPassword').val();
        const confirmPassword = $('#confirmPassword').val();

        // Kiểm tra client-side
        if (!fullName) {
            Toastify({
                text: "Họ tên không được để trống!",
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#dc3545",
                stopOnFocus: true
            }).showToast();
            return;
        }
        if (!phoneNumber || !phoneNumber.match(/^0[0-9]{9}$/)) {
            Toastify({
                text: "Số điện thoại không hợp lệ!",
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#dc3545",
                stopOnFocus: true
            }).showToast();
            return;
        }
        if (!email || !email.match(/^[A-Za-z0-9+_.-]+@(.+)$/)) {
            Toastify({
                text: "Email không hợp lệ!",
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#dc3545",
                stopOnFocus: true
            }).showToast();
            return;
        }
        if (!birthDate) {
            Toastify({
                text: "Ngày sinh không được để trống!",
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#dc3545",
                stopOnFocus: true
            }).showToast();
            return;
        }
        if (!password) {
            Toastify({
                text: "Mật khẩu không được để trống!",
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#dc3545",
                stopOnFocus: true
            }).showToast();
            return;
        }
        if (password !== confirmPassword) {
            Toastify({
                text: "Mật khẩu xác nhận không khớp!",
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#dc3545",
                stopOnFocus: true
            }).showToast();
            return;
        }

        // Gửi AJAX request
        $.ajax({
            url: '/register',
            type: 'POST',
            contentType: 'application/json',
            headers: {
                [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
            },
            data: JSON.stringify({
                fullName: fullName,
                phone: phoneNumber,
                address: address,
                email: email,
                birthDate: birthDate,
                password: password,
                confirmPassword: confirmPassword
            }),
            success: function (response) {
                console.log("Register Response:", response);
                if (response.token) {
                    // Lưu token và thông tin người dùng
                    localStorage.setItem('jwtToken', response.token);
                    localStorage.setItem('userName', response.name);

                    // Hiển thị thông báo thành công
                    Toastify({
                        text: "Đăng ký thành công!",
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        backgroundColor: "#28a745",
                        stopOnFocus: true
                    }).showToast();

                    // Đóng modal và reset form
                    $('#loginModal').modal('hide');
                    $('#registerForm')[0].reset();

                    // Chuyển hướng sau 3 giây để người dùng thấy thông báo
                    setTimeout(function () {
                        window.location.href = "/";
                    }, 3000);
                }
            },
            error: function (xhr) {
                console.error("Register Error:", xhr);
                let errorMsg = xhr.responseJSON?.error || "Đã xảy ra lỗi khi đăng ký!";
                Toastify({
                    text: errorMsg,
                    duration: 3000,
                    gravity: "top",
                    position: "right",
                    backgroundColor: "#dc3545",
                    stopOnFocus: true
                }).showToast();
            }
        });
    });
});