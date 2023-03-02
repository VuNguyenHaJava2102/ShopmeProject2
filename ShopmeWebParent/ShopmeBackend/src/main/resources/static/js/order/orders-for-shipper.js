let icons = {
    PICKED: "fa-people-carry-box",
    SHIPPING: "fa-shipping-fast",
    DELIVERED: "fa-box-open",
    RETURNED: "fa-undo",
};

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
        }).done(function(response) {
            showNotificationDialog("Notification", "Order status has been updated successfully!");
            changeLinkToIcon(response.orderId, response.status);
        }).fail(function() {
            showNotificationDialog("Notification", "Order status has been updated failed!");
        });
    });
});

// function
// 1
let showNotificationDialog = (title, body) => {
    $("#modalTitle").text(title);
    $("#modalBody").text(body);
    $("#modalDialog").modal();
}

// 2
let changeLinkToIcon = (orderId, status) => {
    let id = status + orderId;
    let clickedLink = $("#" + id);
    let class1 = `"fas ${icons[status]} fa-2x icon-green"`; // icons[status] is enhanced object literals
    clickedLink.replaceWith(`<i class=${class1}></i>`);
}