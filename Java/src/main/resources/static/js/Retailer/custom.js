$(document).ready(() => {
    $('[data-toggle="tooltip"]').tooltip();

    const productDataTable = $('#productTable').DataTable({
        "columnDefs": [
            { "width": "40%", "targets": 0 },
            { "width": "10%", "targets": 1 }
        ],
        "order": [[1, "desc"]],
        "lengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Tümünü Göster"]],
        "language": {
            "url": "/js/dataTables/Turkish.json"
        },
    });

    $('#orderRedirect').on("click", () => {
        window.location.replace("/Retailer/ListOrders");
    });

    $('#productRedirect').on("click", () => {
        window.location.replace("/Supplier");
    });

    $('#logout').on("click", () => {
        window.location.replace("/Login/Logout");
    });

    let uniqueId = null;
    $('a[data-name ="productBuy"]').on("click", (e) => {
        uniqueId = e.currentTarget.getAttribute("td-uniqueId");
        let row = $('#' + uniqueId);
        const header = row.children()[0].innerHTML;
        const description = row.children()[1].innerHTML;
        const price = row.children()[2].innerHTML;
        const quantity = 1;
        const additionDate = new Date($(row.children()[3]).attr("data-additionDate")).toLocaleString("tr-TR", { timeZone: "Europe/Istanbul" });
        const photo = row.children()[4].children[0].src;

        $('#buyHeader').html(header);
        $('#buyDescription').html(description);
        $('#buyPrice').html(price);
        $('#buyQuantity').val(quantity);
        $('#buyAdditionDate').html(additionDate);
        $('#buyPhotoImg').attr('src', photo);
    });

    let cartList = JSON.parse(sessionStorage.getItem('CartList'))
    $('#buyProductSubmit').on("click", () => {
        const cartItem = { "UniqueId": uniqueId, "Quantity": parseInt($('#buyQuantity').val()) };

        cartList = cartList === null ? [] : JSON.parse(sessionStorage.getItem('CartList'));

        if (cartList.some(item => item.UniqueId === uniqueId)) {
            cartList.forEach(item => {
                if (item.UniqueId === uniqueId) {
                    item.Quantity = parseInt(item.Quantity) + 1;
                    sessionStorage.setItem('CartList', JSON.stringify(cartList));
                }
            });
        } else {
            cartList.push(cartItem);
            sessionStorage.setItem('CartList', JSON.stringify(cartList));
        }

        $('#buyProductModal').modal('hide');
        const alertModal = $('#alertSuccessModal');
        alertModal.modal('show');
    });

    $('#checkOut').on("click", () => {
        window.location.replace("/Retailer/CheckOut?CartProductList=" + encodeURIComponent(sessionStorage.getItem('CartList')));
    });

    $('#checkOutPOST').on("click", () => {
        const postedFormData = new FormData();
        postedFormData.append("CartProductList", sessionStorage.getItem('CartList'));

        $.ajax({
            url: '/Retailer/CheckOutPOST',
            type: 'POST',
            data: postedFormData,
            processData: false,
            contentType: false,
            success: (response) => {
                $('#buyProductModal').modal('hide');
                if (response.length === 36) {
                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    sessionStorage.clear();
                    setTimeout(() => {
                        alertModal.modal('hide');
                    }, 1500);
                } else {
                    $('#alertFailModal').modal('show');
                }
            },
            error: () => {
                $('#buyProductModal').modal('hide');
                $('#alertFailModal').modal('show');
            }
        });
    });
});