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

    $('#orderRedirect').on("click", () => {
        window.location.replace("/Supplier/Orders");
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
        const quantity = row.children()[3].innerHTML;
        const additionDate = new Date($(row.children()[4]).attr("data-additionDate")).toLocaleString("sv-SE", { timeZone: "Europe/Istanbul" });
        const photoList = $(row.children()[5].children);
        const isActive = row.children()[6].children[0].checked;

        $('#inspectHeader').html(header);
        $('#inspectDescription').html(description);
        $('#inspectPrice').html(price);
        $('#inspectQuantity').html(quantity);
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

        if (isActive) {
            $('#inspectIsActive').attr("checked", "checked");
        }
    });

    let photoFiles = null;
    $("#addPhotoImg").click(() => {
        $("#addPhoto").trigger("click");
    });

    $("#addPhoto").change((evt) => {
        if (evt.currentTarget.files && evt.currentTarget.files[0]) {
            const reader = new FileReader();
            reader.onload = (e) => {
                $('#addPhotoImg').attr('src', e.target.result);
            };
            reader.readAsDataURL(evt.currentTarget.files[0]);
            photoFiles = evt.currentTarget.files;
        }
    });

    $('#addProductSubmit').on("click", () => {
        const formData = new FormData();
        formData.append("SupplierName", "Alp")
        formData.append("Header", $('#addHeader').val());
        formData.append("Description", $('#addDescription').val());
        formData.append("Price", $('#addPrice').val());
        formData.append("Quantity", $('#addQuantity').val());
        formData.append("AdditionDate", $('#addAdditionDate').val());
        Array.from(photoFiles).forEach(photo => {
            formData.append("PhotoList", photo);
        });
        formData.append("IsActive", $('#addIsActive').prop("checked"))

        $.ajax({
            url: 'Supplier/Add',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: (response) => {
                $('#addProductModal').modal('hide');
                if (response.length === 36) {
                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    setTimeout(() => {
                        alertModal.modal('hide');
                        window.location.replace("/Supplier?Notification=Ürün başarılı bir şekilde eklendi.");
                    }, 1500);
                } else {
                    $('#alertFailModal').modal('show');
                }
            },
            error: () => {
                $('#addProductModal').modal('hide');
                $('#alertFailModal').modal('show');
            }
        });
    });

    $("#editPhoto").on("change", (element) => {
        if (element.currentTarget.files && element.currentTarget.files[0]) {
            const reader = new FileReader();
            reader.onload = (e) => {
                $('#editPhoto').attr('value', e.target.result);
                $('#editPhotoImg').attr('src', e.target.result);
            };
            reader.readAsDataURL(element.currentTarget.files[0]);
            photoFiles = element.currentTarget.files;
        }
    });

    $('#editPhotoImg').on("click", () => {
        $("#editPhoto").trigger("click");
    });

    let uniqueId = null;
    $('a[data-name="productEdit"]').on("click", (e) => {
        uniqueId = e.currentTarget.getAttribute("td-uniqueId");
        let row = $('#' + uniqueId);
        const header = row.children()[0].children[0].innerHTML;
        const description = row.children()[1].innerHTML;
        const price = row.children()[2].innerHTML;
        const quantity = row.children()[3].innerHTML;
        const additionDate = new Date($(row.children()[4]).attr("data-additionDate")).toLocaleString("sv-SE", { timeZone: "Europe/Istanbul" }).replace(' ', 'T');
        const photo = row.children()[5].children[0].src;
        const photoList = $(row.children()[5].children);
        const isActive = row.children()[6].children[0].checked;

        $('#editHeader').val(header);
        $('#editDescription').val(description);
        $('#editPrice').val(price);
        $('#editQuantity').val(quantity);
        $('#editAdditionDate').val(additionDate);
        $('#editPhoto').attr('value', photo);
        $('#editPhotoImg').attr('src', photo);

        $('#inspectEditPhotoList').html("");
        const customSliderLeft = $('<a href="#customSliderLeft" type="button" data-name="customSliderLeft" class="sliderButton-display-left">');
        customSliderLeft.click(() => {
            plusDivs(-1)
        });
        customSliderLeft.html("&#10094;");
        customSliderLeft.appendTo('#inspectEditPhotoList');
        const customSliderRight = $('<a href="#customSliderRight" type="button" data-name="customSliderRight" class="sliderButton-display-right">');
        customSliderRight.click(() => {
            plusDivs(1)
        });
        customSliderRight.html("&#10095;");
        customSliderRight.appendTo('#inspectEditPhotoList');
        for (let counter = 0; counter < photoList.length; counter++) {
            const img = $('<img class="customSlider w-32rem" alt="#">');
            img.attr('src', photoList[counter].src);
            img.appendTo('#inspectEditPhotoList');
        }
        showDivs(customSliderIndex);

        if (isActive) {
            $('#editIsActive').attr("checked", "checked");
        }
    });

    $('#editProductSubmit').on("click", () => {
        const form = $('#editProductForm');
        const postedFormData = new FormData(form[0]);

        postedFormData.append("UniqueId", uniqueId)

        const photoDataURL = $('#editPhoto')[0].defaultValue;

        Array.from(photoFiles).forEach(photo => {
            console.log(photo)
            postedFormData.append("PhotoList", photo);
        });

        const newIsActive = $('#editIsActive').prop("checked");
        postedFormData.set("IsActive", newIsActive);

        const formData = form.serializeArray();
        const newHeader = formData.find(data => data.name === "Header").value;
        const newDescription = formData.find(data => data.name === "Description").value;
        const newPrice = formData.find(data => data.name === "Price").value;
        const newQuantity = formData.find(data => data.name === "Quantity").value;
        const newAdditionDate = formData.find(data => data.name === "AdditionDate").value;
        const newPhoto = photoDataURL;

        $.ajax({
            url: 'Supplier/Edit',
            type: 'POST',
            data: postedFormData,
            processData: false,
            contentType: false,
            success: (response) => {
                $('#editProductModal').modal('hide');
                if (response === true) {
                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    setTimeout(() => {
                        alertModal.modal('hide')
                    }, 1500);

                    let row = $('#' + uniqueId);
                    row.children()[0].innerHTML = newHeader;
                    row.children()[1].innerHTML = newDescription;
                    row.children()[2].innerHTML = newPrice
                    row.children()[3].innerHTML = newQuantity
                    row.children()[4].innerHTML = newAdditionDate
                    row.children()[5].children[0].src = newPhoto
                    row.children()[6].children[0].checked = newIsActive
                } else {
                    $('#alertFailModal').modal('show');
                }
            },
            error: () => {
                $('#editProductModal').modal('hide');
                $('#alertFailModal').modal('show');
            }
        });
    });

    $('a[data-name="productDelete"]').on("click", (e) => {
        uniqueId = e.currentTarget.getAttribute("td-uniqueId");
    });

    $('#deleteProductSubmit').on("click", () => {
        $.ajax({
            url: 'Supplier/Delete',
            type: 'POST',
            data: $.param({ UniqueId: uniqueId }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: (response) => {
                $('#deleteProductModal').modal('hide');
                if (response === true) {
                    productDataTable
                        .row($('#' + uniqueId))
                        .remove()
                        .draw();

                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    setTimeout(() => {
                        alertModal.modal('hide')
                    }, 1500);
                } else {
                    $('#alertFailModal').modal('show');
                    setTimeout(() => {
                        window.location.replace("/Supplier/?Notification=Daha önceden faturalandırılmış ürün silinemez.");
                    }, 1500);
                }
            },
            error: () => {
                $('#deleteProductModal').modal('hide');
                $('#alertFailModal').modal('show');
            }
        });
    });

    $('a[data-name="orderApprove"]').on("click", (e) => {
        let uniqueId = e.currentTarget.getAttribute("td-uniqueId");
        $.ajax({
            url: '/Supplier/OrderApprove',
            type: 'POST',
            data: $.param({ UniqueId: uniqueId }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success: (response) => {
                if (response.length === 36) {
                    const alertModal = $('#alertSuccessModal');
                    alertModal.modal('show');
                    setTimeout(() => {
                        alertModal.modal('hide')
                    }, 1500);
                    window.location.replace("/Supplier/Orders?Notification=Sipariş başarılı bir şekilde onaylandı.");
                } else {
                    $('#alertFailModal').modal('show');
                }
            },
            error: () => {
                $('#alertFailModal').modal('show');
            }
        });
    });
});

const dataURLtoFile = (dataUrl, filename) => {
    let arr = dataUrl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, { type: mime });
}