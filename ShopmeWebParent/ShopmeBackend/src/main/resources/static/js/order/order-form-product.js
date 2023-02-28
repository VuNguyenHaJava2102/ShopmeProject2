$(document).ready(function() {
    // Event
    // 1
    $(".cost-input").on("change", function() {
        changeProductCostTotal();
    });

    // 2
    $(".quantity-input").on("change", function() {
        changeSubtotalInputForQuantity($(this));
        changeProductCostTotal();
        changeSubtotalOverview();
        changeShippingCost($(this));
    });

    // 3
    $(".unit-price-input").on("change", function() {
        changeSubtotalInputForUnitPrice($(this));
        changeSubtotalOverview();
    });
});

// Function
// 1
let changeSubtotalInputForQuantity = (quantityInput) => {
    let newQuantity = quantityInput.val();
    let row = quantityInput.attr("id").slice(-1);
    let unitPrice = parseFloat($("#unitPrice" + row).val());
    $("#subtotal" + row).val(unitPrice * newQuantity);
}

// 2
let changeSubtotalInputForUnitPrice = (unitPriceInput) => {
    let newPrice = unitPriceInput.val();
    let row = unitPriceInput.attr("id").slice(-1);
    let quantity = parseFloat($("#quantity" + row).val());
    $("#subtotal" + row).val(newPrice * quantity);
}

// 3
let changeProductCostTotal = () => {
    let costTotal = 0.0;
    $(".cost-input").each(function() {
        let costInput = $(this);
        let row = costInput.attr("id").slice(-1);
        let cost = parseFloat(costInput.val());

        let quantityInput = $("#quantity" + row);
        let quantity = parseInt(quantityInput.val());
        costTotal += cost * quantity;
    });
    $("#productCost").val(costTotal);
}

// 4
let changeSubtotalOverview = () => {
    let subtotalOverview = 0.0;
    $(".subtotal-input").each(function() {
        let subtotal = parseFloat($(this).val());
        subtotalOverview += subtotal;
    });
    $("#subtotal").val(subtotalOverview);
    let shipCostTotal = parseFloat($("#shippingCost").val());
    console.log(shipCostTotal);
    let total = shipCostTotal + subtotalOverview;
    console.log(total);
    $("#total").val(total);
}

// 5
let changeShippingCost = (quantityInput) => {
    let row = quantityInput.attr("id");
    let shipInput = $("#shipping" + row);
    let shipValue = shipInput.val();
}

