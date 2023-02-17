$(document).ready(function() {
   checkQuantityInputs();

   $(".link-delete").on("click", function(event) {
      event.preventDefault();

      let clickedLink = $(this);
      let productName = clickedLink.attr("pName");
      let message = `Are you sure to delete item: '${productName}' from cart?`;
      let copyLink = clickedLink.attr("href");

      $("#modalContent").text(message);
      $("#btnYes").attr("href", copyLink);
      $("#confirmDialog").modal("show");
   });
});

let checkQuantityInputs = () => {
   $(".quantity-value").each(function() {
      let productId = $(this).attr("pid");
      let linkMinus = $("#minus" + productId);
      let linkPlus = $("#plus" + productId);
      let value = parseInt($(this).val());

      if(value === 1) {
         linkMinus.addClass("disabled");
      } else {
         linkMinus.removeClass("disabled");
      }

      if(value === 5) {
         linkPlus.addClass("disabled");
      } else {
         linkPlus.removeClass("disabled");
      }
   });
}