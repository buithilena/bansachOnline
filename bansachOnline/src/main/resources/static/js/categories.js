$(document).ready(function() {
    let currentPage = 1;
    const pageSize = 10;

    // Load danh sách danh mục
    function loadCategories(page = 1, search = '') {
        $.ajax({
            url: '/api/quanly/categories', // Sử dụng đúng endpoint từ QuanlyController
            method: 'GET',
            data: { page, pageSize, search },
            success: function(response) {
                $('#categories-tbody').empty();
                // Kiểm tra nếu response là mảng (API trả về List<DanhMuc>)
                response.forEach(category => {
                    $('#categories-tbody').append(`
                        <tr>
                            <td>${category.id}</td>
                            <td>${category.tenDanhMuc}</td>
                            <td>${category.ghiChu || ''}</td>
                            <td>
                                <button class="btn btn-warning btn-sm edit-category" data-id="${category.id}"><i class="fas fa-edit"></i></button>
                                <button class="btn btn-danger btn-sm delete-category" data-id="${category.id}"><i class="fas fa-trash"></i></button>
                            </td>
                        </tr>
                    `);
                });
                // Tính tổng số trang dựa trên response (cần điều chỉnh nếu API trả về totalPages)
                const totalItems = response.length; // Giả sử API trả về số lượng items
                const totalPages = Math.ceil(totalItems / pageSize);
                renderPagination(totalPages, page);
            },
            error: function() {
                Swal.fire('Lỗi!', 'Không thể tải danh sách danh mục.', 'error');
            }
        });
    }

    // Render phân trang
    function renderPagination(totalPages, currentPage) {
        $('#categories-pagination').empty();
        for (let i = 1; i <= totalPages; i++) {
            $('#categories-pagination').append(`
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }
    }

    // Thêm danh mục
    $('#saveCategory').click(function() {
        const tenDanhMuc = $('#category_name').val();
        const ghiChu = $('#category_note').val();

        if (!tenDanhMuc) {
            $('#category_name').addClass('is-invalid');
            return;
        }

        $.ajax({
            url: '/api/quanly/categories',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ tenDanhMuc, ghiChu }),
            success: function() {
                Swal.fire('Thành công!', 'Danh mục đã được thêm.', 'success');
                $('#addCategoryModal').modal('hide');
                $('#addCategoryForm')[0].reset();
                loadCategories(currentPage);
            },
            error: function() {
                Swal.fire('Lỗi!', 'Không thể thêm danh mục.', 'error');
            }
        });
    });

    // Sửa danh mục
    $(document).on('click', '.edit-category', function() {
        const id = $(this).data('id');
        $.ajax({
            url: `/api/quanly/categories/${id}`,
            method: 'GET',
            success: function(category) {
                $('#edit_category_id').val(category.id);
                $('#edit_category_name').val(category.tenDanhMuc);
                $('#edit_category_note').val(category.ghiChu);
                $('#editCategoryModal').modal('show');
            },
            error: function() {
                Swal.fire('Lỗi!', 'Không thể tải thông tin danh mục.', 'error');
            }
        });
    });

    $('#saveEditCategory').click(function() {
        const id = $('#edit_category_id').val();
        const tenDanhMuc = $('#edit_category_name').val();
        const ghiChu = $('#edit_category_note').val();

        if (!tenDanhMuc) {
            $('#edit_category_name').addClass('is-invalid');
            return;
        }

        $.ajax({
            url: `/api/quanly/categories/${id}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ tenDanhMuc, ghiChu }),
            success: function() {
                Swal.fire('Thành công!', 'Danh mục đã được cập nhật.', 'success');
                $('#editCategoryModal').modal('hide');
                loadCategories(currentPage);
            },
            error: function() {
                Swal.fire('Lỗi!', 'Không thể cập nhật danh mục.', 'error');
            }
        });
    });

    // Xóa danh mục
    $(document).on('click', '.delete-category', function() {
        const id = $(this).data('id');
        Swal.fire({
            title: 'Bạn có chắc?',
            text: 'Bạn muốn xóa danh mục này?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Xóa',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/api/quanly/categories/${id}`,
                    method: 'DELETE',
                    success: function() {
                        Swal.fire('Thành công!', 'Danh mục đã được xóa.', 'success');
                        loadCategories(currentPage);
                    },
                    error: function() {
                        Swal.fire('Lỗi!', 'Không thể xóa danh mục.', 'error');
                    }
                });
            }
        });
    });

    // Tìm kiếm danh mục
    $('#search-categories').on('input', function() {
        const search = $(this).val();
        currentPage = 1;
        loadCategories(currentPage, search);
    });

    // Chuyển trang
    $(document).on('click', '.page-link', function(e) {
        e.preventDefault();
        currentPage = $(this).data('page');
        loadCategories(currentPage, $('#search-categories').val());
    });

    // Xóa thông báo lỗi khi người dùng nhập lại
    $('#category_name, #edit_category_name').on('input', function() {
        $(this).removeClass('is-invalid');
    });

    // Load danh mục khi khởi động
    loadCategories();
});