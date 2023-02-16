let btnLoadCountries;
let selectorCountry;
let btnAddOrNew;
let btnUpdate;
let btnDelete;
let fieldCountryName;
let fieldCountryCode;
let confirmDialog;

let totalCountry = 0;
let totalCountryLabel;

$(document).ready(function() {
    btnLoadCountries = $("#btnLoadCountries");
    selectorCountry = $("#selectorCountry");
    btnAddOrNew = $("#btnAddOrNew");
    btnUpdate = $("#btnUpdate");
    btnDelete = $("#btnDelete");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");
    confirmDialog = $("#confirmDialog");
    totalCountryLabel = $("#totalCountry");

    btnLoadCountries.on("click", function() {
        loadCountries(true); // Func 1
    });

    selectorCountry.on("change", function() {
        showSelectedCountry(); // Func 2
    });

    btnAddOrNew.on("click", function() {
        if(btnAddOrNew.attr("value") == "New") {
            fieldCountryName.prop("disabled", false).val("").focus();
            fieldCountryCode.prop("disabled", false).val("");
            btnAddOrNew.val("Add");
            btnUpdate.prop("disabled", true);
            btnDelete.prop("disabled", true);

            if(selectorCountry.val() != null) {
                loadCountries(false);
            }
        } else {
            addNewCountry(); // Func 3
        }
    });

    btnUpdate.on("click", function() {
        updateCountry();
    });

    btnDelete.on("click", function() {
        showConfirmDialogForCountry("Are you sure to delete this country and its state!");
    })
});

// Function
// 1
function loadCountries(isShowToast) {
    totalCountry = 0;
    let url = contextPath + "countries/get-all";
    $.get(url, function(responseJson) {
        selectorCountry.empty();

       $.each(responseJson, function(index, country) {
           totalCountry++;
           let value = country.id + "-" + country.code;
           selectorCountry.append(`
               <option value="${value}">${country.name}</option>
           `);
       });
    }).done(function() {
        btnLoadCountries.attr("value", "Refresh Country List");
        fieldCountryName.val("");
        fieldCountryCode.val("");
        if(isShowToast) {
            showToastMessage("All countries have been loaded!");
        }
        totalCountryLabel.text(`Total country: ${totalCountry}`);
    }).fail(function() {
        if(isShowToast) {
            showToastMessage("Could not connect to server or server encountered an error!");
        }
    });
}

// 1.2
function loadCountriesAndSelectOne(countryId) {
    totalCountry = 0;
    let url = contextPath + "countries/get-all";
    $.get(url, function(responseJson) {
        selectorCountry.empty();

        $.each(responseJson, function(index, country) {
            totalCountry++;
            let value = country.id + "-" + country.code;
            if(countryId == country.id) {
                selectorCountry.append(`
                    <option value="${value}" selected>${country.name}</option>
                `);
            } else {
                selectorCountry.append(`
                    <option value="${value}">${country.name}</option>
                `);
            }
        });
        totalCountryLabel.text(`Total country: ${totalCountry}`);
    });
}

// 2
function showSelectedCountry() {
    fieldCountryName.prop("disabled", false);
    fieldCountryCode.prop("disabled", false);
    btnAddOrNew.prop("value", "New");
    btnUpdate.prop("disabled", false);
    btnDelete.prop("disabled", false);

    let selectedCountry = $("#selectorCountry option:selected");
    fieldCountryName.val(selectedCountry.text());
    fieldCountryCode.val(selectedCountry.attr("value").split("-")[1]);
}

// 3
function addNewCountry() {
    let url = contextPath + "countries/save";
    let name = fieldCountryName.val();
    let code = fieldCountryCode.val();
    if(name == "" || code == "") {
        showNotificationDialogForCountry();
        return;
    }

    let jsonData = {
        name: name,
        code: code
    }

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: "application/json"
    }).done(function(returnedCountryId) {
        fieldCountryName.val("").focus();
        fieldCountryCode.val("");

        loadCountriesAndSelectOne(returnedCountryId);
        showToastMessage("Country: " + name + " has been created!");
    }).fail(function() {
        showToastMessage("Could not connect to server or server encountered an error!");
    });
}

// 4
function updateCountry() {
    let url = contextPath + "countries/save";
    let id = selectorCountry.val().split("-")[0];
    let name = fieldCountryName.val();
    let code = fieldCountryCode.val();
    if(name == "" || code == "") {
        showNotificationDialogForCountry();
        return;
    }

    let jsonData = {
        id: id,
        name: name,
        code: code
    }

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: "application/json"
    }).done(function(returnedCountryId) {
        doneFunction1();
        loadCountriesAndSelectOne(returnedCountryId);
        showToastMessage("Country: " + name + " has been updated!");
    }).fail(function() {
        showToastMessage("Could not connect to server or server encountered an error!");
    });
}

// 5
function showConfirmDialogForCountry(modalContent) {
    confirmDialog.modal("show");
    $("#modalContent").text(modalContent);
    $("#btnYes").on("click", function(e) {
        e.preventDefault();
        deleteCountry();
        $("#btnNo").click();
    });
}

// 5.1
function deleteCountry() {
    let selectedCountryOption = $("#selectorCountry option:selected");
    let selectedOptionValue = selectedCountryOption.attr("value");
    let selectedOptionName = selectedCountryOption.text();

    let countryId = selectedOptionValue.split("-")[0];
    let url = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function() {
        doneFunction1();
        loadCountries(false);
        showToastMessage(`Country: ${selectedOptionName} has been deleted`);
    }).fail(function() {
        showToastMessage("Could not connect to server or server encountered an error!");
    });
}

// Utility function
// 1
function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast("show");
}

// 2
function doneFunction1() {
    fieldCountryName.val("").prop("disabled", true);
    fieldCountryCode.val("").prop("disabled", true);
    btnUpdate.prop("disabled", true)
    btnDelete.prop("disabled", true)
}

// 3
function showNotificationDialogForCountry() {
    $("#modalBody").text("Country name and code can't be blank!");
    $("#modalDialog").modal("show");
}