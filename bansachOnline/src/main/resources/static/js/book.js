$(document).ready(function () {
    let currentPage = 1;
    const pageSize = 10;
    let imageInputs = 0; // Biến đếm số lượng input ảnh

    // Hàm lấy danh sách sách
    function loadBooks(page = 1) {
        $.ajax({
            url: `/api/quanly/books?page=${page}&size=${pageSize}`,
            method: 'GET',
            success: function (response) {
                $('#products-tbody').empty();
                response.forEach(book => {
                    const mainImage = book.bookImages.find(img => img.isMain) || { imageUrl: '/images/placeholder.png' };
                    const donViGia = book.donViGias && book.donViGias.length > 0 ? book.donViGias[0] : { gia: 0, discount: 0 };
                    const discountedPrice = donViGia.gia * (1 - (donViGia.discount || 0) / 100);
                    $('#products-tbody').append(`
                        <tr>
                            <td>${book.id}</td>
                            <td>${book.tenSach}</td>
                            <td><img src="${mainImage.imageUrl}" alt="${book.tenSach}" style="width: 50px;"></td>
                            <td>${book.tacGia ? book.tacGia.tenTacGia : 'N/A'}</td>
                            <td>${book.danhMuc ? book.danhMuc.tenDanhMuc : 'N/A'}</td>
                            <td>${book.nhaXuatBan ? book.nhaXuatBan.tenNhaXuatBan : 'N/A'}</td>
                            <td>${book.doiTuong ? book.doiTuong.tenDoiTuong : 'N/A'}</td>
                            <td>${discountedPrice.toLocaleString('vi-VN')} VNĐ</td>
                            <td>${book.soLuong}</td>
                            <td>
                                <button class="btn btn-sm btn-warning edit-book" data-id="${book.id}"><i class="fas fa-edit"></i> Sửa</button>
                                <button class="btn btn-sm btn-danger delete-book" data-id="${book.id}"><i class="fas fa-trash"></i> Xóa</button>
                            </td>
                        </tr>
                    `);
                });
                updatePagination(response.length, page);
                $('#total-products').text(response.length);
            },
            error: function () {
                Swal.fire('Lỗi', 'Không thể tải danh sách sách!', 'error');
            }
        });
    }

    // Hàm cập nhật phân trang
    function updatePagination(totalItems, currentPage) {
        const totalPages = Math.ceil(totalItems / pageSize);
        $('#products-pagination').empty();
        for (let i = 1; i <= totalPages; i++) {
            $('#products-pagination').append(`
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }
    }

    // Load danh sách tác giả, danh mục, nhà xuất bản, đối tượng
    function loadDropdowns(modalId = '') {
        // Tác giả
        $.ajax({
            url: '/api/quanly/authors',
            method: 'GET',
            success: function (authors) {
                $(`${modalId} #product_author, ${modalId} #edit_product_author`).empty().append('<option value="">Chọn tác giả</option>');
                authors.forEach(author => {
                    $(`${modalId} #product_author, ${modalId} #edit_product_author`).append(`<option value="${author.id}">${author.tenTacGia}</option>`);
                });
            },
            error: function () {
                Swal.fire('Lỗi', 'Không thể tải danh sách tác giả!', 'error');
            }
        });

        // Danh mục
        $.ajax({
            url: '/api/quanly/categories',
            method: 'GET',
            success: function (categories) {
                $(`${modalId} #product_category, ${modalId} #edit_product_category`).empty().append('<option value="">Chọn danh mục</option>');
                categories.forEach(category => {
                    $(`${modalId} #product_category, ${modalId} #edit_product_category`).append(`<option value="${category.id}">${category.tenDanhMuc}</option>`);
                });
            },
            error: function () {
                Swal.fire('Lỗi', 'Không thể tải danh sách danh mục!', 'error');
            }
        });

        // Nhà xuất bản
        $.ajax({
            url: '/api/quanly/publishers',
            method: 'GET',
            success: function (publishers) {
                $(`${modalId} #product_publisher, ${modalId} #edit_product_publisher`).empty().append('<option value="">Chọn nhà xuất bản</option>');
                publishers.forEach(publisher => {
                    $(`${modalId} #product_publisher, ${modalId} #edit_product_publisher`).append(`<option value="${publisher.id}">${publisher.tenNhaXuatBan}</option>`);
                });
            },
            error: function () {
                Swal.fire('Lỗi', 'Không thể tải danh sách nhà xuất bản!', 'error');
            }
        });

        // Đối tượng
        $.ajax({
            url: '/api/quanly/doituongs',
            method: 'GET',
            success: function (doituongs) {
                $(`${modalId} #product_target, ${modalId} #edit_product_target`).empty().append('<option value="">Chọn đối tượng</option>');
                doituongs.forEach(doituong => {
                    $(`${modalId} #product_target, ${modalId} #edit_product_target`).append(`<option value="${doituong.id}">${doituong.tenDoiTuong}</option>`);
                });
            },
            error: function () {
                Swal.fire('Lỗi', 'Không thể tải danh sách đối tượng!', 'error');
            }
        });
    }

    // Thêm ảnh vào bảng
    $('#add-image').on('click', function () {
        const imageUrl = $('#image-url').val();
        const isMain = $('#image-is-main').is(':checked');
        const imageOrder = parseInt($('#image-order').val()) || 0;

        if (!imageUrl) {
            Swal.fire('Lỗi', 'Vui lòng nhập URL ảnh!', 'error');
            return;
        }

        $('#image-table-body').append(`
            <tr data-id="${imageInputs}">
                <td>${imageUrl}</td>
                <td>${isMain ? 'Có' : 'Không'}</td>
                <td>${imageOrder}</td>
                <td><img src="${imageUrl}" alt="Preview" style="max-width: 100px;"></td>
                <td>
                    <button class="btn btn-sm btn-danger remove-table-image"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `);

        // Reset form
        $('#image-url').val('');
        $('#image-is-main').prop('checked', false);
        $('#image-order').val('');
        $('#image-preview').hide();
        imageInputs++;
    });

    // Xóa ảnh từ bảng
    $(document).on('click', '.remove-table-image', function () {
        $(this).closest('tr').remove();
    });

    // Xem trước ảnh khi nhập URL
    $('#image-url, #detail_image_url').on('input', function () {
        const url = $(this).val();
        const previewId = $(this).attr('id') === 'image-url' ? '#image-preview' : '#detail_image_preview';
        const previewImgId = $(this).attr('id') === 'image-url' ? '#preview-image' : '#detail_preview_image';
        if (url) {
            $(previewId).show();
            $(previewImgId).attr('src', url);
        } else {
            $(previewId).hide();
        }
    });

    // Thu thập danh sách ảnh từ bảng
    function collectImagesFromTable(tableId = '#image-table-body') {
        const images = [];
        $(tableId + ' tr').each(function () {
            const imageUrl = $(this).find('td:eq(0)').text();
            const isMain = $(this).find('td:eq(1)').text() === 'Có';
            const imageOrder = parseInt($(this).find('td:eq(2)').text()) || 0;
            images.push({
                imageUrl: imageUrl,
                isMain: isMain,
                imageOrder: imageOrder
            });
        });
        return images;
    }

    // Thêm sách
    $('#saveProduct').click(function () {
        if (!$('#addProductForm')[0].checkValidity()) {
            $('#addProductForm')[0].reportValidity();
            return;
        }

        const book = {
            tenSach: $('#product_name').val(),
            moTaNgan: $('#product_description').val(),
            namXuatBan: $('#product_year').val() ? parseInt($('#product_year').val()) : null,
            soTrang: $('#product_pages').val() ? parseInt($('#product_pages').val()) : null,
            soLuong: parseInt($('#product_quantity').val()),
            tacGia: $('#product_author').val() ? { id: parseInt($('#product_author').val()) } : null,
            danhMuc: $('#product_category').val() ? { id: parseInt($('#product_category').val()) } : null,
            nhaXuatBan: $('#product_publisher').val() ? { id: parseInt($('#product_publisher').val()) } : null,
            doiTuong: $('#product_target').val() ? { id: parseInt($('#product_target').val()) } : null
        };
        const donViGia = {
            donVi: 'Cuốn',
            gia: parseFloat($('#product_price').val()),
            discount: $('#product_discount').val() ? parseFloat($('#product_discount').val()) : 0
        };
        const images = collectImagesFromTable();

        if (images.length === 0) {
            Swal.fire('Lỗi', 'Vui lòng thêm ít nhất một ảnh sản phẩm!', 'error');
            return;
        }

        $.ajax({
            url: '/api/quanly/books',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ book, donViGia, images }),
            success: function () {
                $('#addProductModal').modal('hide');
                $('#addProductForm')[0].reset();
                $('#image-table-body').empty();
                loadBooks(currentPage);
                Swal.fire('Thành công', 'Thêm sách thành công!', 'success');
            },
            error: function (xhr) {
                Swal.fire('Lỗi', 'Không thể thêm sách: ' + (xhr.responseText || 'Lỗi không xác định'), 'error');
            }
        });
    });

    // Sửa sách
    $(document).on('click', '.edit-book', function () {
        const bookId = $(this).data('id');
        $.get(`/api/quanly/books/${bookId}`, function (book) {
            $('#edit_product_id').val(book.id);
            $('#edit_product_name').val(book.tenSach);
            $('#edit_product_description').val(book.moTaNgan);
            $('#edit_product_year').val(book.namXuatBan);
            $('#edit_product_pages').val(book.soTrang);
            $('#edit_product_quantity').val(book.soLuong);
            $('#edit_product_author').val(book.tacGia ? book.tacGia.id : '');
            $('#edit_product_category').val(book.danhMuc ? book.danhMuc.id : '');
            $('#edit_product_publisher').val(book.nhaXuatBan ? book.nhaXuatBan.id : '');
            $('#edit_product_target').val(book.doiTuong ? book.doiTuong.id : '');
            const donViGia = book.donViGias && book.donViGias.length > 0 ? book.donViGias[0] : { gia: 0, discount: 0 };
            $('#edit_product_price').val(donViGia.gia);
            $('#edit_product_discount').val(donViGia.discount);

            // Load danh sách ảnh
            $('#detail_image_table_body').empty();
            if (book.bookImages && book.bookImages.length > 0) {
                book.bookImages.forEach(img => {
                    $('#detail_image_table_body').append(`
                        <tr data-id="${img.id}">
                            <td>${img.imageUrl}</td>
                            <td>${img.isMain ? 'Có' : 'Không'}</td>
                            <td>${img.imageOrder}</td>
                            <td><img src="${img.imageUrl}" alt="Preview" style="max-width: 100px;"></td>
                            <td>
                                <button class="btn btn-sm btn-danger remove-table-image"><i class="fas fa-trash"></i></button>
                            </td>
                        </tr>
                    `);
                });
            }

            // Bật các trường nhập ảnh trong modal chỉnh sửa
            $('#detail_image_url, #detail_image_is_main, #detail_image_order, #add_detail_image').prop('disabled', false);
            loadDropdowns('#editProductModal');
            $('#editProductModal').modal('show');
        });
    });

    // Thêm ảnh vào bảng chỉnh sửa
    $('#add_detail_image').on('click', function () {
        const imageUrl = $('#detail_image_url').val();
        const isMain = $('#detail_image_is_main').is(':checked');
        const imageOrder = parseInt($('#detail_image_order').val()) || 0;

        if (!imageUrl) {
            Swal.fire('Lỗi', 'Vui lòng nhập URL ảnh!', 'error');
            return;
        }

        $('#detail_image_table_body').append(`
            <tr data-id="${imageInputs}">
                <td>${imageUrl}</td>
                <td>${isMain ? 'Có' : 'Không'}</td>
                <td>${imageOrder}</td>
                <td><img src="${imageUrl}" alt="Preview" style="max-width: 100px;"></td>
                <td>
                    <button class="btn btn-sm btn-danger remove-table-image"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `);

        // Reset form
        $('#detail_image_url').val('');
        $('#detail_image_is_main').prop('checked', false);
        $('#detail_image_order').val('');
        $('#detail_image_preview').hide();
        imageInputs++;
    });

    // Lưu sửa sách
    $('#saveEditProduct').click(function () {
        if (!$('#editProductForm')[0].checkValidity()) {
            $('#editProductForm')[0].reportValidity();
            return;
        }

        const bookId = $('#edit_product_id').val();
        const book = {
            tenSach: $('#edit_product_name').val(),
            moTaNgan: $('#edit_product_description').val(),
            namXuatBan: $('#edit_product_year').val() ? parseInt($('#edit_product_year').val()) : null,
            soTrang: $('#edit_product_pages').val() ? parseInt($('#edit_product_pages').val()) : null,
            soLuong: parseInt($('#edit_product_quantity').val()),
            tacGia: $('#edit_product_author').val() ? { id: parseInt($('#edit_product_author').val()) } : null,
            danhMuc: $('#edit_product_category').val() ? { id: parseInt($('#edit_product_category').val()) } : null,
            nhaXuatBan: $('#edit_product_publisher').val() ? { id: parseInt($('#edit_product_publisher').val()) } : null,
            doiTuong: $('#edit_product_target').val() ? { id: parseInt($('#edit_product_target').val()) } : null
        };
        const donViGia = {
            donVi: 'Cuốn',
            gia: parseFloat($('#edit_product_price').val()),
            discount: $('#edit_product_discount').val() ? parseFloat($('#edit_product_discount').val()) : 0
        };
        const images = collectImagesFromTable('#detail_image_table_body');

        if (images.length === 0) {
            Swal.fire('Lỗi', 'Vui lòng thêm ít nhất một ảnh sản phẩm!', 'error');
            return;
        }

        $.ajax({
            url: `/api/quanly/books/${bookId}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ book, donViGia, images }),
            success: function () {
                $('#editProductModal').modal('hide');
                loadBooks(currentPage);
                Swal.fire('Thành công', 'Cập nhật sách thành công!', 'success');
            },
            error: function (xhr) {
                Swal.fire('Lỗi', 'Không thể cập nhật sách: ' + (xhr.responseText || 'Lỗi không xác định'), 'error');
            }
        });
    });

    // Xóa sách
    $(document).on('click', '.delete-book', function () {
        const bookId = $(this).data('id');
        Swal.fire({
            title: 'Xác nhận',
            text: 'Bạn có chắc muốn xóa sách này?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Xóa',
            cancelButtonText: 'Hủy'
        }).then(result => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/api/quanly/books/${bookId}`,
                    method: 'DELETE',
                    success: function () {
                        loadBooks(currentPage);
                        Swal.fire('Thành công', 'Xóa sách thành công!', 'success');
                    },
                    error: function () {
                        Swal.fire('Lỗi', 'Không thể xóa sách!', 'error');
                    }
                });
            }
        });
    });

    // Tìm kiếm sách
    $('#search-products').on('input', function () {
        const searchTerm = $(this).val().toLowerCase();
        $('#products-tbody tr').filter(function () {
            $(this).toggle($(this).text().toLowerCase().indexOf(searchTerm) > -1);
        });
    });

    // Chuyển trang
    $(document).on('click', '.page-link', function (e) {
        e.preventDefault();
        currentPage = $(this).data('page');
        loadBooks(currentPage);
    });

    // Load dữ liệu ban đầu
    loadBooks();
    loadDropdowns('#addProductModal');
});