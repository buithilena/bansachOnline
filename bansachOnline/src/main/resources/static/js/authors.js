// authors.js

$(document).ready(function () {
    // Xử lý sự kiện click nút Lưu trong modal Thêm Tác Giả
    $('#saveAuthor').click(function () {
        const authorData = {
            tenTacGia: $('#author_name').val(),
            ngaySinh: $('#author_birthday').val() || null,
            quocTich: $('#author_nationality').val() || null
        };

        if (!authorData.tenTacGia) {
            $('#author_name').addClass('is-invalid');
            return;
        }

        $.ajax({
            url: '/api/quanly/authors',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(authorData),
            success: function (response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công!',
                    text: 'Tác giả đã được thêm.',
                    showConfirmButton: false,
                    timer: 1500
                });
                $('#addAuthorModal').modal('hide');
                $('#addAuthorForm')[0].reset();
                loadAuthors();
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi!',
                    text: 'Không thể thêm tác giả. Vui lòng thử lại.',
                });
            }
        });
    });

    // Xử lý sự kiện click nút Lưu thay đổi trong modal Sửa Tác Giả
    $('#saveEditAuthor').click(function () {
        const authorData = {
            id: $('#edit_author_id').val(),
            tenTacGia: $('#edit_author_name').val(),
            ngaySinh: $('#edit_author_birthday').val() || null,
            quocTich: $('#edit_author_nationality').val() || null
        };

        if (!authorData.tenTacGia) {
            $('#edit_author_name').addClass('is-invalid');
            return;
        }

        $.ajax({
            url: `/api/quanly/authors/${authorData.id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(authorData),
            success: function (response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công!',
                    text: 'Thông tin tác giả đã được cập nhật.',
                    showConfirmButton: false,
                    timer: 1500
                });
                $('#editAuthorModal').modal('hide');
                loadAuthors();
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi!',
                    text: 'Không thể cập nhật tác giả. Vui lòng thử lại.',
                });
            }
        });
    });

    // Xử lý tìm kiếm tác giả
    $('#search-authors').on('input', function () {
        const searchTerm = $(this).val().toLowerCase();
        loadAuthors(searchTerm);
    });

    // Hàm tải danh sách tác giả
    function loadAuthors(searchTerm = '') {
        $.ajax({
            url: '/api/quanly/authors',
            method: 'GET',
            success: function (authors) {
                $('#authors-tbody').empty();
                authors.forEach(author => {
                    if (!searchTerm || author.tenTacGia.toLowerCase().includes(searchTerm)) {
                        $('#authors-tbody').append(`
                            <tr>
                                <td>${author.id}</td>
                                <td>${author.tenTacGia}</td>
                                <td>${author.ngaySinh || ''}</td>
                                <td>${author.quocTich || ''}</td>
                                <td>
                                    <button class="btn btn-warning btn-sm edit-author" data-id="${author.id}">Sửa</button>
                                    <button class="btn btn-danger btn-sm delete-author" data-id="${author.id}">Xóa</button>
                                </td>
                            </tr>
                        `);
                    }
                });

                // Xử lý sự kiện click nút Sửa
                $('.edit-author').click(function () {
                    const id = $(this).data('id');
                    $.ajax({
                        url: `/api/quanly/authors/${id}`,
                        method: 'GET',
                        success: function (author) {
                            $('#edit_author_id').val(author.id);
                            $('#edit_author_name').val(author.tenTacGia);
                            $('#edit_author_birthday').val(author.ngaySinh || '');
                            $('#edit_author_nationality').val(author.quocTich || '');
                            $('#editAuthorModal').modal('show');
                        }
                    });
                });

                // Xử lý sự kiện click nút Xóa
                $('.delete-author').click(function () {
                    const id = $(this).data('id');
                    Swal.fire({
                        title: 'Bạn có chắc?',
                        text: 'Tác giả này sẽ bị xóa vĩnh viễn!',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Xóa',
                        cancelButtonText: 'Hủy'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            $.ajax({
                                url: `/api/quanly/authors/${id}`,
                                method: 'DELETE',
                                success: function () {
                                    Swal.fire({
                                        icon: 'success',
                                        title: 'Đã xóa!',
                                        text: 'Tác giả đã được xóa.',
                                        showConfirmButton: false,
                                        timer: 1500
                                    });
                                    loadAuthors();
                                },
                                error: function () {
                                    Swal.fire({
                                        icon: 'error',
                                        title: 'Lỗi!',
                                        text: 'Không thể xóa tác giả.',
                                    });
                                }
                            });
                        }
                    });
                });
            },
            error: function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi!',
                    text: 'Không thể tải danh sách tác giả.',
                });
            }
        });
    }

    // Tải danh sách tác giả khi trang được load
    loadAuthors();
});