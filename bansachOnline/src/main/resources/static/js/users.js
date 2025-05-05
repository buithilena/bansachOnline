
    $(document).ready(function () {
        $('#loginForm').submit(function (e) {
            e.preventDefault();
            const phoneNumber = $('#loginPhone').val();
            const password = $('#loginPassword').val();
            $.ajax({
                url: '/api/login',
                type: 'POST',
                contentType: 'application/json',
                headers: {
                    [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
                },
                data: JSON.stringify({phoneNumber, password}),
                success: function (response) {
                    console.log("Login Response:", response);
                    if (response.token) {
                        localStorage.setItem('jwtToken', response.token);
                        localStorage.setItem('userName', response.name); // Lưu tên
                        Toastify({
                            text: "Đăng nhập thành công!",
                            duration: 3000,
                            gravity: "top",
                            position: "right",
                            backgroundColor: "#28a745",
                            stopOnFocus: true
                        }).showToast();
                        if (response.role === "ADMIN") {
                            window.location.href = "/quanly";
                        } else {
                            window.location.href = "/";
                        }
                    }
                },
                error: function (xhr) {
                    console.error("Login Error:", xhr);
                    let errorMsg = xhr.responseJSON?.error || "Đã xảy ra lỗi khi đăng nhập!";
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


$(document).ready(function () {
    loadUsers();

    // Tải danh sách người dùng
    function loadUsers(page = 1, search = '') {
        $.ajax({
            url: `/api/quanly/users?page=${page}&size=10&search=${search}`,
            method: 'GET',
            success: function (response) {
                console.log('Phản hồi API:', response); // Ghi log để kiểm tra
                if (response && response.data && Array.isArray(response.data)) {
                    renderUsers(response.data);
                    Utils.renderPagination(response.totalPages, page, 'users'); // Sửa dòng này
                } else {
                    console.error('Phản hồi không đúng định dạng:', response);
                    Swal.fire('Lỗi', 'Dữ liệu trả về không đúng định dạng!', 'error');
                }
            },
            error: function (xhr) {
                console.error('Lỗi API:', xhr); // Ghi log lỗi
                Swal.fire('Lỗi', xhr.responseText || 'Không thể tải danh sách người dùng!', 'error');
            }
        });
    }

    // Hiển thị danh sách người dùng
    function renderUsers(users) {
        const tbody = $('#users-tbody');
        tbody.empty();
        if (users.length === 0) {
            tbody.append('<tr><td colspan="7" class="text-center">Không có người dùng nào</td></tr>');
            return;
        }
        users.forEach(user => {
            tbody.append(`
    <tr>
        <td>${user.id}</td>
        <td>${user.name || ''}</td>
        <td>${user.email || ''}</td>
        <td>${user.phoneNumber || ''}</td>
        <td>${user.address || ''}</td>
        <td>${user.role || ''}</td>
        <td>
            <button class="btn btn-sm btn-warning edit-user" data-id="${user.id}">Sửa</button>
            <button class="btn btn-sm btn-danger delete-user" data-id="${user.id}">Xóa</button>
        </td>
    </tr>
`);
        });
    }

    // Thêm người dùng
    $('#saveUser').click(function () {
        const user = {
            name: $('#user_name').val(),
            email: $('#user_email').val(),
            phoneNumber: $('#user_phone').val(),
            address: $('#user_address').val(),
            dateOfBirth: $('#user_birthday').val(),
            role: $('#user_role').val(),
            password: $('#user_password').val()
        };

        $.ajax({
            url: '/api/quanly/users',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(user),
            success: function () {
                $('#addUserModal').modal('hide');
                Swal.fire('Thành công', 'Thêm người dùng thành công!', 'success');
                loadUsers();
            },
            error: function (xhr) {
                Swal.fire('Lỗi', xhr.responseText || 'Không thể thêm người dùng!', 'error');
            }
        });
    });

    // Sửa người dùng
    $(document).on('click', '.edit-user', function () {
        const id = $(this).data('id');
        $.ajax({
            url: `/api/quanly/users/${id}`,
            method: 'GET',
            success: function (user) {
                $('#edit_user_id').val(user.id);
                $('#edit_user_name').val(user.name);
                $('#edit_user_email').val(user.email);
                $('#edit_user_phone').val(user.phoneNumber);
                $('#edit_user_address').val(user.address);
                $('#edit_user_birthday').val(user.dateOfBirth);
                $('#edit_user_role').val(user.role);
                $('#edit_user_password').val('');
                $('#editUserModal').modal('show');
            },
            error: function () {
                Swal.fire('Lỗi', 'Không thể tải thông tin người dùng!', 'error');
            }
        });
    });

    // Lưu chỉnh sửa người dùng
    $('#saveEditUser').click(function () {
        const id = $('#edit_user_id').val();
        const user = {
            name: $('#edit_user_name').val(),
            email: $('#edit_user_email').val(),
            phoneNumber: $('#edit_user_phone').val(),
            address: $('#edit_user_address').val(),
            dateOfBirth: $('#edit_user_birthday').val(),
            role: $('#edit_user_role').val(),
            password: $('#edit_user_password').val()
        };

        $.ajax({
            url: `/api/quanly/users/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(user),
            success: function () {
                $('#editUserModal').modal('hide');
                Swal.fire('Thành công', 'Cập nhật người dùng thành công!', 'success');
                loadUsers();
            },
            error: function (xhr) {
                Swal.fire('Lỗi', xhr.responseText || 'Không thể cập nhật người dùng!', 'error');
            }
        });
    });

    // Xóa người dùng
    $(document).on('click', '.delete-user', function () {
        const id = $(this).data('id');
        Swal.fire({
            title: 'Bạn có chắc?',
            text: 'Bạn muốn xóa người dùng này?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Xóa',
            cancelButtonText: 'Hủy'
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/api/quanly/users/${id}`,
                    method: 'DELETE',
                    success: function () {
                        Swal.fire('Thành công', 'Xóa người dùng thành công!', 'success');
                        loadUsers();
                    },
                    error: function () {
                        Swal.fire('Lỗi', 'Không thể xóa người dùng!', 'error');
                    }
                });
            }
        });
    });


    // Tìm kiếm người dùng
    $('#search-users').on('input', function () {
        loadUsers(1, $(this).val());
    });

    // Xử lý sự kiện click cho phân trang
    $(document).on('click', '#users-pagination .page-link', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        const search = $('#search-users').val();
        if (page) {
            loadUsers(page, search);
        }
    });
});