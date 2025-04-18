// publishers.js

$(document).ready(function () {
    // Xử lý sự kiện click nút Lưu trong modal Thêm Nhà Xuất Bản
    $('#savePublisher').click(function () {
        const publisherData = {
            tenNhaXuatBan: $('#publisher_name').val()
        };

        if (!publisherData.tenNhaXuatBan) {
            $('#publisher_name').addClass('is-invalid');
            return;
        }

        $.ajax({
            url: '/api/quanly/publishers',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(publisherData),
            success: function (response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công!',
                    text: 'Nhà xuất bản đã được thêm.',
                    showConfirmButton: false,
                    timer: 1500
                });
                $('#addPublisherModal').modal('hide');
                $('#addPublisherForm')[0].reset();
                loadPublishers();
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi!',
                    text: 'Không thể thêm nhà xuất bản. Vui lòng thử lại.',
                });
            }
        });
    });

    // Xử lý sự kiện click nút Lưu thay đổi trong modal Sửa Nhà Xuất Bản
    $('#saveEditPublisher').click(function () {
        const publisherData = {
            id: $('#edit_publisher_id').val(),
            tenNhaXuatBan: $('#edit_publisher_name').val()
        };

        if (!publisherData.tenNhaXuatBan) {
            $('#edit_publisher_name').addClass('is-invalid');
            return;
        }

        $.ajax({
            url: `/api/quanly/publishers/${publisherData.id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(publisherData),
            success: function (response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công!',
                    text: 'Thông tin nhà xuất bản đã được cập nhật.',
                    showConfirmButton: false,
                    timer: 1500
                });
                $('#editPublisherModal').modal('hide');
                loadPublishers();
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi!',
                    text: 'Không thể cập nhật nhà xuất bản. Vui lòng thử lại.',
                });
            }
        });
    });

    // Xử lý tìm kiếm nhà xuất bản
    $('#search-publishers').on('input', function () {
        const searchTerm = $(this).val().toLowerCase();
        loadPublishers(searchTerm);
    });

    // Hàm tải danh sách nhà xuất bản
    function loadPublishers(searchTerm = '') {
        $.ajax({
            url: '/api/quanly/publishers',
            method: 'GET',
            success: function (publishers) {
                $('#publishers-tbody').empty();
                publishers.forEach(publisher => {
                    if (!searchTerm || publisher.tenNhaXuatBan.toLowerCase().includes(searchTerm)) {
                        $('#publishers-tbody').append(`
                            <tr>
                                <td>${publisher.id}</td>
                                <td>${publisher.tenNhaXuatBan}</td>
                                <td>
                                    <button class="btn btn-warning btn-sm edit-publisher" data-id="${publisher.id}">Sửa</button>
                                    <button class="btn btn-danger btn-sm delete-publisher" data-id="${publisher.id}">Xóa</button>
                                </td>
                            </tr>
                        `);
                    }
                });

                // Xử lý sự kiện click nút Sửa
                $('.edit-publisher').click(function () {
                    const id = $(this).data('id');
                    $.ajax({
                        url: `/api/quanly/publishers/${id}`,
                        method: 'GET',
                        success: function (publisher) {
                            $('#edit_publisher_id').val(publisher.id);
                            $('#edit_publisher_name').val(publisher.tenNhaXuatBan);
                            $('#editPublisherModal').modal('show');
                        }
                    });
                });

                // Xử lý sự kiện click nút Xóa
                $('.delete-publisher').click(function () {
                    const id = $(this).data('id');
                    Swal.fire({
                        title: 'Bạn có chắc?',
                        text: 'Nhà xuất bản này sẽ bị xóa vĩnh viễn!',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Xóa',
                        cancelButtonText: 'Hủy'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            $.ajax({
                                url: `/api/quanly/publishers/${id}`,
                                method: 'DELETE',
                                success: function () {
                                    Swal.fire({
                                        icon: 'success',
                                        title: 'Đã xóa!',
                                        text: 'Nhà xuất bản đã được xóa.',
                                        showConfirmButton: false,
                                        timer: 1500
                                    });
                                    loadPublishers();
                                },
                                error: function () {
                                    Swal.fire({
                                        icon: 'error',
                                        title: 'Lỗi!',
                                        text: 'Không thể xóa nhà xuất bản.',
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
                    text: 'Không thể tải danh sách nhà xuất bản.',
                });
            }
        });
    }

    // Tải danh sách nhà xuất bản khi trang được load
    loadPublishers();
});