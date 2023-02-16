$(document).ready(function() {
    dropDownBrand.on("change", function() {
        getCategories();
    });

    $("#cancelBtn").on("click", function() {
        window.location = productUrlModule;
    });

    getCategoriesOrNot();
});

function getCategoriesOrNot() {
    let categoryId = $("#categoryId").val();
    if(categoryId == undefined) {
        getCategories();
    }
}

function getCategories() {
    dropDownCategory.empty();
    let brandId = dropDownBrand.val();
    let url = brandUrlModule + "/" + brandId + "/get-categories";
    $.get(url, function(jsonResponse) {
        $.each(jsonResponse, function(index, category) {
            dropDownCategory.append(`<option value="${category.id}">${category.name}</option>`);
        });
    })
}
