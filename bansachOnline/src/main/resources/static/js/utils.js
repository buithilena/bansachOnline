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

// Xuất Utils để sử dụng toàn cục
window.Utils = Utils;