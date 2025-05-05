document.addEventListener('DOMContentLoaded', function () {
    const menuItems = document.querySelectorAll('.menu-item');
    const contentArea = document.getElementById('content-area');
    const tabLinks = document.querySelectorAll('.tab-link');

    // Xử lý chuyển đổi section
    menuItems.forEach(item => {
        item.addEventListener('click', function (e) {
            e.preventDefault();
            const section = this.getAttribute('data-section');

            menuItems.forEach(menu => menu.classList.remove('active'));
            this.classList.add('active');

            const contents = contentArea.querySelectorAll('.card-content');
            contents.forEach(content => content.style.display = 'none');

            switch (section) {
                case 'personal':
                    document.getElementById('personal-content').style.display = 'block';
                    break;
                case 'orders':
                    document.getElementById('orders-content').style.display = 'block';
                    break;
                case 'addresses':
                    // Xử lý section địa chỉ nếu cần
                    break;
                case 'cart':
                    // Xử lý section giỏ hàng nếu cần
                    break;
                case 'logout':
                    window.location.href = '/login';
                    return;
            }
        });
    });

    // Xử lý tab lọc đơn hàng
    tabLinks.forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            const status = this.getAttribute('data-status');

            tabLinks.forEach(tab => tab.classList.remove('active'));
            this.classList.add('active');

            // Gọi AJAX hoặc gửi request để lọc đơn hàng theo trạng thái
            filterOrders(status);
        });
    });

    // Hàm bật chế độ chỉnh sửa thông tin cá nhân
    window.enableEdit = function () {
        const inputs = document.querySelectorAll('#profile-form input');
        inputs.forEach(input => input.removeAttribute('readonly'));
        document.getElementById('save-btn').style.display = 'inline-block';
    };

    // Hàm lọc đơn hàng (giả lập, cần tích hợp backend)
    function filterOrders(status) {
        // Gửi request AJAX đến backend để lấy danh sách đơn hàng theo trạng thái
        // Ví dụ: fetch(`/api/orders?status=${status}`)
        console.log(`Lọc đơn hàng theo trạng thái: ${status}`);
        // Cập nhật DOM với danh sách đơn hàng mới
    }
});