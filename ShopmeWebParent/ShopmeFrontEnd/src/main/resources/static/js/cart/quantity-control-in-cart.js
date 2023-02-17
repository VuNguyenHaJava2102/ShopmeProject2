$(document).ready(function() {
   checkQuantityInputs();

   // $(".link-minus").each(function() {
   //    $(this).on("click", function(event) {
   //       event.preventDefault();
   //
   //       let productId = $(this).attr("pid");
   //       let form = $("#form-minus" + productId);
   //
   //       form.submit();
   //       console.log("minus-s");
   //    });
   // });
   //
   // $(".link-plus").each(function() {
   //    $(this).on("click", function(event) {
   //       event.preventDefault();
   //
   //       let productId = $(this).attr("pid");
   //       let form = $("#form-plus" + productId);
   //
   //       form.submit();
   //       console.log("plus-s");
   //    });
   // });

   // $(".link-plus").each(function(index) {
   //    $(this).on("click", function(event) {
   //       event.preventDefault();
   //
   //       let productId = $(this).attr("pid");
   //       let quantityInput = $("#quantity" + productId);
   //       let quantityValue = parseInt(quantityInput.val());
   //       let newQuantityValue = quantityValue + 1;
   //       let url = contextPath + "cart/update/" + productId + "/" + newQuantityValue;
   //
   //       window.location = url;
   //
   //       $.ajax({
   //          type: "POST",
   //          url: url,
   //          beforeSend: function(xhr) {
   //             xhr.setRequestHeader(csrfHeaderName, csrfValue);
   //          }
   //       }).done(function() {
   //          window.location = contextPath + "cart";
   //          checkQuantityInputs();
   //       });
   //    });
   // });

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