$(document).ready(function() {
    // event
    // 1
    $(".link-status").on("click", function(event) {
       event.preventDefault();

       let href = $(this).attr("href");
       let orderId = $(this).attr("orderId");
       let status = href.split("/").pop();

       $(".modal-title").text("Update Status Confirmation");
       $("#modalContent").text(`Are you sure to update status of order ID ${orderId} to ${status}?`);
       $("#btnYes").attr("href", href);
       $("#confirmDialog").modal();
    })

    $("#btnYes").on("click", function(event) {
        event.preventDefault();
        $("#btnNo").click();
        let url = $(this).attr("href");
        $.ajax({
            type: "POST",
            url: url,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            }
        }).done(function() {
            showNotificationDialog("Notification", "Order status has been updated successfully!");
        }).fail(function() {
            showNotificationDialog("Notification", "Order status has been updated failed!");
        });
    });
});

// function
let showNotificationDialog = (title, body) => {
    $("#modalTitle").text(title);
    $("#modalBody").text(body);
    $("#modalDialog").modal();
}