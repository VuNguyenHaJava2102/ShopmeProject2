let returnOrderDialog;
let modalTitle;
let noteTextarea;
let orderId;

$(document).ready(function() {
    returnOrderDialog = $("#returnOrderDialog");
    modalTitle = $("#returnOrderDialogTitle");
    noteTextarea = $("#noteTextarea");

    $(".link-return").on("click", function(event) {
        event.preventDefault();

        showReturnDialog($(this));
    });
});

// function
// 1
let showReturnDialog = (link) => {
    orderId = link.attr("orderId");
    modalTitle.text("Return Order ID #" + orderId);
    returnOrderDialog.modal();
}

// 2
let submitReturnOrderForm = () => {
    let reason = $("input[name='reason']:checked").val();
    let note = noteTextarea.val();

    let url = contextPath + "orders/return";
    let request = {
        orderId: orderId,
        reason: reason,
        note: note,
    };

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: JSON.stringify(request),
        contentType: "application/json"
    }).done(function(response) {
        $("#secondBtn").click();
        showNotificationDialog("Sent request successfully!");

        $("#linkReturn" + response.orderId).remove();
        $("#status" + response.orderId).text("RETURNED_REQUESTED");
    }).fail(function(err) {
        $("#secondBtn").click();
        showNotificationDialog("Sent request failed!");
    });

    return false;
}

// 3
let showNotificationDialog = (modalBody) => {
    $("#modalTitle").text("Return Order ID#" + orderId);
    $("#modalBody").text(modalBody);
    $("#notificationDialog").modal();
}