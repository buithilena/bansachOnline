class Utils {
    // Hiển thị thông báo với SweetAlert2, có fallback nếu không tải được
    static showAlert(icon, title, text, options = {}) {
        const defaultOptions = {
            timer: 1500,
            showConfirmButton: false,
            ...options
        };

        if (typeof Swal !== 'undefined') {
            Swal.fire({ icon, title, text, ...defaultOptions });
        } else {
            console.warn(`SweetAlert2 không khả dụng. Dữ liệu: ${title} - ${text}`);
            alert(`${title}: ${text}`);
        }
    }

    // Tải danh mục từ API và hiển thị vào thẻ select
    static loadCategories(selectId, defaultOptionText = "Chọn danh mục", options = {}) {
        const { addNewOption = false, onSuccess = null, onError = null } = options;

        const $select = $(selectId);
        if (!$select.length) {
            console.error(`Không tìm thấy phần tử với ID: ${selectId}`);
            Utils.showAlert('error', 'Lỗi', `Không tìm thấy phần tử ${selectId}!`);
            return;
        }

        $.ajax({
            url: '/api/quanly/danhmuc',
            method: 'GET',
            success: (data) => {
                $select.empty();
                $select.append(`<option value="">${defaultOptionText}</option>`);

                if (!Array.isArray(data)) {
                    console.error('Dữ liệu danh mục không hợp lệ:', data);
                    Utils.showAlert('error', 'Lỗi', 'Dữ liệu danh mục không đúng định dạng!');
                    return;
                }

                data.forEach(danhMuc => {
                    $select.append(`<option value="${danhMuc.id}">${danhMuc.tenDanhMuc}</option>`);
                });

                if (addNewOption) {
                    $select.append('<option value="add-category">Thêm danh mục mới...</option>');
                }

                if (typeof onSuccess === 'function') {
                    onSuccess(data);
                }
            },
            error: (xhr) => {
                const errorMsg = xhr.responseText || 'Không thể tải danh mục!';
                Utils.showAlert('error', 'Lỗi', errorMsg);
                if (typeof onError === 'function') {
                    onError(xhr);
                }
            }
        });
    }

    // Hàm tiện ích: Xác nhận hành động với người dùng
    static confirmAction(title, text, callback) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'warning',
                title: title,
                text: text,
                showCancelButton: true,
                confirmButtonText: 'Xác nhận',
                cancelButtonText: 'Hủy'
            }).then((result) => {
                if (result.isConfirmed && typeof callback === 'function') {
                    callback();
                }
            });
        } else {
            if (confirm(`${title}: ${text}`) && typeof callback === 'function') {
                callback();
            }
        }
    }

    // Hiển thị phân trang
    static renderPagination(totalPages, currentPage, section) {
        const $pagination = $(`#${section}-pagination`);
        $pagination.empty();

        // Thêm nút Previous
        $pagination.append(`
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>
            </li>
        `);

        // Thêm các nút số trang
        for (let i = 1; i <= totalPages; i++) {
            $pagination.append(`
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }

        // Thêm nút Next
        $pagination.append(`
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>
            </li>
        `);
    }
}

$(document).ready(function() {
    // Xử lý sự kiện submit form tìm kiếm
    $('#search-form').on('submit', function(e) {
        e.preventDefault(); // Ngăn chặn reload trang

        var query = $(this).find('input[name="query"]').val();
        var categoryId = $(this).find('input[name="categoryId"]').val() || '';

        // Gửi yêu cầu AJAX đến endpoint /book/search
        $.ajax({
            url: '/book/search',
            type: 'GET',
            data: {
                query: query,
                categoryId: categoryId,
                page: 0,
                size: 10
            },
            success: function(response) {
                // Cập nhật danh sách sản phẩm
                updateProductList(response.content);

                // Cập nhật phân trang
                updatePagination(response);

                // Hiển thị thông báo nếu không có kết quả
                if (response.content.length === 0) {
                    $('#no-results').removeClass('d-none');
                    $('.product-list').html('');
                } else {
                    $('#no-results').addClass('d-none');
                }
            },
            error: function(xhr, status, error) {
                Toastify({
                    text: "Lỗi khi tìm kiếm sách: " + xhr.responseText,
                    duration: 3000,
                    gravity: "top",
                    position: "right",
                    backgroundColor: "#dc3545",
                    stopOnFocus: true
                }).showToast();
            }
        });
    });

    // Hàm cập nhật danh sách sản phẩm
    function updateProductList(books) {
        var productList = $('.product-list');
        productList.empty(); // Xóa danh sách sản phẩm hiện tại

        books.forEach(function(book) {
            var discountBadge = book.hasDiscount ?
                `<span class="badge bg-danger position-absolute discount-badge" id="discount-badge-${book.id}">${book.defaultDonViTinh.discount}%</span>` : '';

            var originalPrice = book.hasDiscount ?
                `<div id="original-price-${book.id}" class="original-price">${Number(book.defaultDonViTinh.gia).toLocaleString('vi-VN')} đ</div>` : '';

            var productCard = `
                <div class="product-card">
                    <div class="card border rounded shadow-sm d-flex flex-column h-100">
                        ${discountBadge}
                        <img src="${book.mainImageUrl || '/images/placeholder.png'}" class="card-img-top" alt="${book.tenSach}">
                        <div class="card-body text-center d-flex flex-column">
                            <a href="/book/sanpham?id=${book.id}" class="card-title text-decoration-none">
                                <h6>${book.tenSach}</h6>
                            </a>
                            <div class="price-section">
                                <div id="discounted-price-${book.id}" class="discounted-price" data-price="${book.discountedPrice}">
                                    ${Number(book.discountedPrice).toLocaleString('vi-VN')} đ
                                </div>
                                ${originalPrice}
                            </div>
                            <button class="btn btn-primary w-100 mt-auto" onclick="addToCart(${book.id})">
                                Thêm vào giỏ hàng
                            </button>
                        </div>
                    </div>
                </div>`;
            productList.append(productCard);
        });
    }

    // Hàm cập nhật phân trang
    function updatePagination(pageData) {
        var pagination = $('#pagination');
        pagination.empty();

        if (pageData.totalPages > 1) {
            var prevDisabled = pageData.number === 0 ? 'disabled' : '';
            var nextDisabled = pageData.number === pageData.totalPages - 1 ? 'disabled' : '';

            pagination.append(`
                <li class="page-item ${prevDisabled}">
                    <a class="page-link" href="#" onclick="loadPage(${pageData.number - 1})">Previous</a>
                </li>
            `);

            for (var i = 0; i < pageData.totalPages; i++) {
                var active = i === pageData.number ? 'active' : '';
                pagination.append(`
                    <li class="page-item ${active}">
                        <a class="page-link" href="#" onclick="loadPage(${i})">${i + 1}</a>
                    </li>
                `);
            }

            pagination.append(`
                <li class="page-item ${nextDisabled}">
                    <a class="page-link" href="#" onclick="loadPage(${pageData.number + 1})">Next</a>
                </li>
            `);
        }
    }

    // Hàm tải trang mới khi nhấn vào phân trang
    window.loadPage = function(page) {
        var query = $('#search-form input[name="query"]').val();
        var categoryId = $('#search-form input[name="categoryId"]').val() || '';

        $.ajax({
            url: '/book/search',
            type: 'GET',
            data: {
                query: query,
                categoryId: categoryId,
                page: page,
                size: 10
            },
            success: function(response) {
                updateProductList(response.content);
                updatePagination(response);
                if (response.content.length === 0) {
                    $('#no-results').removeClass('d-none');
                    $('.product-list').html('');
                } else {
                    $('#no-results').addClass('d-none');
                }
            },
            error: function(xhr, status, error) {
                Toastify({
                    text: "Lỗi khi tải trang: " + xhr.responseText,
                    duration: 3000,
                    gravity: "top",
                    position: "right",
                    backgroundColor: "#dc3545",
                    stopOnFocus: true
                }).showToast();
            }
        });
    };
});

// Xuất Utils để sử dụng toàn cục
window.Utils = Utils;