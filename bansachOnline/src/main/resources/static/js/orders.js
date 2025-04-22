$(document).ready(function () {
    const apiUrl = '/api/quanly/orders';
    let currentPage = 1;
    let pageSize = 10;
    let searchQuery = '';

    // Load danh sách đơn hàng
    function loadOrders() {
        $('#orders-tbody').html('<tr><td colspan="6" class="text-center">Đang tải...</td></tr>'); // Loading indicator
        $.ajax({
            url: `${apiUrl}?page=${currentPage}&size=${pageSize}&search=${encodeURIComponent(searchQuery)}`,
            method: 'GET',
            success: function (response) {
                console.log('API response:', response); // Debug response
                try {
                    if (response && response.data && Array.isArray(response.data)) {
                        renderOrders(response.data);
                        renderPagination(response.totalPages || 1, response.currentPage || 1);
                    } else {
                        console.error('Invalid response format:', response);
                        renderOrders([]); // Hiển thị thông báo không có dữ liệu
                        renderPagination(1, 1);
                        Swal.fire('Cảnh báo', 'Dữ liệu trả về không đúng định dạng', 'warning');
                    }
                } catch (error) {
                    console.error('Error processing response:', error);
                    Swal.fire('Lỗi', 'Lỗi khi xử lý dữ liệu đơn hàng', 'error');
                }
            },
            error: function (xhr, status, error) {
                console.error('AJAX error:', xhr.responseText, status, error);
                $('#orders-tbody').html('<tr><td colspan="6" class="text-center">Lỗi khi tải dữ liệu</td></tr>');
                Swal.fire('Lỗi', `Không thể tải danh sách đơn hàng: ${xhr.responseText || 'Lỗi không xác định'}`, 'error');
            }
        });
    }

    // Render danh sách đơn hàng
    function renderOrders(orders) {
        console.log('Orders received:', orders); // Debug dữ liệu
        const tbody = $('#orders-tbody');
        tbody.empty();

        if (!orders || !Array.isArray(orders) || orders.length === 0) {
            tbody.append('<tr><td colspan="6" class="text-center">Không có đơn hàng nào</td></tr>');
            return;
        }

        orders.forEach(order => {
            console.log('Processing order:', order); // Debug từng đơn hàng
            try {
                const row = `
                    <tr>
                        <td>${order.id || 'N/A'}</td>
                        <td>${order.user && order.user.name ? order.user.name : 'N/A'}</td>
                        <td>${order.orderDate ? new Date(order.orderDate).toLocaleDateString('vi-VN') : 'N/A'}</td>
                        <td>${typeof order.totalPrice === 'number' ? order.totalPrice.toLocaleString('vi-VN') : Number(order.totalPrice || 0).toLocaleString('vi-VN')} VNĐ</td>
                        <td>${order.status ? order.status.charAt(0).toUpperCase() + order.status.slice(1).toLowerCase() : 'N/A'}</td>
                        <td>
                            <button class="btn btn-warning btn-sm edit-order" data-id="${order.id || ''}">Sửa</button>
                            <button class="btn btn-danger btn-sm delete-order" data-id="${order.id || ''}">Xóa</button>
                        </td>
                    </tr>`;
                tbody.append(row);
            } catch (error) {
                console.error('Error rendering order:', order, error);
            }
        });
    }

    // Render phân trang
    function renderPagination(totalPages, currentPage) {
        const pagination = $('#orders-pagination');
        pagination.empty();

        if (totalPages <= 1) return;

        // Nút Previous
        pagination.append(`
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>
            </li>
        `);

        // Các trang
        for (let i = 1; i <= totalPages; i++) {
            const active = i === currentPage ? 'active' : '';
            pagination.append(`
                <li class="page-item ${active}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }

        // Nút Next
        pagination.append(`
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>
            </li>
        `);
    }

    // Xử lý tìm kiếm
    $('#search-orders').on('input', function () {
        searchQuery = $(this).val().trim();
        currentPage = 1;
        loadOrders();
    });

    // Xử lý phân trang
    $(document).on('click', '.page-link', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page && page > 0) {
            currentPage = page;
            loadOrders();
        }
    });

    // Xử lý xóa đơn hàng
    $(document).on('click', '.delete-order', function () {
        const id = $(this).data('id');
        if (!id) {
            Swal.fire('Lỗi', 'ID đơn hàng không hợp lệ', 'error');
            return;
        }
        Swal.fire({
            title: 'Xác nhận xóa?',
            text: 'Bạn có chắc muốn xóa đơn hàng này?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Xóa',
            cancelButtonText: 'Hủy'
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `${apiUrl}/${id}`,
                    method: 'DELETE',
                    success: function () {
                        Swal.fire('Thành công', 'Đơn hàng đã được xóa', 'success');
                        loadOrders();
                    },
                    error: function (xhr) {
                        console.error('Delete error:', xhr.responseText);
                        Swal.fire('Lỗi', `Không thể xóa đơn hàng: ${xhr.responseText || 'Lỗi không xác định'}`, 'error');
                    }
                });
            }
        });
    });

    // Xử lý sửa đơn hàng
    $(document).on('click', '.edit-order', function () {
        const id = $(this).data('id');
        if (!id) {
            Swal.fire('Lỗi', 'ID đơn hàng không hợp lệ', 'error');
            return;
        }
        $.ajax({
            url: `${apiUrl}/${id}`,
            method: 'GET',
            success: function (order) {
                if (order && order.id) {
                    $('#edit_order_id').val(order.id);
                    $('#edit_order_status').val(order.status || 'PENDING');
                    $('#editOrderModal').modal('show');
                } else {
                    Swal.fire('Lỗi', 'Dữ liệu đơn hàng không hợp lệ', 'error');
                }
            },
            error: function (xhr) {
                console.error('Fetch order error:', xhr.responseText);
                Swal.fire('Lỗi', `Không thể tải thông tin đơn hàng: ${xhr.responseText || 'Lỗi không xác định'}`, 'error');
            }
        });
    });

    // Lưu thay đổi đơn hàng
    $('#saveEditOrder').click(function () {
        const id = $('#edit_order_id').val();
        const status = $('#edit_order_status').val();

        if (!id || !status) {
            Swal.fire('Lỗi', 'Vui lòng điền đầy đủ thông tin', 'error');
            return;
        }

        $.ajax({
            url: `${apiUrl}/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ status: status }),
            success: function () {
                $('#editOrderModal').modal('hide');
                Swal.fire('Thành công', 'Đơn hàng đã được cập nhật', 'success');
                loadOrders();
            },
            error: function (xhr) {
                console.error('Update error:', xhr.responseText);
                Swal.fire('Lỗi', `Không thể cập nhật đơn hàng: ${xhr.responseText || 'Lỗi không xác định'}`, 'error');
            }
        });
    });

    // Load danh sách đơn hàng khi vào trang
    loadOrders();
});