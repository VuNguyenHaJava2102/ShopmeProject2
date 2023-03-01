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
    let total = shipCostTotal + subtotalOverview;
    $("#total").val(total);
}

// 5
let changeShippingCost = (quantityInput) => {
    let newQuantity = quantityInput.val();
    let row = quantityInput.attr("id").slice(-1);
    let unitShip = parseFloat($("#unitShip" + row).val());
    let shipSubtotal = newQuantity * unitShip;
    $("#shipping" + row).val(shipSubtotal);

    // Change shipping total in overview tab
    let totalShip = 0.0;
    $(".ship-input").each(function() {
        let ship = parseFloat($(this).val());
        totalShip += ship;
    });

    // shipping total changes => total changes
    $("#shippingCost").val(totalShip);
    let subtotalOverview = parseFloat($("#subtotal").val());
    let total = totalShip + subtotalOverview;
    $("#total").val(total);
}

