let quantityInput;

$(document).ready(function() {
    quantityInput = $(".quantity-value");
    checkQuantity();

   $(".link-minus").on("click", function(event) {
       event.preventDefault();

       let curQuantityInputValue = parseInt(quantityInput.val());
       let newQuantity = curQuantityInputValue - 1;
       quantityInput.attr("value", newQuantity);
       checkQuantity();
   });

    $(".link-plus").on("click", function(event) {
        event.preventDefault();

        let curQuantityInputValue = parseInt(quantityInput.val());
        let newQuantity = curQuantityInputValue + 1;
        quantityInput.attr("value", newQuantity);
        checkQuantity();
    });
});

let checkQuantity = () => {
    let quantityValue = parseInt(quantityInput.val());

    if(quantityValue === 1) {
        $(".minus-li").addClass("disabled");
    } else {
        $(".minus-li").removeClass("disabled");
    }

    if(quantityValue === 5) {
        $(".plus-li").addClass("disabled");
    } else {
        $(".plus-li").removeClass("disabled");
    }
}