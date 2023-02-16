$(document).ready(function() {
   $("#addToCartBtn").on("click", function(event) {
      event.preventDefault();
      handleAddToCartBtn();
   });
});

// Function:
// 1
let handleAddToCartBtn = () => {
   let quantity = $(".quantity-value").val();
   let url = contextPath + "cart/add/" + productId + "/" + quantity;

   $.ajax({
      type: "POST",
      url: url,
      beforeSend: function(xhr) {
         xhr.setRequestHeader(csrfHeaderName, csrfValue);
      },
   }).done(function(response) {
      showNotificationDialog("Notification", response);
   }).fail(function() {
      showNotificationDialog("Error", "Some errors occurred with server!");
   });
}

let showNotificationDialog = (title, message) => {
   $("#modalBody").text(message);
   $("#modalTitle").text(title);
   $("#notificationDialog").modal("show");
}