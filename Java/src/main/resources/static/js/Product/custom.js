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

    $('#platformRedirect').on("click", () => {
        window.location.replace("/Platform");
    });

    $('#logout').on("click", () => {
        window.location.replace("/Login/Logout");
    });

    let photoFile = null;
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
            photoFile = evt.currentTarget.files[0];
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
        formData.append("Photo", photoFile);
        formData.append("IsActive", $('#addIsActive').prop("checked"))

        $.ajax({
            url: '/Add',
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
                        window.location.replace("/");
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
        }
        ///fileToDataURL(element);
    });

    $('#editPhotoImg').on("click", () => {
        $("#editPhoto").trigger("click");
    });

    let uniqueId = null;
    $('a[data-name ="productEdit"]').on("click", (e) => {
        uniqueId = e.currentTarget.getAttribute("td-uniqueId");
        let row = $('#' + uniqueId);
        const header = row.children()[0].innerHTML;
        const description = row.children()[1].innerHTML;
        const price = row.children()[2].innerHTML;
        const quantity = row.children()[3].innerHTML;
        const additionDate = new Date($(row.children()[4]).attr("data-additionDate")).toLocaleString("sv-SE", { timeZone: "Europe/Istanbul" }).replace(' ', 'T');
        const photo = row.children()[5].children[0].src;
        const isActive = row.children()[6].children[0].checked;

        $('#editHeader').val(header);
        $('#editDescription').val(description);
        $('#editPrice').val(price);
        $('#editQuantity').val(quantity);
        $('#editAdditionDate').val(additionDate);
        $('#editPhoto').attr('value', photo);
        $('#editPhotoImg').attr('src', photo);
        if (isActive) {
            $('#editIsActive').attr("checked", "checked");
        }
    });

    $('#editProductSubmit').on("click", () => {
        const form = $('#editProductForm');
        const postedFormData = new FormData(form[0]);

        postedFormData.append("UniqueId", uniqueId)

        const photoDataURL = $('#editPhoto')[0].defaultValue;
        const profilePhotoFile = dataURLtoFile(photoDataURL, 'hello.png');
        postedFormData.set("Photo", profilePhotoFile)

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
            url: '/Edit',
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
                    console.log(row.children()[6].children[0])
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

    $('a[data-name ="productDelete"]').on("click", (e) => {
        uniqueId = e.currentTarget.getAttribute("td-uniqueId");
    });

    $('#deleteProductSubmit').on("click", () => {
        $.ajax({
            url: '/Delete',
            type: 'POST',
            data: $.param({
                UniqueId: uniqueId,
            }),
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
                }
            },
            error: () => {
                $('#deleteProductModal').modal('hide');
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

const fileToDataURL = (element) => {
    let file = element.target.files[0];
    let reader = new FileReader();
    reader.onloadend = () => {
        console.log(reader.result)
    }
    reader.readAsDataURL(file);
}