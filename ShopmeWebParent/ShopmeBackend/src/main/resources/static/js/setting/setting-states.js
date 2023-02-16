let btnLoadCountriesForState;
let selectorCountryForState;
let selectorState;
let fieldStateName;

let btnAddOrNewInState;
let btnUpdateInState;
let btnDeleteInState;

let totalState = 0;
let totalStateLabel;

$(document).ready(function() {
    btnLoadCountriesForState = $("#btnLoadCountriesForState");
    selectorCountryForState = $("#selectorCountryForState");
    selectorState = $("#selectorState");
    fieldStateName = $("#fieldStateName");

    btnAddOrNewInState = $("#btnAddOrNewInState");
    btnUpdateInState = $("#btnUpdateInState");
    btnDeleteInState = $("#btnDeleteInState");
    totalStateLabel = $("#totalState");

    btnLoadCountriesForState.on("click", function() {
        loadCountriesForState();
    });

    selectorCountryForState.on("change", function() {
       loadStatesAfterSelectCountry();
    });

    selectorState.on("change", function() {
        showSelectedState();
    });

    btnAddOrNewInState.on("click", function() {
        if(btnAddOrNewInState.attr("value") == "New") {
            fieldStateName.prop("disabled", false).val("").focus();
            btnAddOrNewInState.val("Add");
            btnUpdateInState.prop("disabled", true);
            btnDeleteInState.prop("disabled", true);
        } else {
            addNewState();
        }
    });

    btnUpdateInState.on("click", function() {
       updateState();
    });

    btnDeleteInState.on("click", function() {
        showConfirmDialog("Are you sure to delete this state!");
    });
});

// Call-back function:
// 1
function loadCountriesForState() {
    selectorCountryForState.empty();
    selectorState.empty();

    let url = contextPath + "countries/get-all";

    $.get(url, function(responseJson) {
        $.each(responseJson, function(index, country) {
            selectorCountryForState.append(`<option value="${country.id}">${country.name}</option>`);
        });
    }).done(function() {
        let firstCountryInList = $("#selectorCountryForState option:selected");
        let countryName = firstCountryInList.text();

        showToastMessage(`All countries and all state of country: ${countryName} have been loaded!`);
        loadStatesAfterSelectCountry();
    }).fail(function() {
        showToastMessage("Could not connect to server or server encountered an error!");
    });
}

// 2
function loadStatesAfterSelectCountry() {
    totalState = 0;
    selectorState.empty();

    let selectedCountryOption = $("#selectorCountryForState option:selected");
    let selectedCountryId = selectedCountryOption.attr("value");
    let selectedCountryName = selectedCountryOption.text();

    let url = contextPath + "states/get-all/" + selectedCountryId;

    $.get(url, function(responseJson) {
        $.each(responseJson, function(index, state) {
            totalState++;
            selectorState.append(`<option value="${state.id}">${state.name}</option>`);
        });
    }).done(function() {
        fieldStateName.val("").prop("disabled", true);
        btnAddOrNewInState.prop("value", "New");
        btnUpdateInState.prop("disabled", true);
        btnDeleteInState.prop("disabled", true);
        totalStateLabel.text(`Total State: ${totalState}`);
        showToastMessage(`All states of ${selectedCountryName} have been loaded!`);
    }).fail(function() {
        showToastMessage("Could not connect to server or server encountered an error!");
    });
}

// 3
function showSelectedState() {
    fieldStateName.prop("disabled", false);
    btnAddOrNewInState.prop("value", "New");
    btnUpdateInState.prop("disabled", false);
    btnDeleteInState.prop("disabled", false);

    let selectedStateOption = $("#selectorState option:selected");
    let selectedStateName = selectedStateOption.text();
    fieldStateName.val(selectedStateName);
}

// 4
function addNewState() {
    let selectedCountryOption = $("#selectorCountryForState option:selected");
    if(selectedCountryOption.length == 0) {
        alert("Please select or load countries before add new state!");
        return;
    }

    let countryId = selectedCountryOption.attr("value");
    let countryName = selectedCountryOption.text();

    let url = contextPath + "states/save";
    let name = fieldStateName.val();

    if(name == "") {
        showNotificationDialogForState();
        return;
    }

    let jsonData = {
        name: name,
        country: {
            id: countryId,
            name: countryName
        }
    }

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: "application/json"
    }).done(function(returnedStateId) {
        fieldStateName.val("").focus();
        loadStatesAndSelectOne(returnedStateId);
        showToastMessage("State: " + name + " has been created!");
    }).fail(function() {
        showToastMessage("Could not connect to server or server encountered an error!");
    });
}

// 4.1
function loadStatesAndSelectOne(stateId) {
    totalState = 0;
    selectorState.empty();
    let selectedCountryOption = $("#selectorCountryForState option:selected");
    let selectedCountryId = selectedCountryOption.attr("value");

    let url = contextPath + "states/get-all/" + selectedCountryId;
    $.get(url, function(responseJson) {
        $.each(responseJson, function(index, state) {
            totalState++;
            if(stateId == state.id) {
                selectorState.append(`<option value="${state.id}" selected>${state.name}</option>`);
            } else {
                selectorState.append(`<option value="${state.id}">${state.name}</option>`);
            }
        });
        totalStateLabel.text(`Total State: ${totalState}`);
    });
}

// 5
function updateState() {
    let selectedCountryOption = $("#selectorCountryForState option:selected");
    let selectedStateOption = $("#selectorState option:selected");

    let url = contextPath + "states/save";
    let countryId = selectedCountryOption.attr("value");
    let countryName = selectedCountryOption.text();
    let stateId = selectedStateOption.attr("value");
    let stateName = fieldStateName.val();
    if(name == "") {
        showNotificationDialogForState();
        return;
    }

    let jsonData = {
        id: stateId,
        name: stateName,
        country: {
            id: countryId,
            name: countryName
        }
    }

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: "application/json"
    }).done(function(returnedStateId) {
        doneFunction1();

        loadStatesAndSelectOne(returnedStateId);
        showToastMessage("State: " + name + " has been updated!");
    }).fail(function() {
        showToastMessage("Could not connect to server or server encountered an error!");
    });
}

// 6
function showConfirmDialog(modalContent) {
    confirmDialog.modal("show");
    $("#modalContent").text(modalContent);
    $("#btnYes").on("click", function(e) {
        e.preventDefault();
        deleteState();
        $("#btnNo").click();
    });
}

// 6.1
function deleteState() {
    let selectedStateOption = $("#selectorState option:selected");
    let selectedStateId = selectedStateOption.attr("value");
    let url = contextPath + "states/delete/" +selectedStateId;

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function() {
        doneFunction1();

        loadStatesAfterSelectCountry();
        showToastMessage("State has been deleted");
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

// 2. done function for update(5) and delete(6)
function doneFunction1() {
    fieldStateName.val("").prop("disabled", true);
    btnUpdateInState.prop("disabled", true)
    btnDeleteInState.prop("disabled", true)
}

// 3
function showNotificationDialogForState() {
    $("#modalBody").text("State name can't be blank!");
    $("#modalDialog").modal("show");
}