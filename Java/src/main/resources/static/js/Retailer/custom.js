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

    $('#productListRedirect').on("click", () => {
        window.location.replace("/");
    });

    $('#sideBarRedirectToCart').on("click", () => {
        window.location.replace("/Retailer/CheckOut");
    });

    $('#orderRedirect').on("click", () => {
        window.location.replace("/Retailer/Orders");
    });

    $('#productRedirect').on("click", () => {
        window.location.replace("/Supplier");
    });

    $('#logout').on("click", () => {
        window.location.replace("/Login/Logout");
    });


    let customSliderIndex = 1;
    const showDivs = (nThCustomSlider) => {
        let counter;
        let customSliderList = document.getElementsByClassName('customSlider w-32rem');
        if (nThCustomSlider > customSliderList.length) {
            customSliderIndex = 1
        }
        if (nThCustomSlider < 1) {
            customSliderIndex = customSliderList.length
        }
        for (counter = 0; counter < customSliderList.length; counter++) {
            customSliderList[counter].style.display = "none";
        }
        customSliderList[customSliderIndex - 1].style.display = "block";
    }

    const plusDivs = (nThCustomSlider) => {
        showDivs(customSliderIndex += nThCustomSlider);
    }

    $('a[data-name="inspectProduct"]').on("click", (e) => {
        uniqueId = e.currentTarget.getAttribute("td-uniqueId");
        let row = $('#' + uniqueId);
        const header = row.children()[0].children[0].innerHTML;
        const description = row.children()[1].innerHTML;
        const price = row.children()[2].innerHTML;
        const additionDate = new Date($(row.children()[3]).attr("data-additionDate")).toLocaleString("sv-SE", { timeZone: "Europe/Istanbul" });
        const photoList = $(row.children()[4].children);

        $('#inspectHeader').html(header);
        $('#inspectDescription').html(description);
        $('#inspectPrice').html(price);
        $('#inspectAdditionDate').html(additionDate);

        $('#inspectPhotoList').html("");
        const customSliderLeft = $('<a href="#customSliderLeft" type="button" data-name="customSliderLeft" class="sliderButton-display-left">');
        customSliderLeft.click(() => {
            plusDivs(-1)
        });
        customSliderLeft.html("&#10094;");
        customSliderLeft.appendTo('#inspectPhotoList');
        const customSliderRight = $('<a href="#customSliderRight" type="button" data-name="customSliderRight" class="sliderButton-display-right">');
        customSliderRight.click(() => {
            plusDivs(1)
        });
        customSliderRight.html("&#10095;");
        customSliderRight.appendTo('#inspectPhotoList');
        for (let counter = 0; counter < photoList.length; counter++) {
            const img = $('<img class="customSlider w-32rem" alt="#">');
            img.attr('src', photoList[counter].src);
            img.appendTo('#inspectPhotoList');
        }
        showDivs(customSliderIndex);
    });

    let uniqueId = null;
    $('a[data-name="productBuy"]').on("click", (e) => {
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

    $('#buyProductSubmit').on("click", () => {
        const formData = new FormData();
        formData.append("UniqueId", uniqueId);
        formData.append("Quantity", parseInt($('#buyQuantity').val()));

        $.ajax({
            url: '/Retailer/AddToCart',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: (response) => {
                $('#buyProductModal').modal('hide');
                if (response.length === 36) {
                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    window.location.replace("/?Notification=Sepete başarılı bir şekilde eklendi.");
                    setTimeout(() => {
                        alertModal.modal('hide');
                    }, 1500);
                } else {
                    const alertModal = $('#alertFailModal');
                    alertModal.modal('show');
                    setTimeout(() => {
                        alertModal.modal('hide');
                    }, 1500);
                    window.location.replace("/?Notification=" + response);
                }
            },
            error: () => {
                $('#buyProductModal').modal('hide');
                $('#alertFailModal').modal('show');
            }
        });
    });

    $('#checkOut').on("click", () => {
        window.location.replace("/Retailer/CheckOut");
    });

    $('#checkOutPOST').on("click", () => {
        $.ajax({
            url: '/Retailer/CheckOutPOST',
            type: 'POST',
            success: (response) => {
                $('#buyProductModal').modal('hide');
                if (response.length === 36) {
                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    sessionStorage.clear();
                    window.location.replace("/?Notification=Ödeme başarılı bir şekilde tamamlandı.");
                    setTimeout(() => {
                        alertModal.modal('hide');
                    }, 1500);
                } else {
                    const alertModal = $('#alertFailModal');
                    alertModal.modal('show');
                    setTimeout(() => {
                        alertModal.modal('hide');
                    }, 1500);
                    window.location.replace("/Retailer/CheckOut?Notification=" + response);
                }
            },
            error: () => {
                $('#buyProductModal').modal('hide');
                $('#alertFailModal').modal('show');
            }
        });
    });

    $('a[data-name="removeFromCart"]').on('click', (e) => {
        $.ajax({
            url: '/Retailer/RemoveFromCart',
            type: 'POST',
            data: $.param({ UniqueId: e.currentTarget.getAttribute("td-uniqueId"), }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: (response) => {
                if (response.length === 36) {
                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    window.location.replace("/Retailer/CheckOut?Notification=Sepetten başarılı bir şekilde silindi.");
                    setTimeout(() => {
                        alertModal.modal('hide');
                    }, 1500);
                } else {
                    const alertModal = $('#alertFailModal');
                    alertModal.modal('show');
                    setTimeout(() => {
                        alertModal.modal('hide');
                    }, 1500);
                    window.location.replace("/Retailer/CheckOut?Notification=" + response);
                }
            },
            error: () => {
                $('#alertFailModal').modal('show');
            }
        });
    });
});