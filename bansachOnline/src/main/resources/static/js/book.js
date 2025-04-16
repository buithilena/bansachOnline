$(document).ready(function () {
    // Load danh sách đối tượng vào dropdown
    function loadDoiTuong() {
        const doiTuongSelect = $('#doi_tuong_id');
        doiTuongSelect.prop('disabled', true);
        $.ajax({
            url: '/api/quanly/doituong',
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                // Xóa các option hiện tại (trừ option mặc định và "Thêm mới")
                doiTuongSelect.find('option:not(:first):not([value="add-doi-tuong"])').remove();
                // Thêm các đối tượng từ API
                data.forEach(function (doiTuong) {
                    doiTuongSelect.append(
                        `<option value="${doiTuong.id}">${doiTuong.tenDoiTuong}</option>`
                    );
                });
                doiTuongSelect.prop('disabled', false);
            },
            error: function (err) {
                console.error('Lỗi khi tải danh sách đối tượng:', err);
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Không thể tải danh sách đối tượng!',
                });
                doiTuongSelect.prop('disabled', false);
            }
        });
    }

    // Gọi loadDoiTuong khi modal addBookModal được mở
    $('#addBookModal').on('show.bs.modal', function () {
        console.log('Opening addBookModal');
        loadDoiTuong();
    });

    // Xử lý chọn "Thêm đối tượng mới"
    $('#doi_tuong_id').on('change', function () {
        console.log('doi_tuong_id changed:', $(this).val());
        if ($(this).val() === 'add-doi-tuong') {
            console.log('Attempting to open addDoiTuongModal');
            // Đóng các modal khác trước khi mở
            $('.modal').modal('hide');
            // Kiểm tra xem modal có tồn tại không
            if ($('#addDoiTuongModal').length) {
                $('#addDoiTuongModal').modal('show');
            } else {
                console.error('Modal #addDoiTuongModal not found in DOM');
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Không tìm thấy modal thêm đối tượng!',
                });
            }
            $(this).val(''); // Reset dropdown
        }
    });

    // Xử lý preview ảnh khi nhập URL
    function previewImage(inputId, previewId) {
        $(`#${inputId}`).on('input', function () {
            const url = $(this).val();
            const preview = $(`#${previewId}`);
            if (url) {
                preview.attr('src', url).show();
                // Kiểm tra xem URL có hợp lệ không
                preview.on('error', function () {
                    $(this).hide();
                    Swal.fire({
                        icon: 'warning',
                        title: 'Cảnh báo',
                        text: 'URL ảnh không hợp lệ!',
                    });
                });
            } else {
                preview.hide();
            }
        });
    }

    // Áp dụng preview cho ảnh lớn và nhỏ
    previewImage('doi_tuong_image_max', 'preview_image_max');
    previewImage('doi_tuong_image_min', 'preview_image_min');

    // Xử lý lưu đối tượng mới
    $('#saveDoiTuong').on('click', function () {
        const doiTuongData = {
            tenDoiTuong: $('#doi_tuong_name').val().trim(),
            imageMax: $('#doi_tuong_image_max').val().trim(),
            imageMin: $('#doi_tuong_image_min').val().trim(),
            ghiChu: $('#doi_tuong_note').val().trim()
        };

        // Kiểm tra dữ liệu đầu vào
        if (!doiTuongData.tenDoiTuong || !doiTuongData.imageMax || !doiTuongData.imageMin) {
            Swal.fire({
                icon: 'warning',
                title: 'Cảnh báo',
                text: 'Vui lòng điền đầy đủ các trường bắt buộc!',
            });
            return;
        }

        // Vô hiệu hóa nút Lưu để tránh click nhiều lần
        const saveBtn = $(this);
        saveBtn.prop('disabled', true).text('Đang lưu...');

        $.ajax({
            url: '/api/quanly/doituong',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(doiTuongData),
            success: function (response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Thành công',
                    text: 'Đã thêm đối tượng mới!',
                });
                $('#addDoiTuongModal').modal('hide');
                $('#addDoiTuongForm')[0].reset(); // Reset form
                $('#preview_image_max, #preview_image_min').hide(); // Ẩn preview ảnh
                loadDoiTuong(); // Tải lại danh sách đối tượng
            },
            error: function (err) {
                console.error('Lỗi khi thêm đối tượng:', err);
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi',
                    text: 'Không thể thêm đối tượng! Vui lòng thử lại.',
                });
            },
            complete: function () {
                saveBtn.prop('disabled', false).text('Lưu');
            }
        });
    });

    // Reset form và preview khi đóng modal
    $('#addDoiTuongModal').on('hidden.bs.modal', function () {
        $('#addDoiTuongForm')[0].reset();
        $('#preview_image_max, #preview_image_min').hide();
    });
});